package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Appointment;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Visit1Record;

public class LIS2A2_SIU_Msg extends LIS2A2Msg {

    public LIS2A2_SIU_Msg(List<LIS2A2Record> records) {
        super(records);
        // TODO Auto-generated constructor stub
    }

    public LIS2A2_SIU_Msg() {
        super(new ArrayList<>());
    }

    public List<HL7_V24_HeaderRecord> getHeaderRecords() {
        return records.stream()
            .filter(record -> record instanceof HL7_V24_HeaderRecord)
            .map(record -> (HL7_V24_HeaderRecord) record)
            .collect(Collectors.toList());
    }

    public List<HL7_V24_HeaderRecord> getHL7v24HeaderRecords() {
        return records.stream()
            .filter(record -> record instanceof HL7_V24_HeaderRecord)
            .map(record -> (HL7_V24_HeaderRecord) record)
            .collect(Collectors.toList());
    }

    public List<HL7_V24_Appointment> getSchedulingActivity() {
        return records.stream()
            .filter(record -> record instanceof HL7_V24_Appointment)
            .map(record -> (HL7_V24_Appointment) record)
            .collect(Collectors.toList());
    }

    public List<PatientRecord> getPatientRecords() {
        return records.stream()
            .filter(record -> record instanceof PatientRecord)
            .map(record -> (PatientRecord) record)
            .collect(Collectors.toList());
    }

    public List<HL7_V24_PatientRecord> getHL7v24PatientRecords() {
        return records.stream()
            .filter(record -> record instanceof HL7_V24_PatientRecord)
            .map(record -> (HL7_V24_PatientRecord) record)
            .collect(Collectors.toList());
    }

    public List<HL7_V24_Visit1Record> getVisit1Records() {
        return records.stream()
            .filter(record -> record instanceof HL7_V24_Visit1Record)
            .map(record -> (HL7_V24_Visit1Record) record)
            .collect(Collectors.toList());
    }

    public List<HL7_V24_Visit1Record> getHL7v24Visit1Records() {
        return records.stream()
            .filter(record -> record instanceof HL7_V24_Visit1Record)
            .map(record -> (HL7_V24_Visit1Record) record)
            .collect(Collectors.toList());
    }
}
