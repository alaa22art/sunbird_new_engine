package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class TerminationRecord extends LIS2A2Record {

	public static TerminationRecord create(int sequenceNumber) {
		return (TerminationRecord) new TerminationRecord().setField(1, Type.L.name()).setField(2, String.valueOf(sequenceNumber));
	}

	public TerminationRecord() {
	}

	TerminationRecord(DelimitedData<Field> data) {
		super(data);
	}

	@Override
	protected LIS2A2Record getNew(DelimitedData<Field> data) {
		return new TerminationRecord(data);
	}

	@Override
	public String toString() {
		return "L{" +
				"data=" + data +
				'}';
	}

	public LIS2A2Record setTerminationCode(String terminationCode) {
		return addRepeat(3, terminationCode);
	}
}
