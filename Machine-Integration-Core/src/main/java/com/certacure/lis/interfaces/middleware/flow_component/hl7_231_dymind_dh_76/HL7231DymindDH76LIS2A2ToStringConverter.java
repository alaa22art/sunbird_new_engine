package com.certacure.lis.interfaces.middleware.flow_component.hl7_231_dymind_dh_76;

import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageTransactionType;
import com.certacure.lis.interfaces.service.LkpService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.MessageTransactionService;
import com.certacure.core.common.util.SpringUtil;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class HL7231DymindDH76LIS2A2ToStringConverter extends FlowComponent<RecipientConf> {

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
								.match(LIS2A2Msg.class, this::convertAndForward)
								.build();
	}

	private void convertAndForward(LIS2A2Msg lis2a2Msg) {

		String astmMessage = lis2a2Msg.asString();

		if (!astmMessage.isEmpty()) {
			createNewMessageTransactionType(lis2a2Msg, astmMessage, MessageTransactionType.TEST_SELECTION);
		}

		conf.recipient.tell(astmMessage, self());
	}

	private void createNewMessageTransactionType(LIS2A2Msg message, String astmMessage, MessageTransactionType msgType) {
		MessageTransactionService messageTransactionService = (MessageTransactionService) SpringUtil.getBean("MessageTransactionService");
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		LkpService lkpService = (LkpService) SpringUtil.getBean("LkpService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());

		MessageTransaction messageTransaction = new MessageTransaction();
		//to be fixed later
		//		LkpMessageTransactionDirection msgDirection = lkpService.findOneLkp(
		//				(LkpMessageTransactionDirection e) -> e.getCode().equals(MessageTransactionDirections.IN),
		//				LkpMessageTransactionDirection.class);

//		LkpMessageTransactionType msgTransactionType = lkpService.findOneAnyLkp(
//				(LkpMessageTransactionType e) -> e.getCode().equals(MessageTransactionType.RESULT_RECEIVED),
//				LkpMessageTransactionType.class);

		LIS2A2OrderMsg lIS2A2OrderMsg;
		lIS2A2OrderMsg = (LIS2A2OrderMsg) message;
		messageTransaction.setMachine(machine);
		messageTransaction.setMessageType(null);
		messageTransaction.setNotes("TEST SELECTION DONE");
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		//		messageTransaction.setMessageDirection(msgDirection);
		messageTransaction.setMessageBody(astmMessage);
		//messageTransaction.setIsProcessed(false);
		messageTransaction.setIsSuccess(false);

		if (!lIS2A2OrderMsg.getPatientRecords().isEmpty()) {
			messageTransaction.setBarcode(
					lIS2A2OrderMsg.getOrderRecords(lIS2A2OrderMsg.getPatientRecords().get(0)).get(0).getSpecimenId().trim());

		} else {
			String barcode = "XXXXXXXXX";
			messageTransaction.setBarcode(barcode);
		}

		messageTransactionService.add(messageTransaction);
	}

	public Machine getMachineInfoByPath() {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(
				getContext().parent().toString());
		return machine;
	}
}
