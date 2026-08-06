package com.certacure.lis.interfaces.middleware.core;

import akka.actor.ActorRef;

public class ActorRefConnectionContainer {

	private ActorRef actorRef;

	private ActorRefConnectionContainer(ActorRef actorRef) {
		this.actorRef = actorRef;

	}

	public ActorRef getRef(ActorRef actorRef) {
		if (this.actorRef == null) {
			this.actorRef = actorRef;
		}
		return this.actorRef;
	}

	private static ActorRefConnectionContainer instance = null;

	public static synchronized ActorRefConnectionContainer getRefActor(ActorRef actorRef) {
		if (instance == null) {
			instance = new ActorRefConnectionContainer(actorRef);
		}
		return instance;
	}
}
