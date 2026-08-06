package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.Visit2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_EventTypeRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Visit1Record;

public class LIS2A2_UNKOWN_Msg extends LIS2A2Msg {

	public LIS2A2_UNKOWN_Msg() {
		super(new ArrayList<>());
	}

	public LIS2A2_UNKOWN_Msg(List<LIS2A2Record> records) {
		super(records);
	}

	
	public LIS2A2_UNKOWN_Msg addRecord(LIS2A2Record record) {
		List<LIS2A2Record> newRecords = new ArrayList<>(this.records);
		newRecords.add(record);
		return new LIS2A2_UNKOWN_Msg(newRecords);
	}

	@Override
	public String toString() {
		return "LIS2A2_UNKOWN__Msg{" +
				"records=" + records +
				'}';
	}
}