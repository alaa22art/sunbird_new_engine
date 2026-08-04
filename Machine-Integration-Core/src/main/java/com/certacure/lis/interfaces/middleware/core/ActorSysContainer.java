package com.certacure.lis.interfaces.middleware.core;

import akka.actor.ActorSystem;

/**
 * Created by icent on 2017. 1. 23..
 */
public class ActorSysContainer {

	private ActorSystem sys;

	private ActorSysContainer() {
		sys = ActorSystem.create("Main");
	}

	public ActorSystem getSystem() {
		return sys;
	}

	private static ActorSysContainer instance = null;

	public static synchronized ActorSysContainer getInstance() {
		if (instance == null) {
			instance = new ActorSysContainer();
		}
		return instance;
	}
}
