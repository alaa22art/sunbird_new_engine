package com.certacure.lis.interfaces.middleware.flow_component.LabOrderCollecterScheduler;

import java.util.concurrent.TimeUnit;

import com.certacure.lis.interfaces.middleware.core.ConfMsg;
import com.typesafe.config.Config;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class LabOrderControllerConf implements ConfMsg {

	private static final long serialVersionUID = 1L;
	public final ActorRef highLevelRecipient;


	public LabOrderControllerConf( ActorRef lowLevelRecipient,
			ActorRef highLevelRecipient) {
		this.highLevelRecipient = highLevelRecipient;

	}


	public LabOrderControllerConf(
			ActorRef highLevelRecipient) {
		this.highLevelRecipient = highLevelRecipient;

	}


	@Override
	public String toString() {
		return "DatabaseOutboundEmailSenderControllerConf{" +
				"highLevelRecipient=" + highLevelRecipient +
				'}';
	}

	/*public enum ConfKey {
		address,
		port,
		recipientActor,
		reconnectInterval
	}*/


	public enum ConfKey {
		highLevelRecipient,
		recipientActor
	}

	public static LabOrderControllerConf create(Config config, ActorContext ctx) {
		
		ActorRef highLevelRecipient;
		Future<ActorRef> highLevelRecipientFuture = ctx	.actorSelection(config.getString(ConfKey.highLevelRecipient.name()))
														.resolveOne(Duration.apply(1, TimeUnit.SECONDS));
		try {
			//lowLevelRecipient = Await.result(lowLevelRecipientFuture, Duration.apply(1, TimeUnit.SECONDS));
			highLevelRecipient = Await.result(highLevelRecipientFuture, Duration.apply(1, TimeUnit.SECONDS));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new LabOrderControllerConf(highLevelRecipient);
	}


}