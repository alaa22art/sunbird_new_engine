package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V24_DiagnosisRecord extends LIS2A2Record {
	
	
	
	private String DG1_1_SetID_DG1;
	private String DG1_2_DiagnosisCodingMethod;
	private String DG1_3_DiagnosisCode_DG1;
	private String DG1_4_DiagnosisDescription;
	private String DG1_5_DiagnosisDateTime;
	private String DG1_6_DiagnosisType;
	private String DG1_7_MajorDiagnosticCategory;
	private String DG1_8_DiagnosticRelatedGroup;
	private String DG1_9_DRGApprovalIndicator;
	private String DG1_10_DRGGrouperReviewCode;
	private String DG1_11_OutlierType;
	private String DG1_12_OutlierDays;
	private String DG1_13_OutlierCost;
	private String DG1_14_GrouperVersionAndType;
	private String DG1_15_DiagnosisPriority;
	private String DG1_16_DiagnosingClinician;
	private String DG1_17_DiagnosisClassification;
	private String DG1_18_ConfidentialIndicator;
	private String DG1_19_AttestationDateTime;


	public static HL7_V24_DiagnosisRecord create(int sequenceNumber) {
		return (HL7_V24_DiagnosisRecord) 
				new HL7_V24_DiagnosisRecord().setField(1, Type.DG1.name()).setField(2, String.valueOf(sequenceNumber));
	}

	private HL7_V24_DiagnosisRecord() {
	}

	public HL7_V24_DiagnosisRecord(DelimitedData<Field> fields) {
		super(fields);
	}

	
	
	

	public String getDG1_ID() {
		return this.getFieldValue(2);
	}

	public String getDiagnosisCodingMethod() {
		return this.getFieldValue(2);
	}

	public String getDiagnosisCode_DG1() {
		return this.getFieldValue(3);
	}

	public String getDiagnosisDescription() {
		return this.getFieldValue(4);
	}

	public String getDiagnosisDateTime() {
		return this.getFieldValue(5);
	}

	public String getDiagnosisType() {
		return this.getFieldValue(6);
	}

	public String getMajorDiagnosticCategory() {
		return this.getFieldValue(7);
	}

	public String getDiagnosticRelatedGroup() {
		return this.getFieldValue(8);
	}

	public String getDRGApprovalIndicator() {
		return this.getFieldValue(9);
	}

	public String getDRGGrouperReviewCode() {
		return this.getFieldValue(10);
	}

	public String getOutlierType() {
		return this.getFieldValue(11);
	}

	public String getOutlierDays() {
		return this.getFieldValue(12);
	}

	public String getOutlierCost() {
		return this.getFieldValue(13);
	}

	public String getGrouperVersionAndType() {
		return this.getFieldValue(14);
	}

	public String getDiagnosisPriority() {
		return this.getFieldValue(15);
	}

	public String getDiagnosingClinician() {
		return this.getFieldValue(16);
	}

	public String getDiagnosisClassification() {
		return this.getFieldValue(17);
	}

	public String getConfidentialIndicator() {
		return this.getFieldValue(18);
	}

	public String getAttestationDateTime() {
		return this.getFieldValue(19);
	}
	
	public String getSet_DG1_ID() {
		return DG1_1_SetID_DG1;
	}

	public String getDG1_2_DiagnosisCodingMethod() {
		return DG1_2_DiagnosisCodingMethod;
	}

	public String getDG1_3_DiagnosisCode_DG1() {
		return DG1_3_DiagnosisCode_DG1;
	}

	public String getDG1_4_DiagnosisDescription() {
		return DG1_4_DiagnosisDescription;
	}

	public String getDG1_5_DiagnosisDateTime() {
		return DG1_5_DiagnosisDateTime;
	}

	public String getDG1_6_DiagnosisType() {
		return DG1_6_DiagnosisType;
	}

	public String getDG1_7_MajorDiagnosticCategory() {
		return DG1_7_MajorDiagnosticCategory;
	}

	public String getDG1_8_DiagnosticRelatedGroup() {
		return DG1_8_DiagnosticRelatedGroup;
	}

	public String getDG1_9_DRGApprovalIndicator() {
		return DG1_9_DRGApprovalIndicator;
	}

	public String getDG1_10_DRGGrouperReviewCode() {
		return DG1_10_DRGGrouperReviewCode;
	}

	public String getDG1_11_OutlierType() {
		return DG1_11_OutlierType;
	}

	public String getDG1_12_OutlierDays() {
		return DG1_12_OutlierDays;
	}

	public String getDG1_13_OutlierCost() {
		return DG1_13_OutlierCost;
	}

	public String getDG1_14_GrouperVersionAndType() {
		return DG1_14_GrouperVersionAndType;
	}

	public String getDG1_15_DiagnosisPriority() {
		return DG1_15_DiagnosisPriority;
	}

	public String getDG1_16_DiagnosingClinician() {
		return DG1_16_DiagnosingClinician;
	}

	public String getDG1_17_DiagnosisClassification() {
		return DG1_17_DiagnosisClassification;
	}

	public String getDG1_18_ConfidentialIndicator() {
		return DG1_18_ConfidentialIndicator;
	}

	public String getDG1_19_AttestationDateTime() {
		return DG1_19_AttestationDateTime;
	}


	public HL7_V24_HeaderRecord setDG1_1_SetID_DG1(String dG1_1_SetID_DG1) {
		
		return (HL7_V24_HeaderRecord) setField(1, dG1_1_SetID_DG1);
		
	}

	public HL7_V24_HeaderRecord setDG1_2_DiagnosisCodingMethod(String dG1_2_DiagnosisCodingMethod) {

		return (HL7_V24_HeaderRecord) setField(2, dG1_2_DiagnosisCodingMethod);
	}

	public HL7_V24_HeaderRecord setDG1_3_DiagnosisCode_DG1(String dG1_3_DiagnosisCode_DG1) {
		DG1_3_DiagnosisCode_DG1 = dG1_3_DiagnosisCode_DG1;
		return (HL7_V24_HeaderRecord) setField(3, dG1_3_DiagnosisCode_DG1);
	}

	public HL7_V24_HeaderRecord setDG1_4_DiagnosisDescription(String dG1_4_DiagnosisDescription) {
		DG1_4_DiagnosisDescription = dG1_4_DiagnosisDescription;
		return (HL7_V24_HeaderRecord) setField(4, dG1_4_DiagnosisDescription);
	}

	public HL7_V24_HeaderRecord setDG1_5_DiagnosisDateTime(String dG1_5_DiagnosisDateTime) {
		DG1_5_DiagnosisDateTime = dG1_5_DiagnosisDateTime;
		return (HL7_V24_HeaderRecord) setField(5, dG1_5_DiagnosisDateTime);
	}

	public HL7_V24_HeaderRecord setDG1_6_DiagnosisType(String dG1_6_DiagnosisType) {
		DG1_6_DiagnosisType = dG1_6_DiagnosisType;
		return (HL7_V24_HeaderRecord) setField(6, dG1_6_DiagnosisType);
	}

	public HL7_V24_HeaderRecord setDG1_7_MajorDiagnosticCategory(String dG1_7_MajorDiagnosticCategory) {
		DG1_7_MajorDiagnosticCategory = dG1_7_MajorDiagnosticCategory;
		return (HL7_V24_HeaderRecord) setField(7, dG1_7_MajorDiagnosticCategory);
	}

	public HL7_V24_HeaderRecord setDG1_8_DiagnosticRelatedGroup(String dG1_8_DiagnosticRelatedGroup) {
		DG1_8_DiagnosticRelatedGroup = dG1_8_DiagnosticRelatedGroup;
		return (HL7_V24_HeaderRecord) setField(8, dG1_8_DiagnosticRelatedGroup);
	}

	public HL7_V24_HeaderRecord setDG1_9_DRGApprovalIndicator(String dG1_9_DRGApprovalIndicator) {
		DG1_9_DRGApprovalIndicator = dG1_9_DRGApprovalIndicator;
		return (HL7_V24_HeaderRecord) setField(9, dG1_9_DRGApprovalIndicator);
	}

	public HL7_V24_HeaderRecord setDG1_10_DRGGrouperReviewCode(String dG1_10_DRGGrouperReviewCode) {
		DG1_10_DRGGrouperReviewCode = dG1_10_DRGGrouperReviewCode;
		return (HL7_V24_HeaderRecord) setField(10, dG1_10_DRGGrouperReviewCode);
	}
	

	public HL7_V24_HeaderRecord setDG1_11_OutlierType(String dG1_11_OutlierType) {
		DG1_11_OutlierType = dG1_11_OutlierType;
		return (HL7_V24_HeaderRecord) setField(11, dG1_11_OutlierType);
	}

	public HL7_V24_HeaderRecord setDG1_12_OutlierDays(String dG1_12_OutlierDays) {
		DG1_12_OutlierDays = dG1_12_OutlierDays;
		return (HL7_V24_HeaderRecord) setField(12, dG1_12_OutlierDays);
	}

	public HL7_V24_HeaderRecord setDG1_13_OutlierCost(String dG1_13_OutlierCost) {
		DG1_13_OutlierCost = dG1_13_OutlierCost;
		return (HL7_V24_HeaderRecord) setField(13, dG1_13_OutlierCost);
	}

	public HL7_V24_HeaderRecord setDG1_14_GrouperVersionAndType(String dG1_14_GrouperVersionAndType) {
		DG1_14_GrouperVersionAndType = dG1_14_GrouperVersionAndType;
		return (HL7_V24_HeaderRecord) setField(14, dG1_14_GrouperVersionAndType);
	}

	public HL7_V24_HeaderRecord setDG1_15_DiagnosisPriority(String dG1_15_DiagnosisPriority) {
		DG1_15_DiagnosisPriority = dG1_15_DiagnosisPriority;
		return (HL7_V24_HeaderRecord) setField(15, dG1_15_DiagnosisPriority);
	}

	public HL7_V24_HeaderRecord setDG1_16_DiagnosingClinician(String dG1_16_DiagnosingClinician) {
		DG1_16_DiagnosingClinician = dG1_16_DiagnosingClinician;
		return (HL7_V24_HeaderRecord) setField(16, dG1_16_DiagnosingClinician);
	}

	public HL7_V24_HeaderRecord setDG1_17_DiagnosisClassification(String dG1_17_DiagnosisClassification) {
		DG1_17_DiagnosisClassification = dG1_17_DiagnosisClassification;
		return (HL7_V24_HeaderRecord) setField(17, dG1_17_DiagnosisClassification);
	}

	public HL7_V24_HeaderRecord setDG1_18_ConfidentialIndicator(String dG1_18_ConfidentialIndicator) {
		DG1_18_ConfidentialIndicator = dG1_18_ConfidentialIndicator;
		return (HL7_V24_HeaderRecord) setField(18, dG1_18_ConfidentialIndicator);
	}

	public HL7_V24_HeaderRecord setDG1_19_AttestationDateTime(String dG1_19_AttestationDateTime) {
		DG1_19_AttestationDateTime = dG1_19_AttestationDateTime;
		return (HL7_V24_HeaderRecord) setField(19, dG1_19_AttestationDateTime);
	}
	public HL7_V24_HeaderRecord setID_DG1(String dG1_1_SetID_DG1) {
		DG1_1_SetID_DG1 = dG1_1_SetID_DG1;
		return (HL7_V24_HeaderRecord) setField(1, dG1_1_SetID_DG1);
	}

	public HL7_V24_HeaderRecord setDiagnosisCodingMethod(String dG1_2_DiagnosisCodingMethod) {
		DG1_2_DiagnosisCodingMethod = dG1_2_DiagnosisCodingMethod;
		return (HL7_V24_HeaderRecord) setField(2, dG1_2_DiagnosisCodingMethod);
	}

	public HL7_V24_HeaderRecord setDiagnosisCode_DG1(String dG1_3_DiagnosisCode_DG1) {
		DG1_3_DiagnosisCode_DG1 = dG1_3_DiagnosisCode_DG1;
		return (HL7_V24_HeaderRecord) setField(3, dG1_3_DiagnosisCode_DG1);
	}

	public HL7_V24_HeaderRecord setDiagnosisDescription(String dG1_4_DiagnosisDescription) {
		DG1_4_DiagnosisDescription = dG1_4_DiagnosisDescription;
		return (HL7_V24_HeaderRecord) setField(4, dG1_4_DiagnosisDescription);
	}

	public HL7_V24_HeaderRecord setDiagnosisDateTime(String dG1_5_DiagnosisDateTime) {
		DG1_5_DiagnosisDateTime = dG1_5_DiagnosisDateTime;
		return (HL7_V24_HeaderRecord) setField(5, dG1_5_DiagnosisDateTime);
	}

	public HL7_V24_HeaderRecord setDiagnosisType(String dG1_6_DiagnosisType) {
		DG1_6_DiagnosisType = dG1_6_DiagnosisType;
		return (HL7_V24_HeaderRecord) setField(6, dG1_6_DiagnosisType);
	}

	public HL7_V24_HeaderRecord setMajorDiagnosticCategory(String dG1_7_MajorDiagnosticCategory) {
		DG1_7_MajorDiagnosticCategory = dG1_7_MajorDiagnosticCategory;
		return (HL7_V24_HeaderRecord) setField(7, dG1_7_MajorDiagnosticCategory);
	}

	public HL7_V24_HeaderRecord setDiagnosticRelatedGroup(String dG1_8_DiagnosticRelatedGroup) {
		DG1_8_DiagnosticRelatedGroup = dG1_8_DiagnosticRelatedGroup;
		return (HL7_V24_HeaderRecord) setField(8, dG1_8_DiagnosticRelatedGroup);
	}

	public HL7_V24_HeaderRecord setDRGApprovalIndicator(String dG1_9_DRGApprovalIndicator) {
		DG1_9_DRGApprovalIndicator = dG1_9_DRGApprovalIndicator;
		return (HL7_V24_HeaderRecord) setField(9, dG1_9_DRGApprovalIndicator);
	}

	public HL7_V24_HeaderRecord setDRGGrouperReviewCode(String dG1_10_DRGGrouperReviewCode) {
		DG1_10_DRGGrouperReviewCode = dG1_10_DRGGrouperReviewCode;
		return (HL7_V24_HeaderRecord) setField(10, dG1_10_DRGGrouperReviewCode);
	}

	public HL7_V24_HeaderRecord setOutlierType(String dG1_11_OutlierType) {
		DG1_11_OutlierType = dG1_11_OutlierType;
		return (HL7_V24_HeaderRecord) setField(11, dG1_11_OutlierType);
	}

	public HL7_V24_HeaderRecord setOutlierDays(String dG1_12_OutlierDays) {
		DG1_12_OutlierDays = dG1_12_OutlierDays;
		return (HL7_V24_HeaderRecord) setField(12, dG1_12_OutlierDays);
	}

	public HL7_V24_HeaderRecord setOutlierCost(String dG1_13_OutlierCost) {
		DG1_13_OutlierCost = dG1_13_OutlierCost;
		return (HL7_V24_HeaderRecord) setField(13, dG1_13_OutlierCost);
	}

	public HL7_V24_HeaderRecord setGrouperVersionAndType(String dG1_14_GrouperVersionAndType) {
		DG1_14_GrouperVersionAndType = dG1_14_GrouperVersionAndType;
		return (HL7_V24_HeaderRecord) setField(14, dG1_14_GrouperVersionAndType);
	}

	public HL7_V24_HeaderRecord setDiagnosisPriority(String dG1_15_DiagnosisPriority) {
		DG1_15_DiagnosisPriority = dG1_15_DiagnosisPriority;
		return (HL7_V24_HeaderRecord) setField(15, dG1_15_DiagnosisPriority);
	}

	public HL7_V24_HeaderRecord setDiagnosingClinician(String dG1_16_DiagnosingClinician) {
		DG1_16_DiagnosingClinician = dG1_16_DiagnosingClinician;
		return (HL7_V24_HeaderRecord) setField(16, dG1_16_DiagnosingClinician);
	}

	public HL7_V24_HeaderRecord setDiagnosisClassification(String dG1_17_DiagnosisClassification) {
		DG1_17_DiagnosisClassification = dG1_17_DiagnosisClassification;
		return (HL7_V24_HeaderRecord) setField(17, dG1_17_DiagnosisClassification);
	}

	public HL7_V24_HeaderRecord setConfidentialIndicator(String dG1_18_ConfidentialIndicator) {
		DG1_18_ConfidentialIndicator = dG1_18_ConfidentialIndicator;
		return (HL7_V24_HeaderRecord) setField(18, dG1_18_ConfidentialIndicator);
	}

	public HL7_V24_HeaderRecord setAttestationDateTime(String dG1_19_AttestationDateTime) {
		DG1_19_AttestationDateTime = dG1_19_AttestationDateTime;
		return (HL7_V24_HeaderRecord) setField(19, dG1_19_AttestationDateTime);
	}

	@Override
	public String toString() {
		return "DG1{" +
				"data=" + data +
				'}';
	}

	@Override
	protected LIS2A2Record getNew(DelimitedData<Field> data) {
		// TODO Auto-generated method stub
		return null;
	}

}
