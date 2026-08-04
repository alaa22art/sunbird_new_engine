package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V25_MessageAcknowledgmentSegment extends LIS2A2Record {

	public static HL7_V25_MessageAcknowledgmentSegment create(int sequenceNumber) {
		return (HL7_V25_MessageAcknowledgmentSegment) new HL7_V25_MessageAcknowledgmentSegment()	.setField(1, Type.MSA.name())
													.setField(2, String.valueOf(sequenceNumber));
	}

	public HL7_V25_MessageAcknowledgmentSegment() {
	}

	public HL7_V25_MessageAcknowledgmentSegment(DelimitedData<Field> data) {
		super(data);
	}

	@Override
	protected HL7_V25_MessageAcknowledgmentSegment getNew(DelimitedData<Field> data) {
		return new HL7_V25_MessageAcknowledgmentSegment(data);
	}

	@Override
	public String toString() {
		return "MSA{" +
				"data=" + data +
				'}';
	}
}