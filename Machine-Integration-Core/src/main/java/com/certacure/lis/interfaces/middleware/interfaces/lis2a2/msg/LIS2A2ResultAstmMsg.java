package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBXRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ResultRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.HeaderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.OrderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.PatientASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.ResultASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;

public class LIS2A2ResultAstmMsg extends LIS2A2Msg {

	private long messageTransactionID;

	public long getMessageTransactionID() {
		return messageTransactionID;
	}

	public void setMessageTransactionID(long messageTransactionID) {
		this.messageTransactionID = messageTransactionID;
	}

	public LIS2A2ResultAstmMsg(List<LIS2A2Record> records) {
		super(records);
	}

	public LIS2A2ResultAstmMsg(List<LIS2A2Record> records, long TransMsgID) {
		super(records);
		messageTransactionID = TransMsgID;
	}

	public List<HeaderASTMRecord> getHeaderASTMRecords() {
		return records	.stream()
						.filter(record -> record instanceof HeaderASTMRecord)
						.map(record -> (HeaderASTMRecord) record)
						.collect(Collectors.toList());
	}

	public List<PatientASTMRecord> getPatientASTMRecords() {
		return records	.stream()
						.filter(record -> record instanceof PatientASTMRecord)
						.map(record -> (PatientASTMRecord) record)
						.collect(Collectors.toList());
	}

	public List<OrderASTMRecord> getOrderRecords(PatientASTMRecord patientRecord) {
		return getRecords(patientRecord, OrderASTMRecord.class);
	}

	public List<OBRRecord> getOBRRecords(PatientASTMRecord patientRecord) {
		return getRecords(patientRecord, OBRRecord.class);
	}

	public List<ResultRecord> getResultRecords(OrderRecord orderRecord) {
		return getRecords(orderRecord, ResultRecord.class);
	}
	
	public List<ResultASTMRecord> getResultRecords(OrderASTMRecord orderRecord) {
		return getRecords(orderRecord, ResultASTMRecord.class);
	}

	public List<OBXRecord> getOBXRecords(OBRRecord obxRecord) {
		return getRecords(obxRecord, OBXRecord.class);
	}

	@Override
	public String toString() {
		return "LIS2A2ResultMsg{" +
				"records=" + records +
				'}';
	}

	
}