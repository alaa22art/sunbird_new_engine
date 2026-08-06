package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.Visit2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_DiagnosisRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_EventTypeRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Visit1Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Visit2Record;

public class LIS2A2_ADT_Msg extends LIS2A2Msg {

	public LIS2A2_ADT_Msg() {
		super(new ArrayList<>());
	}

	public LIS2A2_ADT_Msg(List<LIS2A2Record> records) {
		super(records);
	}

	public List<HL7_V24_HeaderRecord> getHeaderRecords() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V24_HeaderRecord)
						.map(record -> (HL7_V24_HeaderRecord) record)
						.collect(Collectors.toList());
	}
	
	public List<HL7_V24_HeaderRecord> getHL7v24HeaderRecords() {
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
	
	
	public List<HL7_V24_PatientRecord> getHL7v24PatientRecords() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V24_PatientRecord)
						.map(record -> (HL7_V24_PatientRecord) record)
						.collect(Collectors.toList());
	}
	
	public List<HL7_V24_DiagnosisRecord> getHL7v24DiagnosisRecords() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V24_DiagnosisRecord)
						.map(record -> (HL7_V24_DiagnosisRecord) record)
						.collect(Collectors.toList());
	}


	public List<HL7_V24_EventTypeRecord> getEventTypeRecords() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V24_EventTypeRecord)
						.map(record -> (HL7_V24_EventTypeRecord) record)
						.collect(Collectors.toList());
	}
	
	public List<HL7_V24_EventTypeRecord> getHL7v24EventTypeRecords() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V24_EventTypeRecord)
						.map(record -> (HL7_V24_EventTypeRecord) record)
						.collect(Collectors.toList());
	}
	
	
	

	public List<HL7_V24_Visit1Record> getVisit1Records() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V24_Visit1Record)
						.map(record -> (HL7_V24_Visit1Record) record)
						.collect(Collectors.toList());
	}
	
	
	public List<HL7_V24_Visit1Record> getHL7v24Visit1Records() {
		return records	.stream()
						.filter(record -> record instanceof HL7_V24_Visit1Record)
						.map(record -> (HL7_V24_Visit1Record) record)
						.collect(Collectors.toList());
	}
	
	public List<Visit2Record> getVisit2Records() {
		return records	.stream()
						.filter(record -> record instanceof Visit2Record)
						.map(record -> (Visit2Record) record)
						.collect(Collectors.toList());
	}
	
	public List<HL7_V24_Visit2Record> getHL7v24Visit2Records() {
		return records	.stream()
				.filter(record -> record instanceof HL7_V24_Visit2Record)
				.map(record -> (HL7_V24_Visit2Record) record)
				.collect(Collectors.toList());
	}

	public LIS2A2_ADT_Msg addRecord(LIS2A2Record record) {
		List<LIS2A2Record> newRecords = new ArrayList<>(this.records);
		newRecords.add(record);
		return new LIS2A2_ADT_Msg(newRecords);
	}

	@Override
	public String toString() {
		return "LIS2A2_ADT_Msg{" +
				"records=" + records +
				'}';
	}

	
}