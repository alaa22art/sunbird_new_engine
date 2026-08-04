package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.CompomentField;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class OrderRecord extends LIS2A2Record {

	public static OrderRecord create(int sequenceNumber) {
		return (OrderRecord) new OrderRecord().setField(1, Type.O.name()).setField(2, String.valueOf(sequenceNumber));
	}

	public OrderRecord() {
	}

	OrderRecord(DelimitedData<Field> data) {
		super(data);
	}

	public OrderRecord addAnalysis(String code) {
		return (OrderRecord) addRepeat(5, new CompomentField()
																.setComponent(1, code));
																//.setComponent(2, code));
																//.setComponent(3, type)
																//.setComponent(4, code));
	}
	
	
	public OrderRecord addAnalysis(String name, String code) {
		return (OrderRecord) addRepeat(5, new CompomentField()
																.setComponent(1, name)
																.setComponent(2, code));
																//.setComponent(3, type)
																//.setComponent(4, code));
	}
	
	public OrderRecord addAnalysis(String loinc, String name, String type, String code) {
		return (OrderRecord) addRepeat(5, new CompomentField()
																.setComponent(1, loinc)
																.setComponent(2, name)
																.setComponent(3, type)
																.setComponent(4, code));
	}

	public OrderRecord addAnalysisAstmE138102(String loinc, String name, String type, String code) {
		return (OrderRecord) addRepeat(5, new CompomentField("!")
																	.setComponent(1, loinc)
																	.setComponent(2, name)
																	.setComponent(3, type)
																	.setComponent(4, code));
	}

	public OrderRecord addAnalysisAstmE138194(String loinc, String name, String type, String code, String dilution) {
		return (OrderRecord) addRepeat(5, new CompomentField()
																.setComponent(1, loinc)
																.setComponent(2, name)
																.setComponent(3, type)
																.setComponent(4, code)
																.setComponent(5, dilution));
	}
	
	
	public OrderRecord addAnalysisAU(String code) {
		return (OrderRecord) addRepeat(33, new CompomentField()
																.setComponent(1, code)
																.setComponent(2, "0"));

	}

	public OrderRecord addCollectionDate(String collectionDate) {
		return (OrderRecord) addRepeat(8, collectionDate);
	}

	public OrderRecord addPriorityCode(String priorityCode) {
		return (OrderRecord) addRepeat(6, priorityCode);
	}

	public OrderRecord addReciveDatetime(String reciveDatetime) {
		return (OrderRecord) addRepeat(23, reciveDatetime);
	}
	
	
	public OrderRecord addRequestDate(String collectionDate) {
		return (OrderRecord) addRepeat(7, collectionDate);
	}
	
	@Override
	protected OrderRecord getNew(DelimitedData<Field> data) {
		return new OrderRecord(data);
	}

	public String getSpecimenDescriptor() {
		return getFieldValue(15);
	}

	public String getSpecimenId() {
		return getComponentValue(3, 1);
	}

	public String getSpecimenId(int index) {
		return getFieldValue(index);
	}

	public String getSpecimenId(int index, int field, String componentSeparator) {
		List<Field> idFields = getRepeats(index);
		return idFields.stream().map(f -> f.toComponentField(componentSeparator).getComponent(field).trim()).collect(toList()).get(0);
	}

	public String getSpecimenId02() {
		return getComponentValue(4, 1);
	}

	public String getSpecimenIds(int componentIndex) {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(componentIndex).trim()).collect(toList()).get(0);
	}

	public String getSpecimenIds(int componentIndex, int Index) {
		List<Field> idFields = getRepeats(Index);
		return idFields.stream().map(f -> f.toComponentField().getComponent(componentIndex).trim()).collect(toList()).get(0);
	}
	
	public String getSpecimenDetails(int componentIndex, int Index)
	{
		/*
		 *															.setComponent(1, sampleNumber)
																	.setComponent(2, queryTag)
																	.setComponent(3, queryControlID)
																	.setComponent(4, rackNumber)
																	.setComponent(5, positionNumber)); 
		 */
		List<Field> idFields = getRepeats(Index);
		return idFields.stream().map(f -> f.toComponentField().getComponent(componentIndex).trim()).collect(toList()).get(0);
	}
	
	
	

	public String getSpecimnPositionInfo() {
		 return getFieldValue(4);
	}

	public String getSysmexSpecimenId() {
		List<Field> idFields = getRepeats(4);
		return idFields.stream().map(f -> f.toComponentField().getComponent(3).trim()).collect(toList()).get(0);
	}

	public OrderRecord setActionCode(String actionCode) {
		return (OrderRecord) addRepeat(12, actionCode);
	}

	public OrderRecord setInstrumentSampleNoAU(String sampleNo) {
		return (OrderRecord) setComponent(3, 2, sampleNo);
	}

	public OrderRecord setInstrumentSpecimenIdAU(String specimenId) {
		return (OrderRecord) setComponent(3, 1, specimenId);
	}

	public OrderRecord setMessageControlId(String messageControlId) {
		return (OrderRecord) setField(3, messageControlId);
	}

	public OrderRecord setOfflineDilution(String specimenName) {
		return (OrderRecord) addRepeat(19, specimenName);
	}
	
	public OrderRecord setReportType(String reportType) {
		return (OrderRecord) addRepeat(26, reportType);
	}

	public OrderRecord setSpecimenDescriptor(String specimenName) {
		return (OrderRecord) addRepeat(16, specimenName);
	}
	
	

	public OrderRecord setSpecimenId(String specimenId) {
		return (OrderRecord) setComponent(3, 1, specimenId);
	}

	public OrderRecord setSpecimenDetails(String queryTag, String queryControlID, String rackNumber, String positionNumber , String barcodeMode) {
		return (OrderRecord) addRepeat(4, new CompomentField("^")
																	.setComponent(1, queryTag)
																	.setComponent(2, queryControlID)
																	.setComponent(3, rackNumber)
																	.setComponent(4, positionNumber)
																	.setComponent(5, barcodeMode));
	}

	public OrderRecord setSpecimnPositionInfo(String specimenPosition) {
		return (OrderRecord) setComponent(4, 1, specimenPosition);
	}

	public OrderRecord setSpecimnPositionInfoAU(String specimenPosition) {
		return (OrderRecord) addRepeat(32, specimenPosition);
	}

	@Override
	public String toString() {
		return "O{" +
				"data=" + data +
				'}';
	}

	public String getOrderDateTime() {
		return getFieldValue(22);
	}

	
}
