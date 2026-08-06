package com.sunbird.lis.interfaces.middleware.core.observer;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.CoreEventLog;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.middleware.core.observer.ObserverProtocol.LogEvent;
import com.sunbird.lis.interfaces.middleware.core.observer.ObserverProtocol.LogLevel;
import com.sunbird.lis.interfaces.service.CoreEventLogService;
import com.sunbird.lis.interfaces.service.MachineService;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.Logging.InitializeLogger;
import akka.japi.pf.ReceiveBuilder;

public class ObserverLoggingAdapter extends AbstractActor {

	private final Map<Integer, LogLevel> logLevels = new HashMap<Integer, LogLevel>() {

		/**
		* 
		*/
		private static final long serialVersionUID = 1L;

		{
			put(Logging.ErrorLevel(), LogLevel.ERROR);
			put(Logging.WarningLevel(), LogLevel.WARNING);
			put(Logging.InfoLevel(), LogLevel.INFO);
			put(Logging.DebugLevel(), LogLevel.DEBUG);
		}
	};

	public ObserverLoggingAdapter() {
		receive(
				ReceiveBuilder
								.match(InitializeLogger.class, __ -> sender().tell(Logging.loggerInitialized(), self()))
								.match(Logging.LogEvent.class, event ->
									{

										if (event == null || event.message() == null) {
											return;
										}
										context().system().eventStream().publish(
												new LogEvent(
														logLevels.get(event.level()),
														event.message() == null ? null : event.message().toString(),
														event.logSource(),
														event.timestamp()));
										String eventMessage = "";
										CoreEventLog dbLogEvent = new CoreEventLog(event);
										CoreEventLogService eventLogService = (CoreEventLogService) SpringUtil.getBean(
												"CoreEventLogService");
										try {

											if (event.message().toString().contains("msg")
													&& event.message().toString().contains("sender")) {
												HashMap<String, Object> result = new ObjectMapper().readValue(
														event.message().toString(),
														HashMap.class);
												if (result.size() > 0) {
													eventMessage += result.get("sender").toString();
													eventMessage += result.get("msg");
													MachineService machineService = (MachineService) SpringUtil.getBean(
															"MachineService");
													Machine machine = machineService.getMachineByActorPath(
															result.get("machinePath").toString());
													dbLogEvent.setText(eventMessage);
													dbLogEvent.setMachine(machine);
													dbLogEvent.setBranchId(machine.getBranchId());
													dbLogEvent.setTenantId(machine.getTenantId());
												}
											}

										} catch (Exception e) {

										}

										try {
											eventLogService.addCoreEventLog(dbLogEvent);
										} catch (Exception e) {
											e.printStackTrace();
										}

									})
								.build());
	}
}
