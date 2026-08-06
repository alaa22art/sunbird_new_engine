package com.sunbird.lis.interfaces.middleware.parser.hl7;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;
import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.DataInboundHL7Message;
import com.sunbird.lis.interfaces.entities.DataOrderInboundHL7Message;
import com.sunbird.lis.interfaces.entities.LkpMessageSourceType;
import com.sunbird.lis.interfaces.entities.MachineOrder;
import com.sunbird.lis.interfaces.entities.MachineResult;
import com.sunbird.lis.interfaces.service.LkpService;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.group.OML_O33_ORDER;
import ca.uhn.hl7v2.model.v25.message.ACK;
import ca.uhn.hl7v2.model.v25.message.OML_O33;
import ca.uhn.hl7v2.model.v25.message.OUL_R22;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.SPM;



import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;

public class HL7Parser extends PipeParser {

	public enum MessageSourceType {
		
		
		
		
		LIS("LIS"),
        HL7("New Message"),
        UNKNOWN("UNKNOWN"),
        ADT_A01("Patient Admission"),
        ADT_A02("Transfer"),
        ADT_A03("Discharge"),
        ADT_A04("Create Visit"),
        ADT_A05("Patient Preadmission"),
        ADT_A08("Update Admission"),
        ADT_A11("Cancel Admission"),
        ADT_A13(""),
        ADT_A28("Create Patient"),
        ADT_A31("Update Patient"),
        SIU_S12("Create Appointment"),
        SIU_S13("Update Appointment"),
        SIU_S15("Cancel Appointment"),
        DFT_P03("Create Order"),
        JSON_QMS("JSON_QMS"),
        DFT_P03_CLERANCE("Financial Clearnce"),
        DFT_P03_COMPLETE("Complete Order"),
        DFT_P03_CANCEL("Cancel Order"),   
        DFT_P03_CREATE("Create Order"),   
        DFT_P03_DUPICATED_RAD_ORDER_CORRECT("Dupicated Rad Order Correct"),   
        ACTIVATE_APPOINTMRNT("ACTIVATE_APPOINTMRNT"),
        ASTM("ASTM"),
        ACK("ACK");
		
		
		

	/*	LIS("LIS"),
		HL7("HL7"),
		UNKNOWN("UNKNOWN"),
		ADT_A01("ADT_A01"),
		ADT_A02("ADT_A02"),
		ADT_A03("ADT_A03"),
		ADT_A04("ADT_A04"),
		ADT_A05("ADT_A05"),
		ADT_A08("ADT_A08"),
		ADT_A11("ADT_A11"),
		ADT_A28("ADT_A28"),
		ADT_A13("ADT_A13"),
		ADT_A31("ADT_A31"),
		SIU_S12("SIU_S12"),
		SIU_S13("SIU_S13"),
		SIU_S15("SIU_S15"),
		DFT_P03("DFT_P03"),
		
		JSON_QMS("JSON_QMS"),
		ASTM("ASTM"),
		ACK("ACK");*/
	    

		private String value;

