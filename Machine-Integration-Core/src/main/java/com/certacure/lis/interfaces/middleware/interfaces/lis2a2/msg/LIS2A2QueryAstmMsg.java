package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import java.util.List;
import java.util.stream.Collectors;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.QueryRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.HeaderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.QueryASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;

public class LIS2A2QueryAstmMsg extends LIS2A2Msg {

	private long messageTransactionID;

	public LIS2A2QueryAstmMsg(List<LIS2A2Record> records) {
		super(records);
	}

	public LIS2A2QueryAstmMsg(List<LIS2A2Record> records, long messageTransactionID) {
		super(records);
		this.messageTransactionID = messageTransactionID;
	}

	public long getMessageTransactionID() {
		return messageTransactionID;
	}

	public void setMessageTransactionID(long messageTransactionID) {
		this.messageTransactionID = messageTransactionID;
	}

	public List<HeaderASTMRecord> getHeaderRecords() {
		return records	.stream()
						.filter(record -> record instanceof HeaderASTMRecord)
						.map(record -> (HeaderASTMRecord) record)
						.collect(Collectors.toList());
	}

	public List<QueryASTMRecord> getQueryRecords() {
		return records	.stream()
						.filter(record -> record instanceof QueryASTMRecord)
						.map(record -> (QueryASTMRecord) record)
						.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "LIS2A2QueryMsg{" +
				"records=" + records +
				"messageTransactionID=" + messageTransactionID +
				'}';
	}
}