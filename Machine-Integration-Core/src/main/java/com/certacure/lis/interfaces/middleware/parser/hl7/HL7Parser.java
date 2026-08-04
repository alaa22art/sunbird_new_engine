package com.certacure.lis.interfaces.middleware.parser.hl7;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.common.business.exception.BusinessException;
import com.certacure.core.common.business.exception.BusinessException.ErrorSeverity;
import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.DataOrderInboundHL7Message;
import com.certacure.lis.interfaces.entities.LkpMessageSourceType;
import com.certacure.lis.interfaces.entities.MachineOrder;
import com.certacure.lis.interfaces.entities.MachineResult;
import com.certacure.lis.interfaces.entities.OutboundHl7MessageSequance;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.service.LkpService;
import com.certacure.lis.interfaces.service.OutboundHl7MessageSequanceService;

import ca.uhn.hl7v2.model.v25.segment.*;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.*;
import ca.uhn.hl7v2.model.v25.datatype.CX;
import ca.uhn.hl7v2.model.v25.datatype.XPN;
import ca.uhn.hl7v2.model.v25.group.OML_O33_ORDER;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;

public class HL7Parser extends PipeParser {
	
	private Message hl7Message;
	private List<MachineOrder> lstOrder;
	private CanonicalModelClassFactory mcf;
	private HapiContext context;
	private Message message;
	private OML_O33 orderOMLO33Message;
	private OUL_R22 oulr22;
	private OML_O33 oml_o33;
	private ORU_R01 orur01;
	private OUL_R22 oul_r22;
	private QBP_Q11 qbp_q11;
	private Message ackMessage;
	private OutboundHl7MessageSequance outHl7MessageSeq;
	private OutboundHl7MessageSequanceService outHl7MessageSeqService;

	
	
	

	public enum MessageSourceType {

		LIS("LIS"), HL7("New Message"), UNKNOWN("UNKNOWN"), ADT_A01("Patient Admission"), ADT_A02("Transfer"),
		ADT_A03("Discharge"), ADT_A04("Create Visit"), ADT_A05("Patient Preadmission"), ADT_A08("Update Admission"),
		ADT_A11("Cancel Admission"), ADT_A13(""), ADT_A28("Create Patient"), ADT_A31("Update Patient"),
		SIU_S12("Create Appointment"), SIU_S13("Cancel Appointment"), SIU_S15("Update Appointment"),
		DFT_P03("Create Order"), JSON_QMS("JSON_QMS"), DFT_P03_CLERANCE("Financial Clearnce"),
		DFT_P03_COMPLETE("Complete Order"), DFT_P03_CANCEL("Cancel Order"), DFT_P03_CREATE("Create Order"),
		ACTIVATE_APPOINTMRNT("ACTIVATE_APPOINTMRNT"), ASTM("ASTM"), ACK("ACK");

		/*
		 * LIS("LIS"), HL7("HL7"), UNKNOWN("UNKNOWN"), ADT_A01("ADT_A01"),
		 * ADT_A02("ADT_A02"), ADT_A03("ADT_A03"), ADT_A04("ADT_A04"),
		 * ADT_A05("ADT_A05"), ADT_A08("ADT_A08"), ADT_A11("ADT_A11"),
		 * ADT_A28("ADT_A28"), ADT_A13("ADT_A13"), ADT_A31("ADT_A31"),
		 * SIU_S12("SIU_S12"), SIU_S13("SIU_S13"), SIU_S15("SIU_S15"),
		 * DFT_P03("DFT_P03"),
		 * 
		 * JSON_QMS("JSON_QMS"), ASTM("ASTM"), ACK("ACK");
		 */

		private String value;
		

		private MessageSourceType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		
	}

	public enum MessageDirection {
		IN("IN"), OUT("OUT");

		private String value;

