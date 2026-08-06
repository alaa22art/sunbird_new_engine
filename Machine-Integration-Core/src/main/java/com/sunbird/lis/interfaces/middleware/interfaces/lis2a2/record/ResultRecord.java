package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class ResultRecord extends LIS2A2Record {

	public static ResultRecord create(int sequenceNumber) {
		return (ResultRecord) new ResultRecord().setField(1, Type.R.name()).setField(2, String.valueOf(sequenceNumber));
	}

	private ResultRecord() {
	}

	ResultRecord(DelimitedData<Field> data) {
		super(data);
	}

	public String getAnalysisCode() {
		String compValue = getComponentValue(3, 4);

		return compValue;
	}

	public String getAnalysisCodeRuby() {
		String compValue = getComponentValue(3, 7);

		return compValue;
	}

	public String getAnalysisCodeRepeted() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(a -> a.toComponentField().getComponent(4).trim()).collect(toList()).get(0);
	}

	public String getAnalysisResultCode() {
		return getComponentValue(3, 5);
	}

	public String getResultCode() {
		return getComponentValue(3, 11);
	}

	public String getAnalysisCode(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
	}

	public String getAnalysisResultCode(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
	}

	public String getAnalysisName() {
		return getComponentValue(3, 2);
	}

	public String getAnalysisName(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
	}

	public String getResultValue() {
		return getFieldValue(4);
	}

	public String getResultValue(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
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

	public String getAbnormalFlag() {
		return getFieldValue(7);
	}
	
	public String getAbnormalFlag(int field, int component) {
		return getComponentValue(field, component);
	}

	public String getStatus() {
		return getFieldValue(9);
	}

	public String getReferanceRanges() {
		return getFieldValue(6);
	}

	public String getReferanceRangesASTM02() {
		return getFieldValue(7);

	}

	@Override
	protected LIS2A2Record getNew(DelimitedData<Field> data) {
		return new ResultRecord(data);
	}

	@Override
	public String toString() {
		return "R{" +
				"data=" + data +
				'}';
	}

	public String getReferanceRanges(int field, int component) {
		return getComponentValue(field, component);
	}
}