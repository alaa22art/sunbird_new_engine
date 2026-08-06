package com.sunbird.lis.interfaces.middleware.core;

import akka.actor.ActorRef;

public class ActorRefDriverManagerContainer {

	private ActorRef actorRef;

	private ActorRefDriverManagerContainer(ActorRef actorRef) {
		this.actorRef = actorRef;
	}

	public ActorRef getRef(ActorRef actorRef) {
		this.actorRef = actorRef;
		return this.actorRef;
	}

	private static ActorRefDriverManagerContainer instance = null;

	public static synchronized ActorRefDriverManagerContainer getRefActor(ActorRef actorRef) {
		if (instance == null) {
			instance = new ActorRefDriverManagerContainer(actorRef);
		}
		return instance;
	}
}
