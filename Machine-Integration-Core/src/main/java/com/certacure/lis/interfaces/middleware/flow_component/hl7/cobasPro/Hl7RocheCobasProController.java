package com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPro;

import static com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPure.Hl7PureProtocol.FSBytes;
import static com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPure.Hl7PureProtocol.VTBytes;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.CR;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import javax.swing.Timer;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionType;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MachineOrder;
import com.certacure.lis.interfaces.entities.MachineQuery;
import com.certacure.lis.interfaces.entities.MachineResult;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser;
import com.certacure.lis.interfaces.middleware.parser.hl7.UniqueSequenceGenerator;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageTransactionType;
import com.certacure.lis.interfaces.middleware.util.LowLevelUtils;
import com.certacure.lis.interfaces.service.CoreEventLogService;
import com.certacure.lis.interfaces.service.LabBranchService;
import com.certacure.lis.interfaces.service.LkpService;
import com.certacure.lis.interfaces.service.MachineOrderQueryResponceService;
import com.certacure.lis.interfaces.service.MachineOrderService;
import com.certacure.lis.interfaces.service.MachineQueryService;
import com.certacure.lis.interfaces.service.MachineResultService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.MachineTestsService;
import com.certacure.lis.interfaces.service.MachineTypePanelService;
import com.certacure.lis.interfaces.service.MachineTypeService;
import com.certacure.lis.interfaces.service.MessageTransactionService;
import com.certacure.lis.interfaces.service.PostDetailFinancialTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.japi.pf.ReceiveBuilder;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ACK;
import ca.uhn.hl7v2.model.v25.message.OML_O33;
import ca.uhn.hl7v2.model.v25.message.QBP_Q11;
import ca.uhn.hl7v2.model.v25.message.RSP_K11;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.v25.segment.MSA;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.QAK;
import ca.uhn.hl7v2.model.v25.segment.QPD;
import ca.uhn.hl7v2.parser.Parser;

public class Hl7RocheCobasProController extends FlowComponent<Hl7RocheCobasProControllerConf> {

	public enum RETURNED_RESULT {
		SUCCUSS, FAILED
	}

	private PartialFunction<Object, BoxedUnit> idleState;
	private PartialFunction<Object, BoxedUnit> receivingState;
	private PartialFunction<Object, BoxedUnit> sendingState;

	private final List<String> msgBeingSent = new ArrayList<>();
	private int sendingFrameNumber;
	private static int counter = 0;
	private Timer senderTimer;
	private String resendMessage = "";
	private List<String> lstResendMessage;

	private HL7Parser hl7Parser;
	private PipeParser parser;

