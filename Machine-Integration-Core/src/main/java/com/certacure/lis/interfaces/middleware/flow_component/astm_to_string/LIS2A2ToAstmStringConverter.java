package com.certacure.lis.interfaces.middleware.flow_component.astm_to_string;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionType;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.astm.LIS2A2OrderASTM;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageTransactionType;
import com.certacure.lis.interfaces.service.LkpService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.MessageTransactionService;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;


import akka.japi.pf.ReceiveBuilder;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.OML_O33;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class LIS2A2ToAstmStringConverter extends FlowComponent<RecipientConf> {

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
								.match(LIS2A2Msg.class, this::convertAndForward)
								.match(OML_O33.class, this::convertAndForward)
								.build();
	}

	private void convertAndForward(OML_O33 OMLO33)
	{
		String strOML_O33;
		try {
			strOML_O33 = OMLO33.encode();
			if (!strOML_O33.isEmpty()) 
			{
				createNewMessageTransactionType(OMLO33 , strOML_O33, MessageTransactionType.TEST_SELECTION);
				conf.recipient.tell(OMLO33, self());
			}
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void convertAndForward(LIS2A2Msg[] lis2a2Msg) {

		String[] arrAstmMessage = new String[lis2a2Msg.length];
		for (int index = 0; index < lis2a2Msg.length; index++) {
			String astmMessage = lis2a2Msg[index].asString();
			arrAstmMessage[index] = astmMessage;

			if (!astmMessage.isEmpty()) {
				createNewMessageTransactionType(lis2a2Msg[index], astmMessage, MessageTransactionType.TEST_SELECTION);
			}
		}

		conf.recipient.tell(arrAstmMessage, self());

	}

	private void convertAndForward(LIS2A2Msg lis2a2Msg) {

		String astmMessage = lis2a2Msg.asString();

		if (!astmMessage.isEmpty()) {
			createNewMessageTransactionType(lis2a2Msg, astmMessage, MessageTransactionType.TEST_SELECTION);
		}

		conf.recipient.tell(astmMessage, self());
	}

	private void createNewMessageTransactionType(OML_O33 OML_O33, String astmMessage, MessageTransactionType msgType) {
		MessageTransactionService messageTransactionService = (MessageTransactionService) SpringUtil.getBean("MessageTransactionService");
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		LkpService lkpService = (LkpService) SpringUtil.getBean("LkpService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());

		MessageTransaction messageTransaction = new MessageTransaction();

		LkpMessageTransactionDirection msgDirection = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", MessageDirection.OUT.getValue(), FilterOperator.eq)),
				LkpMessageTransactionDirection.class);

		LkpMessageTransactionType msgTransactionType = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", msgType.getValue(), FilterOperator.eq)),
				LkpMessageTransactionType.class);

		
			OML_O33 OML_O33_MESSAGE = (OML_O33) OML_O33;
		
			
			messageTransaction.setMachine(machine);
			messageTransaction.setMessageType(msgTransactionType);
			messageTransaction.setNotes("TEST SELECTION DONE");
			messageTransaction.setBranchId(machine.getBranchId());
			messageTransaction.setTenantId(machine.getTenantId());
			messageTransaction.setMessageDirection(msgDirection);
			messageTransaction.setMessageBody(astmMessage);
			//messageTransaction.setIsProcessed(false);
			messageTransaction.setIsSuccess(false);
			String strBarcode = OML_O33_MESSAGE.getSPECIMEN().getSPM().getSpecimenID().getEip1_PlacerAssignedIdentifier().getEi1_EntityIdentifier().getValue();
	
		if(!strBarcode.isEmpty())
		{
			messageTransaction.setBarcode(strBarcode);
			
		}else
		{
			String barcode = "XXXXXXXXX"; 
			messageTransaction.setBarcode(barcode);
		}
			messageTransactionService.add(messageTransaction);
		
	}


	private void createNewMessageTransactionType(LIS2A2Msg message, String astmMessage, MessageTransactionType msgType) {
		MessageTransactionService messageTransactionService = (MessageTransactionService) SpringUtil.getBean("MessageTransactionService");
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		LkpService lkpService = (LkpService) SpringUtil.getBean("LkpService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());

		MessageTransaction messageTransaction = new MessageTransaction();

		LkpMessageTransactionDirection msgDirection = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", MessageDirection.OUT.getValue(), FilterOperator.eq)),
				LkpMessageTransactionDirection.class);

		LkpMessageTransactionType msgTransactionType = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", msgType.getValue(), FilterOperator.eq)),
				LkpMessageTransactionType.class);
		
		
		if (message instanceof LIS2A2OrderASTM) 
		{
			LIS2A2OrderASTM lis2a2OrderASTM;
			lis2a2OrderASTM = (LIS2A2OrderASTM) message;
			messageTransaction.setMachine(machine);
			messageTransaction.setMessageType(msgTransactionType);
			messageTransaction.setNotes("TEST SELECTION DONE");
			messageTransaction.setBranchId(machine.getBranchId());
			messageTransaction.setTenantId(machine.getTenantId());
			messageTransaction.setMessageDirection(msgDirection);
			messageTransaction.setMessageBody(astmMessage);
			// messageTransaction.setIsProcessed(false);
			messageTransaction.setIsSuccess(false);

			if (!lis2a2OrderASTM.getAllRecords().isEmpty()) {
				messageTransaction.setBarcode(lis2a2OrderASTM.getOrderRecords().get(0).getSpecimenId(2));

			} else {
				// String barcode = "XXXXXXXXX";
				// messageTransaction.setBarcode(barcode);
			}

			messageTransactionService.add(messageTransaction);
		}

	}
	
	public Machine getMachineInfoByPath() {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(
				getContext().parent().toString());
		return machine;
	}
}
