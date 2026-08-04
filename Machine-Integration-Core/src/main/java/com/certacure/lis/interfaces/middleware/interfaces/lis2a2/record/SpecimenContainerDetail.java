package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record;


import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class SpecimenContainerDetail extends LIS2A2Record {

	public static SpecimenContainerDetail create(int sequenceNumber) {
		return (SpecimenContainerDetail) new SpecimenContainerDetail()
				.setField(1, Type.SAC.name());
	}

	private SpecimenContainerDetail() {
	}

	SpecimenContainerDetail(DelimitedData<Field> fields) {
		super(fields);
	}
	

	@Override
	protected SpecimenContainerDetail getNew(DelimitedData<Field> fields) {
		return new SpecimenContainerDetail(fields);
	}

	@Override
	public String toString() {
		return "SAC{" +
				"data=" + data +
				'}';
	}

}
