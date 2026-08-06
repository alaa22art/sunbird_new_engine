package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class OBXRecord extends LIS2A2Record {

	public static OBXRecord create(int sequenceNumber) {
		return (OBXRecord) new OBXRecord().setField(1, Type.OBX.name()).setField(2, String.valueOf(sequenceNumber));
	}

	private OBXRecord() {
	}

	OBXRecord(DelimitedData<Field> data) {
		super(data);
	}

	public String getAnalysisCode() {
		return getComponentValue(3, 4);
	}

	public String getAnalysisCodeRepeted() {
		List<Field> idFields = getRepeats(3);
		//return idFields.stream().map(f -> f.toComponentField().getComponent(4).trim()).collect(toList()).get(0);
		return idFields.stream().map(a -> a.toComponentField().getComponent(4).trim()).collect(toList()).get(0);
	}

	public String getAnalysisResultCode() {
		return getComponentValue(3, 5);
	}

	public String getAnalysisCode(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
	}

	public String getAnalysisCode(int Index) {
		return getFieldValue(Index);
	}

	public String getAnalysisResultCode(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
	}

	public String getAnalysisName() {
		return getComponentValue(3, 2);
	}

	public String getResultValue() {
		return getFieldValue(4);
	}

	public String getResultValueComponent() {
		return getComponentValue(4, 1);
	}

	public String getResultValueComponent(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
	}

	public String getUnit() {
		return getFieldValue(5);
	}

	public String getUnit(int index) {
		return getFieldValue(index);
	}

	@Override
	protected LIS2A2Record getNew(DelimitedData<Field> data) {
		return new OBXRecord(data);
	}

	@Override
	public String toString() {
		return "R{" +
				"data=" + data +
				'}';
	}
}