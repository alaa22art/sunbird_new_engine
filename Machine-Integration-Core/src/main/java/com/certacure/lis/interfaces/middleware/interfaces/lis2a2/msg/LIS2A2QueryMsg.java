package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.QueryRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_PatientDemographicQueryRecord;

public class LIS2A2QueryMsg extends LIS2A2Msg {

	private long messageTransactionID;
	private String strSpecimenNumber;
	private String strPositionDetails;
	private String strOriginalQueryID;
	

	public LIS2A2QueryMsg(List<LIS2A2Record> records) {
		super(records);
	}

	public LIS2A2QueryMsg(List<LIS2A2Record> records, long messageTransactionID) {
		super(records);
		this.messageTransactionID = messageTransactionID;
		
	}
	
	public LIS2A2QueryMsg(List<LIS2A2Record> records, 
			HL7_V25_PatientDemographicQueryRecord lstHL7_V25_PatientDemographicQueryRecord , 
			long messageTransactionID) {
		super(records);
		
		records.get(0);
		
		this.messageTransactionID = messageTransactionID;
		this.setSpecimenNumber("");
		this.setSpecimenNumber("");
		
				
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

	public List<QueryRecord> getQueryRecords() {
		return records	.stream()
						.filter(record -> record instanceof QueryRecord)
						.map(record -> (QueryRecord) record)
						.collect(Collectors.toList());
	}
	
	public List<HL7_V25_PatientDemographicQueryRecord> getHL7QueryRecords() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V25_PatientDemographicQueryRecord)
						.map(record -> (HL7_V25_PatientDemographicQueryRecord) record)
						.collect(Collectors.toList());
	}
	

	@Override
	public String toString() {
		return "LIS2A2QueryMsg{" +
				"records=" + records +
				"messageTransactionID=" + messageTransactionID +
				'}';
	}

	public List<HL7_V25_PatientDemographicQueryRecord> getQueryParametrDefinitionRecord() {
		return records	.stream()
				.filter(record -> record instanceof HL7_V25_PatientDemographicQueryRecord)
				.map(record -> (HL7_V25_PatientDemographicQueryRecord) record)
				.collect(Collectors.toList());
	}

	public String getSpecimenNumber() {
		return strSpecimenNumber;
	}

	public void setSpecimenNumber(String strSpecimenNumber) {
		this.strSpecimenNumber = strSpecimenNumber;
	}

	public String getPositionDetails() {
		return strPositionDetails;
	}

	public void setPositionDetails(String strPositionDetails) {
		this.strPositionDetails = strPositionDetails;
	}
}