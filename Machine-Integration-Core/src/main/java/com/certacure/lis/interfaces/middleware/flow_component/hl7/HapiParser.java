package com.certacure.lis.interfaces.middleware.flow_component.hl7;

import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.OUL_R22;
import ca.uhn.hl7v2.model.v251.*;

public class HapiParser {

	// instantiate a PipeParser, which handles the "traditional or default encoding"
	private PipeParser PipeParser;
	private Message hl7Message;

	public HapiParser() {
		PipeParser = new PipeParser();
	}

	public Message parseMessage(String strHl7message) {

		try {
			// parse the string format message into a Java message object
			Message hl7Message = PipeParser.parse(strHl7message);

			if (hl7Message instanceof OUL_R22)
			{
				OUL_R22 unsolicitedSpecimenOrientedObservationMessage = (OUL_R22) hl7Message;
			}

		} catch (Exception ex) {

		}

		return hl7Message;
	}

}
