package com.certacure.lis.interfaces.middleware.flow_component.astme_keep_alive;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class AstmKeepAliveValidator extends FlowComponent<AstmKeepAliveControllerConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
				.match(String.class, this::convertAndForward)
				.build();
	}
	
	private AstmKeepAliveValidator() {
	
	}
	
	private void convertAndForward(String msgAsString) {
		System.out.print("aaa");
	
	}



}