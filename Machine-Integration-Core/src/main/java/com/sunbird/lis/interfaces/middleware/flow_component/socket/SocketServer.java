package com.sunbird.lis.interfaces.middleware.flow_component.socket;

import java.net.InetSocketAddress;

import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.middleware.core.ActorRefConnectionContainer;
import com.sunbird.lis.interfaces.middleware.core.FlowComponent;
import com.sunbird.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.ConnectionStatus;
import com.sunbird.lis.interfaces.service.MachineService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.io.Tcp;
import akka.io.Tcp.Bound;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.japi.pf.ReceiveBuilder;
import akka.util.ByteString;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class SocketServer extends FlowComponent<SocketServerConf> {

    Config config= null;
    private MachineService machineService;

    @Override
    protected void init() throws Exception {
        Machine machine= getMachine();

        final ActorRef tcp= Tcp.get(context().system()).manager();
        tcp.tell(TcpMessage.bind(self(),
            new InetSocketAddress(machine.getServerIpAddress(), machine.getServerPort()),
            5), self());

    }

    private Machine getMachine() {
        machineService= (MachineService) SpringUtil.getBean("MachineService");
        Machine machine= machineService.getMachineByActorPath(getContext().parent().toString());
        return machine;
    }

    @Override
    protected PartialFunction<Object, BoxedUnit> getBehaviour() {
        return ReceiveBuilder.match(Bound.class, msg -> {
            // This match to indicate the port is open
            log.info("Server listening for connection on " + conf.address + ":" + conf.portNumber);
            machineService= (MachineService) SpringUtil.getBean("MachineService");
            Machine machine= machineService.getMachineByActorPath(getContext().parent().toString());
            // update machine port status on database to open
            machine.setIsPortOpen(true);
            machineService.updateMachine(machine);
            // alaa : need to review no need for it
            ActorRef currentActorConnection= ActorRefConnectionContainer.getRefActor(null)
                .getRef(null);
            if (currentActorConnection != null) {
                ConnectionStatus connectionStatus= new ConnectionStatus();
                connectionStatus.status= "Bound";
                connectionStatus.port= machine.getServerPort();
                connectionStatus.machine= machine;
                currentActorConnection.tell(connectionStatus, self());
            }
        }).match(CommandFailed.class, msg ->

        {
            throw new RuntimeException("Unable to start server on " + conf.address + ":" +
                conf.portNumber + " - " + msg.cmd().failureMessage());
        }).match(Connected.class, msg -> {
            // this match indicate that the machine is connected
            config= ConfigFactory.load();
            MachineService machineService= (MachineService) SpringUtil.getBean("MachineService");
            Connected connectionInfo= msg;
            String remoteIpAddress= connectionInfo.remoteAddress().getHostString().toString();
            Integer port= connectionInfo.localAddress().getPort();
            Machine machine= machineService.getMachineByPort(port);
            String checkMachineIpAddress= config.getString("enable.check.machineip");

            if (checkMachineIpAddress.equals("1")) {
                if (!remoteIpAddress.equals(machine.getMachineIpAddress().toString())) { return; }
            }
            if (machine.getConnected() == false) {
                sender().tell(TcpMessage.register(self()), self());
                context().become(getConnectedState(sender()));

                unstashAll();

                // update on machine connection status on database
                machine.setConnected(true);
                machineService.updateMachine(machine);
            }

        }).match(BytesMessage.class, msg -> {
            stash();
        }).build();
    }

    public void closeConnection() {
        final ActorRef tcp= Tcp.get(context().system()).manager();
        tcp.tell(TcpMessage.close(), self());
    }

    private PartialFunction<Object, BoxedUnit> getConnectedState(ActorRef connection) {
        return ReceiveBuilder.match(BytesMessage.class, msg -> {
            connection.tell(TcpMessage.write(ByteString.fromArray(msg.getByteArray())), self());
        }).match(Received.class,
            msg -> conf.recipientActor.tell(new BytesMessage(msg.data().toArray()), self()))
            .match(CommandFailed.class, msg -> {
                log.error("Connection with client lost");
                context().unbecome();
            }).match(ConnectionClosed.class, msg -> {
                log.error("Connection closed");
                context().become(getBehaviour(), true);
                sender().tell(TcpMessage.close(), self());
                MachineService machineService= (MachineService) SpringUtil
                    .getBean("MachineService");
                Machine machine= machineService
                    .getMachineByActorPath(getContext().parent().toString());
                machine.setConnected(false);
                machineService.updateMachine(machine);
            }).match(Connected.class, msg -> {
                sender().tell(TcpMessage.close(), self());
            }).build();
    }
}