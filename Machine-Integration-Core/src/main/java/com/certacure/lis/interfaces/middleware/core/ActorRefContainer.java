package com.certacure.lis.interfaces.middleware.core;

import akka.actor.ActorRef;

public class ActorRefContainer {

	private ActorRef sys;

	private ActorRefContainer(ActorRef sys) {
		this.sys = sys;

	}

	public ActorRef getRef(ActorRef sys) {
		if (this.sys == null) {
			this.sys = sys;
		}
		return this.sys;
	}

	private static ActorRefContainer instance = null;

	public static synchronized ActorRefContainer getRefActor(ActorRef sys) {
		if (instance == null) {
			instance = new ActorRefContainer(sys);
		}
		return instance;
	}
}
