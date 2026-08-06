package com.sunbird.lis.interfaces.middleware.flow_component.scheduler;

import java.util.concurrent.TimeUnit;

import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.middleware.core.ConfMsg;
import com.sunbird.lis.interfaces.service.MachineService;
import com.typesafe.config.Config;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class SchedulerServerConf implements ConfMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final String address;
	public final int portNumber;
	public final String machineName;
	public final ActorRef recipientActor;

	public SchedulerServerConf(String address, int portNumber, String machineName, ActorRef recipientActor) {
		this.address = address;
		this.portNumber = portNumber;
		this.machineName = machineName;
		this.recipientActor = recipientActor;
	}

	@Override
	public String toString() {
		return "SocketServerConf{" +
				"address='" + address + '\'' +
				", portNumber=" + portNumber +
				", machineName=" + machineName +
				", recipientActor=" + recipientActor +
				'}';
	}

	public enum ConfKey {
		address,
		port,
		machineName,
		recipientActor
	}

	public static SchedulerServerConf create(Config config, ActorContext ctx) {
		ActorRef recipientActor;
		Future<ActorRef> actorRefFuture = ctx
												.actorSelection(config.getString(ConfKey.recipientActor.name()))
												.resolveOne(Duration.apply(1, TimeUnit.SECONDS));
		Machine machine = getMachine(ctx);
		try {
			recipientActor = Await.result(actorRefFuture, FiniteDuration.apply(1, TimeUnit.SECONDS));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("machine.getServerIpAddress()" + machine.getServerIpAddress() + ", machine.getServerPort()"
				+ machine.getServerPort() + ", machine.getName()" + machine.getName() + ", recipientActor" + recipientActor);
		return new SchedulerServerConf(machine.getServerIpAddress(), machine.getServerPort(), machine.getName(), recipientActor);
	}

	private static Machine getMachine(ActorContext ctx) {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(ctx.self().toString());
		return machine;
	}

}
