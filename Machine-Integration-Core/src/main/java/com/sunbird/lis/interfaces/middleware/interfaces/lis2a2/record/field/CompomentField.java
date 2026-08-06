package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;

public class CompomentField extends Field {

	private final DelimitedData<Field> data;

	public CompomentField() {
		this(new DelimitedData<>("^"));
	}
	
	public CompomentField(String delimited ) {
		this(new DelimitedData<>(delimited));
	}
	
	


	private CompomentField(DelimitedData<Field> data) {
		super(data.asString());
		this.data = data;
	}

	public CompomentField setComponent(int index, String value) {
		return setComponent(index, new PrimitiveField(value));
	}

	public CompomentField setComponent(int index, Field value) {
		return new CompomentField(data.setField(index - 1, value));
	}

	public String getComponent(int index) {
		if (data.getSize() >= index) {
			return data.get(index - 1).toPrimitiveField().asString();
		} else {
			return "";
		}
	}

	@Override
	public String toString() {
		return "CompomentField{" +
				"data=" + data +
				'}';
	}
}