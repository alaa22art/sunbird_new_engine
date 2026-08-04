package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V25_PatientDemographicResponseRecord extends LIS2A2Record {

	public static HL7_V25_PatientDemographicResponseRecord create(int sequenceNumber) {
		return (HL7_V25_PatientDemographicResponseRecord) new HL7_V25_PatientDemographicResponseRecord()	.setField(1, Type.RCP.name())
													.setField(2, String.valueOf(sequenceNumber));
	}

	public HL7_V25_PatientDemographicResponseRecord() {
	}

	public HL7_V25_PatientDemographicResponseRecord(DelimitedData<Field> data) {
		super(data);
	}

	@Override
	protected HL7_V25_PatientDemographicResponseRecord getNew(DelimitedData<Field> data) {
		return new HL7_V25_PatientDemographicResponseRecord(data);
	}

	@Override
	public String toString() {
		return "RCP{" +
				"data=" + data +
				'}';
	}
}