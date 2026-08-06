package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

import ca.uhn.fhir.rest.client.api.Header;

public class HeaderRecord extends LIS2A2Record {
	
	
	public enum HL7_v24_MESSAGE_TYPE
	{
		ADT("ADT"),
		DFT("DFT"),
		ORU ("ORU"),
		MFN ("MFN");

		
		
		private HL7_v24_MESSAGE_TYPE(String value) {
			this.value = value;
		}

		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	};
	
	
	public enum HL7_v24_TRIGGER_EVENT
	{
		//: A01 , A28, A31 , A04, ,A03,A01,A11,A08,A02, A13, A54 
		A01("A01"),
		A03("A03"),
		A05("A05"),
		A04("A04"),
		A08("A08"),
		A11("A11"),
		A13("A13"),
		A28("A28");
		
		
		private HL7_v24_TRIGGER_EVENT(String value) {
			this.value = value;
		}

		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
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


	private HeaderRecord() {
	
		
	}

	HeaderRecord(DelimitedData<Field> data) {
		super(data);
	}
	
	public static HeaderRecord create() {
		return (HeaderRecord) new HeaderRecord().setField(1, Type.MSH.name())
												.setField(2, "\\~^&");
	}
	
	public static HeaderRecord create(String delimiter)  {
		return (HeaderRecord) new HeaderRecord().setField(1, Type.MSH.name())
												.setField(2, delimiter);
	}
	
	public HeaderRecord setSendingApp( String strSendingApp) {
		return (HeaderRecord) setField(3, strSendingApp);
	}
	
	public HeaderRecord setSendingFacility (String strSendingFacility ) {
		return (HeaderRecord) setField(4, strSendingFacility);
	}
	
	
	public HeaderRecord setReceivingFacility (String strReceivingFacility ) {
		return (HeaderRecord) setField(6, strReceivingFacility);
	}
	
	public HeaderRecord setDateTimeOfMessage(String strDateTimeOfMessage ) {
		return (HeaderRecord) setField(7,strDateTimeOfMessage);
	}
	
	public HeaderRecord setSecurity(String strDateTimeOfMessage ) {
		return (HeaderRecord) setField(8, strDateTimeOfMessage);
	}
	
	public HeaderRecord setMessageType(String strMessageType ) {
		return (HeaderRecord) setField(9, strMessageType);
		
	}
	
	public HeaderRecord setMessageControlId(String messageControlId) {
		return (HeaderRecord) setField(10, messageControlId);
	}
	
	public HeaderRecord setProcessingID(String strProcessingID) {
		return (HeaderRecord) setField(11, strProcessingID);
	}
	
	public HeaderRecord setVersion(String strVersion) {
		return (HeaderRecord) setField(12, strVersion);
	}
	
	public HeaderRecord setSequanceNo(String strSequanceNo) {
		return (HeaderRecord) setField(13, strSequanceNo);
	}
	
	public HeaderRecord setContinuationPointer(String strContinuationPointer) {
		return (HeaderRecord) setField(14, strContinuationPointer);
	}
	
	public HeaderRecord setAcceptAckType(String strAcceptAckType) {
		return (HeaderRecord) setField(15, strAcceptAckType);
	}
	
	public HeaderRecord setAppAckType(String strAppAckType) {
		return (HeaderRecord) setField(16, strAppAckType);
	}
	
	public HeaderRecord setCountryCode(String strCountryCode) {
		return (HeaderRecord) setField(17, strCountryCode);
	}
	
	public HeaderRecord setCharacterSet(String strCharacterSet) {
		return (HeaderRecord) setField(18, strCharacterSet);
	}
	
	public HeaderRecord setPrincipalLanguageOfMessage(String strPrincipalLanguageOfMessage) {
		return (HeaderRecord) setField(19, strPrincipalLanguageOfMessage);
	}
	
	public HeaderRecord setAlternateCharacterSetHandlingScheme(String strAlternateCharacterSetHandlingScheme) {
		return (HeaderRecord) setField(20, strAlternateCharacterSetHandlingScheme);
	}
	
	public HeaderRecord setConformanceStatementID(String strConformanceStatementID) {
		return (HeaderRecord) setField(21, strConformanceStatementID);
	}
	
	
	
	
	
	public String getSendingApp( ) {
		return this.getFieldValue(2);
	}
	
	public String getSendingFacility ( ) {
		return this.getFieldValue(4);
	}
	
	public String getReceivingApp ( ) {
		return this.getFieldValue(5);
	}
	
	public String getReceivingFacility (  ) {
		return this.getFieldValue(6);
	}
	
	public String getDateTimeOfMessage( ) {
		return this.getFieldValue(6);
		
	}
	
	public String getSecurity( ) {
		return this.getFieldValue(8);
	}
	
	public String getMessageType( ) {
		hl7MessageType = HL7_v24_MESSAGE_TYPE.valueOf(this.getComponentValue(8, 1));
		hl7MessageTrigger = HL7_v24_TRIGGER_EVENT.valueOf(this.getComponentValue(8, 2));
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
		return this.getFieldValue(18);
	}
	
	public String getPrincipalLanguageOfMessage() {
		return this.getFieldValue(19);
	}
	
	public String getAlternateCharacterSetHandlingScheme() {
		return this.getFieldValue(20);
	}
	
	public String getConformanceStatementID () {
		return this.getFieldValue(21);
	}
	
	
	

	public HL7_v24_TRIGGER_EVENT getEventTrigger() {
		return this.hl7MessageTrigger;
	}

	public void setEventTrigger(HL7_v24_TRIGGER_EVENT eventTrigger) {
		this.hl7MessageTrigger =  HL7_v24_TRIGGER_EVENT.valueOf( this.getComponentValue(8, 2));
	}

	@Override
	protected HeaderRecord getNew(DelimitedData<Field> fields) {
		return new HeaderRecord(fields);
	}

	@Override
	public String toString() {
		return "MSH{" +
				"data=" + data +
				'}';
	}

	/**
	 * @return
	 */
	public String getUserName() {

		return getFieldValue(3);
	}

}