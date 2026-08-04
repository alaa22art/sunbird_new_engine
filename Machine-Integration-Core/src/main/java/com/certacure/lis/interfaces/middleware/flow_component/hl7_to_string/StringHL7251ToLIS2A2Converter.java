package com.certacure.lis.interfaces.middleware.flow_component.hl7_to_string;

import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.CR;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ENQ;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.EOT;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.LF;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.STX;

import java.util.ArrayList;
import java.util.List;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionType;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.enums.Enums.MSG_TYPE;
import com.certacure.lis.interfaces.middleware.flow_component.hl7.attalica.SiemensAttalicaResultData;
import com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPure.PureResultData;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_ACK_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_ADT_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_DFT_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_ORL_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_ORU_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_QBP_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_SIU_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_UNKOWN_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7_V24_SpecimenRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_PatientDemographicQueryRecord;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageSourceType;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageTransactionType;
import com.certacure.lis.interfaces.middleware.util.MachineTypeEnum;
import com.certacure.lis.interfaces.service.DataInboundHL7MessageService;
import com.certacure.lis.interfaces.service.LkpService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.MessageTransactionService;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class StringHL7251ToLIS2A2Converter extends FlowComponent<RecipientConf> {

	private MessageTransactionService messageTransactionService;
	private MachineService machineService;
	private MessageTransaction messageTransaction;
	private httpRequstTransaction httpReqeustTransaction;
	private Machine machine;
	private DataInboundHL7Message inboundHL7Message;
	private httpRequstTransaction reqeustTransaction;

	private DataInboundHL7MessageService inboundHL7MessageService;
	private LkpMessageTransactionType lkpMessageTransactionType;
	private String sampleBarcode = "No Sample Number";
	private LkpMessageTransactionDirection lkpMessageTransactionDirection;
	private MSG_TYPE msg_type;
	private String messageControllID;
	private String patientID;
	private String nationalID;
	private String barcode;
	private LkpService lkpService;
	private List<LIS2A2ResultMsg> lstLIS2A2ResultMsg;

	class MessageType {

		Class<? extends LIS2A2Msg> refMsgType;
	}

	private void initiateObjects() {
		messageTransactionService = (MessageTransactionService) SpringUtil.getBean("MessageTransactionService");
		inboundHL7MessageService = (DataInboundHL7MessageService) SpringUtil.getBean("DataInboundHL7MessageService");
		machineService = (MachineService) SpringUtil.getBean("MachineService");
	    lkpService = (LkpService) SpringUtil.getBean("LkpService");
		machine = machineService.getMachineByActorPath(getContext().parent().toString());
		inboundHL7Message = new DataInboundHL7Message();
		messageTransaction = new MessageTransaction();
		httpReqeustTransaction = new httpRequstTransaction();
		messageTransaction.setIsSuccess(false);
		messageTransaction.setIsSent(false);
		messageTransaction.setIsValidated(false);
		lstLIS2A2ResultMsg = new ArrayList<>();

	}

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
				.match(String.class, this::convertAndForward)
				.match(PureResultData.class, this::convertAndForward)
				.match(SiemensAttalicaResultData.class, this::convertAndForward)
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


	private void convertAndForward(SiemensAttalicaResultData siemensAttalicaResultData)
	{


		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		LIS2A2ResultMsg list2A2Msg = null;
		initiateObjects();

		try {
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(siemensAttalicaResultData.originalInput);
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			// httpReqeustTransaction.setHL7MsgText(msgAsString);
			list2A2Msg = (LIS2A2ResultMsg) stringToLIS2AMsg(siemensAttalicaResultData.originalInput);

			messageTransaction = messageTransactionService.addMessageTransaction(machine, siemensAttalicaResultData.originalInput,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(), false, false, false,
					"Arrive new Message", messageControllID, "", patientID, nationalID, "", barcode);
			// httpReqeustTransaction.setMessageTransaction(messageTransaction);

		} catch (Exception ex) {
			messageTransaction = messageTransactionService.addMessageTransaction(machine, siemensAttalicaResultData.originalInput,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(), MessageSourceType.HL7.getValue(),
					false, false, false, ex.getMessage(), "undefined");
			// httpReqeustTransaction.setMessageTransaction(messageTransaction);

		}
		conf.recipient.tell(list2A2Msg, self());
	}


	private void convertAndForward(PureResultData pureResultData)
	{
		LIS2A2ResultMsg list2A2Msg = null;
		initiateObjects();

		try {
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(pureResultData.originalInput);
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			// httpReqeustTransaction.setHL7MsgText(msgAsString);
			list2A2Msg = (LIS2A2ResultMsg) stringToLIS2AMsg(pureResultData.originalInput);

			// HL7Parser parser = new HL7Parser(msgAsString, "2.3.1");

			// ORU_R01 ORU_R01_MSG = parser.getOrderOMLO33Message();

			// httpReqeustTransaction.setLis2aMsg(list2A2Msg);
			// httpReqeustTransaction.getMessageTransaction().setMachine(machine);
			messageTransaction = messageTransactionService.addMessageTransaction(machine, pureResultData.originalInput,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(), false, false, false,
					"Arrive new Message", messageControllID, "", patientID, nationalID, "", barcode);
			// httpReqeustTransaction.setMessageTransaction(messageTransaction);

		} catch (Exception ex) {
			messageTransaction = messageTransactionService.addMessageTransaction(machine, pureResultData.originalInput,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(), MessageSourceType.HL7.getValue(),
					false, false, false, ex.getMessage(), "undefined");
			// httpReqeustTransaction.setMessageTransaction(messageTransaction);

		}
		conf.recipient.tell(list2A2Msg, self());
	}

/*	private void convertAndForward(String msgAsString) {

		LIS2A2ResultMsg list2A2Msg = null;
		initiateObjects();

		try {
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(msgAsString);
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			// httpReqeustTransaction.setHL7MsgText(msgAsString);
			list2A2Msg = (LIS2A2ResultMsg) stringToLIS2AMsg(msgAsString);

			// HL7Parser parser = new HL7Parser(msgAsString, "2.3.1");

			// ORU_R01 ORU_R01_MSG = parser.getOrderOMLO33Message();

			// httpReqeustTransaction.setLis2aMsg(list2A2Msg);
			// httpReqeustTransaction.getMessageTransaction().setMachine(machine);
			messageTransaction = messageTransactionService.addMessageTransaction(machine, msgAsString,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(), false, false, false,
					"Arrive new Message", messageControllID, "", patientID, nationalID, "", barcode);
			// httpReqeustTransaction.setMessageTransaction(messageTransaction);

		} catch (Exception ex) {
			messageTransaction = messageTransactionService.addMessageTransaction(machine, msgAsString,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(), MessageSourceType.HL7.getValue(),
					false, false, false, ex.getMessage(), "undefined");
			// httpReqeustTransaction.setMessageTransaction(messageTransaction);

		}
		conf.recipient.tell(list2A2Msg, self());

	}*/

	private void setLkpMessageTransactionType(MessageTransactionType type) {
		lkpMessageTransactionType = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", type.getValue(), FilterOperator.eq)),
				LkpMessageTransactionType.class);
	}

	private void createNewMessageTransaction(String sampleBarcode) {
		messageTransaction = new MessageTransaction();
		messageTransaction.setBarcode(sampleBarcode);
		//messageTransaction.setMessageType(lkpMessageTransactionType);
	}



	private void convertAndForward(String msgAsString) {

		initiateObjects();

		try {
			
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(msgAsString);
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");

			LIS2A2Msg list2A2HL7Msg = stringToLIS2Hl7Message(msgAsString);
			MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());

			if (list2A2HL7Msg instanceof LIS2A2ResultMsg) {
				LIS2A2ResultMsg lis2a2HL7ResultMsg;
				lis2a2HL7ResultMsg = (LIS2A2ResultMsg) list2A2HL7Msg;
				setLkpMessageTransactionType(MessageTransactionType.RESULT_RECEIVED);
				setLkpMessageTransactionDirection(MessageDirection.IN);

				for (HL7_V24_HeaderRecord hl7v24HeaderRecord : lis2a2HL7ResultMsg.getHeaderRecords()) {
					for (HL7_V24_SpecimenRecord hl7v24SpecimenRecord : lis2a2HL7ResultMsg.getSpecimenRecord()) {
						//log.debug("Finding O records for " + hl7v24PatientRecord.asString());
						// globalHeaderRecord = hl7v24headerRecord;
							switch (machineType) {

							case SIEMENS_ATELLICA_HL7_251:
								
								sampleBarcode = hl7v24SpecimenRecord.getSpecimenId();
								createNewMessageTransaction(sampleBarcode);
								messageTransaction = createMessageTransaction(msgAsString, messageTransactionService,
										machine, messageTransaction, lkpMessageTransactionDirection,
										lkpMessageTransactionType, sampleBarcode, hl7v24HeaderRecord);
								lis2a2HL7ResultMsg.setMessageTransactionID(messageTransaction.getRid());
								conf.recipient.tell(lis2a2HL7ResultMsg, self());
								break;
							case ROCHE_COBAS_PRO_HL7_251:

								sampleBarcode = hl7v24SpecimenRecord.getSpecimenId(3,1);
								createNewMessageTransaction(sampleBarcode);
								
								messageTransaction = createMessageTransaction(msgAsString, messageTransactionService,
										machine, messageTransaction, lkpMessageTransactionDirection,
										lkpMessageTransactionType, sampleBarcode, hl7v24HeaderRecord);







								lis2a2HL7ResultMsg.setMessageTransactionID(messageTransaction.getRid());
								conf.recipient.tell(lis2a2HL7ResultMsg, self());
								break;
								//lstLIS2A2ResultMsg.add(lis2a2HL7ResultMsg);
							case ROCHE_COBAS_PURE:
								break;
							}


					}
				}

			}else if (list2A2HL7Msg instanceof LIS2A2_ACK_Msg)
			{
				LIS2A2_ACK_Msg lis2a2HL7ACKMsg;
				lis2a2HL7ACKMsg = (LIS2A2_ACK_Msg) list2A2HL7Msg;
				setLkpMessageTransactionType(MessageTransactionType.ACK_RECEIVED);
				setLkpMessageTransactionDirection(MessageDirection.IN);
				
				for (HL7_V24_HeaderRecord hl7v24HeaderRecord : lis2a2HL7ACKMsg.getHeaderRecords()) 
				{
					createNewMessageTransaction("");
					messageTransaction = createMessageTransaction(msgAsString, messageTransactionService,
							machine, messageTransaction, lkpMessageTransactionDirection,
							lkpMessageTransactionType, sampleBarcode, hl7v24HeaderRecord);
					lis2a2HL7ACKMsg.setMessageTransactionID(messageTransaction.getRid());
				}
			}
			else if (list2A2HL7Msg instanceof LIS2A2_ORL_Msg)
			{
				LIS2A2_ORL_Msg lis2a2HL7ORLMsg;
				lis2a2HL7ORLMsg = (LIS2A2_ORL_Msg) list2A2HL7Msg;
				setLkpMessageTransactionType(MessageTransactionType.ORL_LAB_ORDER_RESPONCE);
				setLkpMessageTransactionDirection(MessageDirection.IN);
				
				for (HL7_V24_HeaderRecord hl7v24HeaderRecord : lis2a2HL7ORLMsg.getHeaderRecords()) 
				{
					createNewMessageTransaction("");
					messageTransaction = createMessageTransaction(msgAsString, messageTransactionService,
							machine, messageTransaction, lkpMessageTransactionDirection,
							lkpMessageTransactionType, sampleBarcode, hl7v24HeaderRecord);
					lis2a2HL7ORLMsg.setMessageTransactionID(messageTransaction.getRid());
				}
			}
			else if (list2A2HL7Msg instanceof LIS2A2QueryMsg)
			{
				LIS2A2QueryMsg lis2a2HL7QueryMsg;
				lis2a2HL7QueryMsg = (LIS2A2QueryMsg) list2A2HL7Msg;
				setLkpMessageTransactionType(MessageTransactionType.QUERY_RECEIVED);
				setLkpMessageTransactionDirection(MessageDirection.IN);


				for (HL7_V24_HeaderRecord hl7v24HeaderRecord : lis2a2HL7QueryMsg.getHeaderRecords()) {
					for (HL7_V25_PatientDemographicQueryRecord hl7v25QueryParameterDefinitionRecordRecord : lis2a2HL7QueryMsg.getQueryParametrDefinitionRecord()) {
						//log.debug("Finding O records for " + hl7v24PatientRecord.asString());
						// globalHeaderRecord = hl7v24headerRecord;
							switch (machineType) {

							case SIEMENS_ATELLICA_HL7_251:
								sampleBarcode = hl7v25QueryParameterDefinitionRecordRecord.getSpecimenId();

								createNewMessageTransaction(sampleBarcode);
								messageTransaction = createMessageTransaction(msgAsString, messageTransactionService,
										machine, messageTransaction, lkpMessageTransactionDirection,
										lkpMessageTransactionType, sampleBarcode, hl7v24HeaderRecord);
								lis2a2HL7QueryMsg.setMessageTransactionID(messageTransaction.getRid());
								//lstLIS2A2ResultMsg.add(lis2a2HL7ResultMsg);


						conf.recipient.tell(lis2a2HL7QueryMsg, self());

								break;

							case ROCHE_COBAS_PRO_HL7_251:
								sampleBarcode = hl7v25QueryParameterDefinitionRecordRecord.getSpecimenId(3);


								createNewMessageTransaction(sampleBarcode);
								messageTransaction = createMessageTransaction(msgAsString, messageTransactionService,
										machine, messageTransaction, lkpMessageTransactionDirection,
										lkpMessageTransactionType, sampleBarcode, hl7v24HeaderRecord);







								lis2a2HL7QueryMsg.setMessageTransactionID(messageTransaction.getRid());
								//lstLIS2A2ResultMsg.add(lis2a2HL7ResultMsg);


						conf.recipient.tell(lis2a2HL7QueryMsg, self());

								break;
							case ROCHE_COBAS_PURE:
								break;
							}


					}
				}


			}
		} catch (Exception ex) {

			messageTransaction = messageTransactionService.addMessageTransaction(machine, msgAsString,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(), MessageSourceType.HL7.getValue(),
					false, false, false, ex.getMessage(), "undefined");
			reqeustTransaction.setMessageTransaction(messageTransaction);



		} finally {

			//LIS2A2ResultMsg[] arrLIS2A2ResultMsg = new LIS2A2ResultMsg[lstLIS2A2ResultMsg.size()];
			//lstLIS2A2ResultMsg.toArray(arrLIS2A2ResultMsg); // fill the array
			//conf.recipient.tell(arrLIS2A2ResultMsg, self());
		}

	}

	private void setLkpMessageTransactionDirection(MessageDirection dir) {
		lkpMessageTransactionDirection = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", dir.getValue(), FilterOperator.eq)),
				LkpMessageTransactionDirection.class);
	}

	private MessageTransaction createMessageTransaction(String msgAsString,
			MessageTransactionService messageTransactionService, Machine machine, MessageTransaction messageTransaction,
			LkpMessageTransactionDirection msgDirection, LkpMessageTransactionType msgTransactionType,
			String sampleBarcode, HL7_V24_HeaderRecord hl7_v24_HeaderRecord) {
		messageTransaction.setMachine(machine);
		messageTransaction.setNotes("Inbound Message Stored");
		messageTransaction.setMessageType(msgTransactionType);
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(msgDirection);
		messageTransaction.setMessageBody(msgAsString);
		messageTransaction.setBarcode(sampleBarcode);
		//messageTransaction.setIsProcessed(false);
		//messageTransaction.setIsSuccess(false);
		//messageTransaction.setIsValidated(false);
		messageTransaction = messageTransactionService.add(messageTransaction);
		return messageTransaction;
	}

	public LIS2A2Msg stringToLIS2Hl7Message(String msgAsString) {
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
				reqeustTransaction.getMessageTransaction().setMessageControlID(messageControllID);
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

		patientID = records.size()>2 ? records.get(2).getFieldValue(3): ""  ;
		nationalID = records.size()>2? records.get(2).getFieldValue(2) : "";

		//reqeustTransaction.getMessageTransaction().setPatientID(patientID);
		//reqeustTransaction.getMessageTransaction().setNationalID(nationalID);

		msgType = getMsgType(records);


		if (msgType.equals(LIS2A2OrderMsg.class))
			return new LIS2A2OrderMsg(records);
		else if (msgType.equals(LIS2A2ResultMsg.class))
			return new LIS2A2ResultMsg(records);
		else if (msgType.equals(LIS2A2_ADT_Msg.class))
			return new LIS2A2_ADT_Msg(records);
		else if (msgType.equals(LIS2A2QueryMsg.class) || msgType.equals(LIS2A2_QBP_Msg.class))
			return new LIS2A2QueryMsg(records);
		else if (msgType.equals(LIS2A2_UNKOWN_Msg.class))
			return new LIS2A2_UNKOWN_Msg(records);
		else if (msgType.equals(LIS2A2_SIU_Msg.class))
			return new LIS2A2_SIU_Msg(records);
		else if (msgType.equals(LIS2A2_DFT_Msg.class))
			return new LIS2A2_DFT_Msg(records);
		else if (msgType.equals(LIS2A2_ACK_Msg.class))
			return new LIS2A2_ACK_Msg(records);
		else if (msgType.equals(LIS2A2_ORL_Msg.class))
			return new LIS2A2_ORL_Msg(records);
		

		else
			throw new RuntimeException("Unexpected message type - " + msgType);
	}



	private void convertAndForward(httpRequstTransaction httpObj) {

		initiateObjects();
		String msgAsString = httpObj.getHL7MsgText();

		try {

			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(msgAsString);
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			// httpReqeustTransaction.setHL7MsgText(msgAsString);
			LIS2A2Msg list2A2Msg = stringToLIS2AMsg(msgAsString);
			// httpReqeustTransaction.setLis2aMsg(list2A2Msg);
			// httpReqeustTransaction.getMessageTransaction().setMachine(machine);
			messageTransaction = messageTransactionService.addMessageTransaction(machine, msgAsString,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(), false, false, false,
					"Arrive new Message", messageControllID, "", patientID, nationalID, "");

			// httpReqeustTransaction.setMessageTransaction(messageTransaction);

			// httpReqeustTransaction.setMessageTransaction(messageTransaction);

		} catch (Exception ex) {
			messageTransaction = messageTransactionService.addMessageTransaction(machine, msgAsString,
					MessageDirection.IN.toString(), MessageSourceType.HL7.toString(), MessageSourceType.HL7.getValue(),
					false, false, false, ex.getMessage(), "undefined");
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

			if (record.length() < 3)
			{
				continue;

			}

			log.debug("Converting String '" + record + "' into LIS2A2Record");
			System.out.print("Converting String '" + record + "' into LIS2A2Record");

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
		// httpReqeustTransaction.getMessageTransaction().setMessageControlID(messageControllID);
		patientID = records.get(2).getFieldValue(3);
		barcode = records.get(3).getFieldValue(3);
		// nationalID = records.get(2).getFieldValue(2);

		// httpReqeustTransaction.getMessageTransaction().setPatientID(patientID);
		// httpReqeustTransaction.getMessageTransaction().setNationalID(nationalID);

		msgType = getMsgType(records);
		if (msgType.equals(LIS2A2OrderMsg.class))
			return new LIS2A2OrderMsg(records);
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
		else if (msgType.equals(LIS2A2_ORU_Msg.class) || (msgType.equals(LIS2A2ResultMsg.class)))
			return new LIS2A2ResultMsg(records);
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
			case ORU:
				msgType = LIS2A2_ORU_Msg.class;
				break;
			case OUL:
				msgType = LIS2A2ResultMsg.class;
				break;
			case QBP:
				msgType = LIS2A2_QBP_Msg.class;
				break;
			case ACK:
				msgType = LIS2A2_ACK_Msg.class;
				break;
			case ORL:
				msgType = LIS2A2_ORL_Msg.class;
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