package com.certacure.lis.interfaces.middleware.flow_component.hl7_to_string;

import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.CR;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ENQ;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.EOT;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.LF;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.STX;

import java.util.ArrayList;
import java.util.List;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.enums.Enums.MSG_TYPE;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_ADT_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_DFT_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_QBP_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_RCP_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_SIU_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_UNKOWN_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageSourceType;
import com.certacure.lis.interfaces.middleware.util.MachineTypeEnum;
import com.certacure.lis.interfaces.service.DataInboundHL7MessageService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.MessageTransactionService;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class HL725StringToLIS2A2Converter extends FlowComponent<RecipientConf> {

	private MessageTransactionService messageTransactionService;
	private MachineService machineService;
	private MessageTransaction messageTransaction;
	private httpRequstTransaction httpReqeustTransaction;
	private Machine machine;
	private DataInboundHL7Message inboundHL7Message;
	private DataInboundHL7MessageService inboundHL7MessageService;
	private MSG_TYPE msg_type;
	private String messageControllID;
	private String patientID;
	private String nationalID;

	class MessageType {

		Class<? extends LIS2A2Msg> refMsgType;
	}

	private void initiateObjects() {
		messageTransactionService = (MessageTransactionService) SpringUtil.getBean("MessageTransactionService");
		inboundHL7MessageService = (DataInboundHL7MessageService) SpringUtil.getBean("DataInboundHL7MessageService");
		machineService = (MachineService) SpringUtil.getBean("MachineService");
		machine = machineService.getMachineByActorPath(getContext().parent().toString());
		inboundHL7Message = new DataInboundHL7Message();
		messageTransaction = new MessageTransaction();
		httpReqeustTransaction = new httpRequstTransaction();
		messageTransaction.setIsSuccess(false);
		messageTransaction.setIsSent(false);
		messageTransaction.setIsValidated(false);

	}

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder.match(String.class, this::convertAndForward)
				.match(httpRequstTransaction.class, this::convertAndForward)

				.build();
	}

	public LIS2A2Msg stringMsgToAstmMsg(String msgAsString) {
		List<LIS2A2Record> records = new ArrayList<>();
		String[] recordLines = msgAsString.split("(?<=" + CR + ")");
		for (String record : recordLines) {
			log.debug("Converting String '" + record + "' into LIS2A2Record");
			LIS2A2Record lis2a2Record = LIS2A2Record.fromString(record);
			log.debug("Conversion successful, lis2a2Record.toString() = " + lis2a2Record.toString());
			records.add(lis2a2Record);
		}

		Class<? extends LIS2A2Msg> msgType = getMsgType(records);
		if (msgType.equals(LIS2A2OrderMsg.class))
			return new LIS2A2OrderMsg(records);
		else if (msgType.equals(LIS2A2ResultMsg.class))
			return new LIS2A2ResultMsg(records);
		else if (msgType.equals(LIS2A2QueryMsg.class))
			return new LIS2A2QueryMsg(records);
		else
			throw new RuntimeException("Unexpected message type - " + msgType);
	}

	private void convertAndForward(String msgAsString) {

		initiateObjects();

		try {
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(msgAsString);
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			httpReqeustTransaction.setHL7MsgText(msgAsString);
			LIS2A2Msg list2A2Msg = stringToLIS2AMsg(msgAsString);
			httpReqeustTransaction.setLis2aMsg(list2A2Msg);
			httpReqeustTransaction.getMessageTransaction().setMachine(machine);
			
			
			
			messageTransaction = messageTransactionService.addMessageTransaction(machine, msgAsString,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(), false, false, false,
					"Arrive new Message", messageControllID, "" , patientID , nationalID,"");
			httpReqeustTransaction.setMessageTransaction(messageTransaction);

		} catch (Exception ex) {
			messageTransaction = messageTransactionService.addMessageTransaction(machine, msgAsString,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(),MessageSourceType.HL7.getValue(), false, false, false,
					ex.getMessage(), "undefined");
			httpReqeustTransaction.setMessageTransaction(messageTransaction);

		} finally {
			conf.recipient.tell(httpReqeustTransaction, self());
		}

	}

	private void convertAndForward(httpRequstTransaction httpObj) {

		initiateObjects();
		String msgAsString = httpObj.getHL7MsgText();

		try {

			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(msgAsString);
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			httpReqeustTransaction.setHL7MsgText(msgAsString);
			LIS2A2Msg list2A2Msg = stringToLIS2AMsg(msgAsString);
			httpReqeustTransaction.setLis2aMsg(list2A2Msg);
			httpReqeustTransaction.getMessageTransaction().setMachine(machine);
			messageTransaction = messageTransactionService.addMessageTransaction(machine, msgAsString,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(), false, false, false,
					"Arrive new Message", messageControllID, "" , patientID , nationalID,"");
			
			httpReqeustTransaction.setMessageTransaction(messageTransaction);
			
			
			httpReqeustTransaction.setMessageTransaction(messageTransaction);

		} catch (Exception ex) {
			messageTransaction = messageTransactionService.addMessageTransaction(machine, msgAsString,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(),MessageSourceType.HL7.getValue(), false, false, false,
					ex.getMessage(), "undefined");
			httpReqeustTransaction.setMessageTransaction(messageTransaction);

		} finally {
			conf.recipient.tell(httpReqeustTransaction, self());
		}

	}

	private void addMessageToInboundTeble(String msgAsString) {
		inboundHL7Message.setBranchId(machine.getBranchId());
		inboundHL7Message.setBranchId(machine.getTenantId());
		inboundHL7Message.setMessageBody(msgAsString);

		inboundHL7MessageService.addInbound(inboundHL7Message);
	}

	public Machine getMachineInfoByPath() {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		return machine;
	}

	public LIS2A2Msg stringToLIS2AMsg(String msgAsString) {
		Class<? extends LIS2A2Msg> msgType;
		List<LIS2A2Record> records = new ArrayList<>();
		String[] recordLines = msgAsString.split("(?<=" + CR + ")");
		for (String record : recordLines) {

			if (record.length() < 3) {
				continue;

			}

			log.debug("Converting String '" + record + "' into LIS2A2Record");
			LIS2A2Record lis2a2Record = LIS2A2Record.fromString(record);

			if (lis2a2Record == null) {

				messageControllID = records.get(0).getFieldValue(9);
				patientID = records.get(2).getFieldValue(3);
				nationalID = records.get(2).getFieldValue(2);
				httpReqeustTransaction.getMessageTransaction().setMessageControlID(messageControllID);
				records.clear();
				break;
			}

			/*
			 * if(lis2a2Record == null) { throw new
			 * RuntimeException("Unexpected message type - Segment " + record); }
			 */

			log.debug("Conversion successful, lis2a2Record.toString() = " + lis2a2Record.toString());
			records.add(lis2a2Record);
		}

		messageControllID = records.get(0).getFieldValue(9);
		httpReqeustTransaction.getMessageTransaction().setMessageControlID(messageControllID);
		patientID = records.get(2).getFieldValue(3);
        nationalID = records.get(2).getFieldValue(2);
        
        httpReqeustTransaction.getMessageTransaction().setPatientID(patientID);
		httpReqeustTransaction.getMessageTransaction().setNationalID(nationalID);


		msgType = getMsgType(records);
		if (msgType.equals(LIS2A2OrderMsg.class))
			return new LIS2A2OrderMsg(records);
		else if (msgType.equals(LIS2A2ResultMsg.class))
			return new LIS2A2ResultMsg(records);
		else if (msgType.equals(LIS2A2_ADT_Msg.class))
			return new LIS2A2_ADT_Msg(records);
		else if (msgType.equals(LIS2A2QueryMsg.class))
			return new LIS2A2QueryMsg(records);
		else if (msgType.equals(LIS2A2_UNKOWN_Msg.class))
			return new LIS2A2_UNKOWN_Msg(records);
		else if (msgType.equals(LIS2A2_SIU_Msg.class))
			return new LIS2A2_SIU_Msg(records);
		else if (msgType.equals(LIS2A2_DFT_Msg.class))
			return new LIS2A2_DFT_Msg(records);
		else if (msgType.equals(LIS2A2_QBP_Msg.class))
			return new LIS2A2_QBP_Msg(records);
		else if (msgType.equals(LIS2A2_RCP_Msg.class))
			return new LIS2A2_RCP_Msg(records);
		else
			throw new RuntimeException("Unexpected message type - " + msgType);
	}

	public List<LIS2A2Msg>[] stringMsgToAstmMsg(String msgAsString, MessageType msgType, Machine machine) {

		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
		switch (machineType) {
		case ABBOTT_ARCHITECT_CI4100:
			return processArchitect4100CIMessage(msgAsString, msgType);

		default:

			return processASTMMessage(msgAsString, msgType);

		}

	}

	private List<LIS2A2Msg>[] processASTMMessage(String msgAsString, MessageType msgType) {
		List<LIS2A2Record> records = null;
		List<LIS2A2Msg>[] arrList2A2Msg = null;

		try {

			records = new ArrayList<>();
			String str = String.valueOf(CR) + String.valueOf(LF);
			String msgAsString2 = msgAsString;
			// = msgAsString.replace(ENQ, '\0').replaceAll(str, String.valueOf(CR));
			msgAsString2 = msgAsString2.replace("OBR", "O").toString();
			msgAsString2 = msgAsString2.replace("OBX", "R").toString();
			// String[] recordLines = msgAsString2.split("(?<=" + CR + ")");
			String[] recordLines = msgAsString2.split("(?<=" + CR + ")", 0);

			// records = new ArrayList();
			// arrList2A2Msg = new ArrayList[lstFinalMessage.size()];

			records = new ArrayList<>();
			for (String recordLine : recordLines) {

				log.debug("Converting String '" + recordLine + "' into LIS2A2Record");

				if (recordLine.equals("]") || recordLine.equals("\r") || recordLine.equals("\n")
						|| (recordLine.length() == 0))
					continue;

				if (LIS2A2Record.fromString(recordLine) != null) {
					LIS2A2Record lis2a2Record = LIS2A2Record.fromString(recordLine);
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
				arrList2A2Msg[0] = new ArrayList<>();
				arrList2A2Msg[0].add(new LIS2A2ResultMsg(records));
			} else if (msgType.refMsgType.equals(LIS2A2QueryMsg.class)) {
				arrList2A2Msg[0] = new ArrayList<>();
				arrList2A2Msg[0].add(new LIS2A2QueryMsg(records));
			}

		} catch (NullPointerException ex) {
			log.error("Unexpected message type - " + msgType + "for message text : " + msgAsString);
		}

		return arrList2A2Msg;
	}

	private List<LIS2A2Msg>[] processArchitect4100CIMessage(String msgAsString, MessageType msgType) {
		List<LIS2A2Record> records = null;
		List<LIS2A2Msg>[] arrList2A2Msg = null;

		try {

			String[] inputMEssage = msgAsString.split("(" + EOT + ")");
			// need to write a function to seperate a large and nessted based on fotter
			// L|[CR]
			final String regexFotter = "(?:(L)\\|(\\d+)\\" + CR + ")";
			List<String> lstFinalMessage = new ArrayList<>();

			for (String element : inputMEssage) {
				String[] inputMEssage2 = element.split(regexFotter);
				System.out.println(inputMEssage2);
				String newMessage = "";

				for (String element2 : inputMEssage2) {
					int indexOFSTX = -1;

					indexOFSTX = element2.indexOf(STX);

					if (indexOFSTX != -1) {
						newMessage = element2.substring(indexOFSTX, element2.length() - 1) + "L|" + CR + LF;
						lstFinalMessage.add(newMessage);
					}

				}

			}

			records = new ArrayList();
			arrList2A2Msg = new ArrayList[lstFinalMessage.size()];

			String str = String.valueOf(CR) + String.valueOf(LF);

			for (int index = 0; index < lstFinalMessage.size(); index++) {
				String msgAsString2 = lstFinalMessage.get(index).replace(ENQ, '\0').replaceAll(str, String.valueOf(CR));
				msgAsString2 = msgAsString2.replace("OBR", "O").toString();
				msgAsString2 = msgAsString2.replace("OBX", "R").toString();
				String[] recordLines = msgAsString2.split("(?<=" + CR + ")");
				// String[] validatedRecordLines = validateRecordLines(recordLines);
				records = new ArrayList<>();
				for (String record : recordLines) {

					log.debug("Converting String '" + record + "' into LIS2A2Record");

					if (record.equals("]") || record.equals("\r") || record.equals("\n"))
						continue;

					if (LIS2A2Record.fromString(record) != null) {
						LIS2A2Record lis2a2Record = LIS2A2Record.fromString(record);
						log.debug("Conversion successful, lis2a2Record.toString() = " + lis2a2Record.toString());
						records.add(lis2a2Record);
					} else {
						continue;
					}

				}

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
					arrList2A2Msg[index] = new ArrayList<>();
					arrList2A2Msg[index].add(new LIS2A2ResultMsg(records));
				} else if (msgType.refMsgType.equals(LIS2A2QueryMsg.class)) {
					arrList2A2Msg[index] = new ArrayList<>();
					arrList2A2Msg[index].add(new LIS2A2QueryMsg(records));
				}

			}

		} catch (NullPointerException ex) {
			log.error("Unexpected message type - " + msgType + "for message text : " + msgAsString);
		}

		return arrList2A2Msg;
	}

	public Class<? extends LIS2A2Msg> getMsgType(List<LIS2A2Record> records) {
		log.debug("Detecting message type");
		Class<? extends LIS2A2Msg> msgType = null;

		try {

			if (records.size() == 0) {
				msgType = LIS2A2_UNKOWN_Msg.class;
			}
			MSG_TYPE messageType = null;

			messageType = MSG_TYPE.valueOf(records.get(0).getComponentValue(8, 1));

			msg_type = messageType;
			System.out.println(msg_type);
			// check message type:
			switch (msg_type) {
			case ADT:
				msgType = LIS2A2_ADT_Msg.class;
				break;
			case SIU:
				msgType = LIS2A2_SIU_Msg.class;
				break;
			case DFT:
				msgType = LIS2A2_DFT_Msg.class;
				break;
			case QBP:
				msgType = LIS2A2_QBP_Msg.class;
				break;
			default:
				msgType = LIS2A2Msg.class;

			}
		} catch (Exception ex) {

			msgType = LIS2A2Msg.class;
		}

		return msgType;
	}

	public static String create() {
		// TODO Auto-generated method stub
		return null;
	}

}