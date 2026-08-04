package com.certacure.lis.interfaces.middleware.flow_component.hl7_23_swelab_bm_500;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class HL723SwelabBM500Validator extends FlowComponent<HL723SwelabBM500ControllerConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
								.match(String.class, this::convertAndForward)
								.build();
	}

	private HL723SwelabBM500Validator() {

	}

	private void convertAndForward(String msgAsString) {
		log.info(msgAsString);

	}

}