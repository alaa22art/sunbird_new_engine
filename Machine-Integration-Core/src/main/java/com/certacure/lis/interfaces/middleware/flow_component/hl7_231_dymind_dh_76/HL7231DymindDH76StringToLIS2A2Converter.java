package com.certacure.lis.interfaces.middleware.flow_component.hl7_231_dymind_dh_76;

import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.CR;

import java.util.ArrayList;
import java.util.List;

import com.certacure.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionType;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MachineQuery;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.flow_component.astm_to_string.RECORD_TYPE;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryAstmMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultAstmMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HeaderRecordAstm;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBXRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.QueryRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ResultRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.HeaderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.OrderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.PatientASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.QueryASTMRecord;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageTransactionType;
import com.certacure.lis.interfaces.service.LkpService;
import com.certacure.lis.interfaces.service.MachineQueryService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.MessageTransactionService;
import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.common.util.SpringUtil;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class HL7231DymindDH76StringToLIS2A2Converter extends FlowComponent<RecipientConf> {

	private MessageTransactionService messageTransactionService;
	private MachineService machineService;
	private LkpService lkpService;
	private MessageTransaction messageTransaction;
	private LkpMessageTransactionDirection lkpMessageTransactionDirection;
	private LkpMessageTransactionType lkpMessageTransactionType;
	private String sampleBarcode = "No Sample Number";
	private HeaderASTMRecord globalHeaderRecord = null;

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder	.match(String.class, this::convertAndForward)

								.build();
	}

	class MessageType {

		Class<? extends LIS2A2Msg> refMsgType;
	}

	/*
	 * @Override public void init() { initiateObjects();
	 * 
	 * }
	 */

	private void initiateObjects() {
		messageTransactionService = (MessageTransactionService) SpringUtil.getBean("MessageTransactionService");
		machineService = (MachineService) SpringUtil.getBean("MachineService");
		lkpService = (LkpService) SpringUtil.getBean("LkpService");
		messageTransaction = new MessageTransaction();
		lkpMessageTransactionType = null;
		lkpMessageTransactionDirection = null;
	}

	private void convertAndForward(String msgAsString) {

		log.info(
				"\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
		log.info(msgAsString);
		log.info(
				"\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");

		initiateObjects();

		// insert arrive message into transaction messages

		Machine machine = getMachine();
		machine = getMachineInfoByPath();

		setLkpMessageTransactionDirection(MessageDirection.IN);

		MessageType messageType = new MessageType();

		List<LIS2A2Msg>[] arrList2A2Msg = processASTMMessage(msgAsString, messageType);

		if ((arrList2A2Msg != null) && (arrList2A2Msg.length > 0)) {
			for (int index = 0; index < arrList2A2Msg.length; index++) {
				LIS2A2Msg lis2A2Msg = arrList2A2Msg[index].get(0);
				// Received Query String Message Type
				if (lis2A2Msg instanceof LIS2A2QueryAstmMsg) {
					LIS2A2QueryAstmMsg lisQueryMsg;
					lisQueryMsg = (LIS2A2QueryAstmMsg) lis2A2Msg;

					setLkpMessageTransactionType(MessageTransactionType.QUERY_RECEIVED);

					for (HeaderASTMRecord headerRecord : lisQueryMsg.getHeaderRecords()) {
						for (QueryASTMRecord queryRecord : lisQueryMsg.getQueryRecords()) {
							log.debug("Finding Q records for " + queryRecord.asString());
							globalHeaderRecord = headerRecord;

							sampleBarcode = queryRecord.getSpecimenId(4).toString();
							createNewMessageTransaction(sampleBarcode);
						}
					}

					messageTransaction = createMessageTransaction(msgAsString, messageTransactionService, machine,
							messageTransaction, lkpMessageTransactionDirection, lkpMessageTransactionType,
							sampleBarcode, globalHeaderRecord);

					lisQueryMsg.setMessageTransactionID(messageTransaction.getRid().longValue());
					storeQueryMsg(lis2A2Msg.toString(), globalHeaderRecord, sampleBarcode, messageTransaction);

					conf.recipient.tell(lisQueryMsg, self());

				}

				if (lis2A2Msg instanceof LIS2A2ResultMsg) {
					LIS2A2ResultAstmMsg LIS2A2ResultMsg;
					LIS2A2ResultMsg = (LIS2A2ResultAstmMsg) lis2A2Msg;
					LIS2A2ResultMsg.getHeaderASTMRecords();

					setLkpMessageTransactionType(MessageTransactionType.RESULT_RECEIVED);

					for (HeaderASTMRecord headerRecord : LIS2A2ResultMsg.getHeaderASTMRecords()) {
						for (PatientASTMRecord patientRecord : LIS2A2ResultMsg.getPatientASTMRecords()) {
							log.debug("Finding O records for " + patientRecord.asString());
							globalHeaderRecord = headerRecord;
							for (OrderASTMRecord orderRecord : LIS2A2ResultMsg.getOrderRecords(patientRecord)) {
								
								sampleBarcode = orderRecord.getSpecimenIds(3, 4).toString().trim();
								createNewMessageTransaction(sampleBarcode);
								messageTransaction = createMessageTransaction(msgAsString,
										messageTransactionService, machine, messageTransaction,
										lkpMessageTransactionDirection, lkpMessageTransactionType, sampleBarcode,
										globalHeaderRecord);
								LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());
								conf.recipient.tell(lis2A2Msg, self());
							}
						}
					}
				}
			}
		}
	}

	private Machine getMachine() {
		return machineService.getMachineByActorPath(getContext().parent().toString());
	}

	private void createNewMessageTransaction(String sampleBarcode) {
		messageTransaction = new MessageTransaction();
		messageTransaction.setBarcode(sampleBarcode);
	}

	private void setLkpMessageTransactionType(MessageTransactionType type) {
		lkpMessageTransactionType = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", type.getValue(), FilterOperator.eq)),
				LkpMessageTransactionType.class);
	}

	private void setLkpMessageTransactionDirection(MessageDirection dir) {
		lkpMessageTransactionDirection = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", dir.getValue(), FilterOperator.eq)),
				LkpMessageTransactionDirection.class);
	}

	private MessageTransaction createMessageTransaction(String msgAsString,
			MessageTransactionService messageTransactionService, Machine machine, MessageTransaction messageTransaction,
			LkpMessageTransactionDirection msgDirection, LkpMessageTransactionType msgTransactionType,
			String sampleBarcode, HeaderRecord globalHeaderRecord) {
		messageTransaction.setMachine(machine);
		messageTransaction.setNotes("Inbound Message Stored");
		messageTransaction.setMessageType(msgTransactionType);
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(msgDirection);
		messageTransaction.setMessageBody(msgAsString);
		//messageTransaction.setIsProcessed(false);
		messageTransaction.setIsSuccess(false);
		messageTransaction = messageTransactionService.add(messageTransaction);
		return messageTransaction;
	}
	
	
	private MessageTransaction createMessageTransaction(String msgAsString,
			MessageTransactionService messageTransactionService, Machine machine, MessageTransaction messageTransaction,
			LkpMessageTransactionDirection msgDirection, LkpMessageTransactionType msgTransactionType,
			String sampleBarcode, HeaderASTMRecord globalHeaderRecord) {
		messageTransaction.setMachine(machine);
		messageTransaction.setNotes("Inbound Message Stored");
		messageTransaction.setMessageType(msgTransactionType);
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(msgDirection);
		messageTransaction.setMessageBody(msgAsString);
		//messageTransaction.setIsProcessed(false);
		messageTransaction.setIsSuccess(false);
		messageTransaction = messageTransactionService.add(messageTransaction);
		return messageTransaction;
	}

	public Machine getMachineInfoByPath() {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		return machine;
	}

	private List<LIS2A2Msg>[] processASTMMessage(String msgAsString, MessageType msgType) {
		List<LIS2A2Record> records = null;
		List<LIS2A2Msg>[] arrList2A2Msg = null;

		try {

			records = new ArrayList<>();
			String msgAsString2 = msgAsString;
			
			msgAsString2 = msgAsString2.replace("OBR", "O").toString();
			msgAsString2 = msgAsString2.replace("OBX", "R").toString();
			String[] recordLines = msgAsString2.split("(?<=" + CR + ")", 0);

			records = new ArrayList<LIS2A2Record>();
			for (int index = 0; index < recordLines.length; index++) {

				log.debug("Converting String '" + recordLines[index] + "' into LIS2A2Record");

				if (recordLines[index].equals("]") || recordLines[index].equals("\r") || recordLines[index].equals("\n")
						|| (recordLines[index].length() == 0))
					continue;

				if (LIS2A2Record.fromString(recordLines[index]) != null) {
					LIS2A2Record lis2a2Record = LIS2A2Record.fromString(recordLines[index]);
					log.debug("Conversion successful, lis2a2Record.toString() = " + lis2a2Record.toString());
					records.add(lis2a2Record);
				} else {
					continue;
				}

			}

			arrList2A2Msg = new ArrayList[1];
			

			msgType.refMsgType = getMsgType(records);

			if (msgType.refMsgType == null) {
				return null;
			}

			/*
			 * if (msgType.refMsgType.equals(LIS2A2OrderMsg.class)) { arrList2A2Msg[index] =
			 * new ArrayList<LIS2A2Msg>(); arrList2A2Msg[index].add(new
			 * LIS2A2OrderMsg(records)); }
			 */

			else if (msgType.refMsgType.equals(LIS2A2ResultMsg.class)) {
				arrList2A2Msg[0] = new ArrayList<LIS2A2Msg>();
				arrList2A2Msg[0].add(new LIS2A2ResultMsg(records));
			} else if (msgType.refMsgType.equals(LIS2A2QueryMsg.class)) {
				arrList2A2Msg[0] = new ArrayList<LIS2A2Msg>();
				arrList2A2Msg[0].add(new LIS2A2QueryMsg(records));
			}

		} catch (NullPointerException ex) {
			log.error("Unexpected message type - " + msgType + "for message text : " + msgAsString);
		}

		return arrList2A2Msg;
	}

	
	
	public Class<? extends LIS2A2Msg> getMsgType(List<LIS2A2Record> records) {
		log.debug("Detecting message type");
		Class<? extends LIS2A2Msg> msgType = null;
		for (LIS2A2Record record : records)
			if (record instanceof OrderRecord)
				msgType = LIS2A2OrderMsg.class;
			else if (record instanceof QueryRecord)
				msgType = LIS2A2QueryMsg.class;
			else if (record instanceof ResultRecord)
				msgType = LIS2A2ResultMsg.class;
			else if (record instanceof OBXRecord)
				msgType = LIS2A2ResultMsg.class;

		return msgType;
	}

	public Class<? extends LIS2A2Msg> getMsgType(LIS2A2Record record) {
		log.debug("Detecting message type");
		Class<? extends LIS2A2Msg> msgType = null;
		record.getFieldValue(2);
		if (record instanceof OrderRecord)
			msgType = LIS2A2OrderMsg.class;
		else if (record instanceof QueryRecord)
			msgType = LIS2A2QueryMsg.class;
		else if (record instanceof ResultRecord)
			msgType = LIS2A2ResultMsg.class;
		else if (record instanceof OBXRecord)
			msgType = LIS2A2ResultMsg.class;

		return msgType;
	}

	public RECORD_TYPE getRecordType(LIS2A2Record record) {
		log.debug("Detecting message type");
		RECORD_TYPE recType = null;

		if (record instanceof OrderRecord)
			recType = RECORD_TYPE.ORDER;
		else if (record instanceof QueryRecord)
			recType = RECORD_TYPE.QUERY;
		else if (record instanceof ResultRecord)
			recType = RECORD_TYPE.RESULT;

		return recType;
	}
	
	private void storeQueryMsg(String lIS2A2QueryMsg, HeaderASTMRecord headerRecord, String specimenId,
			MessageTransaction messageTransaction) {
		Machine machine = getMachineInfoByPath();

		
		MachineQuery machineQuery = new MachineQuery();
		machineQuery.setMachineName(machine.getName());
		machineQuery.setMessageTransaction(messageTransaction);
		machineQuery.setSampleNo(specimenId);
		machineQuery.setMachine(machine);
		MachineQueryService mQueryService = (MachineQueryService) SpringUtil.getBean("MachineQueryService");
		mQueryService.addQuery(machineQuery);
	}


	private void storeQueryMsg(String lIS2A2QueryMsg, HeaderRecord headerRecord, String specimenId,
			MessageTransaction messageTransaction) {
		Machine machine = getMachineInfoByPath();

		
		MachineQuery machineQuery = new MachineQuery();
		machineQuery.setMachineName(machine.getName());
		machineQuery.setMessageTransaction(messageTransaction);
		machineQuery.setSampleNo(specimenId);
		machineQuery.setMachine(machine);
		MachineQueryService mQueryService = (MachineQueryService) SpringUtil.getBean("MachineQueryService");
		mQueryService.addQuery(machineQuery);
	}
}