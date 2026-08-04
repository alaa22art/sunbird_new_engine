package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25;

import java.util.List;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

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

	public String getSpecimenId(int index) {
		return this.getFieldValue(index);
	}

	public String getSpecimenId() {
		return this.getFieldValue(3);
	}

	public List<String> getAnalysisCodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSpecimenPositionInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQueryTag() {
		// TODO Auto-generated method stub
		return this.getFieldValue(2);
	}
	
	public String getContainerRackID() {
		// TODO Auto-generated method stub
		return this.getFieldValue(4);
	}
	
	public String getContainerRackPositionID() {
		// TODO Auto-generated method stub
		return this.getFieldValue(5);
	}

	public Object getBarcodeMode(int iField, int iComp) {
		return this.getComponentValue(iField, iComp , "^");
	}
	
	
	
	
	
}