	private final StringBuilder msgBeingReceived = new StringBuilder();
	private MessageTransaction messageTransaction;
	private MessageTransactionService messageTransactionService;
	private Machine machine;
	private MachineService machineService;
	private LkpMessageTransactionDirection lkpMessageTransactionDirection;
	private LkpService lkpService;
	private LkpMessageTransactionType lkpMessageTransactionType;

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return idleState;
	}

	private Hl7RocheCobasProController() {
		idleState = getIdleState();
		receivingState = getReceivingState();
		sendingState = getSendingState();
	}

	private void goToIdleState() {
		log.debug("Transitioning to idle state");
		unstashAll();
		context().become(idleState);
	}

	private void sendMessage(String msgToSend) {

		//BytesMessage byteArray[] = { new BytesMessage(VTBytes.toString() + msgToSend + VTBytes.toString()) };

		msgBeingSent.clear();
		log.debug("Sending next frame in buffer - " + msgToSend);
		// conf.lowLevelRecipient.tell(VTBytes, self());
		conf.lowLevelRecipient.tell(new BytesMessage(VTBytes.toString()+ msgToSend + FSBytes.toString() + CR), self());
		// conf.lowLevelRecipient.tell(FSBytes, self());
		// context().become(sendingState);

		// conf.lowLevelRecipient.tell(new BytesMessage("\r"), self());
		
	}
	
	public void initiateObjects() {

		/////////////////////////////////////////// set service
		/////////////////////////////////////////// Beans/////////////////////////////////////////////////
		
		machineService = (MachineService) SpringUtil.getBean("MachineService");
		machine = machineService.getMachineByActorPath(getContext().parent().toString());
		
		//coreEventLogService = (CoreEventLogService) SpringUtil.getBean("CoreEventLogService");
		messageTransactionService = (MessageTransactionService) SpringUtil.getBean("MessageTransactionService");
	
		lkpService = (LkpService) SpringUtil.getBean("LkpService");
		messageTransaction = new MessageTransaction();
		lkpMessageTransactionType = null;
		lkpMessageTransactionDirection = null;

	}

	
	public String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	}
	
	private void sendACK() {

		try {

			// MSH|~^\&|myCare|KHCC|VistA|KHCC|20231224104441||ACK|A0175447953|P|2.4|
			// MSA|AA|0175447953|success|S

			String ACK = "MSH|~^\\&|CERTA_LIS" + "||" + "" + "|CERTACURE|" + getCurrentLocalDateTimeStamp() + "||"
					+ "ACK" + "|" + 22 // :
					+ "|" + "|" + "2.4|" + "\r" + "MSA|" + "AA|" + 22 + "|success";

			ACK = LowLevelUtils.VT + ACK + LowLevelUtils.CR + LowLevelUtils.FS + LowLevelUtils.CR;

			conf.lowLevelRecipient.tell(new BytesMessage(ACK), self());
			System.out.println(ACK);
			//httpRequstTransactionObj.getMessageTransaction().setAckMessageText(ACK);

		} catch (Exception e) {
			
			
			
		} finally

		{
			
		}

	}

	private PartialFunction<Object, BoxedUnit> getSendingState() {

		return ReceiveBuilder

				.matchAny(__ -> {

					System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println(__.toString());
					System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				})
				// __ is a variable name for what has been received will not be used inside the
				// code
				/*
				 * .matchEquals(ACKBytes, __ -> { if (msgBeingSent.isEmpty()) {
				 * conf.lowLevelRecipient.tell(EOTBytes, self()); goToIdleState(); resendMessage
				 * = ""; //senderTimer.stop(); counter = 0;
				 * 
				 * } else { resendMessage = ""; //senderTimer.stop(); counter = 0;
				 * sendNextFrameInBuffer(); } })
				 */
				.match(LIS2A2QueryMsg.class, __ -> {
					System.out.println("WWWWWWWWWWWWWWWWWQQQQQQQQQQQQQQQQWWWWWWWWWWWWWWW");
				}).matchAny(__ -> {
					stash();
				}).build();
	}

	private PartialFunction<Object, BoxedUnit> getIdleState() {
		return ReceiveBuilder.match(String.class, this::goToSendingState).matchAny(__ -> {
			msgBeingReceived.append(__);
			
			initiateObjects();

			if (msgBeingReceived.indexOf(VTBytes.toString()) != -1
					&& msgBeingReceived.indexOf(FSBytes.toString()) != -1) {
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println(msgBeingReceived.toString());
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");

				// HapiParser hapi = new HapiParser();
				// hapi.parseMessage(msgBeingReceived.substring(1,
				// msgBeingReceived.lastIndexOf(resendMessage)-1).toString());

				String[] arrMsg = msgBeingReceived.toString().split("<CR>");

				String strHl725Msg = getHl725Message(arrMsg);
				
				strHl725Msg = strHl725Msg.replace(VTBytes.toString(), "");
				strHl725Msg = strHl725Msg.replace(FSBytes.toString(), "");
				
				
				 // Create a new HAPI HL7 parser (PipeParser for v2.5)
	            
	            hl7Parser = new HL7Parser(strHl725Msg, "2.5.1");
	            
	            
	            // Parse the message
	            Message message = hl7Parser.parse(strHl725Msg);
	            
	            // Get message type and version
	            String messageType = message.getMessage().getName();
	            String version = message.getVersion();
	            
	            // Use Terser to dynamically access fields
	            Terser terser = new Terser(message);
	            
	            System.out.println("Message Type: " +  terser.get("/MSH-9"));
	            System.out.println("Version: " + version);
	            
	            if ("OUL".equals(terser.get("/MSH-9")))
	            {
	            	//return ACK 
	            	//hl7Parser.getAckMessage();
	            	sendMessage(getEncodedACKMessage(hl7Parser.getAckMessage()));
	            }
	            
	           if ("QBP".equals(terser.get("/MSH-9")))
	            {
	            	//return ACK 	
	        	   
					String strQueryResponce = getQueryResponseMessage(strHl725Msg);
					sendMessage(strQueryResponce);
					
					setLkpMessageTransactionDirection(MessageDirection.OUT);
					setLkpMessageTransactionType(MessageTransactionType.RESPONCE_SENT);

					createMessageTransaction(strQueryResponce, machine, lkpMessageTransactionDirection,
							lkpMessageTransactionType, terser.get("/QPD-3"), "Message Accepted - Responce Sent", true,
							true, true, true);

				}

				// sendMessage(hl7Parser.getAckMessage().encode());
				// sendMessage(getEncodedACKMessage(hl7Parser.getAckMessage()));
				conf.highLevelRecipient.tell(strHl725Msg, self());

				msgBeingReceived.setLength(0);

				context().become(idleState);

			}
		})

				.build();
	}
	
	private void setLkpMessageTransactionDirection(MessageDirection dir) {
		lkpMessageTransactionDirection = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", dir.getValue(), FilterOperator.eq)),
				LkpMessageTransactionDirection.class);
	}

	private void setLkpMessageTransactionType(MessageTransactionType type) {
		lkpMessageTransactionType = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", type.getValue(), FilterOperator.eq)),
				LkpMessageTransactionType.class);
	}
	
	private void createMessageTransaction(String msgAsString, Machine machine,
			LkpMessageTransactionDirection msgDirection, LkpMessageTransactionType msgTransactionType,
			String sampleBarcode, String strNotes, boolean isProcessed, boolean isSent, boolean isValudated,
			boolean isSuccess) {
		
		messageTransactionService = (MessageTransactionService) SpringUtil.getBean("MessageTransactionService");

		messageTransaction = new MessageTransaction();
		messageTransactionService = (MessageTransactionService) SpringUtil.getBean("MessageTransactionService");

		messageTransaction.setMachine(machine);
		messageTransaction.setBarcode(sampleBarcode);
		messageTransaction.setNotes(strNotes);
		messageTransaction.setMessageType(msgTransactionType);
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(msgDirection);
		messageTransaction.setMessageBody(msgAsString);
		messageTransaction.setIsProcessed(isProcessed);
		messageTransaction.setIsSuccess(isSuccess);
		messageTransaction.setIsValidated(isValudated);
		messageTransaction.setIsSent(isSent);
		messageTransaction = messageTransactionService.add(messageTransaction);

	}
	
		
	public  QueryMessageDetails getQueryMessageDetailsObj(String hl7Message) throws HL7Exception, IOException {
        HapiContext context = new DefaultHapiContext();
        Parser parser = context.getPipeParser();
        
        // Parse the message
        Message message = parser.parse(hl7Message);
        
        try {
            // Access QPD segment directly without type-casting
            ca.uhn.hl7v2.model.Segment qpdSegment = (ca.uhn.hl7v2.model.Segment) message.get("QPD");
            
            // Extract fields from QPD segment using Terser for more flexibility
            ca.uhn.hl7v2.util.Terser terser = new ca.uhn.hl7v2.util.Terser(message);
            
            String messageControlId = terser.get("/MSH-10");
            String queryTag = terser.get("/QPD-2");
            String sampleNumber = terser.get("/QPD-3");
            String position = terser.get("/QPD-5");
            String rack = terser.get("/QPD-4");
            String qualityControlId = terser.get("/QPD-1-1");
            
            // Create and return QueryMessageDetails object
            QueryMessageDetails queryMessageDetails = new QueryMessageDetails(sampleNumber, position, rack, qualityControlId, queryTag, messageControlId);
            context.close();
            return queryMessageDetails;
            
        } catch (Exception e) {
            System.err.println("Error extracting QPD segment: " + e.getMessage());
            context.close();
            return null;
        }
    }
	
	private String getQueryResponseMessage(String strHl7Message) throws HL7Exception, IOException 
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		HapiContext context = new DefaultHapiContext();
		
		
		QueryMessageDetails data = getQueryMessageDetailsObj(strHl7Message);
        if (data != null) {
            System.out.println(data);
            // Or access individual fields:
            // System.out.println("Sample Number: " + data.getSampleNumber());
            // System.out.println("Position: " + data.getPosition());
            // System.out.println("Rack: " + data.getRack());
            // System.out.println("Quality Control ID: " + data.getQualityControlId());
        }
		
		RSP_K11 rsp_k11 = new RSP_K11();
		
		//MSH|^~\&|host||cobas pro||20240226185657+0100||RSP^K11^RSP_K11|12193|P|2.5.1||||||UNICODE UTF-8|||LAB-27R^ROCHE
		rsp_k11.getMSH().getMsh1_FieldSeparator().setValue("|");
		rsp_k11.getMSH().getMsh2_EncodingCharacters().setValue("^~\\&");
		rsp_k11.getMSH().getMsh3_SendingApplication().getHd1_NamespaceID().setValue("host");
		rsp_k11.getMSH().getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue("cobas pro");
		rsp_k11.getMSH().getMsh7_DateTimeOfMessage().getTime().setValue(dateFormat.format(date));
		rsp_k11.getMSH().getMsh9_MessageType().getMsg1_MessageCode().setValue("RSP");
		rsp_k11.getMSH().getMsh9_MessageType().getMsg2_TriggerEvent().setValue("K11");
		rsp_k11.getMSH().getMsh9_MessageType().getMsg3_MessageStructure().setValue("RSP_K11");
		rsp_k11.getMSH().getMsh10_MessageControlID().setValue(UniqueSequenceGenerator.generateHighResSequence());
		rsp_k11.getMSH().getMsh11_ProcessingID().getPt1_ProcessingID().setValue("P");
		rsp_k11.getMSH().getMsh12_VersionID().getVersionID().setValue("2.5.1");
		rsp_k11.getMSH().getMsh15_AcceptAcknowledgmentType().setValue("");
		rsp_k11.getMSH().getMsh16_ApplicationAcknowledgmentType().setValue("");
		rsp_k11.getMSH().getMsh18_CharacterSet(0).setValue("UNICODE UTF-8");
		rsp_k11.getMSH().getMsh21_MessageProfileIdentifier(0).getEi1_EntityIdentifier().setValue("LAB-27R");
		rsp_k11.getMSH().getMsh21_MessageProfileIdentifier(0).getEi2_NamespaceID().setValue("ROCHE");
		
		rsp_k11.getMSA().getAcknowledgmentCode().setValue("AA");
		rsp_k11.getMSA().getMsa2_MessageControlID().setValue(data.getSampleNumber());
		
		
	   // MSA Segment
       // MSA msa = rsp_k11.getMSA();
       // msa.getAcknowledgmentCode().setValue("AA");
       // msa.getMessageControlID().setValue("1898");
        
        // QAK Segment
		/*
		 * MSH|^~\&|host||cobas pro||20240226185657+0100||RSP^K11^RSP_K11|12193|P|2.5.1||||||UNICODE UTF-8
			|||LAB-27R^ROCHE
			MSA|AA|99
			QAK|130|OK|INIBAR^^99ROC
			QPD|INIBAR^^99ROC|130|SID123|50015|4|||||SERPLAS^^99ROC|SC^^99ROC|R
		 */
	
        QAK qak = rsp_k11.getQAK();
        qak.getQak1_QueryTag().setValue(data.getQueryTag());
        qak.getQak2_QueryResponseStatus().setValue("OK");
        qak.getQak3_MessageQueryName().getCe1_Identifier().setValue(data.getQualityControlId());
        qak.getQak3_MessageQueryName().getCe3_NameOfCodingSystem().setValue("99ROC");
        
        
        // QPD Segment
        QPD qpd = rsp_k11.getQPD();
        qpd.getQpd1_MessageQueryName().getCe1_Identifier().setValue(data.getQualityControlId());
        qpd.getQpd1_MessageQueryName().getCe3_NameOfCodingSystem().setValue("99ROC");
        qpd.getQpd2_QueryTag().setValue(data.getQueryTag());
        // qpd.getUserParametersInsuccessivefields().encode(); // QPD-3
        qpd.getField(3, 0).parse(data.getSampleNumber());
        qpd.getField(4, 0).parse(data.getRack());
        qpd.getField(5, 0).parse(data.getPosition());
        qpd.getField(10, 0).parse("SERPLAS"+"^^99ROC");
        qpd.getField(11, 0).parse("SC^^99ROC");
        qpd.getField(12, 0).parse("R");
        
        // Encode message to string
        Parser parser = context.getPipeParser();
        String encodedMessage = parser.encode(rsp_k11);
        
        context.close();
        return encodedMessage;
		
		
	}
	
	public static String formatIfNumeric(String input) {
	    if (input.matches("\\d+")) { // Check if it contains only digits
	        return String.format("%05d", Integer.parseInt(input));
	    } else {
	        return input; // Return as-is if not all digits
	    }
	}
	
