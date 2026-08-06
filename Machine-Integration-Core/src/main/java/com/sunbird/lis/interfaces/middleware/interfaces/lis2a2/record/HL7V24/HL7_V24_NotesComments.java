package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record.Type;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V24_NotesComments extends LIS2A2Record {

	public static HL7_V24_NotesComments create(int sequenceNumber) {
		return (HL7_V24_NotesComments) new HL7_V24_NotesComments().setField(1, Type.NET.name()).setField(2,
				String.valueOf(sequenceNumber));
	}

	public HL7_V24_NotesComments() {
	}

	public HL7_V24_NotesComments(DelimitedData<Field> fields) {
		super(fields);
	}

	@Override
	public String toString() {
		return "NET{" + "data=" + data + '}';
	}

	@Override
	protected LIS2A2Record getNew(DelimitedData<Field> data) {
		// TODO Auto-generated method stub
		return null;
	}

}
