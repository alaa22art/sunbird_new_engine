package com.sunbird.lis.interfaces.middleware.flow_component.astme138102;

import java.util.concurrent.TimeUnit;

import com.sunbird.lis.interfaces.middleware.core.ConfMsg;
import com.typesafe.config.Config;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class AstmE138102ControllerConf implements ConfMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final int maxFrameSize;
	public final ActorRef lowLevelRecipient;
	public final ActorRef highLevelRecipient;

	public AstmE138102ControllerConf(int maxFrameSize, ActorRef lowLevelRecipient, ActorRef highLevelRecipient) {
		this.maxFrameSize = maxFrameSize;
		this.lowLevelRecipient = lowLevelRecipient;
		this.highLevelRecipient = highLevelRecipient;
	}

	@Override
	public String toString() {
		return "AstmE138102ControllerConf{" +
				"maxFrameSize=" + maxFrameSize +
				", lowLevelRecipient=" + lowLevelRecipient +
				", highLevelRecipient=" + highLevelRecipient +
				'}';
	}

	public enum ConfKey {
		maxFrameSize,
		lowLevelRecipient,
		highLevelRecipient
	}

	public static AstmE138102ControllerConf create(Config config, ActorContext ctx) {
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
		return new AstmE138102ControllerConf(maxFrameSize, lowLevelRecipient, highLevelRecipient);
	}
}