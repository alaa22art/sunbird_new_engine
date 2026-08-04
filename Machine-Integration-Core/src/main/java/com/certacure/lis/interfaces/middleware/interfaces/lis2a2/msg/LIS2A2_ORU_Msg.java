package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBXRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.QueryPatientDemographicRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.QueryRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ResultRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;

public class LIS2A2_ORU_Msg extends LIS2A2Msg {

	private long messageTransactionID;

	public LIS2A2_ORU_Msg(List<LIS2A2Record> records) {
		super(records);
	}

	public LIS2A2_ORU_Msg(List<LIS2A2Record> records, long messageTransactionID) {
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
	
	public List<OBRRecord> getOBRRecords() {
		return records	.stream()
						.filter(record -> record instanceof OBRRecord)
						.map(record -> (OBRRecord) record)
						.collect(Collectors.toList());
	}

	public List<PatientRecord> getPatientRecords() {
		return records	.stream()
						.filter(record -> record instanceof PatientRecord)
						.map(record -> (PatientRecord) record)
						.collect(Collectors.toList());
	}
	
	public List<OrderRecord> getOrderRecords(PatientRecord patientRecord) {
		return getRecords(patientRecord, OrderRecord.class);
	}
	
	public List<ResultRecord> getResultRecords(OrderRecord orderRecord) {
		return getRecords(orderRecord, ResultRecord.class);
	}
	
	public List<OBXRecord> getOBXRecords() {
		return records	.stream()
						.filter(record -> record instanceof OBXRecord)
						.map(record -> (OBXRecord) record)
						.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "LIS2A2_ORU_Msg{" +
				"records=" + records +
				'}';
	}
}