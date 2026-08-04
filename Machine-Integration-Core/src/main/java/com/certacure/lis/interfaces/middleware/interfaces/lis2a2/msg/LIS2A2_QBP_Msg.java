package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.LabQueryMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.QueryPatientDemographicRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.QueryRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_PatientDemographicQueryRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_PatientDemographicResponseRecord;

public class LIS2A2_QBP_Msg extends LIS2A2Msg {

	private long messageTransactionID;
	private String specimenId;

	public LIS2A2_QBP_Msg(List<LIS2A2Record> records) {
		super(records);
	}

	public LIS2A2_QBP_Msg(List<LIS2A2Record> records, long messageTransactionID) {
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

	public List<HL7_V25_PatientDemographicQueryRecord> getQueryPatientDemographicRecords() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V25_PatientDemographicQueryRecord)
						.map(record -> (HL7_V25_PatientDemographicQueryRecord) record)
						.collect(Collectors.toList());
	}
	
	public List<HL7_V25_PatientDemographicResponseRecord> getPatientDemographicResponseRecord() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V25_PatientDemographicResponseRecord)
						.map(record -> (HL7_V25_PatientDemographicResponseRecord) record)
						.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "LIS2A2_QBP_Msg{" +
				"records=" + records +
				'}';
	}

	public String getSpecimenId() {
		return specimenId;
	}

	public void setSpecimenId(String specimenId) {
		this.specimenId = specimenId;
	}

}