package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record.Type;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_v24_FinancialTransaction extends LIS2A2Record {

    @Override
    protected LIS2A2Record getNew(DelimitedData<Field> data) {
        // TODO Auto-generated method stub
        return null;
    }

    public HL7_v24_FinancialTransaction(DelimitedData<Field> fields) {
        super(fields);
    }

    public HL7_v24_FinancialTransaction() {
        // TODO Auto-generated constructor stub
    }

    public static HL7_v24_FinancialTransaction create(int sequenceNumber) {

        return (HL7_v24_FinancialTransaction) new HL7_v24_FinancialTransaction()
            .setField(1, Type.FT1.name())
            .setField(2, String.valueOf(sequenceNumber));
    }

    public void setID(String strID) {
        this.setField(1, strID);
    }

    public void setTransactionID(String strTransactionID) {
        this.setField(2, strTransactionID);
    }

    public void setTransactionBatchID(String strTransactionBatchID) {
        this.setField(3, strTransactionBatchID);
    }

    public void setTransactionDate(String strTransactionDate) {
        this.setField(4, strTransactionDate);
    }

    public void setTransactionPostingDate(String strTransactionPostingDate) {
        this.setField(5, strTransactionPostingDate);
    }

    public void setTransactionType(String strTransactionType) {
        this.setField(6, strTransactionType);
    }

    public void setTransactionCode(String strTransactionCode) {
        this.setField(7, strTransactionCode);
    }

    public void setTransactionDescription(String strTransactionDescription) {
        this.setField(8, strTransactionDescription);
    }

    public void setTransactionDescriptionALT(String strTransactionDescriptionALT) {
        this.setField(9, strTransactionDescriptionALT);
    }

    public void setTransactionQuantity(String strTransactionQuantity) {
        this.setField(10, strTransactionQuantity);
    }

    public void setTransactionAmountExtended(String strTransactionAmountExtended) {
        this.setField(11, strTransactionAmountExtended);
    }

    public void setTransactionAmountUnit(String strTransactionAmountUnit) {
        this.setField(12, strTransactionAmountUnit);
    }

    public void setDepartmentCode(String strDepartmentCode) {
        this.setField(13, strDepartmentCode);
    }

    public void setInsurancePlanID(String strInsurancePlanID) {
        this.setField(14, strInsurancePlanID);
    }

    public void setInsuranceAmount(String strInsuranceAmount) {
        this.setField(15, strInsuranceAmount);
    }

    public void setAssignedPatientLocation(String strAssignedPatientLocation) {
        this.setField(16, strAssignedPatientLocation);
    }

    public void setFeeSchedule(String strFeeSchedule) {
        this.setField(17, strFeeSchedule);
    }

    public void setPatientType(String strPatientType) {
        this.setField(18, strPatientType);
    }

    public void setDiagnosisCode(String strDiagnosisCode) {
        this.setField(19, strDiagnosisCode);
    }

    public void setPerformedByCode(String strPerformedByCode) {
        this.setField(20, strPerformedByCode);
    }

    public void setOrderedByCode(String strOrderedByCode) {
        this.setField(21, strOrderedByCode);
    }

    public void setUnitCost(String strUnitCost) {
        this.setField(22, strUnitCost);
    }

    public void setFillerOrderNumber(String strFillerOrderNumber) {
        this.setField(23, strFillerOrderNumber);
    }

    public void setEnteredBy(String strEnteredBy) {
        this.setField(24, strEnteredBy);
    }

    public void setProcedure(String strProcedure) {
        this.setField(25, strProcedure);
    }

    public void setProcedureCodeModifier(String strProcedureCodeModifier) {
        this.setField(26, strProcedureCodeModifier);
    }

    public String getSetID() {
        return this.getFieldValue(1);
    }

    public String getTransactionID() {
        return this.getFieldValue(2);
    }

    public String getTransactionBatchID() {
        return this.getFieldValue(3);
    }

    public String getTransactionDate() {
        return this.getFieldValue(4);
    }

    public String getTransactionPostingDate() {
        return this.getFieldValue(5);
    }

    public String getTransactionType() {
        return this.getFieldValue(6);
    }

    public String getTransactionCode() {
        return this.getFieldValue(7);
    }

    public String getTransactionDescription() {
        return this.getFieldValue(8);
    }

    public String getTransactionDescriptionALT() {
        return this.getFieldValue(9);
    }

    public String getTransactionQuantity() {
        return this.getFieldValue(10);
    }

    public String getTransactionAmountExtended() {
        return this.getFieldValue(11);
    }

    public String getTransactionAmountUnit() {
        return this.getFieldValue(12);
    }

    public String getDepartmentCode() {
        return this.getFieldValue(13);
    }

    public String getInsurancePlanID() {
        return this.getFieldValue(14);
    }

    public String getInsuranceAmount() {
        return this.getFieldValue(15);
    }

    public String getAssignedPatientLocation() {
        return this.getFieldValue(16);
    }

    public String getFeeSchedule() {
        return this.getFieldValue(17);
    }

    public String getPatientType() {
        return this.getFieldValue(18);
    }

    public String getDiagnosisCode() {
        return this.getFieldValue(19);
    }

    public String getPerformedByCode() {
        return this.getFieldValue(20);
    }

    public String getOrderedByCode() {
        return this.getComponentValue(21,1);
    }

    public String getUnitCost() {
        return this.getFieldValue(22);
    }

    public String getFillerOrderNumber() {
        return this.getFieldValue(23);
    }

    public String getEnteredBy() {
        return this.getFieldValue(24);
    }

    public String getProcedure() {
        return this.getFieldValue(25);
    }

    public String getProcedureCodeModifier() {
        return this.getFieldValue(26);
    }

}
