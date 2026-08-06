package com.sunbird.lis.interfaces.middleware.driver;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.stop;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import com.sunbird.lis.interfaces.middleware.ActorContextInfo;
import com.sunbird.lis.interfaces.middleware.core.ConfMsg;
import com.sunbird.lis.interfaces.middleware.core.observer.ObserverProtocol.AnalyzerInitialized;
import com.sunbird.lis.interfaces.middleware.util.ConfigUtil;
import com.typesafe.config.Config;

import akka.actor.AbstractActor;
import akka.actor.ActorContext;
import akka.actor.ActorInitializationException;
import akka.actor.ActorKilledException;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.FiniteDuration;

public class DynamicDriver extends AbstractActor {

	private final List<Component> components = new ArrayList<>();
	private final SupervisorStrategy supervisorStrategy;
	Map<String, ActorContext> mapActorContext = new HashMap<String, ActorContext>();

	private DynamicDriver() {
		supervisorStrategy = new OneForOneStrategy(20, FiniteDuration.apply(1, TimeUnit.MINUTES),
				DeciderBuilder.match(ActorInitializationException.class, e -> escalate())
						.match(ActorKilledException.class, e -> stop()).match(Exception.class, e -> restart())
						.matchAny(__ -> escalate()).build());

		receive(ReceiveBuilder.match(Config.class, config -> {
			for (String componentName : ConfigUtil.getTopLevelEntries(config)) {
				String componentTypeName = config.hasPath("type") ? config.getString("type") : componentName;
				ComponentType componentType = ComponentType.valueOf(componentTypeName);
				ActorRef componentActor = context().actorOf(Props.create(componentType.klass), componentName);
				components.add(new Component(componentName, componentActor, componentType.configFuction));
			}
			context().system().eventStream().publish(
					new AnalyzerInitialized(self(), components.stream().map(c -> 
					c.actorRef).collect(toList())));
			
			for (Component component : components) {
				ConfMsg componentConfig = component.confFunction.apply(config.getConfig(component.name),
						context());

				System.out.println("/////////////////////////////////////////////////////");

				System.out.println(componentConfig.toString());

				System.out.println("/////////////////////////////////////////////////////");
				component.actorRef.tell(componentConfig, self());
			}
			// create map to store each machine context.
			// the key is the actor path.
			// value is machine context
			// get the map of all the machines context if exist.
			Map<String, ActorContext> mapContext = ActorContextInfo.getRefActor(null).getRef(null);
			// if the mapContext is not null
			if (mapContext != null) {
				// check if the context already contains the machine context
				// add the machine context to the map if not contains this machine context
				if (!mapContext.containsKey(context().self().toString())) {
					mapContext.put(context().self().toString(), context());
					mapContext = ActorContextInfo.getRefActor(mapContext).getRef(mapContext);
				}

			} else {
				mapContext = new HashMap<String, ActorContext>();
				// add the new machine context to the map
				mapContext.put(context().self().toString(), context());
				mapContext = ActorContextInfo.getRefActor(mapContext).getRef(mapContext);
			}
		}).build());
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return supervisorStrategy;
	}

	private static class Component {

		public final String name;
		public final ActorRef actorRef;
		public final BiFunction<Config, ActorContext, ConfMsg> confFunction;

		private Component(String name, ActorRef actorRef, BiFunction<Config, ActorContext, ConfMsg> confFunction) {
			this.name = name;
			this.actorRef = actorRef;
			this.confFunction = confFunction;
		}
	}
}