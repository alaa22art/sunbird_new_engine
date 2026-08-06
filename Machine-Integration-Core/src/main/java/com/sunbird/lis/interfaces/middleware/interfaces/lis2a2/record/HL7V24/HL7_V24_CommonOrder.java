package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V24_CommonOrder extends LIS2A2Record {

    @Override
    protected LIS2A2Record getNew(DelimitedData<Field> data) {
        // TODO Auto-generated method stub
        return null;
    }

    public HL7_V24_CommonOrder(DelimitedData<Field> fields) {
        super(fields);
    }

    public HL7_V24_CommonOrder() {
        // TODO Auto-generated constructor stub
    }

    public static HL7_V24_CommonOrder create(int sequenceNumber) {

        return (HL7_V24_CommonOrder) new HL7_V24_CommonOrder()
            .setField(1, Type.ORC.name())
            .setField(2, String.valueOf(sequenceNumber));
    }

    public void setOrderControl(String str) {
        this.setField(1, str);
    }

    public void setPlacerOrderNumber(String str) {
        this.setField(2, str);
    }

    public void setFillerOrderNumber(String str) {
        this.setField(3, str);
    }

    public void setPlacerGroupNumber(String str) {
        this.setField(4, str);
    }

    public void setOrderStatus(String str) {
        this.setField(5, str);
    }

    public void setResponseFlag(String str) {
        this.setField(6, str);
    }

    public void setQuantityTiming(String str) {
        this.setField(7, str);
    }

    public void setParentOrder(String str) {
        this.setField(8, str);
    }

    public void setDateTimeofTransaction(String str) {
        this.setField(9, str);
    }

    public void setEnteredBy(String str) {
        this.setField(10, str);
    }

    public void setVerifiedBy(String str) {
        this.setField(11, str);
    }

    public void setOrderingProvider(String str) {
        this.setField(12, str);
    }

    public void setEnterersLocation(String str) {
        this.setField(13, str);
    }

    public void setCallBackPhoneNumber(String str) {
        this.setField(14, str);
    }

    public void setOrderEffectiveDateTime(String str) {
        this.setField(15, str);
    }

    public void setOrderControlCodeReason(String str) {
        this.setField(16, str);
    }

    public void setEnteringOrganization(String str) {
        this.setField(17, str);
    }

    public void setEnteringDevice(String str) {
        this.setField(18, str);
    }

    public void setActionBy(String str) {
        this.setField(19, str);
    }

    public void setAdvancedBeneficiaryNoticeCode(String str) {
        this.setField(20, str);
    }

    public void setOrderingFacilityName(String str) {
        this.setField(21, str);
    }

    public void setOrderingFacilityAddress(String str) {
        this.setField(22, str);
    }

    public void setOrderingFacilityPhoneNumber(String str) {
        this.setField(23, str);
    }

    public void setOrderingProviderAddress(String str) {
        this.setField(24, str);
    }

    public void setOrderStatusModifier(String str) {
        this.setField(25, str);
    }

    public void setExpectedStartDate(String str) {
        this.setComponent(27, 4, str);
    }

    public void setExpectedEndDate(String str) {
        this.setComponent(27, 5, str);
    }

    public String getExpectedStartDate() {
        return this.getComponentValue(27, 4);
    }

    public String getExpectedEndDate() {
        return this.getComponentValue(27, 5);
    }

    public String getOrderControl() {
        return this.getFieldValue(1);
    }

    public String getPlacerOrderNumber() {
        return this.getFieldValue(2);
    }

    public String getFillerOrderNumber() {
        return this.getFieldValue(3);
    }

    public String getPlacerGroupNumber() {
        return this.getFieldValue(4);
    }

    public String getOrderStatus() {
        return this.getFieldValue(5);
    }

    public String getResponseFlag() {
        return this.getFieldValue(6);
    }

    public String getQuantityTiming() {
        return this.getFieldValue(7);
    }

    public String getParentOrder() {
        return this.getFieldValue(8);
    }

    public String getDateTimeofTransaction() {
        return this.getFieldValue(9);
    }

    public String getEnteredBy() {
        return this.getFieldValue(10);
    }

    public String getVerifiedBy() {
        return this.getFieldValue(11);
    }

    public String getOrderingProvider() {
        return this.getFieldValue(12);
    }

    public String getEnterersLocation() {
        return this.getFieldValue(13);
    }

    public String getCallBackPhoneNumber() {
        return this.getFieldValue(14);
    }

    public String getOrderEffectiveDateTime() {
        return this.getFieldValue(15);
    }

    public String getOrderControlCodeReason() {
        return this.getFieldValue(16);
    }

    public String getEnteringOrganization() {
        return this.getFieldValue(17);
    }

    public String getEnteringDevice() {
        return this.getFieldValue(18);
    }

    public String getActionBy() {
        return this.getFieldValue(19);
    }

    public String getAdvancedBeneficiaryNoticeCode() {
        return this.getFieldValue(20);
    }

    public String getOrderingFacilityName() {
        return this.getFieldValue(21);
    }

    public String getOrderingFacilityAddress() {
        return this.getFieldValue(22);
    }

    public String getOrderingFacilityPhoneNumber() {
        return this.getFieldValue(23);
    }

    public String getOrderingProviderAddress() {
        return this.getFieldValue(24);
    }

    public String getOrderStatusModifier() {
        return this.getFieldValue(25);
    }
}
