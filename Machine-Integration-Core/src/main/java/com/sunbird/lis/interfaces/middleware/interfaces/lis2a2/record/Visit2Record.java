package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class Visit2Record extends LIS2A2Record {

	public static Visit2Record create(int sequenceNumber) {
		return (Visit2Record) 
				new Visit2Record().setField(1, Type.PV2.name()).setField(2, String.valueOf(sequenceNumber));
	}

	private Visit2Record() {
	}

	Visit2Record(DelimitedData<Field> fields) {
		super(fields);
	}

	public Visit2Record setPatientClass(String strPatientClass) {
		return (Visit2Record) setField(2, strPatientClass);
	}
	
	public Visit2Record setAssignedPatientLocation(String strPointOfCare, String strRoom , String strBed ) {
		return (Visit2Record) setComponent(3,1 , strPointOfCare)
				.setComponent(3, 2, strRoom)
				.setComponent(3, 3, strBed);
		
	}
	
	public String getPatientClass() {
		return getFieldValue(2);
	}

	public String getRecordedDateTime() {
		return getComponentValue(3, 1);
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

}
