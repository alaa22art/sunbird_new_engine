package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class TerminationASTMRecord extends LIS2A2Record {

	public static TerminationASTMRecord create(int sequenceNumber) {
		return (TerminationASTMRecord) new TerminationASTMRecord().setField(1, Type.L.name()).setField(2, String.valueOf(sequenceNumber));
	}

	public TerminationASTMRecord() {
	}

	public TerminationASTMRecord(DelimitedData<Field> data) {
		super(data);
	}

	@Override
	protected LIS2A2Record getNew(DelimitedData<Field> data) {
		return new TerminationASTMRecord(data);
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
