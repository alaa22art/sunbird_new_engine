package com.sunbird.lis.interfaces.middleware.flow_component.hl724ClientOverTcp;

import java.util.concurrent.TimeUnit;

import com.sunbird.lis.interfaces.middleware.core.ConfMsg;
import com.typesafe.config.Config;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class Hl724ClientOverTcpControllerConf implements ConfMsg {

	private static final long serialVersionUID = 1L;
	public final String address;
	public final int portNumber;
	public final ActorRef lowLevelRecipient;
	public final ActorRef highLevelRecipient;
	public final FiniteDuration reconnectInterval;
	private int maxFrameSize;


	public Hl724ClientOverTcpControllerConf(String address, int portNumber, ActorRef lowLevelRecipient,
			ActorRef highLevelRecipient, FiniteDuration reconnectInterval) {

		this.address = address;
		this.portNumber = portNumber;
		this.lowLevelRecipient = lowLevelRecipient;
		this.highLevelRecipient = highLevelRecipient;
		this.reconnectInterval = reconnectInterval;

	}


	public Hl724ClientOverTcpControllerConf(int maxFrameSize, ActorRef lowLevelRecipient,
			ActorRef highLevelRecipient) {
		this.maxFrameSize = maxFrameSize;
		this.address = "";
		this.portNumber = 0;
		this.lowLevelRecipient = lowLevelRecipient;
		this.highLevelRecipient = highLevelRecipient;
		this.reconnectInterval = new FiniteDuration(500, TimeUnit.SECONDS);
	}


	@Override
	public String toString() {
		return "Hl724ClientOverTcpControllerConf{" +
				", lowLevelRecipient=" + lowLevelRecipient +
				", highLevelRecipient=" + highLevelRecipient +
				'}';
	}

	/*public enum ConfKey {
		address,
		port,
		recipientActor,
		reconnectInterval
	}*/


	public enum ConfKey {
		maxFrameSize,
		lowLevelRecipient,
		highLevelRecipient,
		address,
		port,
		recipientActor,
		reconnectInterval
	}

	public static Hl724ClientOverTcpControllerConf create(Config config, ActorContext ctx) {
		int maxFrameSize = config.getInt(ConfKey.maxFrameSize.name());
		ActorRef lowLevelRecipient;
		ActorRef highLevelRecipient;
		Future<ActorRef> lowLevelRecipientFuture = ctx	.actorSelection(config.getString(ConfKey.lowLevelRecipient.name()))
														.resolveOne(Duration.apply(1, TimeUnit.SECONDS));
		Future<ActorRef> highLevelRecipientFuture = ctx	.actorSelection(config.getString(ConfKey.highLevelRecipient.name()))
														.resolveOne(Duration.apply(1, TimeUnit.SECONDS));
		try {
			lowLevelRecipient = Await.result(lowLevelRecipientFuture, Duration.apply(1, TimeUnit.SECONDS));
			highLevelRecipient = Await.result(highLevelRecipientFuture, Duration.apply(1, TimeUnit.SECONDS));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new Hl724ClientOverTcpControllerConf(maxFrameSize, lowLevelRecipient, highLevelRecipient);
	}


}