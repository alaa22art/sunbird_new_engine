package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.CompomentField;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;


public class OrderASTMRecord extends LIS2A2Record {

	public static OrderASTMRecord create(int sequenceNumber) {
		return (OrderASTMRecord) new OrderASTMRecord().setField(1, Type.O.name()).setField(2, String.valueOf(sequenceNumber));
	}

	public OrderASTMRecord() {
	}

	public OrderASTMRecord(DelimitedData<Field> data) {
		super(data);
	}

	public OrderASTMRecord addAnalysis(String loinc, String name, String type, String code) {
		return (OrderASTMRecord) addRepeat(5, new CompomentField()
																.setComponent(1, loinc)
																.setComponent(2, name)
																.setComponent(3, type)
																.setComponent(4, code));
	}

	public OrderASTMRecord addAnalysisAstmE138194(String loinc, String name, String type, String code, String dilution) {
		return (OrderASTMRecord) addRepeat(5, new CompomentField()
																.setComponent(1, loinc)
																.setComponent(2, name)
																.setComponent(3, type)
																.setComponent(4, code)
																.setComponent(5, dilution));
	}

	public OrderASTMRecord addAnalysisAstmE138102(String loinc, String name, String type, String code) {
		return (OrderASTMRecord) addRepeat(5, new CompomentField("!")
																	.setComponent(1, loinc)
																	.setComponent(2, name)
																	.setComponent(3, type)
																	.setComponent(4, code));
	}

	public OrderASTMRecord addAnalysisAU(String code) {
		return (OrderASTMRecord) addRepeat(33, new CompomentField()
																.setComponent(1, code)
																.setComponent(2, "0"));

	}

	public OrderASTMRecord addPriorityCode(String priorityCode) {
		return (OrderASTMRecord) addRepeat(6, priorityCode);
	}

	public String getSpecimenId() {
		return getComponentValue(3, 1 , "^");
	}

	public String getSpecimenId(int index) {
		return getFieldValue(index);
	}

	public String getSpecimenId(int index, int field, String componentSeparator) {
		List<Field> idFields = getRepeats(index);
		return idFields.stream().map(f -> f.toComponentField(componentSeparator).getComponent(field).trim()).collect(toList()).get(0);
	}

	public String getSpecimenIds(int componentIndex) {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(componentIndex).trim()).collect(toList()).get(0);
	}

	public String getSpecimenIds(int componentIndex, int Index) {
		List<Field> idFields = getRepeats(Index);
		return idFields.stream().map(f -> f.toComponentField().getComponent(componentIndex).trim()).collect(toList()).get(0);
	}

	public OrderASTMRecord setMessageControlId(String messageControlId) {
		return (OrderASTMRecord) setField(3, messageControlId);
	}

	public OrderASTMRecord setSpecimenId(String specimenId) {
		return (OrderASTMRecord) setComponent(3, 1, specimenId);
	}

	public OrderASTMRecord setInstrumentSpecimenIdAU(String specimenId) {
		return (OrderASTMRecord) setComponent(3, 1, specimenId);
	}

	public OrderASTMRecord setInstrumentSampleNoAU(String sampleNo) {
		return (OrderASTMRecord) setComponent(3, 2, sampleNo);
	}

	public String getSysmexSpecimenId() {
		List<Field> idFields = getRepeats(4);
		return idFields.stream().map(f -> f.toComponentField().getComponent(3).trim()).collect(toList()).get(0);
	}

	public OrderASTMRecord setSpecimnPositionInfo(String specimenPosition) {
		return (OrderASTMRecord) setComponent(4, 1, specimenPosition);
	}

	public OrderASTMRecord setSpecimnPositionInfoAU(String specimenPosition) {
		return (OrderASTMRecord) addRepeat(32, specimenPosition);
	}

	public OrderASTMRecord addCollectionDate(String collectionDate) {
		return (OrderASTMRecord) addRepeat(8, collectionDate);
	}

	public OrderASTMRecord addRequestDate(String collectionDate) {
		return (OrderASTMRecord) addRepeat(7, collectionDate);
	}

	public OrderASTMRecord addReciveDatetime(String reciveDatetime) {
		return (OrderASTMRecord) addRepeat(23, reciveDatetime);
	}

	@Override
	protected OrderASTMRecord getNew(DelimitedData<Field> data) {
		return new OrderASTMRecord(data);
	}

	@Override
	public String toString() {
		return "O{" +
				"data=" + data +
				'}';
	}

	public OrderASTMRecord setActionCode(String actionCode) {
		return (OrderASTMRecord) addRepeat(12, actionCode);
	}

	public OrderASTMRecord setSpecimenDescriptor(String specimenName) {
		return (OrderASTMRecord) addRepeat(16, specimenName);
	}

	public OrderASTMRecord setOfflineDilution(String specimenName) {
		return (OrderASTMRecord) addRepeat(19, specimenName);
	}

	public OrderASTMRecord setReportType(String reportType) {
		return (OrderASTMRecord) addRepeat(26, reportType);
	}

	public String getSpecimenId02() {
		return getComponentValue(4, 1);
	}
}
