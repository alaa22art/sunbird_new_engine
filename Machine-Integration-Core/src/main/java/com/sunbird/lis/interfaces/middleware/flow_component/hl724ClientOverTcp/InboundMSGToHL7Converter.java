package com.sunbird.lis.interfaces.middleware.flow_component.hl724ClientOverTcp;

import static com.sunbird.lis.interfaces.middleware.util.LowLevelUtils.CR;
import static com.sunbird.lis.interfaces.middleware.util.LowLevelUtils.ENQ;
import static com.sunbird.lis.interfaces.middleware.util.LowLevelUtils.EOT;
import static com.sunbird.lis.interfaces.middleware.util.LowLevelUtils.FS;
import static com.sunbird.lis.interfaces.middleware.util.LowLevelUtils.LF;
import static com.sunbird.lis.interfaces.middleware.util.LowLevelUtils.STX;
import static com.sunbird.lis.interfaces.middleware.util.LowLevelUtils.VT;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.AckMessageSequance;
import com.sunbird.lis.interfaces.entities.DataInboundHL7Message;
import com.sunbird.lis.interfaces.entities.ElegabalityApprovalEntity;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MessageTransaction;
import com.sunbird.lis.interfaces.middleware.core.FlowComponent;
import com.sunbird.lis.interfaces.middleware.core.RecipientConf;
import com.sunbird.lis.interfaces.middleware.enums.Enums.MSG_TYPE;
import com.sunbird.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.sunbird.lis.interfaces.middleware.flow_component.scheduler.EligibilityOrderMapper;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_ADT_Msg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_DFT_Msg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_SIU_Msg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_UNKOWN_Msg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.OBXRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.QueryRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.ResultRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Appointment;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_EventTypeRecord;
import com.sunbird.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.sunbird.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageSourceType;
import com.sunbird.lis.interfaces.middleware.util.MachineTypeEnum;
import com.sunbird.lis.interfaces.service.AckMessageSequanceService;
import com.sunbird.lis.interfaces.service.DataInboundHL7MessageService;
import com.sunbird.lis.interfaces.service.ElegabalityApprovalService;
import com.sunbird.lis.interfaces.service.MachineService;
import com.sunbird.lis.interfaces.service.MessageTransactionService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class InboundMSGToHL7Converter extends FlowComponent<RecipientConf> {
	
	
	  @Override
	    protected PartialFunction<Object, BoxedUnit> getBehaviour() {
	        return ReceiveBuilder.match(String.class, this::convertAndForward)
	        		.match(ElegabalityApprovalEntity.class, this::convertAndForward)
	        		.match(EligibilityOrderMapper.class, this::convertAndForward)

	            .build();
	    }
	  
	  
	  Config config = ConfigFactory.load();
		
	private String SYSTEM_ENVIROMENT = config.getString("system.enviroment");
	private boolean SEND_RIS_FLAG_ENABLED = config.getString("system.certacure.new.ris.flag.enable").equals("1") ? true: false ;
    private MessageTransactionService messageTransactionService;
    private MachineService machineService;
    private MessageTransaction messageTransaction;
    private httpRequstTransaction httpReqeustTransaction;
    private Machine machine;
    private DataInboundHL7Message inboundHL7Message;
    private DataInboundHL7MessageService inboundHL7MessageService;
    private MSG_TYPE msg_type;
    private ElegabalityApprovalService elegabalityApprovalService;
    private AckMessageSequance ackMessageSequanceObj;
    private AckMessageSequanceService ackMessageSequanceService;


    class MessageType {

        Class<? extends LIS2A2Msg> refMsgType;
    }

    private void initiateObjects() {
        messageTransactionService= (MessageTransactionService) SpringUtil
            .getBean("MessageTransactionService");
        inboundHL7MessageService= (DataInboundHL7MessageService) SpringUtil
            .getBean("DataInboundHL7MessageService");
        
        ackMessageSequanceService = (AckMessageSequanceService) SpringUtil
                .getBean("AckMessageSequanceService");
        
        machineService= (MachineService) SpringUtil.getBean("MachineService");
        elegabalityApprovalService = (ElegabalityApprovalService) SpringUtil.getBean("ElegabalityApprovalService");
    	
        machine= machineService.getMachineByActorPath(getContext().parent().toString());
        inboundHL7Message= new DataInboundHL7Message();
        messageTransaction= new MessageTransaction();
        httpReqeustTransaction= new httpRequstTransaction();
        messageTransaction.setIsSuccuss(false);
        messageTransaction.setIsSent(false);
        messageTransaction.setIsValidated(false);
        
      

    }

  
    public LIS2A2Msg stringMsgToAstmMsg(String msgAsString) {
        List<LIS2A2Record> records= new ArrayList<>();
        String[] recordLines= msgAsString.split("(?<=" + CR + ")");
        for (String record : recordLines) {
            log.debug("Converting String '" + record + "' into LIS2A2Record");
            LIS2A2Record lis2a2Record= LIS2A2Record.fromString(record);
            log.debug(
                "Conversion successful, lis2a2Record.toString() = " + lis2a2Record.toString());
            records.add(lis2a2Record);
        }

        Class<? extends LIS2A2Msg> msgType= getMsgType(records);
        if (msgType.equals(LIS2A2OrderMsg.class))
            return new LIS2A2OrderMsg(records);
        else if (msgType.equals(LIS2A2ResultMsg.class))
            return new LIS2A2ResultMsg(records);
        else if (msgType.equals(LIS2A2QueryMsg.class))
            return new LIS2A2QueryMsg(records);
        else
            throw new RuntimeException("Unexpected message type - " + msgType);
    }
    
    
    private void convertAndForward(EligibilityOrderMapper eligibilityOrderMapperObject) 
    {
    	
    	 initiateObjects();
         String hl7DFTMsg = buildElegabalityApprovalMessage(eligibilityOrderMapperObject);
         
         conf.recipient.tell(hl7DFTMsg, self());
         
         
         
         try {
             System.out.println(
                 "\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
             System.out.println(hl7DFTMsg);
             System.out.println(
                 "\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
             httpReqeustTransaction.setHL7MsgText(hl7DFTMsg);
             LIS2A2Msg list2A2Msg= stringToLIS2AMsg(hl7DFTMsg);
             httpReqeustTransaction.setLis2aMsg(list2A2Msg);
             httpReqeustTransaction.getMessageTransaction().setMachine(machine);
             messageTransaction = messageTransactionService.addMessageTransaction(machine,
             		hl7DFTMsg,
                 MessageDirection.OUT.toString(), MessageSourceType.DFT_P03_CLERANCE.toString(),MessageSourceType.DFT_P03_CLERANCE.getValue(), true, false,true,
                 "Sent new Message" , eligibilityOrderMapperObject.eligibilityOrderId.toString());
             httpReqeustTransaction.setMessageTransaction(messageTransaction);

         } catch (Exception ex) {
             messageTransaction = messageTransactionService.addMessageTransaction(machine,
             		hl7DFTMsg,
                 MessageDirection.OUT.toString(), MessageSourceType.DFT_P03_CLERANCE.toString(),MessageSourceType.DFT_P03_CLERANCE.getValue(), false, false,false,
                 ex.getMessage(),eligibilityOrderMapperObject.eligibilityOrderId.toString());
             httpReqeustTransaction.setMessageTransaction(messageTransaction);

         } finally {
             //conf.recipient.tell(httpReqeustTransaction, self());
         }
    	
    	
    }


    private String buildElegabalityApprovalMessage(EligibilityOrderMapper eligibilityOrderMapperObject) 
    {
    	/*
    	 * 
    	 * ########FINAL ELEGABALITY MESSAGE 10012024 ##########
		   MSH|~^\&|myCare|KHCC|VistA|KHCC|20240110112046||DFT~P03|5240478842|T|2.4|||AL|NE|JOR 
           EVN|P03|20240110112046|||8431~~8431 
           PID||4000058606|243631|||||||||||||| 
           PV1||I|7||6398884||15860|||||||7|
           FT1|0|6398884|||20240110112046|AA|1330|ACCEPT|CONAPP||||P03~DFT_P03||||||||
           ########FINAL ELEGABALITY MESSAGE 10012024 ##########
    	 */
    	
    	String strSection = "";
    	
    	
    	if(!eligibilityOrderMapperObject.vistaOrderId.contains("LA") && SEND_RIS_FLAG_ENABLED)
    	{
    		strSection = "RIS";
    	}
    	
    	ackMessageSequanceObj = new AckMessageSequance(eligibilityOrderMapperObject.eligibilityOrderId , eligibilityOrderMapperObject.transactionType );
    	
    	ackMessageSequanceObj.setTenantId(115l);
    	ackMessageSequanceObj.setBranchId(1l);
    	ackMessageSequanceObj.setCreatedBy(1l);
    	  
    	ackMessageSequanceService.add(ackMessageSequanceObj);
    	
    	
    	
        StringBuilder hl7Message = new StringBuilder();

        LocalDateTime currentDateTime = LocalDateTime.now();
        
//        String strVistaOrderId = elegabalityApprovalEntity.getVistaOrderID().replaceAll("[^\\d.]", "");

        // Define the desired date-time pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // Format the current date-time
        String formattedDateTime = currentDateTime.format(formatter);
        
        Long msgControlId = ackMessageSequanceObj.getRid() ;
        
        hl7Message.append(VT);
        // Message Header (MSH)
        hl7Message.append("MSH|~^\\&|myCare|KHCC|VistA|KHCC|"+formattedDateTime+"||DFT~P03|"+msgControlId+"|"+SYSTEM_ENVIROMENT+"|2.4|||AL|NE|JOR\r");

        // Patient Identification (PID)
        hl7Message.append("EVN|P03|"+formattedDateTime+"|||8431~~8431||"+ strSection +"\r");

        // Patient Visit Information (PV1)
        hl7Message.append("PID||"+(eligibilityOrderMapperObject.nationalCode != null? eligibilityOrderMapperObject.nationalCode:"")
        		+"|"+(eligibilityOrderMapperObject.patientCode ==null ? "" : eligibilityOrderMapperObject.patientCode) 
        		+"|||||||||||||||\r");

        
        //PV1||I|7||6398884||15860|||||||7|
        hl7Message.append("PV1||"+ (eligibilityOrderMapperObject.patientClass.equals(1)? "I": "O") 
        		+  "|"+ (eligibilityOrderMapperObject.sectionCode!=null ?  eligibilityOrderMapperObject.sectionCode:"")
        		+"||"
        		//+ getApprovalMesgRefId(eligibilityOrderMapperObject)
        		+ eligibilityOrderMapperObject.vistaOrderId
        		//(elegabalityApprovalEntity.getAppointment_id() == null? elegabalityApprovalEntity.getVistaOrderID():elegabalityApprovalEntity.getAppointment_id()) 
        		+ "||" 
        	    + (eligibilityOrderMapperObject.attendingDoctorId != null?  eligibilityOrderMapperObject.attendingDoctorId:"") 
        		+ "|||||||" 
        		+(eligibilityOrderMapperObject.sectionCode != null?eligibilityOrderMapperObject.sectionCode:""  )+"|\r");
        
        // Patient Additional Information (PV2)
        // FT1|0|6398884|||20240110112046|AA|1330|ACCEPT|CONAPP||||P03~DFT_P03||||||||
        String elegableCode = (eligibilityOrderMapperObject.isEligibilOrder ? "AA":"AR");
        String appointmentCode = "CONAPP";
       
//        hl7Message.append("FT1|0|"
//        +getApprovalMesgRefId(elegabalityApprovalEntity)
//        +"|||"
//        +formattedDateTime
//        +"|"
//        +elegableCode+"|"
//        +(elegabalityApprovalEntity.getItemCode() == null? "" : elegabalityApprovalEntity.getItemCode()) 
//        +"||"
//        + (elegableCode.equals("AA") ? "ACCEPT" : "REGECT") 
//        +"|" 
//        +(elegabalityApprovalEntity.getIsAppointment() ? "CONAPP" : "ENC")  
//        +"||||"+"P03~DFT_P03||"+elegabalityApprovalEntity.getIpPrinterAddress() == null? "" : elegabalityApprovalEntity.getIpPrinterAddress() +"||||||"+"\r");

        hl7Message.append("FT1|0|")
        //.append(getApprovalMesgRefId(elegabalityApprovalEntity))
        .append(eligibilityOrderMapperObject.vistaOrderId)
        .append("|||").append(formattedDateTime +"|"+elegableCode+"|")
        .append((eligibilityOrderMapperObject.itemChargeCode !=null?  eligibilityOrderMapperObject.itemChargeCode:"")+"|")
        .append((elegableCode.equals("AA") ? "ACCEPT" : "REGECT") + "|")
        .append(eligibilityOrderMapperObject.isAppointment ? "CONAPP" : "ENC")
        .append("||||"+"P03~DFT_P03|||")
        .append((eligibilityOrderMapperObject.ipPrinterAddress != null? eligibilityOrderMapperObject.ipPrinterAddress :"" )  +"||||||"+"\r");
        
        // Message Trailer (EOF)
//        hl7Message.append("EOF|\r");
        hl7Message.append(FS);
        hl7Message.append(CR);

        return hl7Message.toString();
	}


	private void convertAndForward(String msgAsString) {

        initiateObjects();

        try {
            System.out.println(
                "\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
            System.out.println(msgAsString);
            System.out.println(
                "\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
            httpReqeustTransaction.setHL7MsgText(msgAsString);
            LIS2A2Msg list2A2Msg= stringToLIS2AMsg(msgAsString);
            httpReqeustTransaction.setLis2aMsg(list2A2Msg);
            httpReqeustTransaction.getMessageTransaction().setMachine(machine);
            messageTransaction = messageTransactionService.addMessageTransaction(machine,
                msgAsString,
                MessageDirection.IN.toString(), MessageSourceType.HL7.toString(),MessageSourceType.HL7.getValue(), false, false,false,
                "Arrive new Message" , "undefined");
            httpReqeustTransaction.setMessageTransaction(messageTransaction);

        } catch (Exception ex) {
            messageTransaction = messageTransactionService.addMessageTransaction(machine,
                msgAsString,
                MessageDirection.IN.toString(), MessageSourceType.HL7.toString(),MessageSourceType.HL7.getValue() ,false, false,false,
                ex.getMessage(),"undefined");
            httpReqeustTransaction.setMessageTransaction(messageTransaction);

        } finally {
        	
        	
        	
        	
            conf.recipient.tell(httpReqeustTransaction, self());
        }

    }
    
    
    private void convertAndForward(ElegabalityApprovalEntity elegabalityOrderEntity) {

        initiateObjects();
        String hl7DFTMsg = buildElegabalityApprovalMessage(elegabalityOrderEntity);
        
        conf.recipient.tell(hl7DFTMsg, self());
        
        


        try {
            System.out.println(
                "\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
            System.out.println(hl7DFTMsg);
            System.out.println(
                "\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
            httpReqeustTransaction.setHL7MsgText(hl7DFTMsg);
            LIS2A2Msg list2A2Msg= stringToLIS2AMsg(hl7DFTMsg);
            httpReqeustTransaction.setLis2aMsg(list2A2Msg);
            httpReqeustTransaction.getMessageTransaction().setMachine(machine);
            messageTransaction = messageTransactionService.addMessageTransaction(machine,
            		hl7DFTMsg,
                MessageDirection.OUT.toString(), MessageSourceType.DFT_P03_CLERANCE .toString(),MessageSourceType.DFT_P03_CLERANCE.getValue(), true, false,true,
                "Sent new Message" , elegabalityOrderEntity.getRid().toString());
            httpReqeustTransaction.setMessageTransaction(messageTransaction);

        } catch (Exception ex) {
            messageTransaction = messageTransactionService.addMessageTransaction(machine,
            		hl7DFTMsg,
                MessageDirection.OUT.toString(), MessageSourceType.DFT_P03_CLERANCE.toString(),MessageSourceType.DFT_P03_CLERANCE.getValue(), false, false,false,
                ex.getMessage(),"undefined");
            httpReqeustTransaction.setMessageTransaction(messageTransaction);

        } finally {
            //conf.recipient.tell(httpReqeustTransaction, self());
        }

    }
   
    public String buildElegabalityApprovalMessage(ElegabalityApprovalEntity elegabalityApprovalEntity  ) {
    	
    	/*
    	 * 
    	 * ########FINAL ELEGABALITY MESSAGE 10012024 ##########
		   MSH|~^\&|myCare|KHCC|VistA|KHCC|20240110112046||DFT~P03|5240478842|T|2.4|||AL|NE|JOR 
           EVN|P03|20240110112046|||8431~~8431 
           PID||4000058606|243631|||||||||||||| 
           PV1||I|7||6398884||15860|||||||7|
           FT1|0|6398884|||20240110112046|AA|1330|ACCEPT|CONAPP||||P03~DFT_P03||||||||
           ########FINAL ELEGABALITY MESSAGE 10012024 ##########
    	 */
    	
    	ackMessageSequanceObj = new AckMessageSequance(elegabalityApprovalEntity.getRid() , "APPOINTMENT");
    	
    	ackMessageSequanceObj.setTenantId(elegabalityApprovalEntity.getTenantId());
    	ackMessageSequanceObj.setBranchId(elegabalityApprovalEntity.getBranchId());
    	ackMessageSequanceObj.setCreatedBy(1l);
    	  
    	ackMessageSequanceService.add(ackMessageSequanceObj);
    	
    	
    	
        StringBuilder hl7Message = new StringBuilder();

        LocalDateTime currentDateTime = LocalDateTime.now();
        
//        String strVistaOrderId = elegabalityApprovalEntity.getVistaOrderID().replaceAll("[^\\d.]", "");

        // Define the desired date-time pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // Format the current date-time
        String formattedDateTime = currentDateTime.format(formatter);
        
        Long msgControlId = ackMessageSequanceObj.getRid() ;
        
        hl7Message.append(VT);
        // Message Header (MSH)
        hl7Message.append("MSH|~^\\&|myCare|KHCC|VistA|KHCC|"+formattedDateTime+"||DFT~P03|"+msgControlId+"|"+SYSTEM_ENVIROMENT+"|2.4|||AL|NE|JOR\r");

        // Patient Identification (PID)
        hl7Message.append("EVN|P03|"+formattedDateTime+"|||8431~~8431 \r");

        // Patient Visit Information (PV1)
        hl7Message.append("PID||"+(elegabalityApprovalEntity.getPatientInfoNationalCode() != null? elegabalityApprovalEntity.getPatientInfoNationalCode():"")
        		+"|"+(elegabalityApprovalEntity.getPatientInfoCode() ==null ? "" : elegabalityApprovalEntity.getPatientInfoCode()) 
        		+"|||||||||||||||\r");

        
        //PV1||I|7||6398884||15860|||||||7|
        hl7Message.append("PV1||"+ (elegabalityApprovalEntity.getVisitInfoPatientType() == 1 ? "I": "O") 
        		+  "|"+ (elegabalityApprovalEntity.getVisitInfoSectionCode()!=null ?  elegabalityApprovalEntity.getVisitInfoSectionCode():"")
        		+"||"
        		+ elegabalityApprovalEntity.getVisitInfoId()
        		//(elegabalityApprovalEntity.getAppointment_id() == null? elegabalityApprovalEntity.getVistaOrderID():elegabalityApprovalEntity.getAppointment_id()) 
        		+ "||" 
        	   ////////////////////// + (elegabalityApprovalEntity.getAttendingDoctorID() != null?  elegabalityApprovalEntity.getAttendingDoctorID():"") 
        		+ "|||||||" 
        		+(elegabalityApprovalEntity.getVisitInfoSectionCode() != null?elegabalityApprovalEntity.getVisitInfoSectionCode():""  )+"|\r");
        
        // Patient Additional Information (PV2)
        // FT1|0|6398884|||20240110112046|AA|1330|ACCEPT|CONAPP||||P03~DFT_P03||||||||
        String elegableCode = (elegabalityApprovalEntity.getIsEligible() != null ? elegabalityApprovalEntity.getIsEligible().equals(true) ? "AA":"AR":"AR");
        String appointmentCode = "CONAPP";
       
//        hl7Message.append("FT1|0|"
//        +getApprovalMesgRefId(elegabalityApprovalEntity)
//        +"|||"
//        +formattedDateTime
//        +"|"
//        +elegableCode+"|"
//        +(elegabalityApprovalEntity.getItemCode() == null? "" : elegabalityApprovalEntity.getItemCode()) 
//        +"||"
//        + (elegableCode.equals("AA") ? "ACCEPT" : "REGECT") 
//        +"|" 
//        +(elegabalityApprovalEntity.getIsAppointment() ? "CONAPP" : "ENC")  
//        +"||||"+"P03~DFT_P03||"+elegabalityApprovalEntity.getIpPrinterAddress() == null? "" : elegabalityApprovalEntity.getIpPrinterAddress() +"||||||"+"\r");

        hl7Message.append("FT1|0|")
        .append(elegabalityApprovalEntity.getVisitInfoId())
        .append("|||").append(formattedDateTime +"|"+elegableCode+"|")
        .append((elegabalityApprovalEntity.getItemCode() !=null?  elegabalityApprovalEntity.getItemCode():"")+"|")
        .append((elegableCode.equals("AA") ? "ACCEPT" : "REGECT") + "|")
        .append(elegabalityApprovalEntity.getIsAppointment() ? "CONAPP" : "ENC")
        .append("||||"+"P03~DFT_P03|||");
      //////  .append((elegabalityApprovalEntity.getIpPrinterAddress() != null? elegabalityApprovalEntity.getIpPrinterAddress():"" )  +"||||||"+"\r");
        
        // Message Trailer (EOF)
//        hl7Message.append("EOF|\r");
        hl7Message.append(FS);
        hl7Message.append(CR);

        return hl7Message.toString();
    }

    private String getApprovalMesgRefId(ElegabalityApprovalEntity elegabalityApprovalEntity) {
    	   // + (elegabalityApprovalEntity.getAppointment_id() == null? elegabalityApprovalEntity.getVistaOrderID() :elegabalityApprovalEntity.getAppointment_id() )
    		
    	/*if(elegabalityApprovalEntity.getVistaOrderID() == null)
    	{
    		return elegabalityApprovalEntity.getVisitInfoId();
    	}else if(elegabalityApprovalEntity.getVistaOrderID().contains("RA")) 
    	{
    		return elegabalityApprovalEntity.getAppointment_id();
		}else 
		{
			return elegabalityApprovalEntity.getVistaOrderID();
			
		}*/
    	
    	return "";
    	
	}


	private void addMessageToInboundTeble(String msgAsString) {
        inboundHL7Message.setBranchId(machine.getBranchId());
        inboundHL7Message.setBranchId(machine.getTenantId());
        inboundHL7Message.setMessageBody(msgAsString);

        inboundHL7MessageService.addInbound(inboundHL7Message);
    }

    public Machine getMachineInfoByPath() {
        MachineService machineService= (MachineService) SpringUtil.getBean("MachineService");
        Machine machine= machineService.getMachineByActorPath(getContext().parent().toString());
        return machine;
    }

    public LIS2A2Msg stringToLIS2AMsg(String msgAsString) {
        Class<? extends LIS2A2Msg> msgType;
        List<LIS2A2Record> records= new ArrayList<>();
        String[] recordLines= msgAsString.split("(?<=" + CR + ")");
        for (String record : recordLines) {

            if (record.length() < 3) {
                continue;

            }

            log.debug("Converting String '" + record + "' into LIS2A2Record");
            LIS2A2Record lis2a2Record= LIS2A2Record.fromString(record);

            if (lis2a2Record == null) {
                records.clear();
                break;
            }

            /*
             * if(lis2a2Record == null) { throw new
             * RuntimeException("Unexpected message type - Segment " + record); }
             */

            log.debug(
                "Conversion successful, lis2a2Record.toString() = " + lis2a2Record.toString());
            records.add(lis2a2Record);
        }

        msgType= getMsgType(records);
        if (msgType.equals(LIS2A2OrderMsg.class))
            return new LIS2A2OrderMsg(records);
        else if (msgType.equals(LIS2A2ResultMsg.class))
            return new LIS2A2ResultMsg(records);
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
        else
            throw new RuntimeException("Unexpected message type - " + msgType);
    }

    public List<LIS2A2Msg>[] stringMsgToAstmMsg(String msgAsString, MessageType msgType,
        Machine machine) {

        MachineTypeEnum machineType= MachineTypeEnum.valueOf(machine.getMachineType().getCode());
        switch (machineType) {
        case ABBOTT_ARCHITECT_CI4100:
            return processArchitect4100CIMessage(msgAsString, msgType);

        default:

            return processASTMMessage(msgAsString, msgType);

        }

    }

    private List<LIS2A2Msg>[] processASTMMessage(String msgAsString, MessageType msgType) {
        List<LIS2A2Record> records= null;
        List<LIS2A2Msg>[] arrList2A2Msg= null;

        try {

            records= new ArrayList<>();
            String str= String.valueOf(CR) + String.valueOf(LF);
            String msgAsString2= msgAsString;
            // = msgAsString.replace(ENQ, '\0').replaceAll(str, String.valueOf(CR));
            msgAsString2= msgAsString2.replace("OBR", "O").toString();
            msgAsString2= msgAsString2.replace("OBX", "R").toString();
            // String[] recordLines = msgAsString2.split("(?<=" + CR + ")");
            String[] recordLines= msgAsString2.split("(?<=" + CR + ")", 0);

            // records = new ArrayList();
            // arrList2A2Msg = new ArrayList[lstFinalMessage.size()];

            records= new ArrayList<>();
            for (String recordLine : recordLines) {

                log.debug("Converting String '" + recordLine + "' into LIS2A2Record");

                if (recordLine.equals("]") || recordLine.equals("\r") || recordLine.equals("\n") ||
                    (recordLine.length() == 0))
                    continue;

                if (LIS2A2Record.fromString(recordLine) != null) {
                    LIS2A2Record lis2a2Record= LIS2A2Record.fromString(recordLine);
                    log.debug("Conversion successful, lis2a2Record.toString() = " +
                        lis2a2Record.toString());
                    records.add(lis2a2Record);
                } else {
                    continue;
                }

            }

            arrList2A2Msg= new ArrayList[1];

            msgType.refMsgType= getMsgType(records);

            if (msgType.refMsgType == null) {
                return null;
            }

            /*
             * if (msgType.refMsgType.equals(LIS2A2OrderMsg.class)) { arrList2A2Msg[index] =
             * new ArrayList<LIS2A2Msg>(); arrList2A2Msg[index].add(new
             * LIS2A2OrderMsg(records)); }
             */

            else if (msgType.refMsgType.equals(LIS2A2ResultMsg.class)) {
                arrList2A2Msg[0]= new ArrayList<>();
                arrList2A2Msg[0].add(new LIS2A2ResultMsg(records));
            } else if (msgType.refMsgType.equals(LIS2A2QueryMsg.class)) {
                arrList2A2Msg[0]= new ArrayList<>();
                arrList2A2Msg[0].add(new LIS2A2QueryMsg(records));
            }

        } catch (NullPointerException ex) {
            log.error("Unexpected message type - " + msgType + "for message text : " + msgAsString);
        }

        return arrList2A2Msg;
    }

    private List<LIS2A2Msg>[] processArchitect4100CIMessage(String msgAsString,
        MessageType msgType) {
        List<LIS2A2Record> records= null;
        List<LIS2A2Msg>[] arrList2A2Msg= null;

        try {

            String[] inputMEssage= msgAsString.split("(" + EOT + ")");
            // need to write a function to seperate a large and nessted based on fotter
            // L|[CR]
            final String regexFotter= "(?:(L)\\|(\\d+)\\" + CR + ")";
            List<String> lstFinalMessage= new ArrayList<>();

            for (String element : inputMEssage) {
                String[] inputMEssage2= element.split(regexFotter);
                System.out.println(inputMEssage2);
                String newMessage= "";

                for (String element2 : inputMEssage2) {
                    int indexOFSTX= -1;

                    indexOFSTX= element2.indexOf(STX);

                    if (indexOFSTX != -1) {
                        newMessage= element2.substring(indexOFSTX, element2.length() - 1) + "L|" +
                            CR + LF;
                        lstFinalMessage.add(newMessage);
                    }

                }

            }

            records= new ArrayList();
            arrList2A2Msg= new ArrayList[lstFinalMessage.size()];

            String str= String.valueOf(CR) + String.valueOf(LF);

            for (int index= 0; index < lstFinalMessage.size(); index++ ) {
                String msgAsString2= lstFinalMessage.get(index).replace(ENQ, '\0').replaceAll(str,
                    String.valueOf(CR));
                msgAsString2= msgAsString2.replace("OBR", "O").toString();
                msgAsString2= msgAsString2.replace("OBX", "R").toString();
                String[] recordLines= msgAsString2.split("(?<=" + CR + ")");
                // String[] validatedRecordLines = validateRecordLines(recordLines);
                records= new ArrayList<>();
                for (String record : recordLines) {

                    log.debug("Converting String '" + record + "' into LIS2A2Record");

                    if (record.equals("]") || record.equals("\r") || record.equals("\n"))
                        continue;

                    if (LIS2A2Record.fromString(record) != null) {
                        LIS2A2Record lis2a2Record= LIS2A2Record.fromString(record);
                        log.debug("Conversion successful, lis2a2Record.toString() = " +
                            lis2a2Record.toString());
                        records.add(lis2a2Record);
                    } else {
                        continue;
                    }

                }

                msgType.refMsgType= getMsgType(records);

                if (msgType.refMsgType == null) {
                    return null;
                }

                /*
                 * if (msgType.refMsgType.equals(LIS2A2OrderMsg.class)) { arrList2A2Msg[index] =
                 * new ArrayList<LIS2A2Msg>(); arrList2A2Msg[index].add(new
                 * LIS2A2OrderMsg(records)); }
                 */

                else if (msgType.refMsgType.equals(LIS2A2ResultMsg.class)) {
                    arrList2A2Msg[index]= new ArrayList<>();
                    arrList2A2Msg[index].add(new LIS2A2ResultMsg(records));
                } else if (msgType.refMsgType.equals(LIS2A2QueryMsg.class)) {
                    arrList2A2Msg[index]= new ArrayList<>();
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
        Class<? extends LIS2A2Msg> msgType= null;
 
        try {
            
            if (records.size() == 0) {
                msgType= LIS2A2_UNKOWN_Msg.class;
            }
            MSG_TYPE messageType= null;

            messageType= MSG_TYPE.valueOf(records.get(0).getComponentValue(8, 1));

            msg_type= messageType;
            System.out.println(msg_type);
            // check message type:
            switch (msg_type) {
            case ADT:
                msgType= LIS2A2_ADT_Msg.class;
                break;
            case SIU:
                msgType= LIS2A2_SIU_Msg.class;
                break;
            case DFT:
                msgType= LIS2A2_DFT_Msg.class;
                break;
            default:
                msgType= LIS2A2Msg.class;

            }
        } catch (Exception ex) {

            msgType= LIS2A2Msg.class;
        }
       
        return msgType;
    }

    public static String create() {
        // TODO Auto-generated method stub
        return null;
    }

}