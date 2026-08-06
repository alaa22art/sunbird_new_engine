package com.sunbird.lis.interfaces.middleware;

import java.util.Map;

import akka.actor.ActorContext;

public class ActorContextInfo {

	private Map<String, ActorContext> mapContext;

	private ActorContextInfo(Map<String, ActorContext> mapContext) {
		this.mapContext = mapContext;
	}

	public Map<String, ActorContext> getRef(Map<String, ActorContext> mapContext) {

		if (mapContext != null) {
			this.mapContext = mapContext;
		}
		return this.mapContext;
	}

	private static ActorContextInfo instance = null;

	public static synchronized ActorContextInfo getRefActor(Map<String, ActorContext> mapContext) {
		if (instance == null) {
			instance = new ActorContextInfo(mapContext);
		}
		return instance;
	}
}
