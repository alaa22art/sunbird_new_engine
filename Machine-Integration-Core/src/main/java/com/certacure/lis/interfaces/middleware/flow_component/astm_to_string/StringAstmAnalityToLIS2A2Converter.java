package com.certacure.lis.interfaces.middleware.flow_component.astm_to_string;

import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.CR;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ENQ;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.EOT;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.LF;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.STX;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ETX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionType;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MachineQuery;
import com.certacure.lis.interfaces.entities.MachineResult;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.middleware.core.AccessResult;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.enums.Enums.MSG_TYPE;
import com.certacure.lis.interfaces.middleware.flow_component.CS_2000.CS2000ResultData;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194Emerald22.Emerald22ResultData;
import com.certacure.lis.interfaces.middleware.flow_component.kx21n.Kx21nResultData;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryAstmMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultAstmMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_ADT_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_DFT_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_SIU_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_UNKOWN_Msg;
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
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.ResultASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Appointment;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_EventTypeRecord;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageSourceType;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageTransactionType;
import com.certacure.lis.interfaces.middleware.util.MachineTypeEnum;
import com.certacure.lis.interfaces.service.DataInboundHL7MessageService;
import com.certacure.lis.interfaces.service.LkpService;
import com.certacure.lis.interfaces.service.MachineQueryService;
import com.certacure.lis.interfaces.service.MachineResultService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.MessageTransactionService;

