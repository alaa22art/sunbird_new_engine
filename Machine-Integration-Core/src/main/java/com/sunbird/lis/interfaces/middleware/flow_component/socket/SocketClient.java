package com.sunbird.lis.interfaces.middleware.flow_component.socket;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.DataInboundHL7Message;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.middleware.core.FlowComponent;
import com.sunbird.lis.interfaces.middleware.core.RecipientConf.ConfKey;
import com.sunbird.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;
import com.sunbird.lis.interfaces.service.DataInboundHL7MessageService;
import com.sunbird.lis.interfaces.service.MachineService;

import akka.actor.AbstractScheduler;
import akka.actor.ActorRef;
import akka.actor.LightArrayRevolverScheduler.TimerTask;
import akka.io.Tcp;
import akka.io.Tcp.Bind;
import akka.io.Tcp.Bound;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.japi.pf.ReceiveBuilder;
import akka.util.ByteString;
import scala.PartialFunction;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;

public class SocketClient extends FlowComponent<SocketClientConf> {

    private MachineService machineService;
    
    
    private Machine machine;
    private ActorRef tcp = null;
    


    @Override
    protected void init() throws Exception {
        machine= getMachine();
        InetSocketAddress remote= new InetSocketAddress(machine.getServerIpAddress(),
            machine.getServerPort());

        tcp = Tcp.get(context().system()).manager();
        tcp.tell(TcpMessage.connect(remote), self());
    }

    @Override
    protected PartialFunction<Object, BoxedUnit> getBehaviour() {

        return ReceiveBuilder.match(CommandFailed.class, cmdFailed -> 
        {
            scheduleRestart();

        }).match(Connected.class, msg -> 
        {

            sender().tell(TcpMessage.register(self()), self());

            context().become(getConnectedState(sender()));
            unstashAll();
            
            
            // update on machine connection status on database
            machine.setConnected(true);
            machine.setIsPortOpen(true);
            machineService.updateMachine(machine);

        }).match(ConnectionManager.class, __ -> 
        {

            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyyMMddHHmmssSSS");
            java.util.Date today= Calendar.getInstance().getTime();
            String currTime= dateFormat.format(today);

            throw new RuntimeException("Server port is not reachable !!!!" + currTime);
        }).match(BytesMessage.class, __ -> stash()).build();
    }

    private Machine getMachine() {
        machineService= (MachineService) SpringUtil.getBean("MachineService");
        machine= machineService.getMachineByActorPath(getContext().parent().toString());
        return machine;
    }

    private PartialFunction<Object, BoxedUnit> getConnectedState(ActorRef connection) {
        return ReceiveBuilder
            .match(BytesMessage.class,
                msg -> connection.tell(TcpMessage.write(ByteString.fromArray(msg.getByteArray())),
                    self()))
            .match(Received.class,
                msg -> conf.recipientActor.tell(new BytesMessage(msg.data().toArray()), self()))
            .match(CommandFailed.class, msg -> {
                //scheduleRestart();
            	Machine machine = getMachineInfoByPath();
            	machine.setIsPortOpen(true);
            	machine.setConnected(true);
             	machineService.updateMachine(machine);
            	
                context().unbecome();
            })
            .match(ConnectionClosed.class, msg -> {
               // scheduleRestart();
            	
            	Machine machine = getMachineInfoByPath();
            	machine.setIsPortOpen(false);
            	machine.setConnected(false);
            	machineService.updateMachine(machine);
                context().unbecome();
            }).build();
    }
    
    public Machine getMachineInfoByPath() {
        machineService= (MachineService) SpringUtil.getBean("MachineService");
        Machine machine= machineService.getMachineByActorPath(getContext().parent().toString());
        return machine;
    }

    private void scheduleRestart() {
        context().system().scheduler().scheduleOnce(
            new FiniteDuration(30, TimeUnit.SECONDS), self(),
            new ConnectionManager(), context().dispatcher(), self());
    }

    private class ConnectionManager implements Serializable 
    {
        
               
    }
}