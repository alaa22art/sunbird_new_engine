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

package com.certacure.lis.interfaces.middleware.flow_component.hl724ClientOverTcp;

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
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.OutboundHl7MessageSequance;
import com.certacure.lis.interfaces.entities.AckMessageSequance;
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
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageSourceType;
import com.certacure.lis.interfaces.service.AckMessageSequanceService;
import com.certacure.lis.interfaces.service.DataInboundHL7MessageService;
import com.certacure.lis.interfaces.service.ElegabalityApprovalOrderService;
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
    private ElegabalityApprovalOrderService elegabalityApprovalOrderService;
    private MessageTransactionService messageTransactionService;
    private MessageTransaction messageTransaction;
    private AckMessageSequance ackMessageSequance;
    private AckMessageSequanceService ackMessageSequanceService;
    

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
        initiateObjects();

        // conf.highLevelRecipient.tell(machine, self());
    }
    

    private void initiateObjects() {
        messageTransactionService= (MessageTransactionService) SpringUtil
            .getBean("MessageTransactionService");
        
        machineService= (MachineService) SpringUtil.getBean("MachineService");
        
        ackMessageSequanceService = (AckMessageSequanceService) SpringUtil.getBean("AckMessageSequanceService");
        
        elegabalityApprovalService = (ElegabalityApprovalService) SpringUtil.getBean("ElegabalityApprovalService");
        
        elegabalityApprovalOrderService = (ElegabalityApprovalOrderService) SpringUtil.getBean("ElegabalityApprovalOrderService");

        machine= machineService.getMachineByActorPath(getContext().parent().toString());
        messageTransaction= new MessageTransaction();
        messageTransaction.setIsSuccess(false);
        messageTransaction.setIsSent(false);
        messageTransaction.setIsValidated(false);

    }


    private PartialFunction<Object, BoxedUnit> getIdleState() {
        return ReceiveBuilder
        		.match(httpRequstTransaction.class, this::goToSendingState)
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
//        System.out.println(msg);
//        messageTransaction = messageTransactionService.addMessageTransaction(machine,
//              msg,
//            MessageDirection.OUT.toString(), MessageSourceType.HL7.toString(), true, false,true,
//            "Sent new Message" , "undefined");
//         unstashAll();
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
        return ReceiveBuilder
        		.match(String.class, this::sendMessage)
        		.match(HL7MessageObj.class, this::sendMessage)
                .match(BytesMessage.class, this::receiveMsg)
                .matchAny(__ -> goToSendingState()).build();
    }
    
    private void sendMessage(String msg) {
        conf.lowLevelRecipient.tell(new BytesMessage(msg), self());
        
    }
    
    private void receiveMsg(BytesMessage msg) {
    	try {
			
    		System.out.println("CLENT DATA SEND START");
    		System.out.println(msg.toString());
    		Long messageControlId = extractMsgontrolID(msg.toString());
    		ackMessageSequance = ackMessageSequanceService.getElegTransByMessageId(messageControlId);
    		
    		if(ackMessageSequance == null)
    		{
    			messageTransaction = messageTransactionService.addMessageTransaction(machine,
        				msg.toString(),
        				MessageDirection.IN.toString(), MessageSourceType.HL7.toString(),MessageSourceType.HL7.getValue(), false, false,false,
        				"RECIVED ACK WITH UNKOWN MESSAGE CONTROL ID!! " , "ACK-Error");
    		}else 
    		{
    			if(ackMessageSequance.getTransactionType().equals("APPOINTMENT"))
    			{
    				elegabalityApprovalService.setElegabalityApprovalAsSent(ackMessageSequance.getEligibilityTransactionID());
        		
    				messageTransaction = messageTransactionService.addMessageTransaction(machine,
        				msg.toString(),
        				MessageDirection.IN.toString(), MessageSourceType.ACK.toString(),MessageSourceType.ACK.getValue(), true, false,true,
        				
        				
        				"ACK MESSAGE ["+messageControlId+"] ACCEPTED" , "ACK APPOINTMENT " + "[ "+ messageControlId+" ] "  + "[" + ackMessageSequance.getEligibilityTransactionID()+"]" );
    			}else if (ackMessageSequance.getTransactionType().equals("ORDER"))
    			{
    				elegabalityApprovalOrderService.setElegabalityApprovalOrderAsSent(ackMessageSequance.getEligibilityTransactionID());
            		
    				messageTransaction = messageTransactionService.addMessageTransaction(machine,
        				msg.toString(),
        				MessageDirection.IN.toString(), MessageSourceType.ACK.toString(),MessageSourceType.ACK.getValue(), true, false,true,
        				"ACK MESSAGE ["+messageControlId+"] ACCEPTED" , "ACK ORDER " + "[ "+ messageControlId+" ] "  + "[" + ackMessageSequance.getEligibilityTransactionID()+"]" );
				}
				
			}
    		
    		
    	
		} catch (Exception e) {
			e.printStackTrace();
		}

        
    }
    
    private Long extractMsgontrolID(String msg) {
		String[] records = msg.split("/r");
		try {
			
			for (String record : records) {
				if (record.contains("MSH|")) {
					String mesageControlId = record.split(Pattern.quote("|"))[9];
					return Long.parseLong(mesageControlId);
				}
			}
			return -1L;
		} catch (Exception e) {
			return -1L;
		}
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
    
    public class HL7MessageObj
    {
    	public String operationType;
    	public String hl7MessageBody;
    	public String vistaOrderID;
    }

	private void sendMessage(HL7MessageObj hl7Msg) 
	{
		 conf.lowLevelRecipient.tell(hl7Msg, self());
	}

}