import akka.actor.TypedActor.PreStart;
import akka.japi.pf.ReceiveBuilder;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.OUL_R22;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class StringAstmAnalityToLIS2A2Converter extends FlowComponent<RecipientConf> {

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
		return ReceiveBuilder.match(String.class, this::convertAndForward)
				.match(Message.class, this::convertAndForward)
				.match(CS2000ResultData.class, this::convertAndForward)
				//.match(PureResultData.class, this::convertAndForward)
				//.match(Hl7SiemensAdvia560ResultData.class, this::convertAndForward)
				//.match(Emerald22ResultData.class, this::convertAndForward)
				//.match(AU480ResultData.class, this::convertAndForward)

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

	private void convertAndForward(Message message) {

		setLkpMessageTransactionDirection(MessageDirection.IN);
		if (message instanceof OUL_R22) {

		}

	}

	private void convertAndForward(String msgAsString) {
		
		
		
		
		//msgAsString = "1H|\\^&|||Alinity ci-series^3.6^SCM24912|||||||P|LIS2-A2|20250617123159+0300 2F 2P|1 3F 3O|1|12637|12637^P3137^1^1^9|^^^1840^Direct LDL^STANDARD|R||||||||||||||||||||F F9 4R|1|^^^1840^Direct LDL^STANDARD^F|2.32|mmol/L|2.59 - 4.11|LOW||R||Admin^Admin||20250617122734|AC06022 B2 5M|1|INV|1840|CA||20250709115629|20250611115629|64502UQ06 88 6M|2|INV|DLDL0-1|SR|08158|20250618||64502UQ06 90 7R|2|^^^1840^Direct LDL^STANDARD^P|0.1660|Abs||||R||Admin^Admin||20250617122734|AC06022 1B 0R|3|^^^1840^Direct LDL^STANDARD^G|29cbf40e-1441-4f68-b083-c1cfe956dfe9|||||R||Admin^Admin||20250617122734|AC06022 7B 1L|1 3A 2H|\\^&|||Alinity ci-series^3.6^SCM24912|||||||P|LIS2-A2|20250617123159+0300 30 3P|1 40 4O|1|12637|12637^P3137^1^1^9|^^^1977^CrEnz^STANDARD|R||||||||||||||||||||F 90 5R|1|^^^1977^CrEnz^STANDARD^F|47.5|umol/L||||R||Admin^Admin||20250617122738|AC06022 6D 6M|1|INV|1977|CA||20250617131754|20250610131754|41103Y600 66 7M|2|INV|CRENZ-1|SR|03024|20250831||41103Y600 8E 0R|2|^^^1977^CrEnz^STANDARD^P|0.0166|Abs||||R||Admin^Admin||20250617122738|AC06022 AE 1R|3|^^^1977^CrEnz^STANDARD^G|f865323c-42f0-4187-882c-a2e90bec5d94|||||R||Admin^Admin||20250617122738|AC06022 84 2L|1 3B 3H|\\^&|||Alinity ci-series^3.6^SCM24912|||||||P|LIS2-A2|20250617123159+0300 31 4P|1 41 5O|1|12637|12637^P3137^1^1^9|^^^1417^Trig2^STANDARD|R||||||||||||||||||||F 6C 6R|1|^^^1417^Trig2^STANDARD^F|1.05|mmol/L||||R||Admin^Admin||20250617122742|AC06022 32 7M|1|INV|1417|CA||20250626125007|20250611125007|65213UD00 63 0M|2|INV|TRIG2-1|SR|23566|20250924||65213UD00 8F 1R|2|^^^1417^Trig2^STANDARD^P|0.0956|Abs||||R||Admin^Admin||20250617122742|AC06022 8C 2R|3|^^^1417^Trig2^STANDARD^G|8fe19085-bba6-4549-a6ed-01c9250677af|||||R||Admin^Admin||20250617122742|AC06022 8B 3L|1 3C 4H|\\^&|||Alinity ci-series^3.6^SCM24912|||||||P|LIS2-A2|20250617123159+0300 32 5P|1 42 6O|1|12637|12637^P3137^1^1^9|^^^1460^Iron2^STANDARD|R||||||||||||||||||||F 6D 7R|1|^^^1460^Iron2^STANDARD^F|7.1|umol/L|9 - 30.4|LOW||R||Admin^Admin||20250617122750|AC06022 69 0M|1|INV|1460|CA||20250702114518|20250617114518|63127UD00 67 1M|2|INV|IRON2-1|SR|05264|20250620||63127UD00 88 2R|2|^^^1460^Iron2^STANDARD^P|0.0177|Abs||||R||Admin^Admin||20250617122750|AC06022 87 3R|3|^^^1460^Iron2^STANDARD^G|64a6eb28-f9bb-4393-a0ca-00b03badbeed|||||R||Admin^Admin||20250617122750|AC06022 8A 4L|1 3D\r\n"
			//	+ "";
		
		

		System.out.println(
				"\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
		System.out.println(msgAsString);
		System.out.println(
				"\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");

		initiateObjects();

		// insert arrive message into transaction messages

		Machine machine = getMachine();
		machine = getMachineInfoByPath();

		setLkpMessageTransactionDirection(MessageDirection.IN);

		MessageType messageType = new MessageType();
		
		//String str = "1H|\\^&|||Alinity ci-series^3.6^SCM24912|||||||P|LIS2-A2|20250407135041+0300 25 2P|1 3F 3O|1|Khurshed 100721|Khurshed 100721^P3139^2^1^24|^^^1005^Urea^STANDARD|R||||||||||||||||||||F 64 4R|1|^^^1005^Urea^STANDARD^F|4.3|mmol/L||EXP||F||Admin^Admin||20250407134849|AC06022 A8 5M|1|INV|1005|CA||20250409154014|20250402154014|36678UN24-02374 A1 6M|2|INV|UREA0-1|SR|02374|20250901||36678UN24-02374 C9 7R|2|^^^1005^Urea^STANDARD^P|-0.0264|Abs||||F||Admin^Admin||20250407134849|AC06022 71 0R|3|^^^1005^Urea^STANDARD^G|ec409334-5e09-48f8-aeaf-f8ebd5853536|||||F||Admin^Admin||20250407134849|AC06022 7E 1L|1 3A 2H|\\^&|||Alinity ci-series^3.6^SCM24912|||||||P|LIS2-A2|20250407135041+0300 26 3P|1 40 4O|1|Ghadeer 101747|Ghadeer 101747^P3139^3^1^24|^^^1005^Urea^STANDARD|R||||||||||||||||||||F 5C 5R|1|^^^1005^Urea^STANDARD^F|4.3|mmol/L||EXP||F||Admin^Admin||20250407134909|AC06022 A6 6M|1|INV|1005|CA||20250409154014|20250402154014|36678UN24-02374 A2 7M|2|INV|UREA0-1|SR|02374|20250901||36678UN24-02374 CA 0R|2|^^^1005^Urea^STANDARD^P|-0.0269|Abs||||F||Admin^Admin||20250407134909|AC06022 6C 1R|3|^^^1005^Urea^STANDARD^G|d8354827-cd5d-424b-8076-4f996e549c04|||||F||Admin^Admin||20250407134909|AC06022 C1 2L|1 3B\r\n"
		//		+ "";
		
		if(msgAsString.indexOf("[CR]") != -1)
		{
			msgAsString = msgAsString.replaceAll("[CR]", "\r\n");
		}

		List<LIS2A2Msg>[] arrList2A2Msg = stringMsgToAstmMsg(msgAsString, messageType, machine);

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
							MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());

							switch (machineType) {

							// ASTM 1302
							case ROCHE_COBAS_PURE:
							case SIEMENS_ADVIA_560:

								sampleBarcode = queryRecord.getSpecimenId(4);

								createNewMessageTransaction(sampleBarcode );

								break;
							// ASTM 1302
							case BECHMAN_COULTER_800_DXH:
							case BECHMAN_COULTER_500_DXH:

								sampleBarcode = queryRecord.getSpecimenId(3, 2, "!");

								createNewMessageTransaction(sampleBarcode);

								break;
							case ROCHE_COBAS_6000:
							case ROCHE_COBAS_C311:
								sampleBarcode = queryRecord.getSpecimenIds(3, 3).toString();
								createNewMessageTransaction(sampleBarcode);

								break;

							case DIASORIN_LIAISON_XL:
								sampleBarcode = queryRecord.getSpecimenIds(1, 3).toString();
								createNewMessageTransaction(sampleBarcode);

								break;
							case ROCHE_COBAS_E411:
								sampleBarcode = queryRecord.getSpecimenIds(2, 3).toString();
								createNewMessageTransaction(sampleBarcode);

								break;

							case BECHMAN_ACCESS_200_DXI:
								sampleBarcode = queryRecord.getSpecimenIds(2, 3).toString();
								createNewMessageTransaction(sampleBarcode);

								break;
							// ASTM 1391
							case ABBOTT_ARCHITECT_CI4100:
							case ABBOTT_ALINITY_CI:
								sampleBarcode = queryRecord.getSpecimenIds(2, 3).toString().toString();
								createNewMessageTransaction(sampleBarcode);

								break;

							case ABBOTT_CELL_DYN_RUBY:
								sampleBarcode = queryRecord.getSpecimenIds(2, 3).toString().toString();
								createNewMessageTransaction(sampleBarcode);

								break;
							case BECHMAN_COULTER_X800:
							case SIEMENS_ADVIA_CENTAUR_XPT:
							case COBASP312:
							case SYSMEX_XP_300:
							case D10:
							case SYSMEX_CS_2000I:
							case ROCHE_COBAS_C111:
							case ROCHE_COBAS_C111_ETB:
								sampleBarcode = queryRecord.getSpecimenIds(2, 3).toString().toString();
								createNewMessageTransaction(sampleBarcode);

								break;

							case ROCHE_ELECSYS_2010:
								sampleBarcode = queryRecord.getComponentValue(2, 3).toString();
								createNewMessageTransaction(sampleBarcode);

								break;
							case SIEMENS_ATELLICA:
								sampleBarcode = queryRecord.getComponentValue(2, 2).toString();
								createNewMessageTransaction(sampleBarcode);

								break;
							// 500 XS
							case SYSMEX_SUITE:
								sampleBarcode = queryRecord.getSpecimenId(4).toString();
								createNewMessageTransaction(sampleBarcode);

								break;
							default:
								break;
							}
						}
					}

					messageTransaction = createMessageTransaction(msgAsString, messageTransactionService, machine,
							messageTransaction, lkpMessageTransactionDirection, lkpMessageTransactionType,
							sampleBarcode, globalHeaderRecord);

					lisQueryMsg.setMessageTransactionID(messageTransaction.getRid().longValue());
					storeQueryMsg(lis2A2Msg.toString(), globalHeaderRecord, sampleBarcode, messageTransaction);

					conf.recipient.tell(lisQueryMsg, self());

				}

				
				
				if (lis2A2Msg instanceof LIS2A2ResultAstmMsg) {
					LIS2A2ResultAstmMsg LIS2A2ResultMsg;
					LIS2A2ResultMsg = (LIS2A2ResultAstmMsg) lis2A2Msg;
					LIS2A2ResultMsg.getHeaderASTMRecords();

					setLkpMessageTransactionType(MessageTransactionType.RESULT_RECEIVED);

					for (HeaderASTMRecord headerRecord : LIS2A2ResultMsg.getHeaderASTMRecords()) {
						for (PatientASTMRecord patientRecord : LIS2A2ResultMsg.getPatientASTMRecords()) {
							log.debug("Finding O records for " + patientRecord.asString());
							globalHeaderRecord = headerRecord;
							for (OrderASTMRecord orderRecord : LIS2A2ResultMsg.getOrderRecords(patientRecord)) {
								MachineTypeEnum machineType = MachineTypeEnum
										.valueOf(machine.getMachineType().getCode());
								switch (machineType) {
								case BECHMAN_COULTER_500_DXH:
								case BECHMAN_COULTER_800_DXH:
									sampleBarcode = orderRecord.getSpecimenId(3, 1, "!");
									createNewMessageTransaction(sampleBarcode);
									messageTransaction = createMessageTransaction(msgAsString,
											messageTransactionService, machine, messageTransaction,
											lkpMessageTransactionDirection, lkpMessageTransactionType, sampleBarcode,
											globalHeaderRecord);
									LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());
									conf.recipient.tell(lis2A2Msg, self());

									break;
								case ROCHE_COBAS_6000:
								case ROCHE_COBAS_C311:
								case ROCHE_COBAS_E411:
								case BECHMAN_ACCESS_200_DXI:
								case DIASORIN_LIAISON_XL:

									sampleBarcode = orderRecord.getSpecimenIds(1, 3).toString();
									createNewMessageTransaction(sampleBarcode);
									messageTransaction = createMessageTransaction(msgAsString,
											messageTransactionService, machine, messageTransaction,
											lkpMessageTransactionDirection, lkpMessageTransactionType, sampleBarcode,
											globalHeaderRecord);
									LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());
									conf.recipient.tell(lis2A2Msg, self());

									break;
								case ABBOTT_ARCHITECT_CI4100:
								case ABBOTT_ALINITY_CI:
									sampleBarcode = orderRecord.getSpecimenIds(1, 3).toString();
									createNewMessageTransaction(sampleBarcode);
									messageTransaction = createMessageTransaction(msgAsString,
											messageTransactionService, machine, messageTransaction,
											lkpMessageTransactionDirection, lkpMessageTransactionType, sampleBarcode,
											globalHeaderRecord);
									LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());
									conf.recipient.tell(lis2A2Msg, self());

									break;

								case ABBOTT_CELL_DYN_RUBY:
									sampleBarcode = orderRecord.getSpecimenIds(1, 3).toString();
									createNewMessageTransaction(sampleBarcode);

									messageTransaction = createMessageTransaction(msgAsString,
											messageTransactionService, machine, messageTransaction,
											lkpMessageTransactionDirection, lkpMessageTransactionType, sampleBarcode,
											globalHeaderRecord);
									LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());
									conf.recipient.tell(lis2A2Msg, self());

									break;

								case BECHMAN_COULTER_X800:
								case SIEMENS_ADVIA_CENTAUR_XPT:
								case COBASP312:
								case SYSMEX_XP_300:
								case D10:
								case SYSMEX_CS_2000I:
								case SYSMEX_KX_21:
								case BECKMAN_UNICEL_800_DXH:

									sampleBarcode = orderRecord.getSpecimenId(3,2 , "^");
									createNewMessageTransaction(sampleBarcode);
									messageTransaction = createMessageTransaction(msgAsString,
											messageTransactionService, machine, messageTransaction,
											lkpMessageTransactionDirection, lkpMessageTransactionType, sampleBarcode,
											globalHeaderRecord);
									LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());
									conf.recipient.tell(lis2A2Msg, self());

									break;
								case ROCHE_COBAS_C111_ETB:
								case ROCHE_COBAS_C111:

									sampleBarcode = orderRecord.getSpecimenIds(1, 4).toString();
									createNewMessageTransaction(sampleBarcode);
									messageTransaction = createMessageTransaction(msgAsString,
											messageTransactionService, machine, messageTransaction,
											lkpMessageTransactionDirection, lkpMessageTransactionType, sampleBarcode,
											globalHeaderRecord);
									LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());
									conf.recipient.tell(lis2A2Msg, self());

									break;
									
								case SYSMEX_SUITE:

									sampleBarcode = orderRecord.getSpecimenIds(3, 4).toString();
									
									createNewMessageTransaction(sampleBarcode);
									messageTransaction = createMessageTransaction(msgAsString,
											messageTransactionService, machine, messageTransaction,
											lkpMessageTransactionDirection, lkpMessageTransactionType, sampleBarcode,
											globalHeaderRecord);
									LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());
									conf.recipient.tell(lis2A2Msg, self());

									break;
								case ROCHE_ELECSYS_2010:
								

									sampleBarcode = orderRecord.getSpecimenIds(1, 3).toString();
									createNewMessageTransaction(sampleBarcode);
									messageTransaction = createMessageTransaction(msgAsString,
											messageTransactionService, machine, messageTransaction,
											lkpMessageTransactionDirection, lkpMessageTransactionType, sampleBarcode,
											globalHeaderRecord);
									LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());
									conf.recipient.tell(lis2A2Msg, self());

									break;
									
								case SIEMENS_ATELLICA:
									
									sampleBarcode = orderRecord.getSpecimenIds(1, 3).toString();
									createNewMessageTransaction(sampleBarcode);
									messageTransaction = createMessageTransaction(msgAsString,
											messageTransactionService, machine, messageTransaction,
											lkpMessageTransactionDirection, lkpMessageTransactionType, sampleBarcode,
											globalHeaderRecord);
									LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());
									conf.recipient.tell(LIS2A2ResultMsg, self());
									
									break;
								default:
									break;
								}
							}
						}
					}

				}

			}

		}

		// sender().tell(lis2A2Msg, self());
	}

	/*private void convertAndForward(PureResultData resultObject) {

		initiateObjects();

		List<LIS2A2Msg>[] arrList2A2Msg = stringMsgToAstmMsg(resultObject);
		Machine machine = getMachine();

		setLkpMessageTransactionDirection(MessageDirection.IN);
		setLkpMessageTransactionType(MessageTransactionType.RESULT_RECEIVED);

		for (List<LIS2A2Msg> lis2A2Msg : arrList2A2Msg) {
			for (LIS2A2Msg lis2A2MsgData : lis2A2Msg) {

				if (lis2A2MsgData instanceof LIS2A2ResultMsg) {

					LIS2A2ResultMsg LIS2A2ResultMsg;
					LIS2A2ResultMsg = (LIS2A2ResultMsg) lis2A2MsgData;
					LIS2A2ResultMsg.getHeaderRecords();
					for (HeaderRecord headerRecord : LIS2A2ResultMsg.getHeaderRecords()) {
						for (PatientRecord patientRecord : LIS2A2ResultMsg.getPatientRecords()) {
							log.debug("Finding O records for " + patientRecord.asString());
							globalHeaderRecord = headerRecord;
							for (OrderRecord orderRecord : LIS2A2ResultMsg.getOrderRecords(patientRecord)) {
								MachineTypeEnum machineType = MachineTypeEnum

										.valueOf(machine.getMachineType().getCode());

								switch (machineType) {

								case ROCHE_COBAS_PURE:
									sampleBarcode = orderRecord.getSpecimenId(4).toString();
									createNewMessageTransaction(sampleBarcode);
									break;
								default:
									break;
								}
							}
						}
					}

					messageTransaction = createMessageTransaction(resultObject.originalInput, messageTransactionService,
							machine, messageTransaction, lkpMessageTransactionDirection, lkpMessageTransactionType,
							sampleBarcode, globalHeaderRecord);
					LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());

					conf.recipient.tell(lis2A2MsgData, self());
				}
			}
		}

	}*/

	/*private void convertAndForward(Hl7SiemensAdvia560ResultData resultObject) {

		initiateObjects();

		//for(int index = 0 ; index < resultObject.length;index++)
		//{
		List<LIS2A2Msg>[] arrList2A2Msg = stringMsgToAstmMsg(resultObject);
		Machine machine = getMachine();

		setLkpMessageTransactionDirection(MessageDirection.IN);
		setLkpMessageTransactionType(MessageTransactionType.RESULT_RECEIVED);

		for (List<LIS2A2Msg> lis2A2Msg : arrList2A2Msg) {
			for (LIS2A2Msg lis2A2MsgData : lis2A2Msg) {

				if (lis2A2MsgData instanceof LIS2A2ResultMsg) {

					LIS2A2ResultMsg LIS2A2ResultMsg;
					LIS2A2ResultMsg = (LIS2A2ResultMsg) lis2A2MsgData;
					LIS2A2ResultMsg.getHeaderRecords();
					for (HeaderRecord headerRecord : LIS2A2ResultMsg.getHeaderRecords()) {
						for (PatientRecord patientRecord : LIS2A2ResultMsg.getPatientRecords()) {
							log.debug("Finding O records for " + patientRecord.asString());
							globalHeaderRecord = headerRecord;
							for (OrderRecord orderRecord : LIS2A2ResultMsg.getOrderRecords(patientRecord)) {
								MachineTypeEnum machineType = MachineTypeEnum

										.valueOf(machine.getMachineType().getCode());

								switch (machineType) {

								case SIEMENS_ADVIA_560:
									sampleBarcode = orderRecord.getSpecimenId(4).toString();
									createNewMessageTransaction(sampleBarcode);
									break;
								default:
									break;
								}
							}
						}
					}

					messageTransaction = createMessageTransaction(resultObject.originalInput, messageTransactionService,
							machine, messageTransaction, lkpMessageTransactionDirection, lkpMessageTransactionType,
							sampleBarcode, globalHeaderRecord);
					LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());

					conf.recipient.tell(lis2A2MsgData, self());
				}
			}
		}
		
	}*/

	private void convertAndForward(CS2000ResultData resultObject) {

		initiateObjects();

		List<LIS2A2Msg>[] arrList2A2Msg = stringMsgToAstmMsg(resultObject);
		Machine machine = getMachine();

		setLkpMessageTransactionDirection(MessageDirection.IN);
		setLkpMessageTransactionType(MessageTransactionType.RESULT_RECEIVED);

		for (List<LIS2A2Msg> lis2A2Msg : arrList2A2Msg) {
			for (LIS2A2Msg lis2A2MsgData : lis2A2Msg) {

				if (lis2A2MsgData instanceof LIS2A2ResultAstmMsg) {

					LIS2A2ResultAstmMsg LIS2A2ResultMsg;
					LIS2A2ResultMsg = (LIS2A2ResultAstmMsg) lis2A2MsgData;
					LIS2A2ResultMsg.getHeaderASTMRecords();
					for (HeaderASTMRecord headerRecord : LIS2A2ResultMsg.getHeaderASTMRecords()) {
						for (PatientASTMRecord patientRecord : LIS2A2ResultMsg.getPatientASTMRecords()) {
							log.debug("Finding O records for " + patientRecord.asString());
							globalHeaderRecord = headerRecord;
							for (OrderASTMRecord orderRecord : LIS2A2ResultMsg.getOrderRecords(patientRecord)) {
								MachineTypeEnum machineType = MachineTypeEnum
										.valueOf(machine.getMachineType().getCode());

								switch (machineType) {

								case SYSMEX_KX_21:
								case SYSMEX_CS_2000I :
									sampleBarcode = orderRecord.getSpecimenId(3).toString();
									
									createNewMessageTransaction(sampleBarcode);
									break;
								default:
									break;
								}
							}
						}
					}

					messageTransaction = createMessageTransaction(resultObject.originalInput, messageTransactionService,
							machine, messageTransaction, lkpMessageTransactionDirection, lkpMessageTransactionType,
							sampleBarcode, globalHeaderRecord);
					
					
					LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());

					conf.recipient.tell(lis2A2MsgData, self());
				}
			}
		}

	}

	/*private void convertAndForward(AU480ResultData resultObject) {

		initiateObjects();

		List<LIS2A2Msg>[] arrList2A2Msg = stringMsgToAstmMsg(resultObject);
		Machine machine = getMachine();

		setLkpMessageTransactionDirection(MessageDirection.IN);
		setLkpMessageTransactionType(MessageTransactionType.RESULT_RECEIVED);

		for (List<LIS2A2Msg> lis2A2Msg : arrList2A2Msg) {
			for (LIS2A2Msg lis2A2MsgData : lis2A2Msg) {

				if (lis2A2MsgData instanceof LIS2A2ResultMsg) {

					LIS2A2ResultMsg LIS2A2ResultMsg;
					LIS2A2ResultMsg = (LIS2A2ResultMsg) lis2A2MsgData;
					LIS2A2ResultMsg.getHeaderRecords();
					for (HeaderRecord headerRecord : LIS2A2ResultMsg.getHeaderRecords()) {
						for (PatientRecord patientRecord : LIS2A2ResultMsg.getPatientRecords()) {
							log.debug("Finding O records for " + patientRecord.asString());
							globalHeaderRecord = headerRecord;
							for (OrderRecord orderRecord : LIS2A2ResultMsg.getOrderRecords(patientRecord)) {
								MachineTypeEnum machineType = MachineTypeEnum
										.valueOf(machine.getMachineType().getCode());

								switch (machineType) {

								case BECHMAN_COULTER_AU480_DXC:
									sampleBarcode = orderRecord.getSpecimenId(4).toString();
									createNewMessageTransaction(sampleBarcode);
									break;
								default:
									break;
								}
							}
						}
					}

					messageTransaction = createMessageTransaction(resultObject.getOriginalInput(),
							messageTransactionService, machine, messageTransaction, lkpMessageTransactionDirection,
							lkpMessageTransactionType, sampleBarcode, globalHeaderRecord);
					LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());

					conf.recipient.tell(lis2A2MsgData, self());
				}
			}
		}

	}*/

	private Machine getMachine() {
		return machineService.getMachineByActorPath(getContext().parent().toString());
	}

	private String getASTMMessageString(CS2000ResultData resultObject) {
		String[] recordLines = resultObject.ResultLines;
		String strMessage = "H|\\^&||||||||||||" + CR;
		strMessage += "P|1||" + CR;
		strMessage += "O|1||" + resultObject.sampleNo + "||||||||||||||||||||||" + CR;

		for (int index = 0; index < recordLines.length; index++) {

			strMessage += "R|" + index + "|^^^" + resultObject.arrResultCode[index] + "|" + recordLines[index]
					+ "|||||||" + CR;

		}

		return strMessage;
	}

	/*private String getASTMMessageString(PureResultData resultObject) {
		String strMessage = "H|\\^&||||||||||||" + CR;
		strMessage += "P|1||" + CR;
		strMessage += "O|1||" + resultObject.sampleNo + "||||||||||||||||||||||" + CR;
		strMessage += "R|" + 1 + "|^^^" + resultObject.testCode + "|" + resultObject.result + "|||||||" + CR;

		return strMessage;
	}*/

	/*private String getASTMMessageString(Hl7SiemensAdvia560ResultData resultObject) {
		String strMessage = "H|\\^&||||||||||||" + CR;
		strMessage += "P|1||" + CR;
		strMessage += "O|1||" + resultObject.sampleNo + "||||||||||||||||||||||" + CR;
		strMessage += "R|" + 1 + "|^^^" + resultObject.testCode + "|" + resultObject.result + "|||||||" + CR;

		return strMessage;
	}*/

	/*private String getASTMMessageString(AU480ResultData resultObject) {
		List<AU480Result> recordLines = resultObject.getResults();
		String strMessage = "H|\\^&||||||||||||" + CR;
		strMessage += "P|1||" + CR;
		strMessage += "O|1||" + resultObject.getSampleNo() + "||||||||||||||||||||||" + CR;

		for (int index = 0; index < recordLines.size(); index++) {

			strMessage += "R|" + index + "|^^^" + recordLines.get(index).getHostCode() + "|"
					+ recordLines.get(index).getTestValue() + "|||||||" + CR;

		}

		return strMessage;
	}*/

	/*private String getASTMMessageString(Emerald22ResultData resultObject) {
		String[] recordLines = resultObject.ResultLines;
		String strMessage = "H|\\^&||||||||||||" + CR;
		strMessage += "P|1||" + CR;
		strMessage += "O|1||" + resultObject.sampleNo + "||||||||||||||||||||||" + CR;

		for (int index = 0; index < recordLines.length; index++) {

			strMessage += "R|" + index + "|^^^" + resultObject.arrResultCode[index] + "|" + recordLines[index]
					+ "|||||||" + CR;

		}

		return strMessage;
	}*/

	private List<LIS2A2Msg>[] stringMsgToAstmMsg(CS2000ResultData resultObject) {
		Machine machine = getMachine();
		machine = getMachineInfoByPath();
		String strMessage = getASTMMessageString(resultObject);
		MessageType messageType = new MessageType();
		List<LIS2A2Msg>[] arrList2A2Msg = stringMsgToAstmMsg(strMessage, messageType, machine);

		return arrList2A2Msg;

	}

	/*private List<LIS2A2Msg>[] stringMsgToAstmMsg(PureResultData resultObject) {
		Machine machine = getMachine();
		machine = getMachineInfoByPath();
		String strMessage = getASTMMessageString(resultObject);
		MessageType messageType = new MessageType();
		List<LIS2A2Msg>[] arrList2A2Msg = stringMsgToAstmMsg(strMessage, messageType, machine);

		return arrList2A2Msg;

	}*/

	/*private List<LIS2A2Msg>[] stringMsgToAstmMsg(Hl7SiemensAdvia560ResultData resultObject) {
		Machine machine = getMachine();
		machine = getMachineInfoByPath();
		String strMessage = getASTMMessageString(resultObject);
		MessageType messageType = new MessageType();
		List<LIS2A2Msg>[] arrList2A2Msg = stringMsgToAstmMsg(strMessage, messageType, machine);

		return arrList2A2Msg;

	}*/

	/*private List<LIS2A2Msg>[] stringMsgToAstmMsg(AU480ResultData resultObject) {
		Machine machine = getMachine();
		machine = getMachineInfoByPath();
		String strMessage = getASTMMessageString(resultObject);
		MessageType messageType = new MessageType();
		List<LIS2A2Msg>[] arrList2A2Msg = stringMsgToAstmMsg(strMessage, messageType, machine);

		return arrList2A2Msg;

	}*/

	/*private List<LIS2A2Msg>[] stringMsgToAstmMsg(Emerald22ResultData resultObject) {
		Machine machine = getMachine();
		machine = getMachineInfoByPath();
		String strMessage = getASTMMessageString(resultObject);
		MessageType messageType = new MessageType();
		List<LIS2A2Msg>[] arrList2A2Msg = stringMsgToAstmMsg(strMessage, messageType, machine);

		return arrList2A2Msg;

	}*/

	private void createNewMessageTransaction(String sampleBarcode) {
		messageTransaction = new MessageTransaction();
		messageTransaction.setBarcode(sampleBarcode);
		//messageTransaction.setMessageType(lkpMessageTransactionType);
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
		messageTransaction.setBarcode(sampleBarcode);
		//messageTransaction.setIsProcessed(false);
		//messageTransaction.setIsSuccess(false);
		//messageTransaction.setIsValidated(false);
		messageTransaction = messageTransactionService.add(messageTransaction);
		return messageTransaction;
	}

	public Machine getMachineInfoByPath() {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		return machine;
	}

	private Machine getMachineInformation(String senderName) {

		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByName(senderName);
		return machine;
	}

	private AccessResult validateAccessInfo(HeaderRecordAstm headerRecord) {

		Machine machine = getMachineInformation(headerRecord.getSenderName());

		if (machine == null) {
			if (headerRecord.getSenderName().isEmpty()) {
				log.debug("Machine Name : " + headerRecord.getSenderName() + "is not Exist");
				return AccessResult.ACCESS_DENIED_NO_MACHINE_DATA_IDENTITY_RECIVED;

			} else {
				log.debug("Machine Name : " + headerRecord.getSenderName() + "is not Exist");
				return AccessResult.ACCESS_DENIED_WRONG_MACHINE_IDENTITY;
			}

		} else {

			if (machine.getUserName() != headerRecord.getUserName()) {
				return AccessResult.ACCESS_DENIED_WRONG_USER_NAME;
			} else if (machine.getPassword() != headerRecord.getPassword()) {
				return AccessResult.ACCESS_DENIED_WRONG_USER_PASS;
			} else if (machine.getIsActive() != true) {
				return AccessResult.ACCESS_DENIED_MACHINE_NOT_ENABLED;
			}
		}

		return AccessResult.ACCESS_ALLOWED;

	}

	public List<LIS2A2Msg>[] stringMsgToAstmMsg(String msgAsString, MessageType msgType, Machine machine) {

		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
		switch (machineType) {
		case ABBOTT_ARCHITECT_CI4100:
			return processArchitect4100CIMessage(msgAsString, msgType);
		case ABBOTT_ALINITY_CI:
			return processAlinityCIMessage(msgAsString, msgType);
		case DIASORIN_LIAISON_XL:
			// return processLiaisonMessages(msgAsString, msgType);
			return processASTMMessage(msgAsString, msgType);
		default:

			return processASTMMessage(msgAsString, msgType);

		}

	}

	private List<LIS2A2Msg>[] processLiaisonMessages(String msgAsString, MessageType msgType) {

		List<LIS2A2Record> records = null;
		List<LIS2A2Msg>[] list2A2Msg;
		LIS2A2Record lis2Header = null;
		LIS2A2Record lis2Fotter = null;

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

		records = new ArrayList<LIS2A2Record>();
		for (int index = 0; index < recordLines.length; index++) {

			log.debug("Converting String '" + recordLines[index] + "' into LIS2A2Record");

			if (recordLines[index].equals("]") || recordLines[index].equals("\r") || recordLines[index].equals("\n")
					|| (recordLines[index].length() == 0))
				continue;

			/*
			 * if(recordLines[index].startsWith("Q|") || recordLines[index].startsWith("R|")
			 * || recordLines[index].startsWith("O|")) { recordLines[index] = "H|1|" + CR +
			 * recordLines[index] + CR + "L|1|N" + CR; }
			 */

			if (LIS2A2Record.fromStringAstm(recordLines[index]) != null) {

				LIS2A2Record lis2a2Record = LIS2A2Record.fromStringAstm(recordLines[index]);
				log.debug("Conversion successful, lis2a2Record.toString() = " + lis2a2Record.toString());
				records.add(lis2a2Record);
			} else {
				continue;
			}

		}

		List<LIS2A2Msg> tempMessages;
		list2A2Msg = new ArrayList[records.size() - 2];
		int index2 = 0;
		List<LIS2A2Msg> tempMessages2;

		for (int index = 0; index < records.size(); index++) {
			tempMessages = new ArrayList<LIS2A2Msg>();

			if (getMsgType(records.get(index)) == LIS2A2OrderMsg.class) {
				tempMessages.add(new LIS2A2OrderMsg(
						Arrays.asList(records.get(0), records.get(index), records.get(records.size() - 1))));
				list2A2Msg[index2] = tempMessages;
				index2++;
			} else if (getMsgType(records.get(index)) == LIS2A2QueryMsg.class) {
				tempMessages.add(new LIS2A2QueryMsg(
						Arrays.asList(records.get(0), records.get(index), records.get(records.size() - 1))));
				list2A2Msg[index2] = tempMessages;
				index2++;
			} else if (getMsgType(records.get(index)) == LIS2A2ResultMsg.class) {
				tempMessages.add(new LIS2A2ResultMsg(
						Arrays.asList(records.get(0), records.get(index), records.get(records.size() - 1))));
				list2A2Msg[index2] = tempMessages;
				index2++;
			} else {
				continue;
			}

		}

		return list2A2Msg;

	}

	private List<LIS2A2Msg>[] processHL7Message(String msgAsString, MessageType msgType) {
		List<LIS2A2Record> records = new ArrayList<LIS2A2Record>();
		List<LIS2A2Msg>[] arrList2A2Msg = null;

		try {

			String str = String.valueOf(CR);
			String hl7MsgAsString = msgAsString;
			String[] recordLines = hl7MsgAsString.split("(?=(O)(\\|)(\\d))");
			String[] recordLinesRslts = null;
			records = new ArrayList<LIS2A2Record>();
			for (int index = 0; index < recordLines.length; index++) {
				recordLinesRslts = recordLines[index].split("(?=(R)(\\|)(\\d))");
				log.debug("Converting String '" + recordLines[index] + "' into LIS2A2Record");

				if (recordLines[index].equals("]") || recordLines[index].equals("\r") || recordLines[index].equals("\n")
						|| (recordLines[index].length() == 0) || index == 0)
					continue;

				if (LIS2A2Record.fromString(recordLinesRslts[index]) != null) {
					LIS2A2Record lis2a2Record = LIS2A2Record
							.fromString("H|1||\r" + "P|1||\r" + recordLinesRslts[1] + "L|1||\r");
					log.debug("Conversion successful, lis2a2Record.toString() = " + lis2a2Record.toString());
					records.add(lis2a2Record);
				} else {
					continue;
				}

			}

			// arrList2A2Msg = new ArrayList[records.size()];

			for (int index = 0; index < 3; index++) {
				// msgType.refMsgType = getMsgType(arrList2A2Msg);

				if (msgType.refMsgType == null) {
					return null;
				} else if (msgType.refMsgType.equals(LIS2A2ResultMsg.class)) {
					arrList2A2Msg[0] = new ArrayList<LIS2A2Msg>();
					arrList2A2Msg[0].add(new LIS2A2ResultMsg(records));
				} else if (msgType.refMsgType.equals(LIS2A2QueryMsg.class)) {
					arrList2A2Msg[0] = new ArrayList<LIS2A2Msg>();
					arrList2A2Msg[0].add(new LIS2A2QueryMsg(records));
				}

			}

			/*
			 * if (msgType.refMsgType.equals(LIS2A2OrderMsg.class)) { arrList2A2Msg[index] =
			 * new ArrayList<LIS2A2Msg>(); arrList2A2Msg[index].add(new
			 * LIS2A2OrderMsg(records)); }
			 */

		} catch (NullPointerException ex) {
			log.error("Unexpected message type - " + msgType + "for message text : " + msgAsString);
		}

		return arrList2A2Msg;
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

			records = new ArrayList<LIS2A2Record>();
			for (int index = 0; index < recordLines.length; index++) {

				log.debug("Converting String '" + recordLines[index] + "' into LIS2A2Record");

				if (recordLines[index].equals("]") || recordLines[index].equals("\r") || recordLines[index].equals("\n")
						|| (recordLines[index].length() == 0))
					continue;

				if (LIS2A2Record.fromStringAstm(recordLines[index]) != null) {
					LIS2A2Record lis2a2Record = LIS2A2Record.fromStringAstm(recordLines[index]);
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
				
			} 
			else if (msgType.refMsgType.equals(LIS2A2ResultAstmMsg.class)) {
				arrList2A2Msg[0] = new ArrayList<LIS2A2Msg>();
				arrList2A2Msg[0].add(new LIS2A2ResultAstmMsg(records));
				
			}else if (msgType.refMsgType.equals(LIS2A2QueryAstmMsg.class)) {
				arrList2A2Msg[0] = new ArrayList<LIS2A2Msg>();
				arrList2A2Msg[0].add(new LIS2A2QueryAstmMsg(records));
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
			List<String> lstFinalMessage = new ArrayList<String>();

			for (int index = 0; index < inputMEssage.length; index++) {
				String[] inputMEssage2 = inputMEssage[index].split(regexFotter);
				System.out.println(inputMEssage2);
				String newMessage = "";

				for (int index2 = 0; index2 < inputMEssage2.length; index2++) {
					int indexOFSTX = -1;

					indexOFSTX = inputMEssage2[index2].indexOf(STX);

					if (indexOFSTX != -1) {
						newMessage = inputMEssage2[index2].substring(indexOFSTX, inputMEssage2[index2].length() - 1)
								+ "L|" + CR + LF;
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
				records = new ArrayList<LIS2A2Record>();
				for (String record : recordLines) {

					log.debug("Converting String '" + record + "' into LIS2A2Record");

					if (record.equals("]") || record.equals("\r") || record.equals("\n"))
						continue;

					if (LIS2A2Record.fromStringAstm(record) != null) {
						LIS2A2Record lis2a2Record = LIS2A2Record.fromStringAstm(record);
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

				else if (msgType.refMsgType.equals(LIS2A2ResultAstmMsg.class)) {
					arrList2A2Msg[index] = new ArrayList<LIS2A2Msg>();
					arrList2A2Msg[index].add(new LIS2A2ResultAstmMsg(records));
				} else if (msgType.refMsgType.equals(LIS2A2QueryAstmMsg.class)) {
					arrList2A2Msg[index] = new ArrayList<LIS2A2Msg>();
					arrList2A2Msg[index].add(new LIS2A2QueryAstmMsg(records));
				}

			}

		} catch (NullPointerException ex) {
			log.error("Unexpected message type - " + msgType + "for message text : " + msgAsString);
		}

		return arrList2A2Msg;
	}

	private List<LIS2A2Msg>[] processAlinityCIMessage(String msgAsString, MessageType msgType) {
		List<LIS2A2Record> records = null;
		List<LIS2A2Msg>[] arrList2A2Msg = null;

		try {

			String[] inputMEssage = msgAsString.split("(" + EOT + ")");
			// need to write a function to seperate a large and nessted based on fotter
			// L|[CR]
			final String regexFotter = "(?:(\\d)(L)(\\|)(\\d))";
			List<String> lstFinalMessage = new ArrayList<String>();

			for (int index = 0; index < inputMEssage.length; index++) {
				String[] inputMEssage2 = inputMEssage[index].split(regexFotter);
				System.out.println(inputMEssage2);
				String newMessage = "";

				for (int index2 = 0; index2 < inputMEssage2.length; index2++) {
					int indexOFSTX = -1;

					indexOFSTX = inputMEssage2[index2].indexOf(STX);

					if (indexOFSTX != -1) {
						newMessage = inputMEssage2[index2].substring(indexOFSTX, inputMEssage2[index2].length() - 1)
								+ "L|" + CR + LF;
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
				String[] recordLines = msgAsString2.split("(?:("+ ETX +")(\\b[0-9A-Fa-f]+\\b))");//
				// String[] validatedRecordLines = validateRecordLines(recordLines);
				records = new ArrayList<LIS2A2Record>();
				for (String record : recordLines) {

					log.debug("Converting String '" + record + "' into LIS2A2Record");

					if (record.equals("]") || record.equals("\r") || record.equals("\n"))
						continue;

					if (LIS2A2Record.fromStringAstm(record) != null) {
						LIS2A2Record lis2a2Record = LIS2A2Record.fromStringAstm(record);
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

				else if (msgType.refMsgType.equals(LIS2A2ResultAstmMsg.class)) {
					arrList2A2Msg[index] = new ArrayList<LIS2A2Msg>();
					arrList2A2Msg[index].add(new LIS2A2ResultAstmMsg(records));
				} else if (msgType.refMsgType.equals(LIS2A2QueryAstmMsg.class)) {
					arrList2A2Msg[index] = new ArrayList<LIS2A2Msg>();
					arrList2A2Msg[index].add(new LIS2A2QueryAstmMsg(records));
				}

			}

		} catch (NullPointerException ex) {
			log.error("Unexpected message type - " + msgType + "for message text : " + msgAsString);
		}

		return arrList2A2Msg;
	}

	public LIS2A2Msg stringMsgToAstmMsgCobasC111(String msgAsString, MessageType msgType) {

		try {
			List<LIS2A2Record> records = new ArrayList<>();
			String str = String.valueOf(CR) + String.valueOf(LF);
			String msgAsString2 = msgAsString.replaceAll(str, String.valueOf(CR));
			String[] recordLines = msgAsString2.split("(?<=" + CR + ")");
			// String[] validatedRecordLines = validateRecordLines(recordLines);

			for (String record : recordLines) {
				log.debug("Converting String '" + record + "' into LIS2A2Record");
				LIS2A2Record lis2a2Record = LIS2A2Record.fromStringAstm(record);
				log.debug("Conversion successful, lis2a2Record.toString() = " + lis2a2Record.toString());
				records.add(lis2a2Record);
			}

			msgType.refMsgType = getMsgType(records);

			if (msgType.refMsgType == null) {
				return null;
			}

			if (msgType.refMsgType.equals(null))
				return null;
			if (msgType.refMsgType.equals(LIS2A2OrderMsg.class))
				return new LIS2A2OrderMsg(records);
			else if (msgType.refMsgType.equals(LIS2A2ResultMsg.class))
				return new LIS2A2ResultMsg(records);
			else if (msgType.refMsgType.equals(LIS2A2QueryMsg.class))
				return new LIS2A2QueryMsg(records);
			else
				return null;

		} catch (NullPointerException ex) {
			log.error("Unexpected message type - " + msgType + "for message text : " + msgAsString);
		}

		return null;

	}

	private String[] validateRecordLines(String[] recordLines) {

		List<String> FilterdRecordList = new ArrayList<String>();

		for (int index = 0; index < recordLines.length; index++) {
			if (recordLines[index].length() > 1) {
				String newRecord = recordLines[index].replaceAll("OBX", "R").replaceAll("OBR", "O");

				int indexOfFirstFieldSep = -1;
				indexOfFirstFieldSep = newRecord.indexOf('|');

				if (indexOfFirstFieldSep != -1) {

					String record = "";

					if (newRecord.contains("H|")) {
						record = newRecord.substring(newRecord.indexOf("H|"), newRecord.indexOf(CR));

					} else if (newRecord.contains("P|")) {
						record = newRecord.substring(newRecord.indexOf("P|"), newRecord.indexOf(CR));

					} else if (newRecord.contains("O|")) {
						record = newRecord.substring(newRecord.indexOf("O|"), newRecord.indexOf(CR));

					} else if (newRecord.contains("Q|")) {
						record = newRecord.substring(newRecord.indexOf("Q|"), newRecord.indexOf(CR));
					} else if (newRecord.contains("R|")) {
						record = newRecord.substring(newRecord.indexOf("R|"), newRecord.indexOf(CR));

					} else if (newRecord.contains("L|")) {
						record = newRecord.substring(newRecord.indexOf("L|"), newRecord.indexOf(CR));

					} else {
						continue;
					}

					FilterdRecordList.add(record.toString() + CR);
				}

			} else {
				continue;
			}

		}

		String[] arrValues = new String[FilterdRecordList.size()];
		for (int index = 0; index < FilterdRecordList.size(); index++) {
			arrValues[index] = FilterdRecordList.get(index).toString();
		}

		return arrValues;

	}

	public Class<? extends LIS2A2Msg> getMsgType(List<LIS2A2Record> records) {
		log.debug("Detecting message type");
		Class<? extends LIS2A2Msg> msgType = null;
		for (LIS2A2Record record : records)
			if (record instanceof OrderASTMRecord)
				msgType = LIS2A2OrderMsg.class;
			else if (record instanceof QueryASTMRecord)
				msgType = LIS2A2QueryAstmMsg.class;
			else if (record instanceof ResultASTMRecord)
				msgType = LIS2A2ResultAstmMsg.class;
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

	private void storeResultMsg(String lIS2A2ResultMsg, HeaderRecordAstm headerRecord, String specimenId,
			MessageTransaction transaction) {
		Machine machine = getMachineInfoByPath();

		AccessResult accessResult = validateAccessInfo(headerRecord);

		switch (accessResult) {
		case ACCESS_DENIED_WRONG_MACHINE_IDENTITY:
			log.error(
					"\n***************************************************************************************************************************\n");
			log.error("Machine " + headerRecord.getSenderName() + " can not access middleware for follwing reason: "
					+ AccessResult.ACCESS_DENIED_WRONG_MACHINE_IDENTITY.toString());
			log.error(
					"\n***************************************************************************************************************************\n");

			Machine machineObj = getMachineInformation(headerRecord.getSenderName());

			if (machineObj != null) {
				if (machineObj.getMachineType().getIsMachineNameRequired()) {
					return;
				}
			}

			break;

		case ACCESS_DENIED_WRONG_USER_NAME:
			log.error(
					"\n***************************************************************************************************************************\n");
			log.error(" \n Machine " + headerRecord.getSenderName()
					+ " can not access middleware for follwing reason: \n "
					+ AccessResult.ACCESS_DENIED_WRONG_USER_NAME.toString());
			log.error(
					"\n***************************************************************************************************************************\n");

			break;

		case ACCESS_DENIED_WRONG_USER_PASS:
			log.error(
					"\n***************************************************************************************************************************\n");
			log.error(" \n Machine " + headerRecord.getSenderName()
					+ " can not access middleware for follwing reason: \n "
					+ AccessResult.ACCESS_DENIED_WRONG_USER_PASS.toString());
			log.error(
					"\n***************************************************************************************************************************\n");

			break;

		case ACCESS_DENIED_LICENCE_EXPIRED:
			log.error(
					"\n***************************************************************************************************************************\n");
			log.error(
					"\n Machine " + headerRecord.getSenderName() + " can not access middleware for follwing reason: \n"
							+ AccessResult.ACCESS_DENIED_LICENCE_EXPIRED.toString());
			log.error(
					"\n***************************************************************************************************************************\n");

			break;

		case ACCESS_DENIED_MACHINE_NOT_ENABLED:
			log.error(
					"\n***************************************************************************************************************************\n");
			log.error(
					"\n Machine " + headerRecord.getSenderName() + " can not access middleware for follwing reason: \n"
							+ AccessResult.ACCESS_DENIED_MACHINE_NOT_ENABLED.toString());
			log.error(
					"\n***************************************************************************************************************************\n");

			break;
		default:
			break;

		}

		// for(index = 0 ; )

		MachineResult mResult = new MachineResult();
		mResult.setMachineName(machine.getName());
		mResult.setSampleNo(specimenId);
		mResult.setIsSentToLIS(false);
		mResult.setMessageTransaction(transaction);
		// mResult.setTestCode();
		// mResult.getDataOrMeasurementValue()
		MachineResultService mResultService = (MachineResultService) SpringUtil.getBean("MachineResultService");
		mResultService.addResult(mResult);
	}

	private void storeQueryMsg(String lIS2A2QueryMsg, HeaderRecord headerRecord, String specimenId,
			MessageTransaction messageTransaction) {
		Machine machine = getMachineInfoByPath();

		/*
		 * AccessResult accessResult = validateAccessInfo(headerRecord); switch
		 * (accessResult) { case ACCESS_DENIED_WRONG_MACHINE_IDENTITY: log.error(
		 * "\n***************************************************************************************************************************\n"
		 * ); log.error("\n Machine " + headerRecord.getSenderName() +
		 * " can not access middleware for follwing reason: \n " +
		 * AccessResult.ACCESS_DENIED_WRONG_MACHINE_IDENTITY.toString()); log.error(
		 * "\n***************************************************************************************************************************\n"
		 * );
		 * 
		 * Machine machineObj = getMachineInformation(headerRecord.getSenderName());
		 * 
		 * if (machineObj != null) { if
		 * (machineObj.getMachineType().getIsMachineNameRequired()) { return; } }
		 * 
		 * break;
		 * 
		 * case ACCESS_DENIED_WRONG_USER_NAME: log.error(
		 * "\n***************************************************************************************************************************\n"
		 * ); log.error("\n Machine " + headerRecord.getSenderName() +
		 * " can not access middleware for follwing reason: \n " +
		 * AccessResult.ACCESS_DENIED_WRONG_USER_NAME.toString()); log.error(
		 * "\n***************************************************************************************************************************\n"
		 * );
		 * 
		 * break;
		 * 
		 * case ACCESS_DENIED_WRONG_USER_PASS: log.error(
		 * "\n***************************************************************************************************************************\n"
		 * ); log.error(" \n Machine " + headerRecord.getSenderName() +
		 * " can not access middleware for follwing reason: \n" +
		 * AccessResult.ACCESS_DENIED_WRONG_USER_PASS.toString()); log.error(
		 * "\n***************************************************************************************************************************\n"
		 * );
		 * 
		 * break;
		 * 
		 * case ACCESS_DENIED_LICENCE_EXPIRED: log.error(
		 * "\n***************************************************************************************************************************\n"
		 * ); log.error("\n Machine " + headerRecord.getSenderName() +
		 * " can not access middleware for follwing reason:\n " +
		 * AccessResult.ACCESS_DENIED_LICENCE_EXPIRED.toString()); log.error(
		 * "\n***************************************************************************************************************************\n"
		 * );
		 * 
		 * break;
		 * 
		 * case ACCESS_DENIED_MACHINE_NOT_ENABLED:
		 * 
		 * log.error(
		 * "\n***************************************************************************************************************************\n"
		 * ); log.error("Machine " + headerRecord.getSenderName() +
		 * " can not access middleware for follwing reason: \n " +
		 * AccessResult.ACCESS_DENIED_MACHINE_NOT_ENABLED.toString()); log.error(
		 * "\n***************************************************************************************************************************\n"
		 * );
		 * 
		 * break; default: break;
		 * 
		 * }
		 */
		MachineQuery machineQuery = new MachineQuery();
		machineQuery.setMachineName(machine.getName());
		machineQuery.setMessageTransaction(messageTransaction);
		machineQuery.setSampleNo(specimenId);
		machineQuery.setMachine(machine);
		MachineQueryService mQueryService = (MachineQueryService) SpringUtil.getBean("MachineQueryService");
		mQueryService.addQuery(machineQuery);
	}
	
	
	private void storeQueryMsg(String lIS2A2QueryMsg, HeaderASTMRecord headerRecord, String specimenId,
			MessageTransaction messageTransaction) 
	{
		Machine machine = getMachineInfoByPath();

		
		MachineQuery machineQuery = new MachineQuery();
		machineQuery.setMachineName(machine.getName());
		machineQuery.setMessageTransaction(messageTransaction);
		machineQuery.setSampleNo(specimenId);
		machineQuery.setMachine(machine);
		MachineQueryService mQueryService = (MachineQueryService) SpringUtil.getBean("MachineQueryService");
		mQueryService.addQuery(machineQuery);
	}

	public static String create() {
		// TODO Auto-generated method stub
		return null;
	}

	private void convertAndForward(Emerald22ResultData emerald22ResultData) {

		initiateObjects();

		List<LIS2A2Msg>[] arrList2A2Msg = null ;
		
		//arrList2A2Msg = stringMsgToAstmMsg(emerald22ResultData);
		Machine machine = getMachine();

		setLkpMessageTransactionDirection(MessageDirection.IN);
		//setLkpMessageTransactionType(MessageTransactionType.RESULT_RECEIVED);

		// Received Query String Message Type
		for (List<LIS2A2Msg> lis2A2Msg : arrList2A2Msg) {
			if (lis2A2Msg instanceof LIS2A2ResultAstmMsg) {
				LIS2A2ResultAstmMsg LIS2A2ResultMsg;
				LIS2A2ResultMsg = (LIS2A2ResultAstmMsg) lis2A2Msg;
				LIS2A2ResultMsg.getHeaderASTMRecords();
				for (HeaderASTMRecord headerRecord : LIS2A2ResultMsg.getHeaderASTMRecords()) {
					for (PatientASTMRecord patientRecord : LIS2A2ResultMsg.getPatientASTMRecords()) {
						log.debug("Finding O records for " + patientRecord.asString());
						globalHeaderRecord = headerRecord;
						for (OrderASTMRecord orderRecord : LIS2A2ResultMsg.getOrderRecords(patientRecord)) {
							MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());

							switch (machineType) {

							case ABBOTT_CELL_DYN_EMERALD_22:
								sampleBarcode = orderRecord.getSpecimenId(4).toString();
								createNewMessageTransaction(sampleBarcode);
								break;
							default:
								break;
							}
						}
					}
				}

				/*messageTransaction = createMessageTransaction(resultObject.originalInput, messageTransactionService,
						machine, messageTransaction, lkpMessageTransactionDirection, lkpMessageTransactionType,
						sampleBarcode, globalHeaderRecord);*/
				LIS2A2ResultMsg.setMessageTransactionID(messageTransaction.getRid());

				conf.recipient.tell(lis2A2Msg, self());
			}
		}
	}

}