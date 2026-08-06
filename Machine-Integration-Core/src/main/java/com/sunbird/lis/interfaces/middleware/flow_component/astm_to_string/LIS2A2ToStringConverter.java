package com.sunbird.lis.interfaces.middleware.flow_component.astm_to_string;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionType;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MessageTransaction;
import com.sunbird.lis.interfaces.middleware.core.FlowComponent;
import com.sunbird.lis.interfaces.middleware.core.RecipientConf;
import com.sunbird.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.sunbird.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.sunbird.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageTransactionType;
import com.sunbird.lis.interfaces.service.LkpService;
import com.sunbird.lis.interfaces.service.MachineService;
import com.sunbird.lis.interfaces.service.MessageTransactionService;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class LIS2A2ToStringConverter extends FlowComponent<RecipientConf> {

    @Override
    public PartialFunction<Object, BoxedUnit> getBehaviour() {
        return ReceiveBuilder
            .match(LIS2A2Msg.class, this::convertAndForward)
            .match(httpRequstTransaction.class, this::convertAndForward)
            .build();
    }

    private void convertAndForward(httpRequstTransaction httpRequestTransaction) {

        conf.recipient.tell(httpRequestTransaction, self());

    }

    private void convertAndForward(LIS2A2Msg lis2a2Msg) {

        String astmMessage= lis2a2Msg.asString();

        if (!astmMessage.isEmpty()) {
            createNewMessageTransactionType(lis2a2Msg, astmMessage,
                MessageTransactionType.TEST_SELECTION);
        }

        conf.recipient.tell(astmMessage, self());
    }

    private void createNewMessageTransactionType(LIS2A2Msg message, String astmMessage,
        MessageTransactionType msgType) {
        MessageTransactionService messageTransactionService= (MessageTransactionService) SpringUtil
            .getBean("MessageTransactionService");
        MachineService machineService= (MachineService) SpringUtil.getBean("MachineService");
        LkpService lkpService= (LkpService) SpringUtil.getBean("LkpService");
        Machine machine= machineService.getMachineByActorPath(getContext().parent().toString());

        MessageTransaction messageTransaction= new MessageTransaction();

        LkpMessageTransactionDirection msgDirection= lkpService.findOneAnyLkp(
            java.util.Arrays.asList(
                new SearchCriterion("code", MessageDirection.OUT.getValue(), FilterOperator.eq)),
            LkpMessageTransactionDirection.class);

        LkpMessageTransactionType msgTransactionType= lkpService.findOneAnyLkp(
            java.util.Arrays
                .asList(new SearchCriterion("code", msgType.getValue(), FilterOperator.eq)),
            LkpMessageTransactionType.class);

        LIS2A2OrderMsg lIS2A2OrderMsg;
        lIS2A2OrderMsg= (LIS2A2OrderMsg) message;
        messageTransaction.setMachine(machine);
        messageTransaction.setMessageType(msgTransactionType);
        messageTransaction.setNotes("TEST SELECTION DONE");
        messageTransaction.setBranchId(machine.getBranchId());
        messageTransaction.setTenantId(machine.getTenantId());
        messageTransaction.setMessageDirection(msgDirection);
        messageTransaction.setMessageBody(astmMessage);
        messageTransaction.setIsValidated(false);
        messageTransaction.setIsSuccuss(false);

        if (!lIS2A2OrderMsg.getPatientRecords().isEmpty()) {
            messageTransaction.setBarcode(
                lIS2A2OrderMsg.getOrderRecords(lIS2A2OrderMsg.getPatientRecords().get(0)).get(0)
                    .getSpecimenId().trim());

        } else {
            String barcode= "XXXXXXXXX";
            messageTransaction.setBarcode(barcode);
        }

        messageTransactionService.add(messageTransaction);
    }

    public Machine getMachineInfoByPath() {
        MachineService machineService= (MachineService) SpringUtil.getBean("MachineService");
        Machine machine= machineService.getMachineByActorPath(
            getContext().parent().toString());
        return machine;
    }
}
