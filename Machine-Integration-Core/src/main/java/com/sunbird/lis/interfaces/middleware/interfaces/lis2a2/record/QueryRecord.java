package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class QueryRecord extends LIS2A2Record {

	public static QueryRecord create(int sequenceNumber) {
		return (QueryRecord) new QueryRecord().setField(1, Type.Q.name()).setField(2, String.valueOf(sequenceNumber));
	}

	private QueryRecord() {
	}

	QueryRecord(DelimitedData<Field> data) {
		super(data);
	}

	public String getSpecimenIds(int componentIndex) {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(componentIndex).trim()).collect(toList()).get(0);
	}

	public String getSpecimenIds(int componentIndex, int index) {
		List<Field> idFields = getRepeats(index);
		return idFields.stream().map(f -> f.toComponentField().getComponent(componentIndex).trim()).collect(toList()).get(0);
	}

	public String getSpecimenIdElecsys(int componentIndex) {
		List<Field> idFields = getRepeats(2);
		return idFields.stream().map(f -> f.toComponentField().getComponent(componentIndex).trim()).collect(toList()).get(0);
	}

	public List<String> getAnalysisCodes() {
		List<Field> analysisFields = getRepeats(5);

		return analysisFields.stream().map(a -> a.toComponentField().getComponent(1)).collect(toList());
	}

	public List<String> getAnalysisCodesRuby() {
		List<Field> analysisFields = getRepeats(3);

		return analysisFields.stream().map(a -> a.toComponentField().getComponent(1)).collect(toList());
	}

	public List<String> getAnalysisCodes(String delimiter) {
		List<Field> analysisFields = getRepeats(5);
		return analysisFields.stream().map(a -> a.toComponentField(delimiter).getComponent(1)).collect(toList());
	}

	public String getSpecimenPositionInfo() {
		return getSequenceNo() + "^" + getCarrierNo() + "^" + getPositionNo() + "^^" + getSampleType() + "^" + getContainerType();
	}

	public String getSpecimenPositionInfoAU() {
		return getMesureTypeAU() + "^" + getSampleKindAU() + "^" + getSampleNoAU() + "^^^" + getSampleIdAU() + "^" + getRackNoAU() + "^"
				+ getCupPositionAU() + "^" + getSampleType();
	}

	public String getElecsysSpecimenPositionInfo() {
		return getSequenceNoElecsys() + "^" + getRackNoElecsys() + "^" + getPositionNoElecsys() + "^^" + getCarrierNoElecsys() + "^"
				+ getContainerTypeElecsys();
	}

	public String getElecsysSpecimenPositionInfoASTM02() {
		return getSequenceNoElecsys() + "!" + getRackNoElecsys() + "!" + getPositionNoElecsys() + "!!" + getCarrierNoElecsys() + "!"
				+ getContainerTypeElecsys();
	}

	public String getSysmexSpecimenPositionInfo() {
		return getFieldValue(3);
	}

	public String getSysmexSpecimenId() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(3).trim()).collect(toList()).get(0);
	}

	public String getSpecimenId(int index) {
		return getFieldValue(index);
	}

	public String getSpecimenId(int index, int field, String componentSeparator) {
		List<Field> idFields = getRepeats(index);
		return idFields.stream().map(f -> f.toComponentField(componentSeparator).getComponent(field).trim()).collect(toList()).get(0);
	}

	public String getSequenceNo() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(4).trim()).collect(toList()).get(0);
	}

	public String getRackNoElecsys() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(4).trim()).collect(toList()).get(0);
	}

	public String getSequenceNoElecsys() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(3).trim()).collect(toList()).get(0);
	}

	public String getCarrierNo() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(5).trim()).collect(toList()).get(0);
	}

	public String getCarrierNoElecsys() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(7).trim()).collect(toList()).get(0);
	}

	public String getPositionNo() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(6).trim()).collect(toList()).get(0);
	}

	public String getPositionNoElecsys() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(5).trim()).collect(toList()).get(0);
	}

	public String getSampleType() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(8).trim()).collect(toList()).get(0);
	}

	public String getContainerType() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(9).trim()).collect(toList()).get(0);
	}

	public String getContainerTypeElecsys() {
		List<Field> idFields = getRepeats(3);
		return idFields.stream().map(f -> f.toComponentField().getComponent(8).trim()).collect(toList()).get(0);
	}

	//Mesure Type Normal Or Repeat
	public String getMesureTypeAU() {
		List<Field> idFields = getRepeats(14);
		return idFields.stream().map(f -> f.toComponentField().getComponent(1).trim()).collect(toList()).get(0);
	}

	//Sample Kind Routine ,STAT,etc...
	public String getSampleKindAU() {
		List<Field> idFields = getRepeats(14);
		return idFields.stream().map(f -> f.toComponentField().getComponent(2).trim()).collect(toList()).get(0);
	}

	//"0001" to "9999": Routine sample 
	//"001" to "999": Emergency sample, STAT sample, control, reagent
	//blank sample, calibrator 
	//"0000": Auto repeat sample
	public String getSampleNoAU() {
		List<Field> idFields = getRepeats(14);
		return idFields.stream().map(f -> f.toComponentField().getComponent(3).trim()).collect(toList()).get(0);
	}

	//(0 to 26)
	public String getSampleIdAU() {
		List<Field> idFields = getRepeats(14);
		return idFields.stream().map(f -> f.toComponentField().getComponent(6).trim()).collect(toList()).get(0);
	}

	public String getRackNoAU() {
		List<Field> idFields = getRepeats(14);
		return idFields.stream().map(f -> f.toComponentField().getComponent(7).trim()).collect(toList()).get(0);
	}

	//"1" to "10": Rack sample 
	//"1" to "22": STAT sample
	public String getCupPositionAU() {
		List<Field> idFields = getRepeats(14);
		return idFields.stream().map(f -> f.toComponentField().getComponent(8).trim()).collect(toList()).get(0);
	}

	//"": Serum 
	//"U": Urine 
	//"X": Other 1 
	//"Y": Other 2 
	//"W": Whole blood 
	//"N": Not specified
	public String getSampleTypeAU() {
		List<Field> idFields = getRepeats(14);
		return idFields.stream().map(f -> f.toComponentField().getComponent(9).trim()).collect(toList()).get(0);
	}

	@Override
	protected LIS2A2Record getNew(DelimitedData<Field> data) {
		return new QueryRecord(data);
	}

	@Override
	public String toString() {
		return "Q{" +
				"data=" + data +
				'}';
	}
}