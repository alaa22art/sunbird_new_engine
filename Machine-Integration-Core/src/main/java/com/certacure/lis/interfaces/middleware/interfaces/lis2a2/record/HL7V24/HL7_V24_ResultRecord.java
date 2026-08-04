package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V24_ResultRecord extends LIS2A2Record {

	public static HL7_V24_ResultRecord create(int sequenceNumber) {
		return (HL7_V24_ResultRecord) new HL7_V24_ResultRecord().setField(1, Type.OBX.name()).setField(2, String.valueOf(sequenceNumber));
	}

	private HL7_V24_ResultRecord() {
	}

	public HL7_V24_ResultRecord(DelimitedData<Field> data) {
		super(data);
	}

	public String getAnalysisCode() {
		String compValue = getComponentValue(3, 4 , "^");
		

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
		return getComponentValue(3, 11 ,"^");
	}
	
	public String getResultCode(int iField, int iComponent) {
		return getComponentValue(iField, iComponent ,"^");
	}
	
	public String getResultCode(int iFieldIndex) {
		return getFieldValue(iFieldIndex);
	}

	public String getAnalysisCode(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
	}
	
	public String getHL7AnalysisCode(int startIndex, int endIndex , String sub) {
		return getComponentValue(startIndex, endIndex , sub);
		
	}

	public String getAnalysisResultCode(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
	}

	public String getAnalysisName() {
		return getComponentValue(3, 2 , "^");
	}

	public String getAnalysisName(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
	}

	public String getResultValue() {
		return getFieldValue(3);
	}

	public String getResultValue(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
	}
	
	public String getHL7ResultValue(int startIndex, int endIndex , String sub) {
		return getComponentValue(startIndex, endIndex , sub);
	}

	public String getResultValueComponent() {
		return getComponentValue(4, 1);
	}

	public String getResultValueComponent(int startIndex, int endIndex) {
		return getComponentValue(startIndex, endIndex);
	}

	public String getUnit() {
		return getFieldValue(4);
	}
	
	public String getHL7Unit() {
		return getFieldValue(7);
	}

	public String getAbnormalFlag() {
		return getFieldValue(7);
	}
	
	public String getHL7AbnormalFlag() {
		return getFieldValue(9);
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
		return new HL7_V24_ResultRecord(data);
	}

	@Override
	public String toString() {
		return "OBX{" +
				"data=" + data +
				'}';
	}

	public String getReferanceRanges(int field, int component) {
		return getComponentValue(field, component);
	}

	public String getResultCodeAlinity() {
		return getComponentValue(3, 7);
	}
}