package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.astm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.HeaderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.OrderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



public class LIS2A2OrderASTM extends LIS2A2Msg {

	public LIS2A2OrderASTM() {
		super(new ArrayList<>());
	}

	public LIS2A2OrderASTM(List<LIS2A2Record> records) {
		super(records);
	}

	public List<HeaderASTMRecord> getHeaderRecords() {
		return records	.stream()
						.filter(record -> record instanceof HeaderASTMRecord)
						.map(record -> (HeaderASTMRecord) record)
						.collect(Collectors.toList());
	}

	public List<PatientRecord> getPatientRecords() {
		return records	.stream()
						.filter(record -> record instanceof PatientRecord)
						.map(record -> (PatientRecord) record)
						.collect(Collectors.toList());
	}

	public List<OrderASTMRecord> getOrderRecords(PatientRecord patientRecord) {
		return getRecords(patientRecord, OrderASTMRecord.class);
	}
	
	public List<OrderASTMRecord>  getOrderRecords() {
		return records	.stream()
				.filter(record -> record instanceof OrderASTMRecord)
				.map(record -> (OrderASTMRecord) record)
				.collect(Collectors.toList());
	}

	public List<OBRRecord> getOBRRecords(PatientRecord patientRecord) {
		return getRecords(patientRecord, OBRRecord.class);
	}

	public LIS2A2OrderASTM addRecord(LIS2A2Record record) {
		List<LIS2A2Record> newRecords = new ArrayList<>(this.records);
		newRecords.add(record);
		return new LIS2A2OrderASTM(newRecords);
	}

	@Override
	public String toString() {
		return "LIS2A2OrderASTM{" +
				"records=" + records +
				'}';
	}
}