		private MessageSourceType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum MessageDirection {
		IN("IN"),
		OUT("OUT");

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
		CONNECTION_CLOSE("CONNECTION_CLOSE");

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
		First,
		Second,
		Last;

		public static int valueOf(Class<hl7PatientNameIndex> class1, hl7PatientNameIndex index) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	public enum hl7TestActionCode {

		Add("A"),
		Rerun("R"),
		Cancel("C");

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

		SerumOrPlasma(1),
		Urine(2),
		CSF(3),
		Supernatant(4),
		Other(5),
		WholeBlood(6),
		Saliva(7);

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

	private Message hl7Message;
	private List<MachineOrder> lstOrder;
	private CanonicalModelClassFactory mcf;
	private HapiContext context;
	private Message message;
	private OML_O33 orderOMLO33Message;
	private OUL_R22 oulr22;

	public OML_O33 getOrderOMLO33Message() {
		return orderOMLO33Message;
	}

	public void setOrderOMLO33Message(OML_O33 orderOMLO33Message) {
		this.orderOMLO33Message = orderOMLO33Message;
	}

	public HL7Parser(String strVersion) {

	}

	public HL7Parser(String strHL7, String strVersion) {
		try {

			message = this.parse(strHL7);

			if (message instanceof OML_O33) {
				setValidationContext(null);
				OML_O33 orderMessage = (OML_O33) message;
				setOrderOMLO33Message(orderMessage);
			} else if (message instanceof ACK) {

				ACK ackMessage = (ACK) message;

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
				//-------------------------------------------------------------------------------//
				for (int index2 = 0; index2 < lstObserverOrder.get(index).lstRequest.size(); index2++) {
					tempOrder = new MachineOrder();

					tempOrder.setPatientId(this.getHL7PatientID(newOMLOrder));
					tempOrder.setPatientFirstName(this.getHL7PatientName(newOMLOrder, 0));
					tempOrder.setPatientLastName(this.getHL7PatientName(newOMLOrder, 1));
					tempOrder.setGender(this.getHL7PatientGender(newOMLOrder));
					tempOrder.setOrderId(lstObserverOrder.get(index).lstRequest.get(index2).OrderId);

					tempOrder.setDateOfBirth(this.getHL7DateOfBirth(newOMLOrder));
					//--------------------------------------------------------------------------------//
					tempOrder.setBarcode(lstObserverOrder.get(index).lstRequest.get(index2).SampleNo);
					//tempOrder.setSpacimenType(this.getHL7SpacimenType(newOMLOrder));
					tempOrder.setSpecimenDescriptor(this.getHL7SpacimenType(newOMLOrder).toString());
					Date specimenCollectionDateAndTime = new SimpleDateFormat("yyyyMMddmmss").parse(
							this.getHL7specimenCollectionDateAndTime(newOMLOrder));
					tempOrder.setSpecimenCollectionDateAndTime(specimenCollectionDateAndTime);
					//--------------------------------------------------------------------------------//
					tempOrder.setTestCode(lstObserverOrder.get(index).lstRequest.get(index2).TestCode);
					//tempOrder.setActionCode(lstObserverOrder.get(index).lstRequest.get(index2).ActionCode);
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
		//List<ObserverRequest> lstRequest = new ArrayList<ObserverRequest>();

		ObservationOrder order = null;
		ObserverRequest request = null;
		String strBarcode = null;
		Long OrderId = null;
		//OML_O33_ORDER OMLOrder = new OML_O33_ORDER(newOMLOrder, null);

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
					 * if (strBarcode != null) {
					 * //order.strSampleNo = strBarcode;
					 * //order.orderId = OrderId.toString();
					 * }
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
	 * List<OML_O33_ORDER> obrSegmentMessageList = null;
	 * String[] arrSegmentMessage = null;
	 * try {
	 * obrSegmentMessageList = newOMLOrder.getSPECIMEN().getORDERAll();
	 * arrSegmentMessage = new String[obrSegmentMessageList.size() + 1];
	 * } catch (HL7Exception e) {
	 * // TODO Auto-generated catch block
	 * e.printStackTrace();
	 * }
	 * 
	 * for (int index = 0; index < arrSegmentMessage.length; index++) {
	 * try {
	 * arrSegmentMessage[index] = obrSegmentMessageList.get(index).getOBSERVATION_REQUEST().getOBR()
	 * .getObr4_UniversalServiceIdentifier().encode().toString();
	 * } catch (HL7Exception e) {
	 * // TODO Auto-generated catch block
	 * e.printStackTrace();
	 * }
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
			strSpecimenCollectionDateAndTime = spmSegmentMessageData.getSpecimenCollectionDateTime().encode().toString();
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

	public String CreateHL7ResultMessage(MachineResult ResultInfo, MachineOrder OrderInfo, long messageControlID) throws HL7Exception {

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
		 * MSH|^~\&|CentraLink|ResultExport|LIMS|ResultImport|20080417084931||OUL^R22^OUL_R22|5|P|2.5||||||8859/1
		 * PID|1|||||||U||||||||||||||||||||||N
		 * PV1|1|N
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
		CreatePID(ResultInfo, OrderInfo);
		CreateSPM(ResultInfo, OrderInfo);
		CreateOBR(ResultInfo, OrderInfo);
		CreateORC(ResultInfo, OrderInfo);
		CreateOBX(ResultInfo, OrderInfo);

		return oulr22.encode();

	}

	private void CreateOBX(MachineResult resultInfo, MachineOrder orderInfo) throws HL7Exception {
		//OBX|1|NM|RBC||4.88||||||F|||19981023093347||||Advia120_06
		//OBX obx = oulr22.getSPECIMEN().getORDER().getRESULT().getOBX();

		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx1_SetIDOBX().setValue("1");
		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx2_ValueType().setValue(resultInfo.getSampleNo());
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx3_ObservationIdentifier().getCe1_Identifier().setValue("");
		oulr22	.getSPECIMEN().getORDER().getRESULT().getOBX().getObx3_ObservationIdentifier().getCe1_Identifier()
				.setValue(resultInfo.getTestCode());
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx4_ObservationSubID().setValue("");
		/*
		 * oulr22 .getSPECIMEN().getORDER().getRESULT().getOBX().getObx15_ProducerSID().getCe1_Identifier()
		 * .setValue(resultInfo.getDataOrMeasurementValue().toString());
		 */

		//Type data = resultInfo.getDataOrMeasurementValue();

		oulr22	.getSPECIMEN().getORDER().getRESULT().getOBX().getObx5_ObservationValue(0)
				.parse(resultInfo.getDataOrMeasurementValue().toString());

		//.setData(resultInfo.getDataOrMeasurementValue().toString());

		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx6_Units().getCe1_Identifier().setValue("");
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx6_Units().getCe2_Text().setValue(resultInfo.getUnits());
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx6_Units().getCe3_NameOfCodingSystem().setValue("");
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx7_ReferencesRange().setValue("");
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx8_AbnormalFlags(0).setValue("");
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx9_Probability().setValue("");
		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx10_NatureOfAbnormalTest(0).setValue(resultInfo.getAbnormalFlag());
		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx11_ObservationResultStatus().setValue(resultInfo.getResultStatus());
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx12_EffectiveDateOfReferenceRange().getTs1_Time().setValue("");
		oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx13_UserDefinedAccessChecks();

		String resultDateTime;

		SimpleDateFormat resultTimeFormat = new SimpleDateFormat("yyyyMMdd");
		//Date dateFromUser = null;

		resultDateTime = resultTimeFormat.format(resultInfo.getCreationDate());
		oulr22	.getSPECIMEN().getORDER().getRESULT().getOBX().getObx14_DateTimeOfTheObservation().getTs1_Time()
				.setValue(resultDateTime);
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx15_ProducerSID().getCe1_Identifier().setValue("");
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx15_ProducerSID().getCe2_Text().setValue("");
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx15_ProducerSID().getCe3_NameOfCodingSystem().setValue("");
		//obx.getObx16_ResponsibleObserver(0).getEffectiveDate().getTime().parse("");
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx17_ObservationMethod(0).getCe1_Identifier().setValue("");
		//oulr22.getSPECIMEN().getORDER().getRESULT().getOBX().getObx18_EquipmentInstanceIdentifier(0).getEi1_EntityIdentifier().setValue("");
		//obx.getObx19_DateTimeOfTheAnalysis().getTs1_Time().setValue("");

		//return obx.encode().toString();

	}

	private void CreateORC(MachineResult resultInfo, MachineOrder OrderInfo) throws HL7Exception {
		//ORC orc = oulr22.getSPECIMEN().getORDER().getORC();

		// * ORC|NW|411|||NW|||||||1^Halabi^Amr^Nizar_Rashed^^||
		oulr22.getSPECIMEN().getORDER().getORC().getOrc1_OrderControl().setValue("NW");
		oulr22	.getSPECIMEN().getORDER().getORC().getOrc2_PlacerOrderNumber().getEi1_EntityIdentifier()
				.setValue(OrderInfo.getOrderId().toString());
		//oulr22.getSPECIMEN().getORDER().getORC().getOrc2_PlacerOrderNumber().getEi2_NamespaceID().setValue("");
		//oulr22.getSPECIMEN().getORDER().getORC().getOrc2_PlacerOrderNumber().getEi3_UniversalID().setValue("");
		//oulr22.getSPECIMEN().getORDER().getORC().getOrc3_FillerOrderNumber().getEi1_EntityIdentifier().setValue(OrderInfo.getBarcode());
		//oulr22.getSPECIMEN().getORDER().getORC().getOrc3_FillerOrderNumber().getEi2_NamespaceID().setValue("");
		//oulr22.getSPECIMEN().getORDER().getORC().getOrc3_FillerOrderNumber().getEi3_UniversalID().setValue("");
		//oulr22.getSPECIMEN().getORDER().getORC().getOrc3_FillerOrderNumber().getEi4_UniversalIDType().setValue("");
		//oulr22.getSPECIMEN().getORDER().getORC().getOrc4_PlacerGroupNumber().getEi1_EntityIdentifier().setValue("");
		//oulr22.getSPECIMEN().getORDER().getORC().getOrc4_PlacerGroupNumber().getEi2_NamespaceID().setValue("");
		//oulr22.getSPECIMEN().getORDER().getORC().getOrc4_PlacerGroupNumber().getEi3_UniversalID().setValue("");
		//oulr22.getSPECIMEN().getORDER().getORC().getOrc5_OrderStatus().setValue("");
		//oulr22.getSPECIMEN().getORDER().getORC().getOrc6_ResponseFlag().setValue("");
		oulr22.getSPECIMEN().getORDER().getORC().getOrc7_QuantityTiming(0).getTq6_Priority().setValue("R");
		oulr22	.getSPECIMEN().getORDER().getORC().getOrc8_ParentOrder().getEip1_PlacerAssignedIdentifier().getEi1_EntityIdentifier()
				.setValue("");

		//return orc.encode().toString();

	}

	private void CreateOBR(MachineResult resultInfo, MachineOrder OrderInfo) throws HL7Exception {
		//* OBR|1|2091|2091|RBC||||||||||||||||||20080417084931|||||^^^^^R
		//OBR obr = oulr22.getSPECIMEN().getORDER().getOBR();

		oulr22.getSPECIMEN().getORDER().getOBR().getObr1_SetIDOBR().setValue("1");
		oulr22	.getSPECIMEN().getORDER().getOBR().getObr2_PlacerOrderNumber().getEi1_EntityIdentifier()
				.setValue(OrderInfo.getOrderId().toString());
		oulr22.getSPECIMEN().getORDER().getOBR().getObr2_PlacerOrderNumber().getEi2_NamespaceID().setValue("");
		oulr22.getSPECIMEN().getORDER().getOBR().getObr2_PlacerOrderNumber().getEi3_UniversalID().setValue("");
		oulr22.getSPECIMEN().getORDER().getOBR().getObr3_FillerOrderNumber().getEi1_EntityIdentifier().setValue("");
		oulr22	.getSPECIMEN().getORDER().getOBR().getObr4_UniversalServiceIdentifier().getCe1_Identifier()
				.setValue(resultInfo.getTestCode());
		//oulr22.getSPECIMEN().getORDER().getOBR().getObr4_UniversalServiceIdentifier().getCe2_Text().setValue(OrderInfo.getTestCode());
		//oulr22.getSPECIMEN().getORDER().getOBR().getObr4_UniversalServiceIdentifier().getCe3_NameOfCodingSystem().setValue("");
		oulr22.getSPECIMEN().getORDER().getOBR().getObr5_PriorityOBR().setValue(OrderInfo.getPriority());
		oulr22.getSPECIMEN().getORDER().getOBR().getObr6_RequestedDateTime().getDegreeOfPrecision().setValue("");

		String resultsRptStatusChngDateTime;

		SimpleDateFormat resultTimeFormat = new SimpleDateFormat("yyyyMMdd");
		//Date dateFromUser = null;

		resultsRptStatusChngDateTime = resultTimeFormat.format(resultInfo.getCreationDate());
		oulr22.getSPECIMEN().getORDER().getOBR().getObr22_ResultsRptStatusChngDateTime().getTime().setValue(resultsRptStatusChngDateTime);
		oulr22.getSPECIMEN().getORDER().getOBR().getObr27_QuantityTiming(0).getTq6_Priority().setValue(OrderInfo.getPriority());

		//System.out.println(oulr22.getSPECIMEN().getORDER().getOBR());
		//return obr.encode().toString();

	}

	private void CreateSPM(MachineResult resultInfo, MachineOrder OrderInfo) throws HL7Exception {
		// Populate the PID Segment
		//SPM|1|2091||QC3
		//SPM spm = oulr22.getSPECIMEN().getSPM();

		oulr22.getSPECIMEN().getSPM().getSpm1_SetIDSPM().setValue("1");
		oulr22	.getSPECIMEN().getSPM().getSpm2_SpecimenID().getPlacerAssignedIdentifier().getEi1_EntityIdentifier()
				.setValue(OrderInfo.getBarcode());
		//spm	.getSpm2_SpecimenID().getEip1_PlacerAssignedIdentifier().getEi1_EntityIdentifier()
		//.setValue(outboundInfo.getDataOutboundHl7Message().getMessageControllerId().toString());
		oulr22.getSPECIMEN().getSPM().getSpm4_SpecimenType().getAlternateIdentifier().setValue("");

		oulr22.getSPECIMEN().getSPM().getSpm5_SpecimenTypeModifierReps();
		oulr22.getSPECIMEN().getSPM().getSpm6_SpecimenAdditives();
		oulr22.getSPECIMEN().getSPM().getSpm7_SpecimenCollectionMethod();
		oulr22.getSPECIMEN().getSPM().getSpm8_SpecimenSourceSite();
		oulr22.getSPECIMEN().getSPM().getSpm9_SpecimenSourceSiteModifier();
		oulr22.getSPECIMEN().getSPM().getSpm10_SpecimenCollectionSite();
		oulr22.getSPECIMEN().getSPM().getSpm11_SpecimenRole();
		oulr22.getSPECIMEN().getSPM().getSpm12_SpecimenCollectionAmount();
		oulr22.getSPECIMEN().getSPM().getSpm13_GroupedSpecimenCount().setValue("");
		oulr22.getSPECIMEN().getSPM().getSpm14_SpecimenDescription();

		//spm.getSetIDSPM().setValue("1");
		oulr22.getSPECIMEN().getSPM().getSpecimenCollectionMethod().getCwe1_Identifier().setValue("");
		oulr22.getSPECIMEN().getSPM().getSpecimenType().getCwe1_Identifier().setValue("");
		oulr22.getSPECIMEN().getSPM().getContainerCondition().getCodingSystemVersionID().setValue("");
		oulr22.getSPECIMEN().getSPM().getSpecimenAvailability().setValue("");

		//System.out.println(oulr22.getSPECIMEN().getSPM().encode());
		//return spm.encode().toString();

	}

	private void CreatePID(MachineResult ResultInfo, MachineOrder OrderInfo) throws HL7Exception {

		// Populate the PID Segment
		//PID|1||ND||Patient^Sick||19750110|M||||||||||||||||||||||N|||||
		PID pid = oulr22.getPATIENT().getPID();
		oulr22.getPATIENT().getPID().getPid1_SetIDPID().setValue("1");
		//oulr22.getPATIENT().getPID().getPid2_PatientID().getCx1_IDNumber().setValue(OrderInfo.getPatientId());
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		oulr22.getPATIENT().getPID().getPid3_PatientIdentifierList(0).getCx1_IDNumber().setValue(OrderInfo.getPatientId());
		//oulr22.getPATIENT().getPID().getPid3_PatientIdentifierList(0).getCx2_CheckDigit().setValue("");
		//oulr22.getPATIENT().getPID().getPid3_PatientIdentifierList(0).getCx3_CheckDigitScheme().setValue("");
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		oulr22.getPATIENT().getPID().getPid4_AlternatePatientIDPID(0).getCx1_IDNumber().setValue("");
		oulr22.getPATIENT().getPID().getPid4_AlternatePatientIDPID(0).getCx2_CheckDigit().setValue("");
		oulr22.getPATIENT().getPID().getPid4_AlternatePatientIDPID(0).getCx3_CheckDigitScheme().setValue("");
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		oulr22	.getPATIENT().getPID().getPid5_PatientName(0).getXpn1_FamilyName().getFn1_Surname()
				.setValue(OrderInfo.getPatientLastName());
		//oulr22.getPATIENT().getPID().getPid5_PatientName(0).getXpn1_FamilyName().getFn2_OwnSurnamePrefix().setValue("");
		//oulr22.getPATIENT().getPID().getPid5_PatientName(0).getXpn1_FamilyName().getFn3_OwnSurname().setValue("");
		oulr22.getPATIENT().getPID().getPid5_PatientName(0).getXpn2_GivenName().setValue(OrderInfo.getPatientFirstName());
		pid.getPid5_PatientName(0).getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof().setValue("");
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//oulr22.getPATIENT().getPID().getPid6_MotherSMaidenName(0).getXpn1_FamilyName().getFn1_Surname().setValue("");
		//oulr22.getPATIENT().getPID().getPid6_MotherSMaidenName(0).getXpn2_GivenName().setValue("");
		oulr22.getPATIENT().getPID().getPid6_MotherSMaidenName(0).getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof().setValue("");
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		String dateBirthValue;

		SimpleDateFormat birthDateTimeFormat = new SimpleDateFormat("yyyyMMdd");
		//Date dateFromUser = null;

		dateBirthValue = birthDateTimeFormat.format(OrderInfo.getDateOfBirth());
		//dateBirthValue = DateUtil.customFormatDate(OrderInfo.getDateOfBirth(), "yyyyMMdd");
		oulr22.getPATIENT().getPID().getPid7_DateTimeOfBirth().getTs1_Time().setValue(dateBirthValue.toString());
		oulr22.getPATIENT().getPID().getPid7_DateTimeOfBirth().getTs2_DegreeOfPrecision().setValue("");
		oulr22.getPATIENT().getPID().getPid8_AdministrativeSex().setValue(OrderInfo.getGender());
		oulr22.getPATIENT().getPID().getPid9_PatientAlias(0).getXpn1_FamilyName().getFn1_Surname().setValue("");
		oulr22.getPATIENT().getPID().getPid9_PatientAlias(0).getXpn1_FamilyName().getFn2_OwnSurnamePrefix().setValue("");
		//pid.getPid30_PatientDeathIndicator().setValue("N");

		//System.out.println(oulr22.getPATIENT().getPID().encode());

		//return pid.encode().toString();

	}

	private void CreateMSH(MachineResult ResultInfo, MachineOrder OrderInfo, Long messageControlID) throws HL7Exception {
		//MSH msh = oulr22.getMSH();

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));

		//MSH|^~\&|CentraLink|ResultExport|LIMS|ResultImport|20080417084931||OUL^R22^OUL_R22|5|P|2.5||||||8859/1
		//msh.getMsh1_FieldSeparator().setValue("^~\\&");

		//oulr22.getMSH().getMsh2_EncodingCharacters().setValue("");
		oulr22.getMSH().getMsh3_SendingApplication().getHd1_NamespaceID().setValue("ACCULAB");
		//oulr22.getMSH().getMsh4_SendingFacility().getHd1_NamespaceID().setValue("");
		//oulr22.getMSH().getMsh4_SendingFacility().getHd2_UniversalID().setValue("LIMS");
		//oulr22.getMSH().getMsh4_SendingFacility().getHd3_UniversalIDType().setValue("hl7");

		oulr22.getMSH().getMsh5_ReceivingApplication().getHd2_UniversalID().setValue("EHOPE");
		//oulr22.getMSH().getMsh6_ReceivingFacility().getHd1_NamespaceID().setValue("");
		//oulr22.getMSH().getMsh6_ReceivingFacility().getHd2_UniversalID().setValue("");
		//oulr22.getMSH().getMsh6_ReceivingFacility().getHd3_UniversalIDType().setValue("");
		oulr22.getMSH().getMsh7_DateTimeOfMessage().getTime().setValue(dateFormat.format(date));
		//oulr22.getMSH().getMsh7_DateTimeOfMessage().getTs1_Time().setValue("");
		//oulr22.getMSH().getMsh7_DateTimeOfMessage().getTs2_DegreeOfPrecision().setValue("");
		//oulr22.getMSH().getMsh8_Security().setValue("OUL^R22^OUL_R22");
		//oulr22.getMSH().getMsh9_MessageType().getMsg1_MessageCode().setValue("5");
		//oulr22.getMSH().getMsh9_MessageType().getMsg2_TriggerEvent().setValue("");
		//oulr22.getMSH().getMsh9_MessageType().getMsg3_MessageStructure().setValue("");
		oulr22.getMSH().getMsh10_MessageControlID().setValue(messageControlID.toString());
		//oulr22.getMSH().getMsh11_ProcessingID().getPt1_ProcessingID().setValue("");
		//oulr22.getMSH().getMsh11_ProcessingID().getPt2_ProcessingMode().setValue("");
		oulr22.getMSH().getMsh12_VersionID().getVersionID().setValue("2.5");
		//oulr22.getMSH().getMsh13_SequenceNumber().setValue(messageControlID.toString());
		//oulr22.getMSH().getMsh14_ContinuationPointer().setValue("");
		//oulr22.getMSH().getMsh15_AcceptAcknowledgmentType().setValue("");
		oulr22.getMSH().getMsh16_ApplicationAcknowledgmentType().setValue("AL");
		//oulr22.getMSH().getMsh17_CountryCode().setValue("");
		oulr22.getMSH().getMsh18_CharacterSet(0).setValue("UNICODE UTF-8");

		//oulr22.getMSH().getSequenceNumber().setValue("123");

		//MSH msh = oulr22.getMSH();

		System.out.println(oulr22.getMSH().encode());

		//return oulr22.getMSH().encode().toString();

	}

	public List<DataOrderInboundHL7Message> getOrderInboundHL7List(OML_O33 OML_O33_MSG) {

		List<DataOrderInboundHL7Message> inOrderInboundList = new ArrayList<DataOrderInboundHL7Message>();
		DataInboundHL7Message inbound = new DataInboundHL7Message();
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		inbound.setMessageBody(OML_O33_MSG.getMessage().toString());
		inbound.setSource(OML_O33_MSG.getMSH().getMsh4_SendingFacility().toString());
		inbound.setPriority(OML_O33_MSG.getSPECIMEN().getORDER().getOBSERVATION_REQUEST().getOBR().getObr5_PriorityOBR().toString());
		inbound.setMessageControllerId(Long.parseLong(OML_O33_MSG.getMSH().getMessageControlID().toString()));
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		List<MachineOrder> lstMachineOrder = this.getOrderList(OML_O33_MSG);
		LkpService lkpService = (LkpService) SpringUtil.getBean("LkpService");
		LkpMessageSourceType msgType = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", MessageSourceType.HL7.getValue(), FilterOperator.eq)),
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

}
