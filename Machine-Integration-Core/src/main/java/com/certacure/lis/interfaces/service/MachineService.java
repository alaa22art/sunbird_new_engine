package com.certacure.lis.interfaces.service;

/**
*
* 
*/
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.core.common.business.exception.BusinessException;
import com.certacure.core.common.business.exception.BusinessException.ErrorSeverity;
import com.certacure.core.common.helper.FilterablePageRequest;
import com.certacure.core.common.util.ReflectionUtil;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.helper.MachineIntegrationRights;
import com.certacure.lis.interfaces.middleware.core.ActorRefConnectionContainer;
import com.certacure.lis.interfaces.middleware.core.ActorSysContainer;
import com.certacure.lis.interfaces.middleware.core.ConnectionController;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.ConnectionStatus;
import com.certacure.lis.interfaces.repo.MachineRepo;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

@Service("MachineService")
public class MachineService extends GenericService<Machine, MachineRepo> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MachineRepo repo;

    @Autowired
    private MachineTestsService machineTestsService;

    @Override
    protected MachineRepo getRepository() {
        return repo;
    }

    Config config= null;

    @PreAuthorize("hasAuthority('" + MachineIntegrationRights.ADD_MACHINE_SETUP + "')")
    public Machine addMachine(Machine machine) {
        ReflectionUtil.disableIntercepterFilters(entityManager, true, true,
            () -> isExistMachineNameAndPort(machine.getName(), machine.getServerPort()));

        // isExistMachineNameAndPort(machine.getName(), machine.getServerPort());

        try {
            Machine newMachine= new Machine();
            newMachine= repo.save(machine);
            machineTestsService.addMachineTests(newMachine);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("machine dataalready exists", "portExist",
                ErrorSeverity.ERROR);
        } catch (Exception ex) {
            throw new BusinessException("machine not stored successfully", "", ErrorSeverity.ERROR);
        }

        return repo.save(machine);

    }

    // @PreAuthorize("hasAuthority('" + MachineIntegrationRights.UPD_MACHINE_SETUP + "')")
    @InterceptorFree
    public Machine updateMachine(Machine machine) {

        isExistMachineNameAndPort(machine.getName(), machine.getServerPort(), machine.getRid());
        if (machine.getIsActive() == false && machine.getIsPortOpen() == true) {
            try {
                closeConnection(machine);
                machine= getMachineById(machine.getRid());
                machine.setIsActive(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return repo.save(machine);
    }

    @PreAuthorize("hasAuthority('" + MachineIntegrationRights.DEL_MACHINE_SETUP + "')")
    public void deleteMachine(Long id) {
        repo.deleteById(id);
    }

    @InterceptorFree
    public Machine getMachineByName(String machineName) {
        return repo.getMachineByName(machineName);
    }

    @InterceptorFree
    public Machine getMachineByActorPath(String actorPath) {
        return repo.getMachineByActorPath(actorPath);
    }

    @InterceptorFree
    public Machine getMachineByPort(Integer serverPort) {
        return repo.getMachineByPort(serverPort);
    }

    @InterceptorFree
    public List<Machine> getAllMachine() {
        return repo.getMachines();
    }

    @InterceptorFree
    public List<Machine> getAllActiveMachine() {
        return repo.getActiveMachines();
    }

    @PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_MACHINE_SETUP + "')")
    public Page<Machine> getMachinePage(FilterablePageRequest filterablePageRequest) {
        String[] joins= new String[] { "machineType" };
        Page<Machine> page= getRepository().find(filterablePageRequest.getFilters(),
            filterablePageRequest.getPageRequest(),
            Machine.class, joins);

        return page;
    }

    public Machine saveMachineActor(Machine machine) {

        Machine machineByName= repo.getMachineByName(machine.getName());
        machineByName.setMachineActorPath(machine.getMachineActorPath());
        return repo.save(machineByName);

    }

    public Machine getMachineById(Long rID) {
        return repo.getMachineById(rID);
    }

    // public Page<MachineQuery> getMachineQueryPage(FilterablePageRequest filterablePageRequest) {
    //
    // String[] joins = new String[] { "machine" };
    // Page<MachineQuery> page = machineQueryService.find(filterablePageRequest.getFilters(),
    // filterablePageRequest.getPageRequest(),
    // MachineQuery.class, joins);
    //
    // return page;
    // }
    //
    // public Page<MachineResult> getMachineResultPage(FilterablePageRequest filterablePageRequest)
    // {
    //
    // String[] joins = new String[] { "machine" };
    // Page<MachineResult> page = machineResultService.find(filterablePageRequest.getFilters(),
    // filterablePageRequest.getPageRequest(),
    // MachineResult.class, joins);
    //
    // return page;
    // }
    //
    // public Page<MachineOrder> getMachineOrderPage(FilterablePageRequest filterablePageRequest) {
    //
    // String[] joins = new String[] { "machine" };
    // Page<MachineOrder> page = machineOrderService.find(filterablePageRequest.getFilters(),
    // filterablePageRequest.getPageRequest(),
    // MachineOrder.class, joins);
    //
    // return page;
    // }

    public void isExistMachineNameAndPort(String name, Integer serverPort, Long rId) {

        boolean isExistName= isValuedMachineName(name, rId);
        boolean isExistPort= isValuedPort(serverPort, rId);

        if (isExistName) {
            throw new BusinessException("machine name already exists", "machineNameExist",
                ErrorSeverity.ERROR);
        }

        if (isExistPort) {
            throw new BusinessException("machine port already exists", "portExist",
                ErrorSeverity.ERROR);
        }
    }

    public Void isExistMachineNameAndPort(String name, Integer serverPort) {
        boolean isExistName= isValuedMachineName(name);
        boolean isExistPort= isPortUsed(serverPort);

        if (isExistName) {
            throw new BusinessException("machine name already exists", "machineNameExist",
                ErrorSeverity.ERROR);
        }

        if (isExistPort) {
            throw new BusinessException("machine port already exists", "portExist",
                ErrorSeverity.ERROR);
        }
        return null;
    }

    @InterceptorFree
    private boolean isValuedPort(Integer serverPort, Long rID) {
        return repo.fetchServerPortExist(serverPort, rID) != null;

    }

    @InterceptorFree
    private boolean isPortUsed(Integer serverPort) {
        return repo.fetchServerPortExist(serverPort) != null;

    }

    @InterceptorFree
    private boolean isValuedMachineName(String name, Long rId) {
        return repo.fetchMachineNameExist(name, rId) != null;

    }

    @InterceptorFree
    private boolean isValuedMachineName(String name) {
        return repo.fetchMachineNameExist(name) != null;

    }

    public void closeConnection(Machine machine) throws Exception {
        config= ConfigFactory.load();
        int timeoutDuration= Integer.parseInt(config.getString("actor.time.out"));
        ActorSystem system= ActorSysContainer.getInstance().getSystem();
        // get connection actor
        ActorRef actorConnectionRef= ActorRefConnectionContainer.getRefActor(null).getRef(null);
        if (actorConnectionRef == null) {
            // if the actor connection is null create a new actor connection
            actorConnectionRef= system.actorOf(Props.create(ConnectionController.class),
                "connection");
            ActorRefConnectionContainer.getRefActor(actorConnectionRef).getRef(actorConnectionRef);
        }
        ConnectionStatus connectionStatus= new ConnectionStatus();
        connectionStatus.status= "Close";
        connectionStatus.port= machine.getServerPort();
        connectionStatus.machine= machine;
        // send connection information to the connection actor
        // if there is no response within time out period exception will be thrown
        Timeout timeout= new Timeout(Duration.create(timeoutDuration, "seconds"));
        Future<Object> future= Patterns.ask(actorConnectionRef, connectionStatus, timeout);
        Await.result(future, timeout.duration());
    }

    public void openConnection(Machine machine) {
       
    	config= ConfigFactory.load();

		int timeoutDuration = Integer.parseInt(config.getString("actor.time.out"));
		try {

			ActorSystem system = ActorSysContainer.getInstance().getSystem();
			// get connection actor
			ActorRef actorConnectionRef = ActorRefConnectionContainer.getRefActor(null).getRef(null);
			if (actorConnectionRef == null) {
				// if the actor connection is null create a new actor connection
				actorConnectionRef = system.actorOf(Props.create(ConnectionController.class), "connection");
				ActorRefConnectionContainer.getRefActor(actorConnectionRef).getRef(actorConnectionRef);
			}
			ConnectionStatus connectionStatus = new ConnectionStatus();
			connectionStatus.status = "Open";
			connectionStatus.port = machine.getServerPort();
			// send connection information to the connection actor
			// if there is no response within time out period exception will be thrown
			Timeout timeout = new Timeout(Duration.create(timeoutDuration, "seconds"));
			Future<Object> future = Patterns.ask(actorConnectionRef, connectionStatus, timeout);
			Await.result(future, timeout.duration());

			System.out.println("Received String message: {}");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
