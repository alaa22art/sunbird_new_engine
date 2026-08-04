package com.certacure.lis.interfaces.middleware.core;

import akka.actor.ActorRef;

public class ActorRefContainerDriverManager {

	private ActorRef sys;

	private ActorRefContainerDriverManager(ActorRef sys) {
		this.sys = sys;
	}

	public ActorRef getRef(ActorRef sys) {
		if (this.sys == null) {
			this.sys = sys;
		}
		return this.sys;
	}

	private static ActorRefContainerDriverManager instance = null;

	public static synchronized ActorRefContainerDriverManager getRefActor(ActorRef sys) {
		if (instance == null) {
			instance = new ActorRefContainerDriverManager(sys);
		}
		return instance;
	}
}
