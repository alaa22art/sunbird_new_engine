package com.sunbird.lis.interfaces.middleware.core;

import java.io.IOException;
import java.util.Set;

import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.middleware.driver.DynamicDriver;
import com.sunbird.lis.interfaces.middleware.util.ConfigUtil;
import com.sunbird.lis.interfaces.service.MachineService;
import com.typesafe.config.Config;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class DriverManager extends AbstractActor {

	public static Props props(Config config, Machine machine) {
		return Props.create(DriverManager.class, () -> new DriverManager(config, machine));
	}

	public DriverManager(Config config, Machine machine) {
		Set<String> driverNames = ConfigUtil.getTopLevelEntries(config);
		String driverName = getDriverName(driverNames);
		Config driverConfig = config.getConfig(driverName);
		//Create machine actor
		ActorRef analyzerDriver = context().actorOf(Props.create(DynamicDriver.class), machine.getName());
		//save the machine actor path
		machine.setMachineActorPath(analyzerDriver.toString());
		try {
			saveActorPath(machine);
		} catch (IOException e) {
			e.printStackTrace();
		}
		analyzerDriver.tell(driverConfig, self());

		receive(ReceiveBuilder
								.match(Object.class, this::unhandled)
								.build());
	}

	private String getDriverName(Set<String> driverNames) {
		return driverNames.stream().findFirst().get();
	}

	public DriverManager() {
		super();
	}

	public void saveActorPath(Machine machineParam) throws IOException {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		machineService.updateMachine(machineParam);
	}

}