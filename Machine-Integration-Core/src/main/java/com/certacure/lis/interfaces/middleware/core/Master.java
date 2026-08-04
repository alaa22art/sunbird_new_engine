package com.certacure.lis.interfaces.middleware.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.middleware.core.observer.Observer;
import com.certacure.lis.interfaces.middleware.core.observer.ObserverProtocol.AnalyzerInitialized;
import com.certacure.lis.interfaces.middleware.core.observer.ObserverProtocol.MsgProcessed;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.service.MachineService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging.LogEvent;
import akka.japi.pf.ReceiveBuilder;

public class Master extends AbstractActor {

	private Master(Config config) {
		
		MacAddressChecker macAddressChecker = new MacAddressChecker();
		
		 // Example usage
      // String macToCheck = "14-75-5B-E4-60-E2"; // my laptop
       String macToCheck = "30-9C-23-72-8C-CF"; //Alia PC
       
       
       //System.out.println("Local MAC Address: " + macAddressChecker.getLocalMacAddress());
       //System.out.println("Comparing with: " + macAddressChecker.macToCheck);
       
       //boolean isMatch = macAddressChecker.compareMacAddress(macToCheck);
       //System.out.println("MAC addresses match: " + isMatch);
       
       
       //if(!isMatch)
       //{
    	//   return;
       //}
		

		String startUp = config.getString("enable.startup.actor");

		if (startUp.equals("0")) {
			return;
		}

		Config configAnalyzer = null;
		ObjectMapper mapper = new ObjectMapper();
		Config observerConfig = config.getConfig("Observer");
		ActorRef obsever = context().actorOf(Observer.props(observerConfig), "Observer");

		context().system().eventStream().subscribe(obsever, MsgProcessed.class);
		context().system().eventStream().subscribe(obsever, LogEvent.class);
		context().system().eventStream().subscribe(obsever, AnalyzerInitialized.class);
		context().system().eventStream().subscribe(obsever, httpRequstTransaction.class);

		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");

		try {
			
			List<Machine> machineList = machineService.getAllActiveMachine();
			
			
			for (Machine machine : machineList) 
			{	
				//check is machine registered or not 
				boolean isRegistered = checkIfRegisteredMachine(machine.getName());
				
				//if (!isRegistered)
				//	continue;
				
				
				resetRunningMachineFactors(machine, machineService);
				if (getDriverPathFromConfig(machine) != null) {
					Map<String, Object> analyserMapper = mapDriverPathToObjects(mapper, machine);
					configAnalyzer = ConfigFactory.parseMap(analyserMapper);
					runMachineInterfaceService(configAnalyzer, machineService, machine);
					Thread.sleep(2 * 1000);
				}
			}
			receive(ReceiveBuilder.match(Object.class, this::unhandled).build());

		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	private boolean checkIfRegisteredMachine(String strMachineName) {
		
		//Zain Al Abeeden 
		String arrRegisteredMachine [] = {"SIEMENS_ATELLICA_HL7", "IZH_SYSMEX_CS_1600"
	    	,"SYSMEX_XN_330" ,"SYSMEX_XN_1000" ,"IZH_COBAS_E_411_2" ,"IZH_COBAS_E_411_1","IZH_COBAS_E_411_3" };
		
		//Louthan
		//String arrRegisteredMachine [] = {"LOUTHAN_ABOTT_ANALITY" , "LOUTHAN_SYSMEX_XN_550" };

		for (int i = 0 ; i < arrRegisteredMachine.length; i++)
		{
			if (strMachineName.equalsIgnoreCase(arrRegisteredMachine[i]))
			{
				return true;
			}
		}
		
		
		return false;
	}

	private void runMachineInterfaceService(Config configAnalyzer, MachineService machineService, Machine machine) {
		// Get the machine info again to avoid any optimistic locking
		machine = machineService.findById(machine.getRid());
		context().actorOf(DriverManager.props(configAnalyzer, machine));

	}

	private Map<String, Object> mapDriverPathToObjects(ObjectMapper mapper, Machine machine)
			throws IOException, JsonParseException, JsonMappingException {
		Map<String, Object> analyserMapper = mapper.readValue(getDriverPathFromConfig(machine),
				new TypeReference<Map<String, Object>>() {
				});
		return analyserMapper;
	}

	private void resetRunningMachineFactors(Machine machine, MachineService machineService) {
		machine.setIsPortOpen(false);
		machine.setConnected(false);
		machineService.updateMachine(machine);
	}

	private String getDriverPathFromConfig(Machine machine) {
		return machine.getMachineType().getDriver().getConfigTxt();
	}

	private Master() {
		this(ConfigFactory.load());
	}

}