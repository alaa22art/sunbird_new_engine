package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field;

import java.util.ArrayList;
import java.util.List;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;

public class TildeField extends Field {

	private final DelimitedData<Field> data;

	public TildeField() {
		this(new DelimitedData<>("~"));
	}

	private TildeField(DelimitedData<Field> data) {
		super(data.asString());
		this.data = data;
	}

	public TildeField addRepeat(Field field) {
		return new TildeField(data.setField(data.getSize(), field));
	}

	public List<Field> getRepeats() {
		List<Field> result = new ArrayList<>();
		for (int i = 0; i < data.getSize(); i++)
			result.add(data.get(i));
		return result;
	}

	@Override
	public String toString() {
		return "TildeField{" +
				"data=" + data +
				'}';
	}
}
