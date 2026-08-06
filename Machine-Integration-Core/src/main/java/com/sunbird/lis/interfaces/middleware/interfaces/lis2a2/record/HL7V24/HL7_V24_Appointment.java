package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record.Type;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V24_Appointment extends LIS2A2Record {

    private String SCH_1_PlacerAppointment;
    private String SCH_2_FillerAppointmentID;
    private String SCH_3_OccurrenceNumber;
    private String SCH_4_PlacerGroupNumber;
    private String SCH_5_ScheduleID;
    private String SCH_6_EventReason;
    private String SCH_7_AppointmentReason;
    private String SCH_8_AppointmentType;
    private String SCH_9_AppointmentDuration;
    private String SCH_10_AppointmentDurationUnits;
    private String SCH_11_AppointmentTimingQuantity;
    private String SCH_12_PlacerContactPerson;
    private String SCH_13_PlacerContactPhoneNumber;
    private String SCH_14_PlacerContactAddress;
    private String SCH_15_PlacerContactLocation;
    private String SCH_16_FillerContactPerson;
    private String SCH_17_FillerContactPhoneNumber;
    private String SCH_18_FillerContactAddress;
    private String SCH_19_FillerContactLocation;
    private String SCH_20_EnteredByPerson;
    private String SCH_21_EnteredByPhoneNumber;
    private String SCH_22_EnteredbyLocation;
    private String SCH_23_ParentPlacerAppointmentID;
    private String SCH_24_ParentFillerAppointmentID;
    private String SCH_25_FillerStatusCode;
    private String SCH_26_PlacerOrderNumber;
    private String SCH_27_FillerOrderNumber;

    public static HL7_V24_Appointment create(int sequenceNumber) {
        return (HL7_V24_Appointment) new HL7_V24_Appointment().setField(1, Type.SCH.name())
            .setField(2, String.valueOf(sequenceNumber));
    }

    public void setPlacerAppointment(String strPlacerAppointment) {
        this.setField(1, strPlacerAppointment);
    }

    public void setFillerAppointmentID(String strFillerAppointmentID) {
        this.setField(2, strFillerAppointmentID);
    }

    public void setOccurrenceNumber(String strOccurrenceNumber) {
        this.setField(3, strOccurrenceNumber);
    }

    public void setPlacerGroupNumber(String strPlacerGroupNumber) {
        this.setField(4, strPlacerGroupNumber);
    }

    public void setScheduleID(String strScheduleID) {
        this.setField(5, strScheduleID);
    }

    public void setEventReason(String strEventReason) {
        this.setField(6, strEventReason);
    }

    public void setAppointmentReason(String strAppointmentReason) {
        this.setField(7, strAppointmentReason);
    }

    public void setAppointmentType(String strAppointmentType) {
        this.setField(8, strAppointmentType);
    }

    public void setAppointmentDuration(String strAppointmentDuration) {
        this.setField(9, strAppointmentDuration);
    }

    public void setAppointmentDurationUnits(String strAppointmentDurationUnits) {
        this.setField(10, strAppointmentDurationUnits);
    }

    public void setAppointmentTimingQuantity(String strAppointmentTimingQuantity) {
        this.setField(11, strAppointmentTimingQuantity);
    }

    public void setPlacerContactPerson(String strPlacerContactPerson) {
        this.setField(12, strPlacerContactPerson);
    }

    public void setPlacerContactPhoneNumber(String strPlacerContactPhoneNumber) {
        this.setField(13, strPlacerContactPhoneNumber);
    }

    public void setPlacerContactAddress(String strPlacerContactAddress) {
        this.setField(14, strPlacerContactAddress);
    }

    public void setPlacerContactLocation(String strPlacerContactLocation) {
        this.setField(15, strPlacerContactLocation);
    }

    public void setFillerContactPerson(String strFillerContactPerson) {
        this.setField(16, strFillerContactPerson);
    }

    public void setFillerContactPhoneNumber(String strFillerContactPhoneNumber) {
        this.setField(17, strFillerContactPhoneNumber);
    }

    public void setFillerContactAddress(String strFillerContactAddress) {
        this.setField(18, strFillerContactAddress);
    }

    public void setFillerContactLocation(String strFillerContactLocation) {
        this.setField(19, strFillerContactLocation);
    }

    public void setEnteredByPerson(String strEnteredByPerson) {
        this.setField(20, strEnteredByPerson);
    }

    public void setEnteredByPhoneNumber(String strEnteredByPhoneNumber) {
        this.setField(21, strEnteredByPhoneNumber);
    }

    public void setEnteredbyLocation(String strEnteredbyLocation) {
        this.setField(22, strEnteredbyLocation);
    }

    public void setParentPlacerAppointmentID(String strParentPlacerAppointmentID) {
        this.setField(23, strParentPlacerAppointmentID);
    }

    public void setParentFillerAppointmentID(String strParentFillerAppointmentID) {
        this.setField(24, strParentFillerAppointmentID);
    }

    public void setFillerStatusCode(String strFillerStatusCode) {
        this.setField(25, strFillerStatusCode);
    }

    public void setPlacerOrderNumber(String strPlacerOrderNumber) {
        this.setField(26, strPlacerOrderNumber);
    }

    public void setFillerOrderNumber(String strFillerOrderNumber) {
        this.setField(27, strFillerOrderNumber);
    }
    
    public void setAppointmentTimingQuantityStartDate(String strStartDate) {
        this.setComponent(11, 4, strStartDate);
    }
    
    public void setAppointmentTimingQuantityEndDate(String strEndDate) {
        this.setComponent(11, 5, strEndDate);
    }
    
    public String getAppointmentTimingQuantityStartDate() {
        return this.getComponentValue(11,4);
    }
    
    public String getAppointmentTimingQuantityEndDate() {
        return this.getComponentValue(11,5);
    }

    public String getPlacerAppointment() {
        return this.getFieldValue(1);
    }

    public String getFillerAppointmentID() {
        return this.getFieldValue(2);
    }

    public String getOccurrenceNumber() {
        return this.getFieldValue(3);
    }

    public String getPlacerGroupNumber() {
        return this.getFieldValue(4);
    }

    public String getScheduleID() {
        return this.getFieldValue(5);
    }

    public String getEventReason() {
        return this.getFieldValue(6);
    }

    public String getAppointmentReason() {
        return this.getFieldValue(7);
    }

    public String getAppointmentType() {
        return this.getFieldValue(8);
    }

    public String getAppointmentDuration() {
        return this.getFieldValue(9);
    }

    public String getAppointmentDurationUnits() {
        return this.getFieldValue(10);
    }

    public String getAppointmentTimingQuantity() {
        return this.getFieldValue(11);
    }

    public String getPlacerContactPerson() {
        return this.getFieldValue(12);
    }

    public String getPlacerContactPhoneNumber() {
        return this.getFieldValue(13);
    }

    public String getPlacerContactAddress() {
        return this.getFieldValue(14);
    }

    public String getPlacerContactLocation() {
        return this.getFieldValue(15);
    }

    public String getFillerContactPerson() {
        return this.getFieldValue(16);
    }

    public String getFillerContactPhoneNumber() {
        return this.getFieldValue(17);
    }

    public String getFillerContactAddress() {
        return this.getFieldValue(18);
    }

    public String getFillerContactLocation() {
        return this.getFieldValue(19);
    }

    public String getEnteredByPerson() {
        return this.getFieldValue(20);
    }

    public String getEnteredByPhoneNumber() {
        return this.getFieldValue(21);
    }

    public String getEnteredbyLocation() {
        return this.getFieldValue(22);
    }

    public String getParentPlacerAppointmentID() {
        return this.getFieldValue(23);
    }

    public String getParentFillerAppointmentID() {
        return this.getFieldValue(24);
    }

    public String getFillerStatusCode() {
        return this.getFieldValue(25);
    }

    public String getPlacerOrderNumber() {
        return this.getFieldValue(26);
    }

    public String getFillerOrderNumber() {
        return this.getFieldValue(27);
    }

    public HL7_V24_Appointment() {}

    public HL7_V24_Appointment(DelimitedData<Field> fields) {
        super(fields);
    }

    @Override
    public String toString() {
        return "EVN{" +
            "data=" + data +
            '}';
    }

    @Override
    protected LIS2A2Record getNew(DelimitedData<Field> data) {
        // TODO Auto-generated method stub
        return null;
    }

}
