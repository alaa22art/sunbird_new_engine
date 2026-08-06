package com.sunbird.lis.interfaces.middleware.core;

import akka.actor.ActorRef;

public class ActorRefVirtualConnectionContainer {

	private ActorRef actorRef;

	private ActorRefVirtualConnectionContainer(ActorRef actorRef) {
		this.actorRef = actorRef;
	}

	public ActorRef getRef(ActorRef actorRef) {
		this.actorRef = actorRef;
		return this.actorRef;
	}

	private static ActorRefVirtualConnectionContainer instance = null;

	public static synchronized ActorRefVirtualConnectionContainer getRefActor(ActorRef actorRef) {
		if (instance == null) {
			instance = new ActorRefVirtualConnectionContainer(actorRef);
		}
		return instance;
	}
}
