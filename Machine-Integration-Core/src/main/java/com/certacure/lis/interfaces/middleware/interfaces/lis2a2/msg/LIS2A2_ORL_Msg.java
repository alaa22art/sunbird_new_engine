package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_Comments;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_CommonOrderSegment;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_MessageAcknowledgmentSegment;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_PatientDemographicQueryRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_PatientDemographicResponseRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_SpecimenSegment;

public class LIS2A2_ORL_Msg extends LIS2A2Msg {

	private long messageTransactionID;

	public LIS2A2_ORL_Msg(List<LIS2A2Record> records) {
		super(records);
	}

	public LIS2A2_ORL_Msg(List<LIS2A2Record> records, long messageTransactionID) {
		super(records);
		this.messageTransactionID = messageTransactionID;
	}

	public long getMessageTransactionID() {
		return messageTransactionID;
	}

	public void setMessageTransactionID(long messageTransactionID) {
		this.messageTransactionID = messageTransactionID;
	}

	public List<HL7_V24_HeaderRecord> getHeaderRecords() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V24_HeaderRecord)
						.map(record -> (HL7_V24_HeaderRecord) record)
						.collect(Collectors.toList());
	}
	
	public List<HL7_V25_MessageAcknowledgmentSegment> getHL7_V25_MessageAcknowledgmentSegment() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V25_MessageAcknowledgmentSegment)
						.map(record -> (HL7_V25_MessageAcknowledgmentSegment) record)
						.collect(Collectors.toList());
	}
	
	public List<HL7_V24_PatientRecord> getHL7v25PatientRecords() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V24_PatientRecord)
						.map(record -> (HL7_V24_PatientRecord) record)
						.collect(Collectors.toList());
	}
	
	public List<HL7_V25_SpecimenSegment> getHL7_V25_SpecimenSegment() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V25_SpecimenSegment)
						.map(record -> (HL7_V25_SpecimenSegment) record)
						.collect(Collectors.toList());
	}
	
	public List<HL7_V25_CommonOrderSegment> getHL7_V25_CommonOrderSegment() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V25_CommonOrderSegment)
						.map(record -> (HL7_V25_CommonOrderSegment) record)
						.collect(Collectors.toList());
	}
	

	@Override
	public String toString() {
		return "LIS2A2_ORL_Msg{" +
				"records=" + records +
				'}';
	}


}