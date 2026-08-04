package com.certacure.lis.interfaces.middleware.flow_component.hl7_231_dymind_dh_76;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class HL7231DymindDH76Validator extends FlowComponent<HL7231DymindDH76ControllerConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
								.match(String.class, this::convertAndForward)
								.build();
	}

	private HL7231DymindDH76Validator() {

	}

	private void convertAndForward(String msgAsString) {
		log.info(msgAsString);

	}

}