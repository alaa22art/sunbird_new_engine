package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record.Type;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V25_Comments extends LIS2A2Record {

	public static HL7_V25_Comments create(int sequenceNumber) {
		return (HL7_V25_Comments) new HL7_V25_Comments().setField(1, Type.NET.name()).setField(2,
				String.valueOf(sequenceNumber));
	}

	public HL7_V25_Comments() {
	}

	public HL7_V25_Comments(DelimitedData<Field> fields) {
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
