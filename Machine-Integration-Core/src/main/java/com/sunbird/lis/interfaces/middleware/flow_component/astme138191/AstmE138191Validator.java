package com.sunbird.lis.interfaces.middleware.flow_component.astme138191;

import com.sunbird.lis.interfaces.middleware.core.FlowComponent;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class AstmE138191Validator extends FlowComponent<AstmE138191ControllerConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
				.match(String.class, this::convertAndForward)
				.build();
	}
	
	private AstmE138191Validator() {
	
	}
	
	private void convertAndForward(String msgAsString) {
		System.out.print("aaa");
	
	}



}