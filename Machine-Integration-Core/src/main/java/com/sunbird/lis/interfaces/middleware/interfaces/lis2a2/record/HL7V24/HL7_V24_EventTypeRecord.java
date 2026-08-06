package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record.Type;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V24_EventTypeRecord extends LIS2A2Record {
	

	public static HL7_V24_EventTypeRecord create(int sequenceNumber) {
		return (HL7_V24_EventTypeRecord) 
				new HL7_V24_EventTypeRecord().setField(1, Type.EVN.name()).setField(2, String.valueOf(sequenceNumber));
	}
	
	private String strEventTypeCode;


	private String strRecordedDateTime;	
	private String strDateTimePlannedEvent;			
    private String strEventReasonCode;
    private String strOperatorID;
    private String strEventOccurred;
    private String strEventFacility;
    
    
    public void setEventTypeCode(String strEventTypeCode) {
		this.setField(1, strEventTypeCode);
	}

	public void setRecordedDateTime(String strRecordedDateTime) {
		this.setField(2, strRecordedDateTime);
	}

	public void setDateTimePlannedEvent(String strDateTimePlannedEvent) {
		this.setField(3, strDateTimePlannedEvent);
	}

	public void setEventReasonCode(String strEventReasonCode) {
		this.setField(4, strEventReasonCode);
	}

	public void setOperatorID(String strOperatorID) {
		this.setField(5, strOperatorID);
	}

	public void setEventOccurred(String strEventOccurred) {
		this.setField(6, strEventOccurred);
	}

	public void setEventFacility(String strEventFacility) {
		this.setField(7, strEventFacility);
	}
	
	
	public String getEventTypeCode( ) {
		return this.getFieldValue(1);
	}

	public String getRecordedDateTime( ) {
		return this.getFieldValue(2);
	}

	public String getDateTimePlannedEvent( ) {
		return this.getFieldValue(3);
	}

	public String getEventReasonCode( ) {
		return this.getFieldValue(4);
	}

	public String getOperatorID( ) {
		//return this.getComponentValue(5, 1);
	    return "Vista";
	}

	public String getEventOccurred( ) {
		return this.getFieldValue(6);
	}

	public String getEventFacility( ) {
		return this.getFieldValue(7);
	}
	
	
	

	public HL7_V24_EventTypeRecord() {
	}

	public HL7_V24_EventTypeRecord(DelimitedData<Field> fields) {
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
