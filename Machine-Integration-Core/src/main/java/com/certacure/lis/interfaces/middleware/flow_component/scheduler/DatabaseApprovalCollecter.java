package com.certacure.lis.interfaces.middleware.flow_component.scheduler;

import static com.certacure.lis.interfaces.middleware.flow_component.astme138191async.AstmE138191AsyncProtocol.ENQBytes;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.ElegabalityApprovalEntity;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;

import com.certacure.lis.interfaces.service.DataInboundHL7MessageService;
import com.certacure.lis.interfaces.service.ElegabalityApprovalService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.PostDetailFinancialTransactionService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.japi.pf.ReceiveBuilder;
import net.sf.ehcache.search.expression.And;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;

public class DatabaseApprovalCollecter extends FlowComponent<RecipientConf> {

    private Config config= null;

    private MachineService machineService;
    private Machine machine;
    private DataInboundHL7Message inboundHL7Message;
    private DataInboundHL7MessageService HL7MessageService;
    private ElegabalityApprovalService elegabalityApprovalService ;
    private PostDetailFinancialTransaction postDetailFinancialTransaction;
    
    private PostDetailFinancialTransactionService postDetailFinancialTransactionService;
    
    @Override
    protected void init() {
        System.out.println("DB COLLECTER BUILD");
        
        postDetailFinancialTransactionService = (PostDetailFinancialTransactionService) SpringUtil
    			.getBean("PostDetailFinancialTransactionService");

        context().system().scheduler().schedule(
            new FiniteDuration(10, TimeUnit.SECONDS), new FiniteDuration(10, TimeUnit.SECONDS),
            self(),
            new DBFetchManager(), context().dispatcher(), self());
       
        
        machine = getMachine();
    }

    @Override
    protected PartialFunction<Object, BoxedUnit> getBehaviour() {
        return ReceiveBuilder
            .match(String.class, this::convertAndForward)

            .match(DBFetchManager.class, __ -> {
                System.out.println("DB COLLECTER CALLED AGINE");
                List<ElegabalityApprovalEntity> listAllPendingOrder = null;
                
                
                // throw new RuntimeException("Restart needed");
                
             
                machine = getMachineInfoByPath();

					if (machine.getIsActive() && machine.getConnected()) 
					{
						
						List<ElegabalityApprovalEntity> lstElegApp =  FetchAllAppointmentApproval();
						
						if(lstElegApp.size() > 0)
						{
							conf.recipient.tell(lstElegApp.get(0), self());
						}

					}
				})
    

				.build();
	}
    
    public Machine getMachineInfoByPath() {
        MachineService machineService= (MachineService) SpringUtil.getBean("MachineService");
        Machine machine= machineService.getMachineByActorPath(getContext().parent().toString());
        return machine;
    }

    
private List<ElegabalityApprovalEntity> FetchAllAppointmentApproval() {
	
	
	elegabalityApprovalService = (ElegabalityApprovalService) SpringUtil
            .getBean("ElegabalityApprovalService");

//    List<DataInboundHL7Message> lstAllHL7Message = HL7MessageService
//        .getAllPendingHL7Message(inboundHL7Message);
    
    List<ElegabalityApprovalEntity> elegabalityApprovalEntities = elegabalityApprovalService
            .getAllPendingElegablityAppointmentApproval();

    System.out.println(elegabalityApprovalEntities);
    
    return elegabalityApprovalEntities;
		
	}

private  List<ElegabalityApprovalEntity>  FetchAllPendingOutboundMessages() {
       

        elegabalityApprovalService = (ElegabalityApprovalService) SpringUtil
                .getBean("ElegabalityApprovalService");

//        List<DataInboundHL7Message> lstAllHL7Message = HL7MessageService
//            .getAllPendingHL7Message(inboundHL7Message);
        
        List<ElegabalityApprovalEntity> elegabalityApprovalEntities = elegabalityApprovalService
                .getAllPendingElegablityApproval();

        System.out.println(elegabalityApprovalEntities);
        
        return elegabalityApprovalEntities;
    }

    private void convertAndForward(httpRequstTransaction httpRequstTransObject) {
        conf.recipient.tell(httpRequstTransObject, self());
    }

   
    private void convertAndForward(String strData) {

    }

    private Machine getMachine() {
        machineService= (MachineService) SpringUtil.getBean("MachineService");
        machine= machineService.getMachineByActorPath(getContext().parent().toString());
        return machine;
    }

    private class DBFetchManager implements Serializable {
        public DBFetchManager() {
            System.out.println("DB FETCH MANAGER CALLED");
        }

    }
    
    public  Date convertStringToDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        return dateFormat.parse(dateString);
    }

}
