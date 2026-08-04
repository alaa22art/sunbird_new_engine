package com.certacure.lis.interfaces.middleware.parser.hl7;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v26.message.OML_O33;
import ca.uhn.hl7v2.model.v26.segment.*;
import ca.uhn.hl7v2.model.v25.datatype.XPN;
import ca.uhn.hl7v2.model.v25.message.RSP_K11;
import ca.uhn.hl7v2.model.v26.datatype.CWE;
import ca.uhn.hl7v2.model.v26.group.*;
import ca.uhn.hl7v2.parser.PipeParser;
import java.util.Date;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageTransactionType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CobasPROHL7Generator {
	
	public class Test{
		String assayCode;
		String name;
		String testCode; 
		String barcode;
		public Test(String code, String name, String barcode) {
			this.assayCode = code;
			this.name= name;
			this.barcode = barcode;
		}
		public String getAssayCode() {
			return assayCode;
		}
		public void setAssayCode(String assayCode) {
			this.assayCode = assayCode;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public String getTestCode() {
			return testCode;
		}
		public void setTestCode(String testCode) {
			this.testCode = testCode;
		}
		
		public String getBarcode() {
			return barcode;
		}
		public void setBarcode(String barcode) {
			this.barcode = barcode;
		}
		
		
		
	}
	
    
    public class CobasPRO_OrderMessageData {
    	private String version;
        private String specimenId;
        private String barcode;
        private Test [] arrTest;
        private String customDefinedCodingSystemidentifier;
        private String laboratoryOrderCode;
        private String HL7TableRiskCodes;
        private String patientDateOfBirth;
        private String patientFileNumber;
        private String queryTag;
        private String position;
        private String rackID;
		private String DOB;
		private String queryControlID;
        
		/*
		 * 						patient.getPatientId(1),
						order.getSpecimenDetails(4, 4), //Position
						order.getSpecimenDetails(3, 4), //rackID
						order.getSpecimenDetails(2, 4), //QueryControlID
						order.getSpecimenDetails(1, 4));//QueryTag
		 * */
        
        public CobasPRO_OrderMessageData(
        		String specimenId,
        		String barcode,
        		Test [] arrTest,
        		String customDefinedCodingSystemidentifier , 
        		String laboratoryOrderCode , 
        		String HL7TableRiskCodes ,
        		String version , 
        		String patientDateOfBirth,
        		String patientFileNumber ,
        		String position,
        		String rackID ,
        		String queryControlID ,
        		String queryTag 
        		
        		 ) {
            this.specimenId = specimenId;
            this.barcode = barcode;
            this.arrTest = arrTest;
            this.customDefinedCodingSystemidentifier = customDefinedCodingSystemidentifier;
            this.laboratoryOrderCode = laboratoryOrderCode;
            this.HL7TableRiskCodes = HL7TableRiskCodes;
            this.version = version;
            this.patientDateOfBirth = patientDateOfBirth;
            this.patientFileNumber = patientFileNumber;
            this.queryTag = queryTag;
            this.position = position;
            this.queryControlID = queryControlID;
            this.rackID = rackID;
            
        }
        
        public CobasPRO_OrderMessageData(String version) {
            //this.specimenId = specimenId;
            //this.barcode = barcode;
            //this.testCodes = testCodes;
            //this.testNames = testNames; 
            //this.customDefinedCodingSystemidentifier = customDefinedCodingSystemidentifier;
            //this.laboratoryOrderCode = laboratoryOrderCode;
            //this.HL7TableRiskCodes = HL7TableRiskCodes;
            this.version = version;
        }
        
        public String getSpecimenId() { return specimenId; }
        public String getBarcode() { return barcode; }
        public Test [] getTest() { return arrTest; }
        public String getCustomDefinedCodingSystemidentifier() { return customDefinedCodingSystemidentifier; }
        public String getLaboratoryOrderCode() { return laboratoryOrderCode; }
        public String getHL7TableRiskCodes() { return HL7TableRiskCodes; }
        public String getVersion(){ return version; }
        public String getpatientFileNumber(){ return patientFileNumber; }
        public String getPatientDateOfBirth(){ return patientDateOfBirth; }
        public String getRackID(){ return rackID; }
        public String getPosition(){ return position; }
        public String getQueryTag(){ return queryTag; }
        public String getDOB(){ return DOB; }
        public String getQueryControID(){ return queryControlID; }

    }
    
    public CobasPROHL7Generator(String string) {
		// TODO Auto-generated constructor stub
	}

	public static String generateOrderMessage(CobasPRO_OrderMessageData cobasProMessageDataObject) throws HL7Exception {
        // Create OML_O33 message
		 // Set current timestamp
		
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMddHHmmssZ");
        String timestamp = dateFormat1.format(new Date());
        
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp2 = dateFormat2.format(new Date());
        
        OML_O33 message = new OML_O33();
        
        // Populate MSH segment
        MSH msh = message.getMSH();
        msh.getFieldSeparator().setValue("|");
        msh.getEncodingCharacters().setValue("^~\\&");
        msh.getSendingApplication().getNamespaceID().setValue("host");
        msh.getReceivingApplication().getNamespaceID().setValue("cobas pro");
        msh.getDateTimeOfMessage().setValue(timestamp);
        msh.getMessageType().getMsg1_MessageCode().setValue("OML");
        msh.getMessageType().getTriggerEvent().setValue("O33");
        msh.getMessageType().getMessageStructure().setValue("OML_O33");
        msh.getMessageControlID().setValue(UniqueSequenceGenerator.generateHighResSequence());
        msh.getProcessingID().getProcessingID().setValue("P");
        msh.getVersionID().getVersionID().setValue("2.5.1");
        msh.getMsh15_AcceptAcknowledgmentType().setValue("NE");
        msh.getMsh16_ApplicationAcknowledgmentType().setValue("AL");
        msh.getMsh18_CharacterSet(0).setValue("UNICODE UTF-8");
        
        msh.getMsh21_MessageProfileIdentifier(0).getEi1_EntityIdentifier().setValue("LAB-28R");
        msh.getMsh21_MessageProfileIdentifier(0).getEi2_NamespaceID().setValue("ROCHE");
        
        // Create PATIENT and PID segment
        message.getPATIENT().getPID().getPatientIdentifierList(0).getIDNumber().setValue(cobasProMessageDataObject.getpatientFileNumber() );
        message.getPATIENT().getPID().getDateTimeOfBirth().setValue(cobasProMessageDataObject.getPatientDateOfBirth());
        message.getPATIENT().getPID().getPatientIdentifierList(0).getIdentifierTypeCode().setValue(""); // Empty
        message.getPATIENT().getPID().getPatientIdentifierList(0).getAssigningAuthority().getNamespaceID().setValue(""); // Empty
        message.getPATIENT().getPID().getPid5_PatientName(0).getXpn7_NameTypeCode().setValue("U");

        message.getPATIENT().getPID().getPid8_AdministrativeSex().setValue("M");
        
        // Create SPECIMEN, SPM segment
        OML_O33_SPECIMEN specimen = message.getSPECIMEN(0);
      
        SPM spm = specimen.getSPM();
        spm.getSetIDSPM().setValue("1");
        spm.getSpecimenID().getEip1_PlacerAssignedIdentifier().getEi1_EntityIdentifier().setValue(cobasProMessageDataObject.getSpecimenId());
        spm.getSpecimenID().getEip1_PlacerAssignedIdentifier().getNamespaceID().setValue(cobasProMessageDataObject.getBarcode());
        spm.getSpecimenType().getIdentifier().setValue("SERPLAS");
        // spm.getSpecimenType().getText().setValue(cobasProMessageDataObject.getCustomDefinedCodingSystemidentifier());
        spm.getSpecimenType().getNameOfCodingSystem().setValue(cobasProMessageDataObject.getCustomDefinedCodingSystemidentifier());
        
        
        //spm.getSpm14_SpecimenDescription(0).parse("");
        
        spm.getSpecimenCollectionDateTime().getRangeStartDateTime().setValue(timestamp2);
        spm.getSpecimenRole(0).getIdentifier().setValue("P");
        spm.getSpecimenRole(0).getNameOfCodingSystem().setValue(cobasProMessageDataObject.getHL7TableRiskCodes());
        spm.getSpm27_ContainerType().getCwe1_Identifier().setValue("SC");
        spm.getSpm27_ContainerType().getCwe3_NameOfCodingSystem().setValue("99ROC");
        
        // Create SPECIMEN_CONTAINER and SAC segment
        //SAC|||9791247951^BARCODE|||||||50061|4
        //SAC|||50061^4
        SAC sac = specimen.getSAC(0);
        
        sac.getSac3_ContainerIdentifier().getEi1_EntityIdentifier().setValue(cobasProMessageDataObject.getSpecimenId());
        sac.getSac3_ContainerIdentifier().getEi2_NamespaceID().setValue("BARCODE");
        sac.getSac10_CarrierIdentifier().getEntityIdentifier().setValue(cobasProMessageDataObject.getRackID());
        sac.getSac11_PositionInCarrier().getValue1().setValue(cobasProMessageDataObject.getPosition());
        
        
        
        
        // Create ORDER groups for each test
        Test[] arrTest = cobasProMessageDataObject.getTest();
          
        for (int i = 0; i < arrTest.length; i++) {
            OML_O33_ORDER order = specimen.getORDER(i);
            
            
            // ORC segment
            ORC orc = order.getORC();
            orc.getOrderControl().setValue("NW");
            orc.getDateTimeOfTransaction().setValue(timestamp2);
            
            // TQ1 segment
           // TQ1 tq1 = order.getTIMING().getTQ1();
         // Create new TQ1 segment for this order
         // Create TIMING group within ORDER
            OML_O33_TIMING timing = order.getTIMING();
            TQ1 tq1 = timing.getTQ1();
           
         // Another way to create the priority repetition
            tq1.insertPriority(0);
          
            tq1.getPriority(0).getIdentifier().setValue("R");
            tq1.getPriority(0).getNameOfCodingSystem().setValue(cobasProMessageDataObject.getHL7TableRiskCodes());
            
            // OBR segment
            OBR obr = order.getOBSERVATION_REQUEST().getOBR();
            obr.getSetIDOBR().setValue(String.valueOf(i + 1));
            obr.getPlacerOrderNumber().getEntityIdentifier().setValue(cobasProMessageDataObject.getSpecimenId());
            //obr.getFillerOrderNumber().getEntityIdentifier().setValue(cobasPro.getSpecimenId());
            obr.getUniversalServiceIdentifier().getIdentifier().setValue(arrTest[i].getAssayCode());
           // obr.getUniversalServiceIdentifier().getText().setValue(testNames[i]);
            obr.getUniversalServiceIdentifier().getNameOfCodingSystem().setValue(cobasProMessageDataObject.getCustomDefinedCodingSystemidentifier());
           //TCD|29230^^99ROC|^1^:^1
            TCD tcd = order.getOBSERVATION_REQUEST().getTCD();
            tcd.getTcd1_UniversalServiceIdentifier().getCwe1_Identifier().setValue(arrTest[i].getAssayCode());
            tcd.getTcd1_UniversalServiceIdentifier().getCwe3_NameOfCodingSystem().setValue(cobasProMessageDataObject.getCustomDefinedCodingSystemidentifier());  
        }
        

        
        // Convert message to string
        PipeParser parser = new PipeParser();
        return parser.encode(message);
    }
    
    public  void main(String[] args) throws HL7Exception {
        // Create CobasPRO object with test data
		String[] testCodes = {"10003", "10019", "29112", "29113", "29114"};
		String[] testNames = {"10003", "10019", "29112", "29113", "29114"};
		
	//	CobasPRO_OrderMessageData cobasProOrder = new CobasPRO_OrderMessageData("9791247951", "BARCODE", testCodes, testNames,"99ROC","LAB-28R","HL70485" , "2.5.1");
    }

	public String generateOrderMessage(LIS2A2Msg lis2a2OrderMsg,
			MessageTransactionType testSelection, String string3, String string4, String string5, String string6) {

		
		OML_O33 oml_o_33 = new OML_O33();
		RSP_K11 rsp_k11 = new RSP_K11();
		
		String strOMLMsg = "";
		String strRSPMsg = "";
		String strFinalOrderMessage = "";
		Test [] arrTest = null;
		
		try {
	
			PatientRecord patient = (PatientRecord) lis2a2OrderMsg.getAllRecords().get(1);
			OrderRecord order = (OrderRecord) lis2a2OrderMsg.getAllRecords().get(2);
			
			String [] strTestCode =  order.getFieldValue(4).split("\\\\");
		  //  orderRecord.getComponentValue(4,2,"^");
			
		
			
		
			if(strTestCode.length > 0 && strTestCode[0] != "") 
			{
				Test [] arrTest1  = new Test[strTestCode.length];
				
				for(int i =0 ; i< strTestCode.length ; i++)
				{
					arrTest1[i] = new Test(strTestCode[i],strTestCode[i] , order.getSpecimenId(2));				
				}
				

				//(OrderRecord) lis2a2OrderMsg.getAllRecords().get(2)
				String strBarcode = formatIfNumeric(arrTest1[0].getBarcode());
			
				CobasPRO_OrderMessageData cobasProOrder = new CobasPRO_OrderMessageData(
						strBarcode,
						"BARCODE",
						arrTest1,
						"99ROC",
						"LAB-28R",
						"HL70485" ,
						"2.5.1" , 
						patient.getDateTimeOfBirth(6),
						patient.getPatientId(1),
						order.getSpecimenDetails(4, 4), //Position
						order.getSpecimenDetails(3, 4), //rackID
						order.getSpecimenDetails(2, 4), //QueryControlID
						order.getSpecimenDetails(1, 4));//QueryTag
						
				 
				strOMLMsg =  generateOrderMessage( cobasProOrder);
				
			
				
				strFinalOrderMessage =  strOMLMsg;
				
			}else
			{
				 //String strRSP_K11 = 
				 CreateMSH((HeaderRecord) lis2a2OrderMsg.getAllRecords().get(0), rsp_k11);
				 CreateMSA((OrderRecord) lis2a2OrderMsg.getAllRecords().get(2), rsp_k11);
			     CreateQAK((OrderRecord) lis2a2OrderMsg.getAllRecords().get(2), rsp_k11);
				
			     strRSPMsg = rsp_k11.encode() +     
			     CreateQPD((OrderRecord) lis2a2OrderMsg.getAllRecords().get(2), rsp_k11);
				
			    
				
				strRSPMsg =  CreateEmptyOrderSegments( (OrderRecord) lis2a2OrderMsg.getAllRecords().get(2), strRSPMsg);
				
				strFinalOrderMessage =  strRSPMsg;
			}
			
			
			
		}catch(Exception ex )
		{
			
		}
		
		
		
//		Test [] arrTest1  = getTestCodeArray(lis2a2OrderMsg) ;
		
	//	 CobasPRO_OrderMessageData cobasProOrderMessageProvider = new CobasPRO_OrderMessageData( arrTest1[0].getBarcode(), "BARCODE", arrTest ,"99ROC","LAB-28R","HL70485" , "2.5.1");
		// String strOMLO33 = generateOrderMessage(lis2a2OrderMsg, testSelection, string3, string4, string5, string6);
				
	return strFinalOrderMessage;
	}
	
	private String CreateEmptyOrderSegments(OrderRecord orderRecord, String strRSP_K11) {
		
		try {

			return strRSP_K11;
			
		} catch (Exception ex)

		{

		}
		return "";
	}

	
	private void CreateMSH(HeaderRecord lIS2A2HeaderRecord, RSP_K11 rsp_k11) throws HL7Exception {
		try
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			
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
			
			
			
		}catch(Exception ex)
		{
			
		}
		
		
	}
	
	private void CreateMSA(OrderRecord orderRecord, RSP_K11 rsp_k11) throws HL7Exception {
		try {
			
			rsp_k11.getMSA().getAcknowledgmentCode().setValue("AA");
			rsp_k11.getMSA().getMsa2_MessageControlID().setValue(orderRecord.getSpecimenDetails(2, 4));
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		
	}
	
	
	private void CreateQAK(OrderRecord orderRecord, RSP_K11 rsp_k11) throws HL7Exception {
		//"QAK|110|OK|RRRBAR^^99ROC"
		try {
			
			////QAK|110|OK|RRRBAR^^99ROC
			
			
			rsp_k11.getQAK().getQak1_QueryTag().setValue(orderRecord.getSpecimenDetails(1,4));
			rsp_k11.getQAK().getQak2_QueryResponseStatus().setValue("OK");
			rsp_k11.getQAK().getQak3_MessageQueryName().getCe1_Identifier().setValue(orderRecord.getSpecimenIds(5, 4));
			rsp_k11.getQAK().getQak3_MessageQueryName().getCe3_NameOfCodingSystem().setValue("99ROC");
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
	}
	
	public static String formatIfNumeric(String input) {
	    if (input.matches("\\d+")) { // Check if it contains only digits
	        return String.format("%05d", Integer.parseInt(input));
	    } else {
	        return input; // Return as-is if not all digits
	    }
	}
	
	private String CreateQPD(OrderRecord orderRecord, RSP_K11 rsp_k11) {
		//QPD|RRRBAR^^99ROC|110|ID123456|50002|1|||||SERPLAS^^99ROC|SC^^99ROC|R
		
		 String qpdSegment = "" ;
		try {
			
			String strBarcode = formatIfNumeric(orderRecord.getSpecimenDetails(1,3));
			
			qpdSegment = orderRecord.getSpecimenIds(5, 4)+ "^^99ROC"
			+ "|" + orderRecord.getSpecimenDetails(1,4)
			
			+ "|" + strBarcode
			+ "|" + orderRecord.getSpecimenDetails(3,4)
			+ "|" + orderRecord.getSpecimenDetails(4,4) 
			+ "|||||SERPLAS^^99ROC|SC^^99ROC|R" + "\r";
			
			
		
            
			//rsp_k11.getQPD().getQpd1_MessageQueryName().getCe1_Identifier().setValue("RRRBAR");
			//rsp_k11.getQPD().getQpd1_MessageQueryName().getCe2_Text().setValue("99ROC");
			//rsp_k11.getQPD().getQpd2_QueryTag().setValue(orderRecord.queryTag);
			//rsp_k11.getQPD().getQpd3_UserParametersInsuccessivefields().getExtraComponents().getComponent(0).setData(");
			//rsp_k11.getQPD().getQak5_ThisPayload().setValue(orderRecord.getSpecimnPositionInfo());
			//rsp_k11.getQPD().getQak6_HitsRemaining().setValue(orderRecord.getSpecimenId());
            
            //System.out.println(qpd.encode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return qpdSegment;
		
	}

	private Test[] getTestCodeArray(LIS2A2Msg lis2a2OrderMsg) {
		
		Test [] arrTest = null;
		
		OrderRecord orderRecord = (OrderRecord) lis2a2OrderMsg.getAllRecords().get(2);
		
		return arrTest;
	}
}

