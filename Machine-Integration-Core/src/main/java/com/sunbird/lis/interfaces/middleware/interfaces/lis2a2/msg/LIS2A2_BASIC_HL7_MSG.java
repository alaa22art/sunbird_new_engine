package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.Visit2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_EventTypeRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Visit1Record;

public class LIS2A2_BASIC_HL7_MSG extends LIS2A2Msg {

	public LIS2A2_BASIC_HL7_MSG() {
		super(new ArrayList<>());
	}

	public LIS2A2_BASIC_HL7_MSG(List<LIS2A2Record> records) {
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

	public List<HL7_V24_EventTypeRecord> getEventTypeRecords() {
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
	
	public List<Visit2Record> getVisit2Records() {
		return records	.stream()
						.filter(record -> record instanceof Visit2Record)
						.map(record -> (Visit2Record) record)
						.collect(Collectors.toList());
	}

	

	@Override
	public String toString() {
		return "LIS2A2_BASIC_HL7_MSG{" +
				"records=" + records +
				'}';
	}
}