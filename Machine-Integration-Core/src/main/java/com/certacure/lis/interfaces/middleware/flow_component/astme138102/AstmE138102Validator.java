package com.certacure.lis.interfaces.middleware.flow_component.astme138102;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class AstmE138102Validator extends FlowComponent<AstmE138102ControllerConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
								.match(String.class, this::convertAndForward)
								.build();
	}

	private AstmE138102Validator() {

	}

	private void convertAndForward(String msgAsString) {
		System.out.print("aaa");

	}

}