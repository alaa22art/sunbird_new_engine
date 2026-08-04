package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HeaderASTMRecord extends LIS2A2Record {

	public static HeaderASTMRecord create() {
		return (HeaderASTMRecord) new HeaderASTMRecord().setField(1, Type.H.name())
												.setField(2, "\\^&");
	}
	
	public static HeaderASTMRecord create(String delimiter)  {
		return (HeaderASTMRecord) new HeaderASTMRecord().setField(1, Type.H.name())
												.setField(2, delimiter);
	}

	private HeaderASTMRecord() {
	}

	public HeaderASTMRecord(DelimitedData<Field> data) {
		super(data);
	}

	public HeaderASTMRecord setMessageControlId(String messageControlId) {
		return (HeaderASTMRecord) setField(3, messageControlId);
	}

	public HeaderASTMRecord setSenderId(String senderId) {
		return (HeaderASTMRecord) setField(5, senderId);
	}

	public HeaderASTMRecord setPassword(String password) {
		return (HeaderASTMRecord) setField(4, password);
	}

	public HeaderASTMRecord setSenderAdress(String streetAddress, String city, String state, String zip, String countryCode) {
		return (HeaderASTMRecord) setComponent(6, 1, streetAddress)
																.setComponent(6, 2, city)
																.setComponent(6, 3, state)
																.setComponent(6, 4, zip)
																.setComponent(6, 5, countryCode);
	}

	public HeaderASTMRecord setReceiverId(String receiverId) {
		return (HeaderASTMRecord) setField(10, receiverId);
	}

	public HeaderASTMRecord setProcessingId(String processingId) {
		return (HeaderASTMRecord) setField(12, processingId);
	}
	
	public HeaderASTMRecord setProcessingId(String processingId , int index) {
		return (HeaderASTMRecord) setField(index, processingId);
	}

	public HeaderASTMRecord setVersionNumber(String versionNumber) {
		return (HeaderASTMRecord) setField(13, versionNumber);
	}

	public HeaderASTMRecord setDateTime(Date dateTime) {
		return (HeaderASTMRecord) setField(14, new SimpleDateFormat("yyyyMMddHHmmss").format(dateTime));
	}

	public HeaderASTMRecord setDateTimeWithoutSecond(Date dateTime) {
		return (HeaderASTMRecord) setField(14, new SimpleDateFormat("yyyyMMddHHmm").format(dateTime));
	}

	public HeaderASTMRecord setSpecialInstruction(String specialInstruction) {
		return (HeaderASTMRecord) setField(11, specialInstruction);
	}

	@Override
	protected HeaderASTMRecord getNew(DelimitedData<Field> fields) {
		return new HeaderASTMRecord(fields);
	}

	@Override
	public String toString() {
		return "H{" +
				"data=" + data +
				'}';
	}

	/**
	 * @return
	 */
	public String getUserName() {

		return getFieldValue(3);
	}

	/**
	 * @return
	 */
	public String getPassword() {
		return getFieldValue(4);
	}

	/**
	 * @return
	 */
	public String getSenderName() {
		if (data.getSize() >= 4) {
			return getFieldValue(5);
		} else {
			return "";
		}
	}
}