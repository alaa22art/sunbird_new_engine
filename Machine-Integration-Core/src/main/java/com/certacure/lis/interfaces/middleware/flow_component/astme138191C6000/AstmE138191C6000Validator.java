package com.certacure.lis.interfaces.middleware.flow_component.astme138191C6000;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class AstmE138191C6000Validator extends FlowComponent<AstmE138191C6000ControllerConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
				.match(String.class, this::convertAndForward)
				.build();
	}
	
	private AstmE138191C6000Validator() {
	
	}
	
	private void convertAndForward(String msgAsString) {
		System.out.print("aaa");
	
	}



}