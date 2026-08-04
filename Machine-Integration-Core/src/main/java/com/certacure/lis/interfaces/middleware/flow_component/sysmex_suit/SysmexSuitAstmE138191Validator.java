package com.certacure.lis.interfaces.middleware.flow_component.sysmex_suit;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class SysmexSuitAstmE138191Validator extends FlowComponent<SysmexSuitAstmE138191ControllerConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
								.match(String.class, this::convertAndForward)
								.build();
	}

	private SysmexSuitAstmE138191Validator() {

	}

	private void convertAndForward(String msgAsString) {
		log.info(msgAsString);

	}

}