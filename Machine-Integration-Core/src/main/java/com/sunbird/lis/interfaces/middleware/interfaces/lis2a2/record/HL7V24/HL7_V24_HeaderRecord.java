package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;

import com.sunbird.lis.interfaces.middleware.enums.Enums.HL7_v24_TRIGGER_EVENT;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V24_HeaderRecord extends LIS2A2Record {

    public enum Type {
        H,
        P,
        O,
        OBR,
        NET,
        OBX,
        MSH,
        EVN,
        R,
        Q,
        L,
        C, PID, ORM, PV1, DG1, SCH, PV2, ORC, FT1
    }

    public enum HL7_v24_MESSAGE_TYPE {
        ADT("ADT"),
        DFT("DFT"),
        ORU("ORU"),
        MFN("MFN"),
        SIU("SIU");

        private HL7_v24_MESSAGE_TYPE(String value) {
            this.value= value;
        }

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value= value;
        }

    };

    private HL7_v24_MESSAGE_TYPE hl7MessageType;
    private HL7_v24_TRIGGER_EVENT hl7MessageTrigger;

    /*private String strFieldSeparator	;	
    private String strEncodingCharacters;
    private String strSendingApplication;
    private String strSendingFacility;
    private String strReceivingApplication;
    private String strReceivingFacility;
    private String strDateTimeOfMessage	;
    private String strSecurity	;
    private String strMessageType;	
    private String strMessageControl;
    private String strProcessingID;
    private String strVersionID;
    private String strSequenceNumber;
    private String strContinuationPointer;
    private String strAcceptAcknowledgmentType;	
    private String strApplicationAcknowledgmentType;	
    private String strCountryCode;
    private String strCharacterSet;	
    private String strPrincipalLanguageOfMessage;	
    private String strAlternateCharacterSetHandlingScheme;
    private String strConformanceStatementID;*/

    private HL7_V24_HeaderRecord() {

    }

    public HL7_V24_HeaderRecord(DelimitedData<Field> data) {
        super(data);
    }

    public static HL7_V24_HeaderRecord create() {
        return (HL7_V24_HeaderRecord) new HL7_V24_HeaderRecord().setField(1, Type.MSH.name())
            .setField(2, "\\~^&");
    }

    public static HL7_V24_HeaderRecord create(String delimiter) {
        return (HL7_V24_HeaderRecord) new HL7_V24_HeaderRecord().setField(1, Type.MSH.name())
            .setField(2, delimiter);
    }

    public HL7_V24_HeaderRecord setSendingApp(String strSendingApp) {
        return (HL7_V24_HeaderRecord) setField(3, strSendingApp);
    }

    public HL7_V24_HeaderRecord setSendingFacility(String strSendingFacility) {
        return (HL7_V24_HeaderRecord) setField(4, strSendingFacility);
    }
    
    public HL7_V24_HeaderRecord setReceivingApplication(String strReceivingApplication) {
        return (HL7_V24_HeaderRecord) setField(5, strReceivingApplication);
    }

    public HL7_V24_HeaderRecord setReceivingFacility(String strReceivingFacility) {
        return (HL7_V24_HeaderRecord) setField(6, strReceivingFacility);
    }

    public HL7_V24_HeaderRecord setDateTimeOfMessage(String strDateTimeOfMessage) {
        return (HL7_V24_HeaderRecord) setField(7, strDateTimeOfMessage);
    }

    public HL7_V24_HeaderRecord setSecurity(String strDateTimeOfMessage) {
        return (HL7_V24_HeaderRecord) setField(8, strDateTimeOfMessage);
    }

    public HL7_V24_HeaderRecord setMessageType(String strMessageType) {
        return (HL7_V24_HeaderRecord) setField(9, strMessageType);

    }

    public HL7_V24_HeaderRecord setMessageControlId(String messageControlId) {
        return (HL7_V24_HeaderRecord) setField(10, messageControlId);
    }

    public HL7_V24_HeaderRecord setProcessingID(String strProcessingID) {
        return (HL7_V24_HeaderRecord) setField(11, strProcessingID);
    }

    public HL7_V24_HeaderRecord setVersion(String strVersion) {
        return (HL7_V24_HeaderRecord) setField(12, strVersion);
    }

    public HL7_V24_HeaderRecord setSequanceNo(String strSequanceNo) {
        return (HL7_V24_HeaderRecord) setField(13, strSequanceNo);
    }

    public HL7_V24_HeaderRecord setContinuationPointer(String strContinuationPointer) {
        return (HL7_V24_HeaderRecord) setField(14, strContinuationPointer);
    }

    public HL7_V24_HeaderRecord setAcceptAckType(String strAcceptAckType) {
        return (HL7_V24_HeaderRecord) setField(15, strAcceptAckType);
    }

    public HL7_V24_HeaderRecord setAppAckType(String strAppAckType) {
        return (HL7_V24_HeaderRecord) setField(16, strAppAckType);
    }

    public HL7_V24_HeaderRecord setCountryCode(String strCountryCode) {
        return (HL7_V24_HeaderRecord) setField(17, strCountryCode);
    }

    public HL7_V24_HeaderRecord setCharacterSet(String strCharacterSet) {
        return (HL7_V24_HeaderRecord) setField(18, strCharacterSet);
    }

    public HL7_V24_HeaderRecord setPrincipalLanguageOfMessage(
        String strPrincipalLanguageOfMessage) {
        return (HL7_V24_HeaderRecord) setField(19, strPrincipalLanguageOfMessage);
    }

    public HL7_V24_HeaderRecord setAlternateCharacterSetHandlingScheme(
        String strAlternateCharacterSetHandlingScheme) {
        return (HL7_V24_HeaderRecord) setField(20, strAlternateCharacterSetHandlingScheme);
    }

    public HL7_V24_HeaderRecord setConformanceStatementID(String strConformanceStatementID) {
        return (HL7_V24_HeaderRecord) setField(21, strConformanceStatementID);
    }

    public String getSendingApp() {
        return this.getFieldValue(2);
    }

    public String getSendingFacility() {
        return this.getFieldValue(3);
    }

    public String getReceivingApp() {
        return this.getFieldValue(4);
    }

    public String getReceivingFacility() {
        return this.getFieldValue(5);
    }

    public String getDateTimeOfMessage() {
        return this.getFieldValue(6);

    }

    public String getSecurity() {
        return this.getFieldValue(8);
    }

    public String getMessageType() {
        hl7MessageType= HL7_v24_MESSAGE_TYPE.valueOf(this.getComponentValue(8, 1));
        hl7MessageTrigger= HL7_v24_TRIGGER_EVENT.valueOf(this.getComponentValue(8, 2));
        return this.getFieldValue(8);
    }

    public String getMessageControlId() {
        return this.getFieldValue(9);
    }

    public String getProcessingID() {
        return this.getFieldValue(10);
    }

    public String getVersion() {

        return this.getFieldValue(11);
    }

    public String getSequanceNo() {
        return this.getFieldValue(12);
    }

    public String getContinuationPointer() {
        return this.getFieldValue(13);
    }

    public String getAcceptAckType() {
        return this.getFieldValue(14);
    }

    public String getAppAckType() {
        return this.getFieldValue(15);
    }

    public String getCountryCode() {
        return this.getFieldValue(16);
    }

    public String getCharacterSet() {
        return this.getFieldValue(17);
    }

    public String getPrincipalLanguageOfMessage() {
        return this.getFieldValue(18);
    }

    public String getAlternateCharacterSetHandlingScheme() {
        return this.getFieldValue(19);
    }

    public String getConformanceStatementID() {
        return this.getFieldValue(20);
    }

    public HL7_v24_TRIGGER_EVENT getEventTrigger() {
        return this.hl7MessageTrigger;
    }

    public void setEventTrigger(HL7_v24_TRIGGER_EVENT eventTrigger) {
        this.hl7MessageTrigger= HL7_v24_TRIGGER_EVENT.valueOf(this.getComponentValue(8, 2));
    }

    @Override
    protected HL7_V24_HeaderRecord getNew(DelimitedData<Field> fields) {
        return new HL7_V24_HeaderRecord(fields);
    }

    @Override
    public String toString() {
        return "MSH{" +
            "data=" + data +
            '}';
    }

    /** @return */
    public String getUserName() {

        return getFieldValue(3);
    }

}