		private MessageDirection(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum MessageTransactionType {
		QUERY_RECEIVED("QUERY_RECEIVED"),
		RESULT_RECEIVED("RESULT_RECEIVED"), 
		TEST_SELECTION("TEST_SELECTION"),
		CONNECTION_OPEN("CONNECTION_OPEN"), 
		RESULT_TO_LIS("RESULT_TO_LIS"), 
		CONNECTION_CLOSE("CONNECTION_CLOSE"),
		ACK_RECEIVED("ACK_RECEIVED"),
		RESPONCE_SENT("RESPONCE_SENT"),
		ORL_LAB_ORDER_RESPONCE("ORL_LAB_ORDER_RESPONCE");

		private String value;

		private MessageTransactionType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public class ObservationOrder {

		public String strSampleNo;
		public String orderId;

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public List<ObserverRequest> lstRequest;

		public ObservationOrder() {
			lstRequest = new ArrayList<ObserverRequest>();
		}

	}

	public class ObserverRequest {

		String TestCode;
		String ActionCode;
		String SampleNo;
		Long OrderId;

		public ObserverRequest(String TestCode, String ActionCode, String SampleNo, Long OrderId) {
			this.TestCode = TestCode;
			this.ActionCode = ActionCode;
			this.SampleNo = SampleNo;
			this.OrderId = OrderId;
		}

	}

	public enum hl7PatientNameIndex {
		First, Second, Last;

		public static int valueOf(Class<hl7PatientNameIndex> class1, hl7PatientNameIndex index) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	public enum hl7TestActionCode {

		Add("A"), Rerun("R"), Cancel("C");

		private String val;

		hl7TestActionCode(String val) {
			this.val = val;
		}

		public String getVal() {
			return val;
		}

		public void setVal(String val) {
			this.val = val;
		}

	}

	public enum hl7SpacimentType {

		SerumOrPlasma(1), Urine(2), CSF(3), Supernatant(4), Other(5), WholeBlood(6), Saliva(7);

		private int val;

		hl7SpacimentType(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		public void setVal(int val) {
			this.val = val;
		}

	}

	
	

	public OML_O33 getOrderOMLO33Message() {
		return orderOMLO33Message;
	}

	public void setOrderOMLO33Message(OML_O33 orderOMLO33Message) {
		this.orderOMLO33Message = orderOMLO33Message;
	}

	public void setResultORU_R01Message(ORU_R01 ORU_R01) {
		this.orur01 = ORU_R01;
	}
	
	public void setResultOUL_R22Message(OUL_R22 OUL_R22) {
		this.oul_r22 = OUL_R22;
	}
	
	private void setResultQBP_Q11Message(QBP_Q11 QBP_Q11) {
		this.qbp_q11 = QBP_Q11;
		
	}
	
	public Message getAckMessage() {
		return ackMessage;
	}

	public void setAckMessage(Message ackMessage) {
		this.ackMessage = ackMessage;
	}
	
	

	public HL7Parser(String strVersion) {

	}

	public HL7Parser(String strHL7, String strVersion) throws IOException {
		try {

			message = this.parse(strHL7);
			
			ackMessage = message.getMessage().generateACK();
			
			Message ackMessage= message.generateACK();

			if (message instanceof OML_O33) {
				setValidationContext(null);
				OML_O33 orderMessage = (OML_O33) message;
				setOrderOMLO33Message(orderMessage);
			}
			if (message instanceof ORU_R01) {
				setValidationContext(null);
				ORU_R01 resultMessage = (ORU_R01) message;
				setResultORU_R01Message(resultMessage);
			}if (message instanceof OUL_R22) {
				setValidationContext(null);
				OUL_R22 resultMessage = (OUL_R22) message;
				setResultOUL_R22Message(resultMessage);
			}
			if (message instanceof QBP_Q11) {
				setValidationContext(null);
				QBP_Q11 queryMessage = (QBP_Q11) message;
				setResultQBP_Q11Message(queryMessage);
			}
			else if (message instanceof ACK) {

				//ACK ackMessage = (ACK) message;

			}

		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	public ca.uhn.hl7v2.model.v25.message.OML_O33 StringToHL7Message(String strMessage) {

		try {
			hl7Message = parse(strMessage);
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return (OML_O33) hl7Message;

	}

	public List<MachineOrder> getOrderList(OML_O33 newOMLOrder) {
		String[] strHl7Lines = newOMLOrder.getMessage().toString().split("\r");
		List<ObservationOrder> lstObserverOrder = null;
		MachineOrder tempOrder = null;

		lstObserverOrder = createOrderList(newOMLOrder);
		List<MachineOrder> lstOrder = new ArrayList<>();

		try {

			for (int index = 0; index < lstObserverOrder.size(); index++) {
				// -------------------------------------------------------------------------------//
				for (int index2 = 0; index2 < lstObserverOrder.get(index).lstRequest.size(); index2++) {
					tempOrder = new MachineOrder();

					tempOrder.setPatientId(this.getHL7PatientID(newOMLOrder));
					tempOrder.setPatientFirstName(this.getHL7PatientName(newOMLOrder, 0));
					tempOrder.setPatientLastName(this.getHL7PatientName(newOMLOrder, 1));
					tempOrder.setGender(this.getHL7PatientGender(newOMLOrder));
					tempOrder.setOrderId(lstObserverOrder.get(index).lstRequest.get(index2).OrderId);

					tempOrder.setDateOfBirth(this.getHL7DateOfBirth(newOMLOrder));
					// --------------------------------------------------------------------------------//
					tempOrder.setBarcode(lstObserverOrder.get(index).lstRequest.get(index2).SampleNo);
					// tempOrder.setSpacimenType(this.getHL7SpacimenType(newOMLOrder));
					tempOrder.setSpecimenDescriptor(this.getHL7SpacimenType(newOMLOrder).toString());
					Date specimenCollectionDateAndTime = new SimpleDateFormat("yyyyMMddmmss")
							.parse(this.getHL7specimenCollectionDateAndTime(newOMLOrder));
					tempOrder.setSpecimenCollectionDateAndTime(specimenCollectionDateAndTime);
					// --------------------------------------------------------------------------------//
					tempOrder.setTestCode(lstObserverOrder.get(index).lstRequest.get(index2).TestCode);
					// tempOrder.setActionCode(lstObserverOrder.get(index).lstRequest.get(index2).ActionCode);
					lstOrder.add(tempOrder);
				}

			}

		} catch (ParseException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return lstOrder;
	}

	private List<ObservationOrder> createOrderList(OML_O33 newOMLOrder) {

		String[] arrSigment = newOMLOrder.getMessage().toString().split("\r");
		List<ObservationOrder> lstOrder = new ArrayList<ObservationOrder>();
		// List<ObserverRequest> lstRequest = new ArrayList<ObserverRequest>();

		ObservationOrder order = null;
		ObserverRequest request = null;
		String strBarcode = null;
		Long OrderId = null;
		// OML_O33_ORDER OMLOrder = new OML_O33_ORDER(newOMLOrder, null);

		for (int index = 0; index < arrSigment.length; index++) {
			switch (arrSigment[index].substring(0, 3)) {
			case "MSH":
				break;
			case "SPM":
				strBarcode = getBarCodeValue(arrSigment[index]);
				break;
			case "ORC":
				order = new ObservationOrder();
				OrderId = Long.parseLong(getOrderId(arrSigment[index]));
				/*
				 * if (strBarcode != null) { //order.strSampleNo = strBarcode; //order.orderId =
				 * OrderId.toString(); }
				 */
				break;
			case "OBR":
				String strTestCode = getTestCodeFromSegment(arrSigment[index]);
				if (strTestCode != null) {
					order.lstRequest.add(new ObserverRequest(strTestCode, "", strBarcode, OrderId));
					lstOrder.add(order);
				}
				break;

			}

		}

		return lstOrder;

	}

	private String getOrderId(String Segment) {
		String[] strFields = Segment.split("\\|");
		return strFields[2].toString();

	}

	private String getBarCodeValue(String Segment) {
		String[] strFields = Segment.split("\\|");
		return strFields[2].toString();

	}

	private String getTestCodeFromSegment(String Segment) {
		String[] strFields = Segment.split("\\|");

		return strFields[4].toString();

	}

	/*
	 * private List<OML_O33_ORDER> getOrderTestCodeArray(OML_O33 newOMLOrder) {
	 * //SPM spmSegmentMessageData = newOMLOrder.getSPECIMEN().getSPM();
	 * List<OML_O33_ORDER> obrSegmentMessageList = null; String[] arrSegmentMessage
	 * = null; try { obrSegmentMessageList =
	 * newOMLOrder.getSPECIMEN().getORDERAll(); arrSegmentMessage = new
	 * String[obrSegmentMessageList.size() + 1]; } catch (HL7Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * for (int index = 0; index < arrSegmentMessage.length; index++) { try {
	 * arrSegmentMessage[index] =
	 * obrSegmentMessageList.get(index).getOBSERVATION_REQUEST().getOBR()
	 * .getObr4_UniversalServiceIdentifier().encode().toString(); } catch
	 * (HL7Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * }
	 * 
	 * return null;
	 * 
	 * }
	 */

	private String getHL7specimenCollectionDateAndTime(OML_O33 newOMLOrder) {
		SPM spmSegmentMessageData = newOMLOrder.getSPECIMEN().getSPM();

		String strSpecimenCollectionDateAndTime = null;
		Date dSpecimenCollectionDateAndTime = null;
		try {
			strSpecimenCollectionDateAndTime = spmSegmentMessageData.getSpecimenCollectionDateTime().encode()
					.toString();
			dSpecimenCollectionDateAndTime = new SimpleDateFormat("yyyyMMdd").parse(strSpecimenCollectionDateAndTime);

		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strSpecimenCollectionDateAndTime;
	}

	private String getHL7SpacimenType(OML_O33 newOMLOrder) {

		SPM spmSegmentMessageData = newOMLOrder.getSPECIMEN().getSPM();

		String strSpecimenType = null;
		try {
			strSpecimenType = spmSegmentMessageData.getSpecimenType().encode().toString();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strSpecimenType;
	}

	private String getHL7Barcode(OML_O33 newOMLOrder) {

		SPM spmSegmentMessageData = newOMLOrder.getSPECIMEN().getSPM();
		try {
			List<OML_O33_ORDER> ss = newOMLOrder.getSPECIMEN().getORDERAll();
		} catch (HL7Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String strBarcoedNumber = null;
		try {
			strBarcoedNumber = spmSegmentMessageData.getSpecimenID().encode().toString();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strBarcoedNumber;
	}

	private Date getHL7DateOfBirth(OML_O33 newOMLOrder) {

		PID pidSegmentMessageData = newOMLOrder.getPATIENT().getPID();
		Date DoB = null;

		String StrDateOfBirth = null;
		try {

			StrDateOfBirth = pidSegmentMessageData.getPid7_DateTimeOfBirth().encode().toString();
			DoB = new SimpleDateFormat("yyyyMMdd").parse(StrDateOfBirth);

		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return DoB;
	}

	private String getHL7PatientGender(OML_O33 newOMLOrder) {

		PID pidSegmentMessageData = newOMLOrder.getPATIENT().getPID();

		String strSectionPatientGender = null;
		try {
			strSectionPatientGender = pidSegmentMessageData.getPid8_AdministrativeSex().encode().toString();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strSectionPatientGender;
	}

	private String getHL7PatientName(OML_O33 newOMLOrder, int index) {

		PID pidSegmentMessageData = newOMLOrder.getPATIENT().getPID();

		String strSectionPatientName = null;
		try {
			strSectionPatientName = pidSegmentMessageData.getPid5_PatientName()[0].encode().toString();
			String arrName[] = strSectionPatientName.split("\\^");
			return arrName[index].toString();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Patient Section  " + index + " Name is " + strSectionPatientName);

		return "";
	}

	private String getHL7PatientName(OUL_R22 newResult, int index) {

		PID pidSegmentMessageData = newResult.getPATIENT().getPID();

		String strSectionPatientName = null;
		try {
			strSectionPatientName = pidSegmentMessageData.getPid5_PatientName()[0].encode().toString();
			if (strSectionPatientName != "") {
				String arrName[] = strSectionPatientName.split("\\^");
				return arrName[index].toString();
			} else {
				return "";
			}

		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Patient Section  " + index + " Name is " + strSectionPatientName);

		return "";
	}

	private String getHL7PatientID(OML_O33 newOMLOrder) {

		PID pidSegmentMessageData = newOMLOrder.getPATIENT().getPID();

		String strPatientId = null;
		try {
			strPatientId = pidSegmentMessageData.getPatientID().encode().toString();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Patient Id is " + strPatientId);

		return strPatientId;
	}

	public String CreateHL7ResultMessage(MachineResult ResultInfo, MachineOrder OrderInfo, long messageControlID)
			throws HL7Exception {

		oulr22 = new OUL_R22();
		try {
			oulr22.initQuickstart("OUL", "R22", "N");
			System.out.print(oulr22.encode());

		} catch (HL7Exception e) {

			e.printStackTrace();
			throw new BusinessException("HL7Exception Exception ", "HL7Exception", ErrorSeverity.ERROR);
		} catch (IOException e) {

			e.printStackTrace();
			throw new BusinessException("IOException Exception ", "IOException", ErrorSeverity.ERROR);
		}

		/*
		 * 
		 * MSH|^~\&|CentraLink|ResultExport|LIMS|ResultImport|20080417084931||OUL^R22^
		 * OUL_R22|5|P|2.5||||||8859/1 PID|1|||||||U||||||||||||||||||||||N PV1|1|N
		 * SPM|1|2091||QC3
		 * OBR|1|2091|2091|RBC||||||||||||||||||20080417084931|||||^^^^^R
		 * ORC|SC|2091|2091||||^^^^^R||20080417084931
		 * OBX|1|NM|RBC||4.88||||||F|||19981023093347||||Advia120_06
		 * 
		 * 
		 */

		if (OrderInfo == null) {
			throw new BusinessException("Sample Order not exist ", "not exist order", ErrorSeverity.ERROR);
		}

		
		CreateMSH(ResultInfo, OrderInfo, messageControlID);
		//CreatePID(ResultInfo, OrderInfo);
		//CreateSPM(ResultInfo, OrderInfo);
		CreateOBR(ResultInfo, OrderInfo);
		CreateORC(ResultInfo, OrderInfo);
		CreateOBX(ResultInfo, OrderInfo);

		return oulr22.encode();

	}

	private void CreateOBX(MachineResult resultInfo, MachineOrder orderInfo) throws HL7Exception {
		// OBX|1|NM|RBC||4.88||||||F|||19981023093347||||Advia120_06
		// OBX obx = oulr22.getSPECIMEN().getORDER().getRESULT().getOBX();

		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx1_SetIDOBX().setValue("1");
		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx2_ValueType().setValue(resultInfo.getSampleNo());
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx3_ObservationIdentifier().getCe1_Identifier().setValue("");
		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx3_ObservationIdentifier().getCe1_Identifier()
				.setValue(resultInfo.getTestCode());
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx4_ObservationSubID().setValue("");
		/*
		 * oulr22 .getSPECIMEN().getORDER().getRESULT().getOBX().getObx15_ProducerSID().
		 * getCe1_Identifier()
		 * .setValue(resultInfo.getDataOrMeasurementValue().toString());
		 */

		// Type data = resultInfo.getDataOrMeasurementValue();

		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx5_ObservationValue(0)
				.parse(resultInfo.getDataOrMeasurementValue().toString());

		// .setData(resultInfo.getDataOrMeasurementValue().toString());

		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx6_Units().getCe1_Identifier().setValue("");
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx6_Units().getCe2_Text().setValue(resultInfo.getUnits());
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx6_Units().getCe3_NameOfCodingSystem().setValue("");
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx7_ReferencesRange().setValue("");
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx8_AbnormalFlags(0).setValue("");
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx9_Probability().setValue("");
		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx10_NatureOfAbnormalTest(0)
				.setValue(resultInfo.getAbnormalFlag());
		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx11_ObservationResultStatus()
				.setValue(resultInfo.getResultStatus());
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx12_EffectiveDateOfReferenceRange().getTs1_Time().setValue("");
		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx13_UserDefinedAccessChecks();

		String resultDateTime;

		SimpleDateFormat resultTimeFormat = new SimpleDateFormat("yyyyMMdd");
		// Date dateFromUser = null;

		resultDateTime = resultTimeFormat.format(resultInfo.getCreationDate());
		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx14_DateTimeOfTheObservation().getTs1_Time()
				.setValue(resultDateTime);
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx15_ProducerSID().getCe1_Identifier().setValue("");
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx15_ProducerSID().getCe2_Text().setValue("");
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx15_ProducerSID().getCe3_NameOfCodingSystem().setValue("");
		// obx.getObx16_ResponsibleObserver(0).getEffectiveDate().getTime().parse("");
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx17_ObservationMethod(0).getCe1_Identifier().setValue("");
		// oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx18_EquipmentInstanceIdentifier(0).getEi1_EntityIdentifier().setValue("");
		// obx.getObx19_DateTimeOfTheAnalysis().getTs1_Time().setValue("");

		// return obx.encode().toString();

	}

	private void CreateORC(MachineResult resultInfo, MachineOrder OrderInfo) throws HL7Exception {
		// ORC orc = oulr22.getSPECIMEN().getORDER().getORC();

		// * ORC|NW|411|||NW|||||||1^Halabi^Amr^Nizar_Rashed^^||
		oulr22.getSPECIMEN().getORDER().getORC().getOrc1_OrderControl().setValue("NW");
		oulr22.getSPECIMEN().getORDER().getORC().getOrc2_PlacerOrderNumber().getEi1_EntityIdentifier()
				.setValue(OrderInfo.getOrderId().toString());
		// oulr22.getSPECIMEN().getORDER().getORC().getOrc2_PlacerOrderNumber().getEi2_NamespaceID().setValue("");
		// oulr22.getSPECIMEN().getORDER().getORC().getOrc2_PlacerOrderNumber().getEi3_UniversalID().setValue("");
		// oulr22.getSPECIMEN().getORDER().getORC().getOrc3_FillerOrderNumber().getEi1_EntityIdentifier().setValue(OrderInfo.getBarcode());
		// oulr22.getSPECIMEN().getORDER().getORC().getOrc3_FillerOrderNumber().getEi2_NamespaceID().setValue("");
		// oulr22.getSPECIMEN().getORDER().getORC().getOrc3_FillerOrderNumber().getEi3_UniversalID().setValue("");
		// oulr22.getSPECIMEN().getORDER().getORC().getOrc3_FillerOrderNumber().getEi4_UniversalIDType().setValue("");
		// oulr22.getSPECIMEN().getORDER().getORC().getOrc4_PlacerGroupNumber().getEi1_EntityIdentifier().setValue("");
		// oulr22.getSPECIMEN().getORDER().getORC().getOrc4_PlacerGroupNumber().getEi2_NamespaceID().setValue("");
		// oulr22.getSPECIMEN().getORDER().getORC().getOrc4_PlacerGroupNumber().getEi3_UniversalID().setValue("");
		// oulr22.getSPECIMEN().getORDER().getORC().getOrc5_OrderStatus().setValue("");
		// oulr22.getSPECIMEN().getORDER().getORC().getOrc6_ResponseFlag().setValue("");
		oulr22.getSPECIMEN().getORDER().getORC().getOrc7_QuantityTiming(0).getTq6_Priority().setValue("R");
		oulr22.getSPECIMEN().getORDER().getORC().getOrc8_ParentOrder().getEip1_PlacerAssignedIdentifier()
				.getEi1_EntityIdentifier().setValue("");

		// return orc.encode().toString();

	}

	private void CreateOBR(MachineResult resultInfo, MachineOrder OrderInfo) throws HL7Exception {
		// * OBR|1|2091|2091|RBC||||||||||||||||||20080417084931|||||^^^^^R
		// OBR obr = oulr22.getSPECIMEN().getORDER().getOBR();

		oulr22.getSPECIMEN().getORDER().getOBR().getObr1_SetIDOBR().setValue("1");
		oulr22.getSPECIMEN().getORDER().getOBR().getObr2_PlacerOrderNumber().getEi1_EntityIdentifier()
				.setValue(OrderInfo.getOrderId().toString());
		oulr22.getSPECIMEN().getORDER().getOBR().getObr2_PlacerOrderNumber().getEi2_NamespaceID().setValue("");
		oulr22.getSPECIMEN().getORDER().getOBR().getObr2_PlacerOrderNumber().getEi3_UniversalID().setValue("");
		oulr22.getSPECIMEN().getORDER().getOBR().getObr3_FillerOrderNumber().getEi1_EntityIdentifier().setValue("");
		oulr22.getSPECIMEN().getORDER().getOBR().getObr4_UniversalServiceIdentifier().getCe1_Identifier()
				.setValue(resultInfo.getTestCode());
		// oulr22.getSPECIMEN().getORDER().getOBR().getObr4_UniversalServiceIdentifier().getCe2_Text().setValue(OrderInfo.getTestCode());
		// oulr22.getSPECIMEN().getORDER().getOBR().getObr4_UniversalServiceIdentifier().getCe3_NameOfCodingSystem().setValue("");
		oulr22.getSPECIMEN().getORDER().getOBR().getObr5_PriorityOBR().setValue(OrderInfo.getPriority());
		oulr22.getSPECIMEN().getORDER().getOBR().getObr6_RequestedDateTime().getDegreeOfPrecision().setValue("");

		String resultsRptStatusChngDateTime;

		SimpleDateFormat resultTimeFormat = new SimpleDateFormat("yyyyMMdd");
		// Date dateFromUser = null;

		resultsRptStatusChngDateTime = resultTimeFormat.format(resultInfo.getCreationDate());
		oulr22.getSPECIMEN().getORDER().getOBR().getObr22_ResultsRptStatusChngDateTime().getTime()
				.setValue(resultsRptStatusChngDateTime);
		oulr22.getSPECIMEN().getORDER().getOBR().getObr27_QuantityTiming(0).getTq6_Priority()
				.setValue(OrderInfo.getPriority());

		// System.out.println(oulr22.getSPECIMEN().getORDER().getOBR());
		// return obr.encode().toString();

	}

	private void CreateSPM(OrderRecord orderRecord, OML_O33 oml_O_33) throws HL7Exception {
		// Populate the PID Segment
		// SPM|1|2091||QC3
		// SPM spm = oulr22.getSPECIMEN().getSPM();

		oml_O_33.getSPECIMEN().getSPM().getSpm1_SetIDSPM().setValue("1");
		oml_O_33.getSPECIMEN().getSPM().getSpm2_SpecimenID().getPlacerAssignedIdentifier().getEi1_EntityIdentifier()
				.setValue(orderRecord.getSpecimenId(2));
		// spm
		// .getSpm2_SpecimenID().getEip1_PlacerAssignedIdentifier().getEi1_EntityIdentifier()
		// .setValue(outboundInfo.getDataOutboundHl7Message().getMessageControllerId().toString());
		oml_O_33.getSPECIMEN().getSPM().getSpm4_SpecimenType().getCwe1_Identifier().setValue(orderRecord.getSpecimenDescriptor());
		oml_O_33.getSPECIMEN().getSPM().getSpm4_SpecimenType().getCwe2_Text().setValue("");
		oml_O_33.getSPECIMEN().getSPM().getSpm4_SpecimenType().getCwe3_NameOfCodingSystem().setValue("HL70487");

		oml_O_33.getSPECIMEN().getSPM().getSpm5_SpecimenTypeModifierReps();
		oml_O_33.getSPECIMEN().getSPM().getSpm6_SpecimenAdditives();
		oml_O_33.getSPECIMEN().getSPM().getSpm7_SpecimenCollectionMethod();
		oml_O_33.getSPECIMEN().getSPM().getSpm8_SpecimenSourceSite();
		oml_O_33.getSPECIMEN().getSPM().getSpm9_SpecimenSourceSiteModifier();
		oml_O_33.getSPECIMEN().getSPM().getSpm10_SpecimenCollectionSite();
		oml_O_33.getSPECIMEN().getSPM().getSpm11_SpecimenRole(0).getCwe1_Identifier().setValue("P");
		oml_O_33.getSPECIMEN().getSPM().getSpm11_SpecimenRole(0).getCwe2_Text().setValue("");
		oml_O_33.getSPECIMEN().getSPM().getSpm11_SpecimenRole(0).getCwe3_NameOfCodingSystem().setValue("HL70369");
		//oml_O_33.getSPECIMEN().getSPM().getSpm12_SpecimenCollectionAmount();
		//oml_O_33.getSPECIMEN().getSPM().getSpm13_GroupedSpecimenCount().setValue("");
		//oml_O_33.getSPECIMEN().getSPM().getSpm14_SpecimenDescription();

		// spm.getSetIDSPM().setValue("1");
		//oml_O_33.getSPECIMEN().getSPM().getSpecimenCollectionMethod().getCwe1_Identifier().setValue("");
		//oml_O_33.getSPECIMEN().getSPM().getSpecimenType().getCwe1_Identifier().setValue("");
		//oml_O_33.getSPECIMEN().getSPM().getContainerCondition().getCodingSystemVersionID().setValue("");
		//oml_O_33.getSPECIMEN().getSPM().getSpecimenAvailability().setValue("");

		// System.out.println(oulr22.getSPECIMEN().getSPM().encode());
		// return spm.encode().toString();

	}

	private void CreatePID(PatientRecord patientRecord, OML_O33 oml_o_33) throws HL7Exception {

		// Populate the PID Segment
		// PID|1||ND||Patient^Sick||19750110|M||||||||||||||||||||||N|||||
		PID pid = oml_o_33.getPATIENT().getPID();
		oml_o_33.getPATIENT().getPID().getPid1_SetIDPID().setValue("1");
		// oulr22.getPATIENT().getPID().getPid2_PatientID().getCx1_IDNumber().setValue(OrderInfo.getPatientId());
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		oml_o_33.getPATIENT().getPID().getPid3_PatientIdentifierList(0).getCx1_IDNumber()
				.setValue(patientRecord.getPatientID());
		// oulr22.getPATIENT().getPID().getPid3_PatientIdentifierList(0).getCx2_CheckDigit().setValue("");
		// oulr22.getPATIENT().getPID().getPid3_PatientIdentifierList(0).getCx3_CheckDigitScheme().setValue("");
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		oml_o_33.getPATIENT().getPID().getPid4_AlternatePatientIDPID(0).getCx1_IDNumber().setValue("");
		oml_o_33.getPATIENT().getPID().getPid4_AlternatePatientIDPID(0).getCx2_CheckDigit().setValue("");
		oml_o_33.getPATIENT().getPID().getPid4_AlternatePatientIDPID(0).getCx3_CheckDigitScheme().setValue("");
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		oml_o_33.getPATIENT().getPID().getPid5_PatientName(0).getXpn1_FamilyName().getFn1_Surname()
				.setValue(patientRecord.getSurname());
		// oulr22.getPATIENT().getPID().getPid5_PatientName(0).getXpn1_FamilyName().getFn2_OwnSurnamePrefix().setValue("");
		// oulr22.getPATIENT().getPID().getPid5_PatientName(0).getXpn1_FamilyName().getFn3_OwnSurname().setValue("");
		oml_o_33.getPATIENT().getPID().getPid5_PatientName(0).getXpn2_GivenName()
				.setValue(patientRecord.getFirstName());
		// pid.getPid5_PatientName(0).getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof().setValue("");
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// oulr22.getPATIENT().getPID().getPid6_MotherSMaidenName(0).getXpn1_FamilyName().getFn1_Surname().setValue("");
		// oulr22.getPATIENT().getPID().getPid6_MotherSMaidenName(0).getXpn2_GivenName().setValue("");
		oml_o_33.getPATIENT().getPID().getPid6_MotherSMaidenName(0)
				.getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof().setValue("");
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		

	
		oml_o_33.getPATIENT().getPID().getPid7_DateTimeOfBirth().getTs1_Time().setValue(patientRecord.getDateTimeOfBirth());
		oml_o_33.getPATIENT().getPID().getPid7_DateTimeOfBirth().getTs2_DegreeOfPrecision().setValue("");
		oml_o_33.getPATIENT().getPID().getPid8_AdministrativeSex().setValue(patientRecord.getAdministrativeSex());
		oml_o_33.getPATIENT().getPID().getPid9_PatientAlias(0).getXpn1_FamilyName().getFn1_Surname().setValue("");
		oml_o_33.getPATIENT().getPID().getPid9_PatientAlias(0).getXpn1_FamilyName().getFn2_OwnSurnamePrefix()
				.setValue("");
		// pid.getPid30_PatientDeathIndicator().setValue("N");

		// System.out.println(oulr22.getPATIENT().getPID().encode());

		// return pid.encode().toString();

	}
	
	

	private void CreateMSH(MachineResult ResultInfo, MachineOrder OrderInfo, Long messageControlID)
			throws HL7Exception {
		// MSH msh = oulr22.getMSH();

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));

		// MSH|^~\&|CentraLink|ResultExport|LIMS|ResultImport|20080417084931||OUL^R22^OUL_R22|5|P|2.5||||||8859/1
		// msh.getMsh1_FieldSeparator().setValue("^~\\&");

		// oulr22.getMSH().getMsh2_EncodingCharacters().setValue("");
		oulr22.getMSH().getMsh3_SendingApplication().getHd1_NamespaceID().setValue("ACCULAB");
		// oulr22.getMSH().getMsh4_SendingFacility().getHd1_NamespaceID().setValue("");
		// oulr22.getMSH().getMsh4_SendingFacility().getHd2_UniversalID().setValue("LIMS");
		// oulr22.getMSH().getMsh4_SendingFacility().getHd3_UniversalIDType().setValue("hl7");

		oulr22.getMSH().getMsh5_ReceivingApplication().getHd2_UniversalID().setValue("EHOPE");
		// oulr22.getMSH().getMsh6_ReceivingFacility().getHd1_NamespaceID().setValue("");
		// oulr22.getMSH().getMsh6_ReceivingFacility().getHd2_UniversalID().setValue("");
		// oulr22.getMSH().getMsh6_ReceivingFacility().getHd3_UniversalIDType().setValue("");
		oulr22.getMSH().getMsh7_DateTimeOfMessage().getTime().setValue(dateFormat.format(date));
		// oulr22.getMSH().getMsh7_DateTimeOfMessage().getTs1_Time().setValue("");
		// oulr22.getMSH().getMsh7_DateTimeOfMessage().getTs2_DegreeOfPrecision().setValue("");
		// oulr22.getMSH().getMsh8_Security().setValue("OUL^R22^OUL_R22");
		// oulr22.getMSH().getMsh9_MessageType().getMsg1_MessageCode().setValue("5");
		// oulr22.getMSH().getMsh9_MessageType().getMsg2_TriggerEvent().setValue("");
		// oulr22.getMSH().getMsh9_MessageType().getMsg3_MessageStructure().setValue("");
		oulr22.getMSH().getMsh10_MessageControlID().setValue(messageControlID.toString());
		// oulr22.getMSH().getMsh11_ProcessingID().getPt1_ProcessingID().setValue("");
		// oulr22.getMSH().getMsh11_ProcessingID().getPt2_ProcessingMode().setValue("");
		oulr22.getMSH().getMsh12_VersionID().getVersionID().setValue("2.5");
		// oulr22.getMSH().getMsh13_SequenceNumber().setValue(messageControlID.toString());
		// oulr22.getMSH().getMsh14_ContinuationPointer().setValue("");
		// oulr22.getMSH().getMsh15_AcceptAcknowledgmentType().setValue("");
		oulr22.getMSH().getMsh16_ApplicationAcknowledgmentType().setValue("AL");
		// oulr22.getMSH().getMsh17_CountryCode().setValue("");
		oulr22.getMSH().getMsh18_CharacterSet(0).setValue("UNICODE UTF-8");

		// oulr22.getMSH().getSequenceNumber().setValue("123");

		// MSH msh = oulr22.getMSH();

		System.out.println(oulr22.getMSH().encode());

		// return oulr22.getMSH().encode().toString();

	}

	private void CreateMSH(LIS2A2Record LIS2A2HeaderRecord, OML_O33 oml_o33) throws HL7Exception {
		
	//MSH|^~\&|host||cobas 4000||20241203144700||OML^O33^OML_O33|12|P|2.5.1|||NE|AL||UNICODE UTF-8|||LAB-28^IHE

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));

		
		oml_o33.getMSH().getMsh1_FieldSeparator().setValue("|");
		oml_o33.getMSH().getMsh2_EncodingCharacters().setValue("^~\\&");
		oml_o33.getMSH().getMsh3_SendingApplication().getHd1_NamespaceID().setValue("CERTCURE");
		oml_o33.getMSH().getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue("CERTA_LIS");
		oml_o33.getMSH().getMsh7_DateTimeOfMessage().getTime().setValue(dateFormat.format(date));
		oml_o33.getMSH().getMsh9_MessageType().getMsg1_MessageCode().setValue("OML");
		oml_o33.getMSH().getMsh9_MessageType().getMsg2_TriggerEvent().setValue("O33");
		oml_o33.getMSH().getMsh9_MessageType().getMsg3_MessageStructure().setValue("OML_O33");
		oml_o33.getMSH().getMsh10_MessageControlID().setValue(UniqueSequenceGenerator.generateHighResSequence());
		oml_o33.getMSH().getMsh11_ProcessingID().getPt1_ProcessingID().setValue("P");
		oml_o33.getMSH().getMsh12_VersionID().getVersionID().setValue("2.5.1");
		oml_o33.getMSH().getMsh15_AcceptAcknowledgmentType().setValue("NE");
		oml_o33.getMSH().getMsh16_ApplicationAcknowledgmentType().setValue("AL");
		oml_o33.getMSH().getMsh18_CharacterSet(0).setValue("UNICODE UTF-8");
		oml_o33.getMSH().getMsh21_MessageProfileIdentifier(0).getEi1_EntityIdentifier().setValue("LAB-28");
		oml_o33.getMSH().getMsh21_MessageProfileIdentifier(0).getEi2_NamespaceID().setValue("IHE");
		


		
		//oml_o33.getMSH().getMsh3_SendingApplication().getHd1_NamespaceID()
				//.setValue(LIS2A2HeaderRecord.getFieldValue(3));
		// oulr22.getMSH().getMsh4_SendingFacility().getHd1_NamespaceID().setValue("");
		// oulr22.getMSH().getMsh4_SendingFacility().getHd2_UniversalID().setValue("LIMS");
		// oulr22.getMSH().getMsh4_SendingFacility().getHd3_UniversalIDType().setValue("hl7");

		//oml_o33.getMSH().getMsh5_ReceivingApplication().getHd2_UniversalID()
			//	.setValue(LIS2A2HeaderRecord.getFieldValue(5));
		// oulr22.getMSH().getMsh6_ReceivingFacility().getHd1_NamespaceID().setValue("");
		// oulr22.getMSH().getMsh6_ReceivingFacility().getHd2_UniversalID().setValue("");
		// oulr22.getMSH().getMsh6_ReceivingFacility().getHd3_UniversalIDType().setValue("");
		
		// oulr22.getMSH().getMsh7_DateTimeOfMessage().getTs1_Time().setValue("");
		// oulr22.getMSH().getMsh7_DateTimeOfMessage().getTs2_DegreeOfPrecision().setValue("");
		// oulr22.getMSH().getMsh8_Security().setValue("OUL^R22^OUL_R22");
		// oulr22.getMSH().getMsh9_MessageType().getMsg1_MessageCode().setValue("5");
		// oulr22.getMSH().getMsh9_MessageType().getMsg2_TriggerEvent().setValue("");
		// oulr22.getMSH().getMsh9_MessageType().getMsg3_MessageStructure().setValue("");
		//oml_o33.getMSH().getMsh10_MessageControlID().setValue("111");
		// oulr22.getMSH().getMsh11_ProcessingID().getPt1_ProcessingID().setValue("");
		// oulr22.getMSH().getMsh11_ProcessingID().getPt2_ProcessingMode().setValue("");
		//oml_o33.getMSH().getMsh12_VersionID().getVersionID().setValue("2.5.1");
		// oulr22.getMSH().getMsh13_SequenceNumber().setValue(messageControlID.toString());
		// oulr22.getMSH().getMsh14_ContinuationPointer().setValue("");
		// oulr22.getMSH().getMsh15_AcceptAcknowledgmentType().setValue("");
		//oml_o33.getMSH().getMsh16_ApplicationAcknowledgmentType().setValue("AL");
		// oulr22.getMSH().getMsh17_CountryCode().setValue("");
		//oml_o33.getMSH().getMsh18_CharacterSet(0).setValue("UNICODE UTF-8");

		// oulr22.getMSH().getSequenceNumber().setValue("123");

		// MSH msh = oulr22.getMSH();

		//System.out.println(oulr22.getMSH().encode());

		// return oulr22.getMSH().encode().toString();

	}
	
	
	private void CreateMSHCobasPro(LIS2A2Record LIS2A2HeaderRecord, OML_O33 oml_o33) throws HL7Exception {
		
		//MSH|^~\&|host||cobas 4000||20241203144700||OML^O33^OML_O33|12|P|2.5.1|||NE|AL||UNICODE UTF-8|||LAB-28^IHE

			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			
			oml_o33.getMSH().getMsh1_FieldSeparator().setValue("|");
			oml_o33.getMSH().getMsh2_EncodingCharacters().setValue("^~\\&");
			oml_o33.getMSH().getMsh3_SendingApplication().getHd1_NamespaceID().setValue("host");
			oml_o33.getMSH().getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue("cobas pro");
			oml_o33.getMSH().getMsh7_DateTimeOfMessage().getTime().setValue(dateFormat.format(date));
			oml_o33.getMSH().getMsh9_MessageType().getMsg1_MessageCode().setValue("OML");
			oml_o33.getMSH().getMsh9_MessageType().getMsg2_TriggerEvent().setValue("O33");
			oml_o33.getMSH().getMsh9_MessageType().getMsg3_MessageStructure().setValue("OML_O33");
			oml_o33.getMSH().getMsh10_MessageControlID().setValue(UniqueSequenceGenerator.generateHighResSequence());
			oml_o33.getMSH().getMsh11_ProcessingID().getPt1_ProcessingID().setValue("P");
			oml_o33.getMSH().getMsh12_VersionID().getVersionID().setValue("2.5.1");
			oml_o33.getMSH().getMsh15_AcceptAcknowledgmentType().setValue("NE");
			oml_o33.getMSH().getMsh16_ApplicationAcknowledgmentType().setValue("AL");
			oml_o33.getMSH().getMsh18_CharacterSet(0).setValue("UNICODE UTF-8");
			oml_o33.getMSH().getMsh21_MessageProfileIdentifier(0).getEi1_EntityIdentifier().setValue("LAB-28R");
			oml_o33.getMSH().getMsh21_MessageProfileIdentifier(0).getEi2_NamespaceID().setValue("ROCHE");
		



		}

	private String getNextOutHl7Sequance() {
		outHl7MessageSeqService = new OutboundHl7MessageSequanceService();
		return outHl7MessageSeqService.getNextSequenceValue().toString();
	}

	public List<DataOrderInboundHL7Message> getOrderInboundHL7List(OML_O33 OML_O33_MSG) {

		List<DataOrderInboundHL7Message> inOrderInboundList = new ArrayList<DataOrderInboundHL7Message>();
		DataInboundHL7Message inbound = new DataInboundHL7Message();
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		inbound.setMessageBody(OML_O33_MSG.getMessage().toString());
		inbound.setSource(OML_O33_MSG.getMSH().getMsh4_SendingFacility().toString());
		inbound.setPriority(OML_O33_MSG.getSPECIMEN().getORDER().getOBSERVATION_REQUEST().getOBR().getObr5_PriorityOBR()
				.toString());
		inbound.setMessageControllerId(Long.parseLong(OML_O33_MSG.getMSH().getMessageControlID().toString()));
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		List<MachineOrder> lstMachineOrder = this.getOrderList(OML_O33_MSG);
		LkpService lkpService = (LkpService) SpringUtil.getBean("LkpService");
		LkpMessageSourceType msgType = lkpService.findOneAnyLkp(
				java.util.Arrays
						.asList(new SearchCriterion("code", MessageSourceType.HL7.getValue(), FilterOperator.eq)),
				LkpMessageSourceType.class);
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		for (int index = 0; index < lstMachineOrder.size(); index++) {
			DataOrderInboundHL7Message tempOrderInbound = new DataOrderInboundHL7Message();
			tempOrderInbound.setInboundHl7Message(inbound);
			MachineOrder mOrder = lstMachineOrder.get(index);
			mOrder.setSourceType(msgType);
			tempOrderInbound.setMachineOrder(mOrder);
			inOrderInboundList.add(tempOrderInbound);
		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		return inOrderInboundList;
	}

	public String getOML_O_33_Message(LIS2A2Msg lis2a2Msg) {

		OML_O33 oml_o_33 = new OML_O33();
		
		String strOMLMsg = "";
		try {
			CreateMSH((HeaderRecord) lis2a2Msg.getAllRecords().get(0), oml_o_33);
			//CreatePID((PatientRecord) lis2a2Msg.getAllRecords().get(1), oml_o_33);
			CreateSPM((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
			CreateSAC((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
			strOMLMsg =  CreateOrderSegments( (OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
			
			
			//CreateORC((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
			//CreateTQ1((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
			//CreateOBR((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
			//CreateTCD((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
			
			
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return strOMLMsg;

	}

	private String CreateOrderSegments(OrderRecord orderRecord, OML_O33 oml_o_33) {
		
		String strFinalOmlo33 = ""; 
		try {
			
			
		    String [] strTestCode =  orderRecord.getFieldValue(4).split("\\\\");
		    orderRecord.getComponentValue(4,2,"^");
		    ORC orcSegment = oml_o_33.getSPECIMEN().getORDER().getORC();
			OBR obrSegment = oml_o_33.getSPECIMEN().getORDER().getOBSERVATION_REQUEST().getOBR();
			TCD tcdSegment = oml_o_33.getSPECIMEN().getORDER().getOBSERVATION_REQUEST().getTCD();
			TQ1 tq1Segment = oml_o_33.getSPECIMEN().getORDER().getTIMING().getTQ1();
			
			//orcSegment.getOrc1_OrderControl().setValue("NW");
			//orcSegment.getOrc9_DateTimeOfTransaction().getTs1_Time().setValue(orderRecord.getOrderDateTime());
			
			
			/*
			 * ORC|NW||||||||20250719154331
			   TQ1|||||||||R^^HL70485
               OBR|1|9791247951||10003^^99ROC
               TCD|10003^^99ROC[CR]
			 */
			
			String strOrders = ""; 
			

			 for(int iCount = 0 ; iCount < strTestCode.length; iCount++ )
			 {
				 
				 strOrders += "ORC|"+"NW" +"||||||||"+ orderRecord.getOrderDateTime() + "\r" 
						   +  "TQ1|||||||||R^^HL70485" + "\r" 
				 		   +  "OBR||"+ orderRecord.getSpecimenId(2) +"||" + strTestCode [iCount] +"|||||||"+strTestCode [iCount] +"^^99ROC" +"\r"
						   +  "TCD|"+ strTestCode [iCount] +"^^99ROC" + "\r"  ;
				 		   
			
				 /*if(iCount ==0)
				 {
					 orcSegment.getOrc1_OrderControl().setValue("NW");
					 orcSegment.getOrc5_OrderStatus().setValue("CM");
					 obrSegment.getObr4_UniversalServiceIdentifier().getCe1_Identifier().setValue(strTestCode[iCount]);
					 obrSegment.getObr11_SpecimenActionCode().setValue("A");
					 
				 }else
				 {
					 
					ORC orcSegment3 =  oml_o_33.insertSPECIMEN(iCount + 1).insertORDER(iCount+1 ).getORC();
					 orcSegment3.getOrderControl().setValue("SC");  // Scheduled Order
				        orcSegment3.getPlacerOrderNumber().getEntityIdentifier().setValue("67890");
				        orcSegment3.getFillerOrderNumber().getEntityIdentifier().setValue("11223");
				        orcSegment3.getOrderStatus().setValue("P");   // Pending
				      
					 
				       // orcSegment3.getOrderControl().setValue("CR");  // Cancel Order
				 }*/
					
				 
			 }
			 
			 strFinalOmlo33 = oml_o_33.encode() + strOrders;
			 
			 
			
			 
			 

		} catch (Exception ex) {

		}
		
		 return strFinalOmlo33;

	}

	private void CreateTCD(OrderRecord orderRecord, OML_O33 oml_o_33) {
		
		try 
		{
			TCD tcdSegment = oml_o_33.getSPECIMEN().getORDER().getOBSERVATION_REQUEST().getTCD();
			
		}catch (Exception e) 
		{
			// TODO: handle exception
		}
		
		

	}

	private void CreateOBR(OrderRecord orderRecord, OML_O33 oml_o_33) {
		//OBR||4603833e10de8e20d9d72e3293c27b39||2951-2^NaS^LN||||||||||||01025232
		
		try {
			
			OBR obrSegment = oml_o_33.getSPECIMEN().getORDER().getOBSERVATION_REQUEST().getOBR();
			obrSegment.getObr4_UniversalServiceIdentifier().getCe1_Identifier().setValue(orderRecord.getComponentValue(4, 3));
			//obrSegment.getObr11_SpecimenActionCode().setValue("G");
			
		//	obrSegment.getObr4_UniversalServiceIdentifier().getCe1_Identifier().setValue(orderRecord.get);
			
		}catch(Exception ex)
		{
			
		}

	}

	private void CreateTQ1(OrderRecord orderRecord, OML_O33 oml_o_33) {
		// TQ1|||||||||R^^HL70485

		// Now you can access the SAC (Specimen Container) segment from the SPECIMEN
		// group
		TQ1 tq1Segment = oml_o_33.getSPECIMEN().getORDER().getTIMING().getTQ1();

		String resultDateTime = "";

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String strDate = formatter.format(date);

		// Set values for the SAC segment (Container identifier, specimen type, etc.)
		try {

			tq1Segment.getTq19_Priority(0).getCwe1_Identifier().setValue("R");
			//tq1Segment.getTq19_Priority(0).getCwe3_NameOfCodingSystem().setValue(strDate.toString());

		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void CreateORC(OrderRecord orderRecord, OML_O33 oml_o_33) {
		// ORC|NW||||||||20241111113208

		// Now you can access the SAC (Specimen Container) segment from the SPECIMEN
		// group
		ORC orcSegment = oml_o_33.getSPECIMEN().getORDER().getORC();

		String resultDateTime;

		SimpleDateFormat resultTimeFormat = new SimpleDateFormat("yyyyMMdd");

		// Set values for the SAC segment (Container identifier, specimen type, etc.)
		try {

			orcSegment.getOrc1_OrderControl().setValue("NW");
			orcSegment.getOrc9_DateTimeOfTransaction().getTime().setValue(orderRecord.getFieldValue(22));

		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void CreateSAC(OrderRecord orderRecord, OML_O33 oml_O_33) {
		// SAC|||9790117937^BARCODE|||||||50261|3
		// Access the SPECIMEN group in the OML_O33 message

		// Now you can access the SAC (Specimen Container) segment from the SPECIMEN
		// group
		SAC sacSegment = oml_O_33.getSPECIMEN().getSAC();

		// Set values for the SAC segment (Container identifier, specimen type, etc.)
		try {

			sacSegment.getSac3_ContainerIdentifier().getEi1_EntityIdentifier()
					.setValue(orderRecord.getSpecimenId(2));
			//sacSegment.getSac1_ExternalAccessionIdentifier().getEi2_NamespaceID().setValue("BARCODE");
			//sacSegment.getSac10_CarrierIdentifier().getEi1_EntityIdentifier()
				//	.setValue(orderRecord.getSpecimnPositionInfo());
			//sacSegment.getSac11_PositionInCarrier().getValue1().setValue("");

		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getOrderHL7Message(LIS2A2Msg lis2a2Msg) {

		OML_O33 oml_o_33 = new OML_O33();
		RSP_K11 rsp_k11 = new RSP_K11();
		
		String strOMLMsg = "";
		String strRSPMsg = "";
		String strFinalOrderMessage = "";
		try {
			
			OrderRecord order = (OrderRecord) lis2a2Msg.getAllRecords().get(2);
			
			String [] strTestCode =  order.getFieldValue(4).split("\\\\");
		  //  orderRecord.getComponentValue(4,2,"^");
			
			if(strTestCode.length > 0 && strTestCode[0] != "") 
			{
			
				
				CreateMSHCobasPro((HeaderRecord) lis2a2Msg.getAllRecords().get(0), oml_o_33);
				CreatePIDCobasPro((PatientRecord) lis2a2Msg.getAllRecords().get(1), oml_o_33);
				CreateSPMCobasPro((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
				CreateSACCobasPro((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
				/*
				 *  ORC|NW||||||||20250719154331
					TQ1|||||||||R^^HL70485
					OBR|1|9791247951||10003^^99ROC
					TCD|10003^^99ROC[CR]
				 */
				//CreateORCCobasPro((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
				//CreateTQ1CobasPro((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
				//CreateTQ1CobasPro((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
				
				
				strOMLMsg =  CreateOrderSegments( (OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
				
				strFinalOrderMessage =  strOMLMsg;
				
				//CreateORC((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
				//CreateTQ1((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
				//CreateOBR((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
				//CreateTCD((OrderRecord) lis2a2Msg.getAllRecords().get(2), oml_o_33);
				
			}else
			{
				
				 //String strRSP_K11 = 
				 CreateMSH((HeaderRecord) lis2a2Msg.getAllRecords().get(0), rsp_k11);
				 CreateMSA((OrderRecord) lis2a2Msg.getAllRecords().get(2), rsp_k11);
			     CreateQAK((OrderRecord) lis2a2Msg.getAllRecords().get(2), rsp_k11);
				
			     strRSPMsg = rsp_k11.encode() +     
			     CreateQPD((OrderRecord) lis2a2Msg.getAllRecords().get(2), rsp_k11);
				
				
				strRSPMsg =  CreateEmptyOrderSegments( (OrderRecord) lis2a2Msg.getAllRecords().get(2), strRSPMsg);
				
				strFinalOrderMessage =  strRSPMsg;
			}
			
			
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return strFinalOrderMessage;

	}

	private void CreateSACCobasPro(OrderRecord orderRecord, OML_O33 oml_o_33) {
		SAC sacSegment = oml_o_33.getSPECIMEN().getSAC();

		// Set values for the SAC segment (Container identifier, specimen type, etc.)
		try {

			oml_o_33.getSPECIMEN().getSAC().getSac2_AccessionIdentifier().getEi1_EntityIdentifier()
			.setValue(orderRecord.getSpecimenId(2)); 
			oml_o_33.getSPECIMEN().getSAC().getSac2_AccessionIdentifier().getEi2_NamespaceID().setValue("BARCODE"); 
			
			oml_o_33.getSPECIMEN().getSAC().getSac10_CarrierIdentifier().getEi1_EntityIdentifier().setValue(orderRecord.getSpecimenDetails(3, 4));
			oml_o_33.getSPECIMEN().getSAC().getSac11_PositionInCarrier().getNa1_Value1().setValue(orderRecord.getSpecimenDetails(4, 4));
	
			//sacSegment.getSac1_ExternalAccessionIdentifier().getEi2_NamespaceID().setValue("BARCODE");
			//sacSegment.getSac10_CarrierIdentifier().getEi1_EntityIdentifier()
				//	.setValue(orderRecord.getSpecimnPositionInfo());
			//sacSegment.getSac11_PositionInCarrier().getValue1().setValue("");
	}
		catch(Exception ex)
		{
			
		}
	}

	private void CreateSPMCobasPro(OrderRecord orderRecord, OML_O33 oml_O_33) throws DataTypeException {
		// Populate the PID Segment
				// SPM|1|001150587013&BARCODE||WB^^HL70487|||||||P^^HL70369|||Kholoud Alrihani|||||||||||||SC^^99ROC 
				// SPM spm = oulr22.getSPECIMEN().getSPM();

				oml_O_33.getSPECIMEN().getSPM().getSpm1_SetIDSPM().setValue("1");
				oml_O_33.getSPECIMEN().getSPM().getSpm2_SpecimenID().getPlacerAssignedIdentifier().getEi1_EntityIdentifier()
						.setValue(orderRecord.getSpecimenId(2)); 
				oml_O_33.getSPECIMEN().getSPM().getSpm2_SpecimenID().getPlacerAssignedIdentifier().getEi2_NamespaceID()
				.setValue("BARCODE"); 
				
			
				// spm
				// .getSpm2_SpecimenID().getEip1_PlacerAssignedIdentifier().getEi1_EntityIdentifier()
				// .setValue(outboundInfo.getDataOutboundHl7Message().getMessageControllerId().toString());
				//oml_O_33.getSPECIMEN().getSPM().getSpm4_SpecimenType().getCwe1_Identifier().setValue(orderRecord.getSpecimenDescriptor());
				
				oml_O_33.getSPECIMEN().getSPM().getSpm4_SpecimenType().getCwe1_Identifier().setValue("SERPLAS");
				oml_O_33.getSPECIMEN().getSPM().getSpm4_SpecimenType().getCwe2_Text().setValue("");
				oml_O_33.getSPECIMEN().getSPM().getSpm4_SpecimenType().getCwe3_NameOfCodingSystem().setValue("99ROC");
				
				oml_O_33.getSPECIMEN().getSPM().getSpm4_SpecimenType().getCwe2_Text().setValue("");
				oml_O_33.getSPECIMEN().getSPM().getSpm4_SpecimenType().getCwe3_NameOfCodingSystem().setValue("HL70487");

				oml_O_33.getSPECIMEN().getSPM().getSpm5_SpecimenTypeModifierReps();
				oml_O_33.getSPECIMEN().getSPM().getSpm6_SpecimenAdditives();
				oml_O_33.getSPECIMEN().getSPM().getSpm7_SpecimenCollectionMethod();
				oml_O_33.getSPECIMEN().getSPM().getSpm8_SpecimenSourceSite();
				oml_O_33.getSPECIMEN().getSPM().getSpm9_SpecimenSourceSiteModifier();
				oml_O_33.getSPECIMEN().getSPM().getSpm10_SpecimenCollectionSite();
				oml_O_33.getSPECIMEN().getSPM().getSpm11_SpecimenRole(0).getCwe1_Identifier().setValue("P");
				oml_O_33.getSPECIMEN().getSPM().getSpm11_SpecimenRole(0).getCwe2_Text().setValue("");
				oml_O_33.getSPECIMEN().getSPM().getSpm11_SpecimenRole(0).getCwe3_NameOfCodingSystem().setValue("HL70369");
				oml_O_33.getSPECIMEN().getSPM().getSpm27_ContainerType().getCwe1_Identifier().setValue("SC");
				oml_O_33.getSPECIMEN().getSPM().getSpm27_ContainerType().getCwe3_NameOfCodingSystem().setValue("99ROC");
				
				//oml_O_33.getSPECIMEN().getSPM().getSpm12_SpecimenCollectionAmount();
				//oml_O_33.getSPECIMEN().getSPM().getSpm13_GroupedSpecimenCount().setValue("");
				//oml_O_33.getSPECIMEN().getSPM().getSpm14_SpecimenDescription();

				// spm.getSetIDSPM().setValue("1");
				//oml_O_33.getSPECIMEN().getSPM().getSpecimenCollectionMethod().getCwe1_Identifier().setValue("");
				//oml_O_33.getSPECIMEN().getSPM().getSpecimenType().getCwe1_Identifier().setValue("");
				//oml_O_33.getSPECIMEN().getSPM().getContainerCondition().getCodingSystemVersionID().setValue("");
				//oml_O_33.getSPECIMEN().getSPM().getSpecimenAvailability().setValue("");

				// System.out.println(oulr22.getSPECIMEN().getSPM().encode());
				// return spm.encode().toString();
		
	}

	private void CreatePIDCobasPro(PatientRecord patientRecord, OML_O33 oml_o_33) throws DataTypeException {
		//PID|||20244749825||^^^^^^U||19861117|F
		//PID|||||^^^^^^U|||U
		
		// Get the PID segment (assuming first occurrence)
		PID pid = oml_o_33.getPATIENT().getPID();
		// Set PID fields
		// Set PID-3 (Patient Identifier List)
		CX patientId = pid.getPatientIdentifierList(0); // First identifier
		patientId.getIDNumber().setValue(patientRecord.getPatientId(0));      // The actual ID
		patientId.getAssigningAuthority().getNamespaceID().setValue(""); 
		// Assigning authority
		pid.getPatientName(0).getGivenName().setValue("");
		pid.getPatientName(0).getFamilyName().getSurname().setValue("");
		// Get the first patient name entry
		XPN patientName = pid.getPatientName(0);
		// Set XPN-7: Name Type Code
		patientName.getNameTypeCode().setValue("U");  // "L" for Legal name
		pid.getDateTimeOfBirth().getTime().setValue(patientRecord.getCobasProDateTimeOfBirth());
		pid.getAdministrativeSex().setValue(patientRecord.getGender());
		
	}

	private String CreateEmptyOrderSegments(OrderRecord orderRecord, String strRSP_K11) {
	
		try {

			return strRSP_K11;
			
		} catch (Exception ex)

		{

		}
		return "";
	}

	private String CreateQPD(OrderRecord orderRecord, RSP_K11 rsp_k11) {
		//QPD|RRRBAR^^99ROC|110|ID123456|50002|1|||||SERPLAS^^99ROC|SC^^99ROC|R
		 PipeParser parser = new PipeParser();
		 String qpdSegment = "" ;
		try {
			
			qpdSegment = "QPD|RRRBAR^^99ROC"
			+ "|" + orderRecord.getSpecimenDetails(1,4)
			
			+ "|" + orderRecord.getSpecimenId(3, 1, "^")
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

	private void CreateQAK(OrderRecord orderRecord, RSP_K11 rsp_k11) throws HL7Exception {
		//"QAK|110|OK|RRRBAR^^99ROC"
		try {
			
			////QAK|110|OK|RRRBAR^^99ROC
			rsp_k11.getQAK().getQak1_QueryTag().setValue(orderRecord.getSpecimenDetails(1,4));
			rsp_k11.getQAK().getQak2_QueryResponseStatus().setValue("OK");
			rsp_k11.getQAK().getQak3_MessageQueryName().getCe1_Identifier().setValue("RRRBAR");
			rsp_k11.getQAK().getQak3_MessageQueryName().getCe3_NameOfCodingSystem().setValue("99ROC");
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
	}

	private void CreateMSA(OrderRecord orderRecord, RSP_K11 rsp_k11) throws HL7Exception {
		try {
			
			rsp_k11.getMSA().getAcknowledgmentCode().setValue("AA");
			rsp_k11.getMSA().getMsa2_MessageControlID().setValue(orderRecord.getSpecimenDetails(1, 4));
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		
	}

	private void CreateMSH(HeaderRecord lIS2A2HeaderRecord, RSP_K11 rsp_k11) throws HL7Exception {
		try
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			

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

}
