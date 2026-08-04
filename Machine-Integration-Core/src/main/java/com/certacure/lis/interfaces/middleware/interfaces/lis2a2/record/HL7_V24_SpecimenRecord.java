package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record;


import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V24_SpecimenRecord extends LIS2A2Record {

	public static HL7_V24_SpecimenRecord create(int sequenceNumber) {
		return (HL7_V24_SpecimenRecord) new HL7_V24_SpecimenRecord()
				.setField(1, Type.SPM.name());
	}

	private HL7_V24_SpecimenRecord() {
	}

	HL7_V24_SpecimenRecord(DelimitedData<Field> fields) {
		super(fields);
	}
	

	@Override
	protected HL7_V24_SpecimenRecord getNew(DelimitedData<Field> fields) {
		return new HL7_V24_SpecimenRecord(fields);
	}

	@Override
	public String toString() {
		return "SPM{" +
				"data=" + data +
				'}';
	}

	public String getSpecimenId() {
		
			return getComponentValue(3, 1,"^");
	}
	
	public String getSpecimenId(int fieldComp, int subComp) {
		
		return getComponentValue(fieldComp, subComp,"&");
}
	
		

}
