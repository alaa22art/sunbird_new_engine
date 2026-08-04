/*package com.certacure.lis.interfaces.middleware.flow_component.hl724ClientOverTcp;

import static com.certacure.lis.interfaces.middleware.flow_component.astme138194archi.AstmE138194ArchiProtocol.ACKBytes;
import static com.certacure.lis.interfaces.middleware.flow_component.astme138194archi.AstmE138194ArchiProtocol.ENQBytes;
import static com.certacure.lis.interfaces.middleware.flow_component.astme138194archi.AstmE138194ArchiProtocol.EOTBytes;
import static com.certacure.lis.interfaces.middleware.flow_component.astme138194archi.AstmE138194ArchiProtocol.VTBytes;
import static com.certacure.lis.interfaces.middleware.flow_component.astme138194archi.AstmE138194ArchiProtocol.FSBytes;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.CR;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ETB;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ETX;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.LF;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.STX;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.EOT;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.VT;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.FS;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.getCheckSum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.enums.Enums.REQUEST_RESULT_TYPE;
import com.certacure.lis.interfaces.middleware.enums.Enums.VALUDATION_RESULT_TYPE;
import com.certacure.lis.interfaces.middleware.flow_component.json.parsingHL7v24Error;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;
import com.certacure.lis.interfaces.service.DataInboundHL7MessageService;
import com.certacure.lis.interfaces.service.ElegabalityApprovalService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.MessageTransactionService;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v25.message.ACK;

import akka.japi.pf.ReceiveBuilder;
import akka.util.Switch;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class Hl724ClientOverTcpController extends FlowComponent<Hl724ClientOverTcpControllerConf> {

    private PartialFunction<Object, BoxedUnit> idleState;
    private PartialFunction<Object, BoxedUnit> receivingState;
    private PartialFunction<Object, BoxedUnit> sendingState;

    private final List<String> msgBeingSent= new ArrayList<>();
    private int sendingFrameNumber;
    private final StringBuilder msgBeingReceived= new StringBuilder();
    private MachineService machineService= (MachineService) SpringUtil.getBean("MachineService");
    private Machine machine;
    private ElegabalityApprovalService elegabalityApprovalService;

    Timer timer;

    @Override
    public PartialFunction<Object, BoxedUnit> getBehaviour() {
        return sendingState;
    }

    private Hl724ClientOverTcpController() {
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

        ACK= VT + ACK + CR + FS + CR;

        // conf.lowLevelRecipient.tell(new BytesMessage(ACK), self());
    }

    private void goToReceivingState() {
        log.debug("Transitioning to receiving state");
        // msgBeingReceived.setLength(0);
        // conf.lowLevelRecipient.tell(ACKBytes, self());
        context().become(receivingState);
    }

    private PartialFunction<Object, BoxedUnit> getSendingState() {
        return ReceiveBuilder.match(String.class, this::sendMessage)
        		.matchAny(__ -> goToSendingState()).build();
    }

    private void sendMessage(String msg) {
		conf.lowLevelRecipient.tell(new BytesMessage(msg), self());

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
            strMsgID = "";
        }
        String NAK= "MSH|~^\\&|Certa|Engine|KHCC|Cloverleaf|" +
            httpRequstTransactionObj.getResponseDateTime() + "||" + "ACK" + "|" + "A" +
            httpRequstTransactionObj.getMessageID() + "|" +
            httpRequstTransactionObj.getEnviroment() + "|" + "2.4" + CR + "MSA" + "|" + "AE" + "|" +
            strMsgID + "|"
            // + httpRequstTransactionObj.getErrorType()
            + strMsgID + httpRequstTransactionObj.getErrorDesc() + "|";

        NAK= VT + NAK + CR + FS + CR;

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

}*/

package com.certacure.lis.interfaces.middleware.flow_component.LabOrderCollecterScheduler;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.service.MachineService;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class LabOrderController extends FlowComponent<LabOrderControllerConf> {




    private MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
    private Machine machine;

    private LabOrderController() {

    }

    @Override
    protected void init() throws Exception {

        super.init();
        machine = machineService.getMachineByActorPath(
            getContext().parent().toString());
        initiateObjects();

        // conf.highLevelRecipient.tell(machine, self());
    }


    private void initiateObjects() {


    	machine.setConnected(true);
    	//machineService.addMachine(machine);
    	
    }

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}





















}