/*	private String CreateQPD(SampleData data) {
		//QPD|RRRBAR^^99ROC|110|ID123456|50002|1|||||SERPLAS^^99ROC|SC^^99ROC|R
		
	//	 String qpdSegment = "" ;
		try {
			/*
			.setComponent(1, sampleNumber)
			.setComponent(2, queryTag)
			.setComponent(3, queryControlID)
			.setComponent(4, rackNumber)
			.setComponent(5, positionNumber)); 
			*/
			

		//	String strBarcode = formatIfNumeric(data.getSampleNumber());
			
	//		qpdSegment = "QPD" +
	//		 "|" + data.getSampleNumber()
			
	//		+ "|" + data.getQualityControlId()
	//		+ "|" + data.getQueryTag()
	//		+ "|||||SERPLAS^^99ROC|SC^^99ROC|R" + "\r";
			
			
			
		
            
			//rsp_k11.getQPD().getQpd1_MessageQueryName().getCe1_Identifier().setValue("RRRBAR");
			//rsp_k11.getQPD().getQpd1_MessageQueryName().getCe2_Text().setValue("99ROC");
			//rsp_k11.getQPD().getQpd2_QueryTag().setValue(orderRecord.queryTag);
			//rsp_k11.getQPD().getQpd3_UserParametersInsuccessivefields().getExtraComponents().getComponent(0).setData(");
			//rsp_k11.getQPD().getQak5_ThisPayload().setValue(orderRecord.getSpecimnPositionInfo());
			//rsp_k11.getQPD().getQak6_HitsRemaining().setValue(orderRecord.getSpecimenId());
            
            //System.out.println(qpd.encode());
	//	} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
	//	return qpdSegment;
		
	//}

	private String getEncodedACKMessage(Terser terser) throws HL7Exception {
		 // Create a context
        HapiContext context = new DefaultHapiContext();//OUL^R22^OUL_R22
        
        // Create an ACK message
       
        ACK ack = new ACK();
       
        // Populate MSH segment
        MSH msh = ack.getMSH();
        msh.getFieldSeparator().setValue("|");
        msh.getEncodingCharacters().setValue("^~\\&");
        msh.getSendingApplication().getNamespaceID().setValue("host");
        msh.getSendingFacility().getNamespaceID().setValue("");
        msh.getReceivingApplication().getNamespaceID().setValue("cobas pro");
        msh.getReceivingFacility().getNamespaceID().setValue("");
        msh.getDateTimeOfMessage().getTime().setValue(terser.get("/MSH-7"));
        msh.getMessageType().getMessageCode().setValue(terser.get("/MSH-9-1"));
        msh.getMessageType().getTriggerEvent().setValue(terser.get("/MSH-9-2"));
        msh.getMessageType().getMessageStructure().setValue("ACK");
        msh.getMessageControlID().setValue( UniqueSequenceGenerator.generateHighResSequence()) ;
        msh.getProcessingID().getProcessingID().setValue("P");
        msh.getVersionID().getVersionID().setValue("2.5.1");
        msh.getCharacterSet(0).setValue("UNICODE UTF-8");
        msh.getMessageProfileIdentifier(0).getEntityIdentifier().setValue("LAB-29");
        msh.getMessageProfileIdentifier(0).getNamespaceID().setValue("IHE");
        
        // Populate MSA segment
        MSA msa = ack.getMSA();
        msa.getAcknowledgmentCode().setValue("AA");
        msa.getMessageControlID().setValue(terser.get("/MSH-10"));
        
        // Encode and print the message
        Parser parser = context.getPipeParser();
        String encodedMessage = parser.encode(ack);
        return encodedMessage;
	}

	public String getEncodedACKMessage(Message message) throws Exception {
	        // Create a context
	        HapiContext context = new DefaultHapiContext();
	        
	        // Create an ACK message
	        Terser terser = new Terser(message);
	        ACK ack = new ACK();
	       
	        // Populate MSH segment
	        MSH msh = ack.getMSH();
	        msh.getFieldSeparator().setValue("|");
	        msh.getEncodingCharacters().setValue("^~\\&");
	        msh.getSendingApplication().getNamespaceID().setValue("host");
	        msh.getSendingFacility().getNamespaceID().setValue("");
	        msh.getReceivingApplication().getNamespaceID().setValue("cobas pro");
	        msh.getReceivingFacility().getNamespaceID().setValue("");
	        msh.getDateTimeOfMessage().getTime().setValue(terser.get("/MSH-7"));
	        msh.getMessageType().getMessageCode().setValue(terser.get("/MSH-9-1"));
	        msh.getMessageType().getTriggerEvent().setValue(terser.get("/MSH-9-2"));
	        msh.getMessageType().getMessageStructure().setValue("ACK");
	        msh.getMessageControlID().setValue( terser.get("/MSH-10")) ;
	        msh.getProcessingID().getProcessingID().setValue("P");
	        msh.getVersionID().getVersionID().setValue("2.5.1");
	        msh.getCharacterSet(0).setValue("UNICODE UTF-8");
	        msh.getMessageProfileIdentifier(0).getEntityIdentifier().setValue("LAB-29");
	        msh.getMessageProfileIdentifier(0).getNamespaceID().setValue("IHE");
	        
	        // Populate MSA segment
	        MSA msa = ack.getMSA();
	        msa.getAcknowledgmentCode().setValue("AA");
	        msa.getMessageControlID().setValue(terser.get("/MSH-10"));
	        
	        // Encode and print the message
	        Parser parser = context.getPipeParser();
	        String encodedMessage = parser.encode(ack);
	        return encodedMessage;
	    
	}

	private void goToSendingState(String strOmlMessage) {
		String msgToSend = "";
		try {
			msgToSend = strOmlMessage;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("Transitioning to sending state");

		// msgBeingSent.clear();
		// conf.lowLevelRecipient.tell(ENQBytes, self());
		// Collections.addAll(msgBeingSent, msgToSend.split("(?<=" + CR + ")"));
		// if (msgBeingSent.get(0).isEmpty() && msgBeingSent.size() == 1) {
		// conf.lowLevelRecipient.tell(EOTBytes, self());
		// goToIdleState();
		// context().become(idleState);
		// } else {
		sendMessage(msgToSend);
		// context().become(sendingState);
		// }
	}

	private String getBarcodeValue(String inputMsg) {
		
		String[] strData = inputMsg.split("\\r");
		String[] strFiledsData;
		String strBarcode = "-----";
		for (int index = 0; index < strData.length; index++) {
			if (strData[index].startsWith("SPM")) {
				strFiledsData = strData[index].split("\\|");
				strBarcode = strFiledsData[2].split("&")[0];
				
				int isExisted =  strBarcode.indexOf("^");
				
				if(isExisted != -1)
				{
					strBarcode =  strBarcode.substring(0, isExisted);
				}
				break;
			}

			if (strData[index].startsWith("Q|")) {
				strFiledsData = strData[index].split("\\|");
				strBarcode = strFiledsData[2].split("&")[0];
				break;
			}

		}

		return strBarcode;
	}

	public Machine getMachineInfoByPath() {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		return machine;
	}

	private PartialFunction<Object, BoxedUnit> getReceivingState() {
		return ReceiveBuilder.matchAny(__ -> {
			msgBeingReceived.append(__);

			if (msgBeingReceived.indexOf(VTBytes.toString()) != -1
					&& msgBeingReceived.indexOf(FSBytes.toString()) != -1) {
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println(msgBeingReceived.toString());
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");

				String[] arrMsg = msgBeingReceived.toString().split("<CR>");
				String strHL725Msg = getHl725Message(arrMsg);
				conf.highLevelRecipient.tell(strHL725Msg, self());
				msgBeingReceived.setLength(0);

				context().become(idleState);

			}
		})

				.build();

	}

	private String getHl725Message(String[] arrMsg) {

		String strASTMMessage = "";

		for (int index = 0; index < arrMsg.length; index++) {
			if (arrMsg[index].contains("MSH")) {
				strASTMMessage += arrMsg[index];// .replace("MSH", "H");
				strASTMMessage += "\r";

			} else if (arrMsg[index].contains("PID")) {
				strASTMMessage += arrMsg[index];// .replace("PID", "P");
				strASTMMessage += "\r";

			} 
			else if (arrMsg[index].contains("MSA")) {
				strASTMMessage += arrMsg[index];// .replace("PID", "P");
				strASTMMessage += "\r";

			}
			else if (arrMsg[index].contains("RCP")) {
				strASTMMessage += arrMsg[index];// .replace("RCP", "L");
				strASTMMessage += "\r";

			} else if (arrMsg[index].contains("QPD")) {
				strASTMMessage += arrMsg[index];// .replace("QPD", "Q");
				strASTMMessage += "\r";

			} else if (arrMsg[index].contains("OBR")) {
				strASTMMessage += arrMsg[index];// .replace("OBR", "O");
				strASTMMessage += "\r";

			} else if (arrMsg[index].contains("SPM")) {
				strASTMMessage += arrMsg[index];// .replace("SPM", "SPM");
				strASTMMessage += "\r";

			} else if (arrMsg[index].contains("OBX")) {
				strASTMMessage += arrMsg[index];// .replace("OBX", "R");
				strASTMMessage += "\r";
			}
			else if (arrMsg[index].contains("ORC")) {
				strASTMMessage += arrMsg[index];// .replace("ORC", "C");
				strASTMMessage += "\r";
			}
			else if (arrMsg[index].contains("SAC")) {
				strASTMMessage += arrMsg[index];// .replace("ORC", "C");
				strASTMMessage += "\r";
			}
		}

		return strASTMMessage;
	}
}