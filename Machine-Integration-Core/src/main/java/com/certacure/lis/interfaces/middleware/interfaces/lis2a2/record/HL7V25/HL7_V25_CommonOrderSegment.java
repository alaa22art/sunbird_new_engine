package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V25_CommonOrderSegment extends LIS2A2Record {

	public static HL7_V25_CommonOrderSegment create(int sequenceNumber) {
		return (HL7_V25_CommonOrderSegment) new HL7_V25_CommonOrderSegment()	.setField(1, Type.ORC.name())
													.setField(2, String.valueOf(sequenceNumber));
	}

	public HL7_V25_CommonOrderSegment() {
	}

	public HL7_V25_CommonOrderSegment(DelimitedData<Field> data) {
		super(data);
	}

	@Override
	protected HL7_V25_CommonOrderSegment getNew(DelimitedData<Field> data) {
		return new HL7_V25_CommonOrderSegment(data);
	}

	@Override
	public String toString() {
		return "ORC{" +
				"data=" + data +
				'}';
	}
}