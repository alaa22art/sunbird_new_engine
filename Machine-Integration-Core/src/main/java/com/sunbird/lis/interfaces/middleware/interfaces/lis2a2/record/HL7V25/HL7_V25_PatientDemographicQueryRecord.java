package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V25_PatientDemographicQueryRecord extends LIS2A2Record {

	public static HL7_V25_PatientDemographicQueryRecord create(int sequenceNumber) {
		return (HL7_V25_PatientDemographicQueryRecord) new HL7_V25_PatientDemographicQueryRecord()	.setField(1, Type.QPD.name())
													.setField(2, String.valueOf(sequenceNumber));
	}

	public HL7_V25_PatientDemographicQueryRecord() {
	}

	public HL7_V25_PatientDemographicQueryRecord(DelimitedData<Field> data) {
		super(data);
	}

	@Override
	protected HL7_V25_PatientDemographicQueryRecord getNew(DelimitedData<Field> data) {
		return new HL7_V25_PatientDemographicQueryRecord(data);
	}

	@Override
	public String toString() {
		return "QPD{" +
				"data=" + data +
				'}';
	}
}