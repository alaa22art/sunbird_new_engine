package com.certacure.lis.interfaces.middleware.flow_component.hl7_23_vidas;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class HL723VidasValidator extends FlowComponent<HL723VidasControllerConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
								.match(String.class, this::convertAndForward)
								.build();
	}

	private HL723VidasValidator() {

	}

	private void convertAndForward(String msgAsString) {
		log.info(msgAsString);

	}

}