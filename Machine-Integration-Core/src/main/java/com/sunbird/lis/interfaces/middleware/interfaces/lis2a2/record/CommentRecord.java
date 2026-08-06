package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class CommentRecord extends LIS2A2Record {

	public static CommentRecord create(int sequenceNumber) {
		return (CommentRecord) new CommentRecord()	.setField(1, Type.C.name())
													.setField(2, String.valueOf(sequenceNumber));
	}

	public CommentRecord() {
	}

	CommentRecord(DelimitedData<Field> data) {
		super(data);
	}

	@Override
	protected CommentRecord getNew(DelimitedData<Field> data) {
		return new CommentRecord(data);
	}

	@Override
	public String toString() {
		return "C{" +
				"data=" + data +
				'}';
	}
}