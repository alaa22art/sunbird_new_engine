package com.certacure.lis.interfaces.middleware.flow_component.DBToJSONOverHttp;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.stream.IntStream;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.enums.Enums.REQUEST_RESULT_TYPE;
import com.certacure.lis.interfaces.middleware.enums.Enums.VALUDATION_RESULT_TYPE;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;
import com.certacure.lis.interfaces.middleware.util.LowLevelUtils;
import com.certacure.lis.interfaces.service.MachineService;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class DBToJSONOverHttpController extends FlowComponent<DBToJSONOverHttpControllerConf> {

    private PartialFunction<Object, BoxedUnit> idleState;
    private PartialFunction<Object, BoxedUnit> receivingState;
    private PartialFunction<Object, BoxedUnit> sendingState;

    private final List<String> msgBeingSent= new ArrayList<>();
    private int sendingFrameNumber;
    private final StringBuilder msgBeingReceived= new StringBuilder();
    private MachineService machineService= (MachineService) SpringUtil.getBean("MachineService");
    private Machine machine;

    Timer timer;

    @Override
    public PartialFunction<Object, BoxedUnit> getBehaviour() {
        return sendingState;
    }

    private DBToJSONOverHttpController() {
        idleState= getIdleState();
        receivingState= getReceivingState();
        sendingState= getSendingState();
    }

    @Override
    protected void init() throws Exception {

        super.init();
        machine= machineService.getMachineByActorPath(
            getContext().parent().toString());
        // conf.highLevelRecipient.tell(machine, self());
    }

    private PartialFunction<Object, BoxedUnit> getIdleState() {
        return ReceiveBuilder.match(httpRequstTransaction.class, this::goToSendingState)
            .matchAny(__ -> goToReceivingState()).build();
    }

    private void goToSendingState(httpRequstTransaction httpRequstTransactionObj) {
        if (httpRequstTransactionObj.getRequest_result_type() == REQUEST_RESULT_TYPE.FAILED ||
            httpRequstTransactionObj.getValudation_result_type() == VALUDATION_RESULT_TYPE.FAILED) {
            sendNAK(httpRequstTransactionObj);
        } else
            if (httpRequstTransactionObj.getRequest_result_type() == REQUEST_RESULT_TYPE.SUCCUSS ||
                httpRequstTransactionObj
                    .getValudation_result_type() == VALUDATION_RESULT_TYPE.SUCCUSS) {
                        sendACK(httpRequstTransactionObj);
                    }
    }

    private void goToSendingState() {
        System.out.println("CLENT DATA SEND START");
        // unstashAll();
    }

    private void sendACK(httpRequstTransaction httpRequstTransactionObj) {
        String ACK= "MSH|~^\\&|Certa|Engine|KHCC|Cloverleaf|" +
            httpRequstTransactionObj.getResponseDateTime() + "||" + "ACK" + "|" +
            httpRequstTransactionObj.getMessageID() + "|" +
            httpRequstTransactionObj.getEnviroment() + "|" + "2.4";

        ACK= LowLevelUtils.VT + ACK + LowLevelUtils.CR + LowLevelUtils.FS + LowLevelUtils.CR;

        // conf.lowLevelRecipient.tell(new BytesMessage(ACK), self());
    }

    private void goToReceivingState() {
        log.debug("Transitioning to receiving state");
        // msgBeingReceived.setLength(0);
        // conf.lowLevelRecipient.tell(ACKBytes, self());
        context().become(receivingState);
    }

    private PartialFunction<Object, BoxedUnit> getSendingState() {
        return ReceiveBuilder.matchAny(__ -> goToSendingState()).build();
    }

    private PartialFunction<Object, BoxedUnit> getReceivingState() {
        return ReceiveBuilder.match(BytesMessage.class, bytesMessage -> {
            System.out
                .println("Data Arrive >>>>>>>>>>>>>>>>>>" + extractPayload(bytesMessage.bytes));
            msgBeingReceived.append(extractPayload(bytesMessage.bytes));
            changeStateAndProcessCompletedMessage();
        }).match(httpRequstTransaction.class, this::goToSendingState)

            .matchAny(__ -> {
                stash();
            }).build();
    }

    private void sendNAK(httpRequstTransaction httpRequstTransactionObj) {
        // MSH|~^\&|UJO7 DFT ORDERING|KHCC|KHCC|myCare|||ACK|A0222784274|P|2.4|
        // MSA|AE|0222784274|MRN is Blank|

        String strMsgID= httpRequstTransactionObj.getMessageID();

        if (strMsgID == null) {
            strMsgID= "";
        }
        String NAK= "MSH|~^\\&|Certa|Engine|KHCC|Cloverleaf|" +
            httpRequstTransactionObj.getResponseDateTime() + "||" + "ACK" + "|" + "A" +
            httpRequstTransactionObj.getMessageID() + "|" +
            httpRequstTransactionObj.getEnviroment() + "|" + "2.4" + LowLevelUtils.CR + "MSA" + "|" + "AE" + "|" +
            strMsgID + "|"
            // + httpRequstTransactionObj.getErrorType()
            + strMsgID + httpRequstTransactionObj.getErrorDesc() + "|";

        NAK= LowLevelUtils.VT + NAK + LowLevelUtils.CR + LowLevelUtils.FS + LowLevelUtils.CR;

        // conf.lowLevelRecipient.tell(new BytesMessage(NAK), self());

        System.out.println("Dead End");
    }

    private void changeStateAndProcessCompletedMessage() {
        // goToIdleState();
        // conf.highLevelRecipient.tell(msgBeingReceived.toString(), self());
        msgBeingReceived.setLength(0);
        // goToIdleState();

    }

    private void goToIdleState() {
        log.debug("Transitioning to idle state");
        unstashAll();
        context().become(idleState);
    }

    private String extractPayload(List<Byte> frameBytes) {

        // List<Byte> payloadBytes = frameBytes.subList(2, index);
        List<Byte> payloadBytes= frameBytes;
        byte[] bytes= new byte[payloadBytes.size()];
        IntStream.range(0, payloadBytes.size()).forEach(i -> bytes[i]= payloadBytes.get(i));
        return new String(bytes);
    }

}