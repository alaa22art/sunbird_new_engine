package com.sunbird.lis.interfaces.middleware;

import akka.actor.ActorContext;

public class ActorContextMaster {

	private ActorContext sys;

	private ActorContextMaster(ActorContext sys) {
		this.sys = sys;
	}

	public ActorContext getRef(ActorContext sys) {

		if (sys != null) {
			this.sys = sys;
		}
		return this.sys;
	}

	private static ActorContextMaster instance = null;

	public static synchronized ActorContextMaster getRefActor(ActorContext sys) {
		if (instance == null) {
			instance = new ActorContextMaster(sys);
		}
		return instance;
	}
}
