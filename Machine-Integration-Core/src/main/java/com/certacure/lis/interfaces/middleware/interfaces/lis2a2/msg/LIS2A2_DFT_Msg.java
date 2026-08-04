package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBXRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_CommonOrder;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_EventTypeRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Visit1Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_v24_FinancialTransaction;

public class LIS2A2_DFT_Msg extends LIS2A2Msg {

    public LIS2A2_DFT_Msg(List<LIS2A2Record> records) {
        super(records);
        // TODO Auto-generated constructor stub
    }

    public LIS2A2_DFT_Msg() {
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

    public List<HL7_V24_EventTypeRecord> getEventTypeRecords() {
        return records.stream()
            .filter(record -> record instanceof HL7_V24_EventTypeRecord)
            .map(record -> (HL7_V24_EventTypeRecord) record)
            .collect(Collectors.toList());
    }

    public List<HL7_V24_EventTypeRecord> getHL7v24EventTypeRecords() {
        return records.stream()
            .filter(record -> record instanceof HL7_V24_EventTypeRecord)
            .map(record -> (HL7_V24_EventTypeRecord) record)
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
    
    public List<HL7_v24_FinancialTransaction> getHL7v24FinancialTransactionRecords() {
        return records.stream()
            .filter(record -> record instanceof HL7_v24_FinancialTransaction)
            .map(record -> (HL7_v24_FinancialTransaction) record)
            .collect(Collectors.toList());
    }
    //HL7_V24_CommonOrder
    
    public List<HL7_V24_CommonOrder> getHL7_V24_CommonOrderRecords() {
        return records.stream()
            .filter(record -> record instanceof HL7_V24_CommonOrder)
            .map(record -> (HL7_V24_CommonOrder) record)
            .collect(Collectors.toList());
    }
///OBRRecord
    public List<OBRRecord> getObservationRequestRecords() {
        return records.stream()
            .filter(record -> record instanceof OBRRecord)
            .map(record -> (OBRRecord) record)
            .collect(Collectors.toList());
    }
    
    
  /// ObservationResultRecord
    public List<OBXRecord> getObservationResultRecord() {
        return records.stream()
            .filter(record -> record instanceof OBXRecord)
            .map(record -> (OBXRecord) record)
            .collect(Collectors.toList());
    }
    
    public String toString() {
        return "LIS2A2_DFT_Msg{" +
            "records=" + records +
            '}';
    }

}
