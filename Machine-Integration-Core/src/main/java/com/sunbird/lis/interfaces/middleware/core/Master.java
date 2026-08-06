package com.sunbird.lis.interfaces.middleware.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.middleware.core.observer.Observer;
import com.sunbird.lis.interfaces.middleware.core.observer.ObserverProtocol.AnalyzerInitialized;
import com.sunbird.lis.interfaces.middleware.core.observer.ObserverProtocol.MsgProcessed;
import com.sunbird.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.sunbird.lis.interfaces.service.MachineService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging.LogEvent;
import akka.japi.pf.ReceiveBuilder;

public class Master extends AbstractActor {

	private Master(Config config) {

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
			for (Machine machine : machineList) {
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