package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.CompomentField;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;


public class HL7_V24_OrderRecord extends LIS2A2Record {

	public static HL7_V24_OrderRecord create(int sequenceNumber) {
		return (HL7_V24_OrderRecord) new HL7_V24_OrderRecord().setField(1, Type.OBR.name()).setField(2, String.valueOf(sequenceNumber));
	}

	public HL7_V24_OrderRecord() {
	}

	public HL7_V24_OrderRecord(DelimitedData<Field> data) {
		super(data);
	}

	public HL7_V24_OrderRecord addAnalysis(String loinc, String name, String type, String code) {
		return (HL7_V24_OrderRecord) addRepeat(5, new CompomentField()
																.setComponent(1, loinc)
																.setComponent(2, name)
																.setComponent(3, type)
																.setComponent(4, code));
	}

	public HL7_V24_OrderRecord addAnalysisAstmE138194(String loinc, String name, String type, String code, String dilution) {
		return (HL7_V24_OrderRecord) addRepeat(5, new CompomentField()
																.setComponent(1, loinc)
																.setComponent(2, name)
																.setComponent(3, type)
																.setComponent(4, code)
																.setComponent(5, dilution));
	}

	public HL7_V24_OrderRecord addAnalysisAstmE138102(String loinc, String name, String type, String code) {
		return (HL7_V24_OrderRecord) addRepeat(5, new CompomentField("!")
																	.setComponent(1, loinc)
																	.setComponent(2, name)
																	.setComponent(3, type)
																	.setComponent(4, code));
	}

	public HL7_V24_OrderRecord addAnalysisAU(String code) {
		return (HL7_V24_OrderRecord) addRepeat(33, new CompomentField()
																.setComponent(1, code)
																.setComponent(2, "0"));

	}

	public HL7_V24_OrderRecord addPriorityCode(String priorityCode) {
		return (HL7_V24_OrderRecord) addRepeat(6, priorityCode);
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

	public HL7_V24_OrderRecord setMessageControlId(String messageControlId) {
		return (HL7_V24_OrderRecord) setField(3, messageControlId);
	}

	public HL7_V24_OrderRecord setSpecimenId(String specimenId) {
		return (HL7_V24_OrderRecord) setComponent(3, 1, specimenId);
	}

	public HL7_V24_OrderRecord setInstrumentSpecimenIdAU(String specimenId) {
		return (HL7_V24_OrderRecord) setComponent(3, 1, specimenId);
	}

	public HL7_V24_OrderRecord setInstrumentSampleNoAU(String sampleNo) {
		return (HL7_V24_OrderRecord) setComponent(3, 2, sampleNo);
	}

	public String getSysmexSpecimenId() {
		List<Field> idFields = getRepeats(4);
		return idFields.stream().map(f -> f.toComponentField().getComponent(3).trim()).collect(toList()).get(0);
	}

	public HL7_V24_OrderRecord setSpecimnPositionInfo(String specimenPosition) {
		return (HL7_V24_OrderRecord) setComponent(4, 1, specimenPosition);
	}

	public HL7_V24_OrderRecord setSpecimnPositionInfoAU(String specimenPosition) {
		return (HL7_V24_OrderRecord) addRepeat(32, specimenPosition);
	}

	public HL7_V24_OrderRecord addCollectionDate(String collectionDate) {
		return (HL7_V24_OrderRecord) addRepeat(8, collectionDate);
	}

	public HL7_V24_OrderRecord addRequestDate(String collectionDate) {
		return (HL7_V24_OrderRecord) addRepeat(7, collectionDate);
	}

	public HL7_V24_OrderRecord addReciveDatetime(String reciveDatetime) {
		return (HL7_V24_OrderRecord) addRepeat(23, reciveDatetime);
	}

	@Override
	protected HL7_V24_OrderRecord getNew(DelimitedData<Field> data) {
		return new HL7_V24_OrderRecord(data);
	}

	@Override
	public String toString() {
		return "OBR{" +
				"data=" + data +
				'}';
	}

	public HL7_V24_OrderRecord setActionCode(String actionCode) {
		return (HL7_V24_OrderRecord) addRepeat(12, actionCode);
	}

	public HL7_V24_OrderRecord setSpecimenDescriptor(String specimenName) {
		return (HL7_V24_OrderRecord) addRepeat(16, specimenName);
	}

	public HL7_V24_OrderRecord setOfflineDilution(String specimenName) {
		return (HL7_V24_OrderRecord) addRepeat(19, specimenName);
	}

	public HL7_V24_OrderRecord setReportType(String reportType) {
		return (HL7_V24_OrderRecord) addRepeat(26, reportType);
	}

	public String getSpecimenId02() {
		return getComponentValue(4, 1);
	}
}
