package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V25_SpecimenSegment extends LIS2A2Record {

	public static HL7_V25_SpecimenSegment create(int sequenceNumber) {
		return (HL7_V25_SpecimenSegment) new HL7_V25_SpecimenSegment()	.setField(1, Type.SPM.name())
													.setField(2, String.valueOf(sequenceNumber));
	}

	public HL7_V25_SpecimenSegment() {
	}

	public HL7_V25_SpecimenSegment(DelimitedData<Field> data) {
		super(data);
	}

	@Override
	protected HL7_V25_SpecimenSegment getNew(DelimitedData<Field> data) {
		return new HL7_V25_SpecimenSegment(data);
	}

	@Override
	public String toString() {
		return "MSA{" +
				"data=" + data +
				'}';
	}
}