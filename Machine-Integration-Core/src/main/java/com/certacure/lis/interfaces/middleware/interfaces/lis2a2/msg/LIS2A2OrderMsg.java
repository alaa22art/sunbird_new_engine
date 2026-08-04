package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;

public class LIS2A2OrderMsg extends LIS2A2Msg {

	public LIS2A2OrderMsg() {
		super(new ArrayList<>());
	}

	public LIS2A2OrderMsg(List<LIS2A2Record> records) {
		super(records);
	}

	public List<HL7_V24_HeaderRecord> getHeaderRecords() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V24_HeaderRecord)
						.map(record -> (HL7_V24_HeaderRecord) record)
						.collect(Collectors.toList());
	}

	public List<PatientRecord> getPatientRecords() {
		return records	.stream()
						.filter(record -> record instanceof PatientRecord)
						.map(record -> (PatientRecord) record)
						.collect(Collectors.toList());
	}

	public List<OrderRecord> getOrderRecords() {
		return records	.stream()
				.filter(record -> record instanceof OrderRecord)
				.map(record -> (OrderRecord) record)
				.collect(Collectors.toList());
	}
	
	public List<OrderRecord> getOrderRecords(PatientRecord patientRecord) {
		return getRecords(patientRecord, OrderRecord.class);
	}

	public List<OBRRecord> getOBRRecords(PatientRecord patientRecord) {
		return getRecords(patientRecord, OBRRecord.class);
	}

	public LIS2A2OrderMsg addRecord(LIS2A2Record record) {
		List<LIS2A2Record> newRecords = new ArrayList<>(this.records);
		newRecords.add(record);
		return new LIS2A2OrderMsg(newRecords);
	}

	@Override
	public String toString() {
		return "LIS2A2OrderMsg{" +
				"records=" + records +
				'}';
	}
}