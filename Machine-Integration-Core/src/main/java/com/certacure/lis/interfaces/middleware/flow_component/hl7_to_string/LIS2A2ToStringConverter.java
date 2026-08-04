package com.certacure.lis.interfaces.middleware.flow_component.hl7_to_string;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionType;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.astm.LIS2A2OrderASTM;
import com.certacure.lis.interfaces.middleware.parser.hl7.CobasPROHL7Generator;
import com.certacure.lis.interfaces.middleware.parser.hl7.CobasPROHL7Generator.CobasPRO_OrderMessageData;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageTransactionType;
import com.certacure.lis.interfaces.middleware.util.MachineTypeEnum;
import com.certacure.lis.interfaces.service.LkpService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.MessageTransactionService;

import akka.japi.pf.ReceiveBuilder;
import ca.uhn.hl7v2.model.v25.message.OML_O33;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class LIS2A2ToStringConverter extends FlowComponent<RecipientConf> {

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder.match(LIS2A2Msg.class, this::convertAndForward)
				.match(LIS2A2Msg[].class, this::convertAndForward)
				.match(httpRequstTransaction.class, this::convertAndForward).build();
	}

	private void convertAndForward(httpRequstTransaction httpRequestTransaction) {

		conf.recipient.tell(httpRequestTransaction, self());

	}

	private void convertAndForward(LIS2A2Msg[] arrlis2a2Msg) {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());

		OML_O33 oml_o_33 = new OML_O33();

		if (machineType == MachineTypeEnum.SIEMENS_ATELLICA_HL7_251) {
			HL7Parser parser = new HL7Parser("2.5.1");
			//oml_o_33 = parser.getOML_O_33_Message(arrlis2a2Msg);

		}

		conf.recipient.tell(oml_o_33, self());
	}

	private void convertAndForward(LIS2A2Msg lis2a2Msg) {

		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
		
		HL7Parser parser;
		String strOML_Msg = null;
		String strHL7Order_Msg = null;
		CobasPROHL7Generator cobasPROHL7GeneratorObj;

		// OML_O33 oml_o_33 = new OML_O33();
		String astmMessage;

		switch (machineType) {
		case SIEMENS_ATELLICA_HL7_251:
			parser = new HL7Parser("2.5.1");
			strOML_Msg = parser.getOML_O_33_Message(lis2a2Msg);
			createNewMessageTransactionType(lis2a2Msg, strOML_Msg, MessageTransactionType.TEST_SELECTION);
			conf.recipient.tell(strOML_Msg, self());
			break;

		case ROCHE_COBAS_PRO_HL7_251:
			//parser = new HL7Parser("2.5.1");
			cobasPROHL7GeneratorObj = new CobasPROHL7Generator("2.5.1");
			
			strHL7Order_Msg = cobasPROHL7GeneratorObj.generateOrderMessage(lis2a2Msg , MessageTransactionType.TEST_SELECTION ,"99ROC","LAB-28R","HL70485" , "");
					
					//.getOrderHL7Message(lis2a2Msg);
			createNewMessageTransactionType(lis2a2Msg, strHL7Order_Msg, MessageTransactionType.TEST_SELECTION);
			conf.recipient.tell(strHL7Order_Msg, self());
			break;

		default:

			astmMessage = lis2a2Msg.asString();

			if (!astmMessage.isEmpty()) {
				LIS2A2OrderASTM lIS2A2OrderASTM;
				lIS2A2OrderASTM = (LIS2A2OrderASTM) lis2a2Msg;
				String strBarcode = lIS2A2OrderASTM.getOrderRecords().get(0).getSpecimenId(2).toString();
				createNewMessageTransactionType(strBarcode, astmMessage, MessageTransactionType.TEST_SELECTION);
			}

			conf.recipient.tell(astmMessage, self());
			break;
		}

	}
	
	

	private void createNewMessageTransactionType(LIS2A2Msg message, String astmMessage,
			MessageTransactionType msgType) {
		
		MessageTransactionService messageTransactionService = (MessageTransactionService) SpringUtil
				.getBean("MessageTransactionService");
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		LkpService lkpService = (LkpService) SpringUtil.getBean("LkpService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());

		MessageTransaction messageTransaction = new MessageTransaction();

		LkpMessageTransactionDirection msgDirection = lkpService.findOneAnyLkp(
				java.util.Arrays
						.asList(new SearchCriterion("code", MessageDirection.OUT.getValue(), FilterOperator.eq)),
				LkpMessageTransactionDirection.class);

		LkpMessageTransactionType msgTransactionType = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", msgType.getValue(), FilterOperator.eq)),
				LkpMessageTransactionType.class);

		LIS2A2OrderMsg lIS2A2OrderMsg;
		lIS2A2OrderMsg = (LIS2A2OrderMsg) message;
		messageTransaction.setMachine(machine);
		messageTransaction.setMessageType(msgTransactionType);
		messageTransaction.setNotes("TEST SELECTION DONE");
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(msgDirection);
		messageTransaction.setMessageBody(astmMessage);
		messageTransaction.setIsValidated(true);
		messageTransaction.setIsSuccess(true);
		messageTransaction.setIsProcessed(true);
		messageTransaction.setIsSent(true);
		
		

		if (!lIS2A2OrderMsg.getAllRecords().isEmpty()) {
			messageTransaction.setBarcode(lIS2A2OrderMsg.getOrderRecords().get(0).getSpecimenId(2).toString());

		} else {
			String barcode = "XXXXXXXXX--NO BARCODE";
			messageTransaction.setBarcode(barcode);
		}

		messageTransactionService.add(messageTransaction);
	}

	
	private void createNewMessageTransactionType(String strBarcode, String astmMessage,
			MessageTransactionType msgType) {
		MessageTransactionService messageTransactionService = (MessageTransactionService) SpringUtil
				.getBean("MessageTransactionService");
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		LkpService lkpService = (LkpService) SpringUtil.getBean("LkpService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());

		MessageTransaction messageTransaction = new MessageTransaction();

		LkpMessageTransactionDirection msgDirection = lkpService.findOneAnyLkp(
				java.util.Arrays
						.asList(new SearchCriterion("code", MessageDirection.OUT.getValue(), FilterOperator.eq)),
				LkpMessageTransactionDirection.class);

		LkpMessageTransactionType msgTransactionType = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", msgType.getValue(), FilterOperator.eq)),
				LkpMessageTransactionType.class);

		
		messageTransaction.setMachine(machine);
		messageTransaction.setMessageType(msgTransactionType);
		messageTransaction.setNotes("TEST SELECTION DONE");
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(msgDirection);
		messageTransaction.setMessageBody(astmMessage);
		messageTransaction.setIsValidated(true);
		messageTransaction.setIsSuccess(true);
		messageTransaction.setIsProcessed(true);
		messageTransaction.setIsSent(true);
		
		

		if (! strBarcode.isEmpty()) {
			messageTransaction.setBarcode(strBarcode);

		} else {
			String barcode = "XXXXXXXXX--NO BARCODE";
			messageTransaction.setBarcode(barcode);
		}

		messageTransactionService.add(messageTransaction);
	}

	
	public Machine getMachineInfoByPath() {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		return machine;
	}
}
