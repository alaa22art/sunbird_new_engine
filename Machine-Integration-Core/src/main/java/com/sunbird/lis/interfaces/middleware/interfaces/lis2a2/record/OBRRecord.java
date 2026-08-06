package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class OBRRecord extends LIS2A2Record {

	public static OBRRecord create(int sequenceNumber) {
		return (OBRRecord) new OBRRecord().setField(1, Type.OBR.name()).setField(2, String.valueOf(sequenceNumber));
	}

	private OBRRecord() {
	}

	OBRRecord(DelimitedData<Field> data) {
		super(data);
	}

	public OBRRecord addAnalysis(String loinc, String name, String type, String code) {
		return (OBRRecord) addRepeatForSysmexSuit(5, code);
	}

	public OBRRecord addPriorityCode(String priorityCode) {
		return (OBRRecord) addRepeat(6, priorityCode);
	}

	public String getSpecimenId() {
		return getComponentValue(3, 1);
	}

	public String getSpecimenId(int index) {
		return getFieldValue(index);
	}

	public String getSpecimenIds(int componentIndex) {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(componentIndex).trim()).collect(toList()).get(0);
	}

	public OBRRecord setSpecimenId(String specimenId) {
		return (OBRRecord) setComponent(3, 1, specimenId);
	}

	public String getSysmexSpecimenId() {
		List<Field> idFields = getRepeats(4);
		return idFields.stream().map(f -> f.toComponentField().getComponent(3).trim()).collect(toList()).get(0);
	}

	public OBRRecord setSpecimnPositionInfo(String specimenPosition) {
		return (OBRRecord) setComponent(4, 1, specimenPosition);
	}

	public OBRRecord addCollectionDate(String collectionDate) {
		return (OBRRecord) addRepeat(8, collectionDate);
	}

	public OBRRecord addRequestDate(String collectionDate) {
		return (OBRRecord) addRepeat(15, collectionDate);
	}

	public OBRRecord addReciveDatetime(String reciveDatetime) {
		return (OBRRecord) addRepeat(23, reciveDatetime);
	}

	@Override
	protected OBRRecord getNew(DelimitedData<Field> data) {
		return new OBRRecord(data);
	}

	@Override
	public String toString() {
		return "O{" +
				"data=" + data +
				'}';
	}

	public OBRRecord setActionCode(String actionCode) {
		return (OBRRecord) addRepeat(12, actionCode);
	}

	public OBRRecord setSpecimenDescriptor(String specimenName) {
		return (OBRRecord) addRepeat(16, specimenName);
	}

	public OBRRecord setOfflineDilution(String specimenName) {
		return (OBRRecord) addRepeat(19, specimenName);
	}

	public OBRRecord setReportType(String reportType) {
		return (OBRRecord) addRepeat(26, reportType);
	}
	 public void setDiagnosticServSectID(String str) {
	        this.setField(24, str);
	    }
	 
	 public String getDiagnosticServSectID() {
	        return this.getFieldValue(24);
	    }
}
