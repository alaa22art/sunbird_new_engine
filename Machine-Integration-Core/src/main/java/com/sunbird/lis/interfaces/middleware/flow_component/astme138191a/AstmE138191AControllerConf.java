package com.sunbird.lis.interfaces.middleware.flow_component.astme138191a;

import java.util.concurrent.TimeUnit;

import com.sunbird.lis.interfaces.middleware.core.ConfMsg;
import com.typesafe.config.Config;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class AstmE138191AControllerConf implements ConfMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final int maxFrameSize;
	public final ActorRef lowLevelRecipient;
	public final ActorRef highLevelRecipient;
	public final ActorRef scheduleLevelRecipient;

	public AstmE138191AControllerConf(int maxFrameSize, ActorRef lowLevelRecipient, ActorRef highLevelRecipient) {
		this.maxFrameSize = maxFrameSize;
		this.lowLevelRecipient = lowLevelRecipient;
		this.highLevelRecipient = highLevelRecipient;
		this.scheduleLevelRecipient = null;
	}

	public AstmE138191AControllerConf(int maxFrameSize, ActorRef lowLevelRecipient, ActorRef highLevelRecipient,
			ActorRef scheduleLevelRecipient) {
		this.maxFrameSize = maxFrameSize;
		this.lowLevelRecipient = lowLevelRecipient;
		this.highLevelRecipient = highLevelRecipient;
		this.scheduleLevelRecipient = scheduleLevelRecipient;
	}

	@Override
	public String toString() {
		return "AstmKeepAliveControllerConf{" +
				"maxFrameSize=" + maxFrameSize +
				", lowLevelRecipient=" + lowLevelRecipient +
				", highLevelRecipient=" + highLevelRecipient +
				", ScheduleLevelRecipient=" + scheduleLevelRecipient +
				'}';
	}

	public enum ConfKey {
		maxFrameSize,
		lowLevelRecipient,
		highLevelRecipient,
		ScheduleLevelRecipient
	}

	public static AstmE138191AControllerConf create(Config config, ActorContext ctx) {
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
		return new AstmE138191AControllerConf(maxFrameSize, lowLevelRecipient, highLevelRecipient);
	}
}