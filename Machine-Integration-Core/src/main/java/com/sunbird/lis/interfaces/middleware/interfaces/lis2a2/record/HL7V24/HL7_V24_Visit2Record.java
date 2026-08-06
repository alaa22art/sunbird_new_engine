package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record.Type;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V24_Visit2Record extends LIS2A2Record {

	public static HL7_V24_Visit2Record create(int sequenceNumber) {
		return (HL7_V24_Visit2Record) 
				new HL7_V24_Visit2Record().setField(1, Type.PV2.name()).setField(2, String.valueOf(sequenceNumber));
	}

	private HL7_V24_Visit2Record() {
	}

	public HL7_V24_Visit2Record(DelimitedData<Field> fields) {
		super(fields);
	}

	public HL7_V24_Visit2Record setPatientClass(String strPatientClass) {
		return (HL7_V24_Visit2Record) setField(2, strPatientClass);
	}
	
	public HL7_V24_Visit2Record setAssignedPatientLocation(String strPointOfCare, String strRoom , String strBed ) {
		return (HL7_V24_Visit2Record) setComponent(3,1 , strPointOfCare)
				.setComponent(3, 2, strRoom)
				.setComponent(3, 3, strBed);
		
	}
	public HL7_V24_Visit2Record setExpectedAdmitDateTime(String ExpectedAdmitDateTime) {
		return (HL7_V24_Visit2Record) setField(8, ExpectedAdmitDateTime);
	}
	
	public String getPatientClass() {
		return getFieldValue(2);
	}

	public String getRecordedDateTime() {
		return getComponentValue(3, 1);
	}
	
	public String getExpectedAdmitDateTime() {
		return getFieldValue(8);
	}
	
	
	

	@Override
	public String toString() {
		return "PV1{" +
				"data=" + data +
				'}';
	}

	@Override
	protected LIS2A2Record getNew(DelimitedData<Field> data) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getActualLengthofInpatientStay() {

		return getFieldValue(11);

	}

}
