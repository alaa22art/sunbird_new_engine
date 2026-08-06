package com.sunbird.lis.interfaces.middleware.core;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.middleware.ActorContextInfo;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.ConnectionStatus;
import com.sunbird.lis.interfaces.service.MachineService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.AbstractActor;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.collection.JavaConversions;

public class ConnectionController extends AbstractActor {

    public static Props props() {
        return Props.create(ConnectionController.class, () -> new ConnectionController());
    }

    private final LoggingAdapter log= Logging.getLogger(context().system(), this);
    Config configAnalyzer= null;
    boolean isFinish= false;
    ActorRef currentActorConnection= null;

    public ConnectionController() {

        // SecUser currentUser = SecurityUtil.getCurrentUser();
        // currentUser.getRid();
        log.info("Received String message: {}");
        System.out.println("Received String message: {}");
        ObjectMapper mapper= new ObjectMapper();

        receive(ReceiveBuilder.match(ConnectionStatus.class, msg -> {

            if (msg.status == "Close") {
                // Get the actor connection for UI
                currentActorConnection= ActorRefVirtualConnectionContainer.getRefActor(sender())
                    .getRef(sender());
                // Get the pre-generated context map for all the machines
                Map<String, ActorContext> mapContext= ActorContextInfo.getRefActor(null)
                    .getRef(null);
                MachineService machineService= (MachineService) SpringUtil
                    .getBean("MachineService");
                Machine machine = machineService.getMachineByPort(msg.port);
                // Get the actor for the selected machine
                ActorContext actorContext= mapContext.get(machine.getMachineActorPath());
                if (actorContext != null) {
                    java.lang.Iterable<ActorRef> machineActorChilds= JavaConversions
                        .asJavaIterable(actorContext.children());
                    // loop on all the virtual actors assigned for the machine and shut them down.
                    for (ActorRef child : machineActorChilds) {
                        actorContext.stop(child);
                    }
                }
                // update the database port and the connection
                machine.setIsPortOpen(false);
                machine.setConnected(false);
                machineService.updateMachine(machine);
                // return the confirmation of closing the port
                currentActorConnection.tell("Finish", self());

            } else if (msg.status == "Open") {
                // Get the actor connection for UI
                currentActorConnection= ActorRefVirtualConnectionContainer.getRefActor(sender())
                    .getRef(sender());
                MachineService machineService= (MachineService) SpringUtil
                    .getBean("MachineService");
                Machine machine= machineService.getMachineByPort(msg.port);
                // Check if the machine configuration exist
                if (machine.getMachineType().getDriver().getConfigTxt() != null) {
                    Map<String, Object> machineConfigText= mapper.readValue(
                        machine.getMachineType().getDriver().getConfigTxt(),
                        new TypeReference<Map<String, Object>>() {});
                    configAnalyzer= ConfigFactory.parseMap(machineConfigText);
                    long millis= System.currentTimeMillis() % 1000;
                    // Create a new driverManager actor for this machine to start the process
                    context().actorOf(DriverManager.props(configAnalyzer, machine),
                        "DriverManager" + machine.getName() + machine.getTenantId() + "_" +
                            machine.getBranchId() + millis);
                }

            } else if (msg.status == "Bound") {
                isFinish= true;
                currentActorConnection.tell("Finish", self());
            }

            log.info("Received String message: {}", msg);

            System.out.println("Received String message: {}" + msg);
            
        }).matchAny(o -> log.info("received unknown message" + o)).build());
    }
}
