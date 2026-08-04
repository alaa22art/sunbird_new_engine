package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

import ca.uhn.fhir.rest.client.api.Header;

public class HeaderRecordAstm extends LIS2A2Record {
	
	
	public static HeaderRecordAstm create() {
		return (HeaderRecordAstm) new HeaderRecordAstm().setField(1, Type.H.name())
												.setField(2, "\\^&");
	}
	
	public static HeaderRecordAstm create(String delimiter)  {
		return (HeaderRecordAstm) new HeaderRecordAstm().setField(1, Type.H.name())
												.setField(2, delimiter);
	}

	private HeaderRecordAstm() {
	}

	HeaderRecordAstm(DelimitedData<Field> data) {
		super(data);
	}

	public HeaderRecord setMessageControlId(String messageControlId) {
		return (HeaderRecord) setField(3, messageControlId);
	}

	public HeaderRecord setSenderId(String senderId) {
		return (HeaderRecord) setField(5, senderId);
	}

	public HeaderRecord setPassword(String password) {
		return (HeaderRecord) setField(4, password);
	}

	public HeaderRecord setSenderAdress(String streetAddress, String city, String state, String zip, String countryCode) {
		return (HeaderRecord) setComponent(6, 1, streetAddress)
																.setComponent(6, 2, city)
																.setComponent(6, 3, state)
																.setComponent(6, 4, zip)
																.setComponent(6, 5, countryCode);
	}

	public HeaderRecord setReceiverId(String receiverId) {
		return (HeaderRecord) setField(10, receiverId);
	}

	public HeaderRecord setProcessingId(String processingId) {
		return (HeaderRecord) setField(12, processingId);
	}
	
	public HeaderRecord setProcessingId(String processingId , int index) {
		return (HeaderRecord) setField(index, processingId);
	}

	public HeaderRecord setVersionNumber(String versionNumber) {
		return (HeaderRecord) setField(13, versionNumber);
	}

	public HeaderRecord setDateTime(Date dateTime) {
		return (HeaderRecord) setField(14, new SimpleDateFormat("yyyyMMddHHmmss").format(dateTime));
	}

	public HeaderRecord setDateTimeWithoutSecond(Date dateTime) {
		return (HeaderRecord) setField(14, new SimpleDateFormat("yyyyMMddHHmm").format(dateTime));
	}

	public HeaderRecord setSpecialInstruction(String specialInstruction) {
		return (HeaderRecord) setField(11, specialInstruction);
	}

	@Override
	protected HeaderRecord getNew(DelimitedData<Field> fields) {
		return new HeaderRecord(fields);
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