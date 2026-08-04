package com.certacure.machine.web.machine.order.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.core.util.IOUtils;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.common.helper.FilterablePageRequest;
import com.certacure.core.common.util.SecurityUtil;
import com.certacure.lis.interfaces.admin.model.SecUser;
import com.certacure.lis.interfaces.admin.service.SecUserService;
import com.certacure.lis.interfaces.entities.AppointmentEntity;
import com.certacure.lis.interfaces.entities.CheckInEntity;
import com.certacure.lis.interfaces.entities.CoreEventLog;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.DataInboundJSONMessage;
import com.certacure.lis.interfaces.entities.DataOrderInboundHL7Message;
import com.certacure.lis.interfaces.entities.ElegabalityApprovalEntity;
import com.certacure.lis.interfaces.entities.ElegabalityApprovalOrderEntity;
import com.certacure.lis.interfaces.entities.LkpMessageSourceType;
import com.certacure.lis.interfaces.entities.MachineOrder;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.entities.OrderCoverageEntity;
import com.certacure.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.certacure.lis.interfaces.entities.StockEntity;
import com.certacure.lis.interfaces.helper.ExternalURL;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageSourceType;
import com.certacure.lis.interfaces.service.AppointmentService;
import com.certacure.lis.interfaces.service.CheckInService;
import com.certacure.lis.interfaces.service.CoreEventLogService;
import com.certacure.lis.interfaces.service.DataInboundHL7MessageService;
import com.certacure.lis.interfaces.service.DataInboundJSONMessageService;
import com.certacure.lis.interfaces.service.DataOrderInboundHL7MessageService;
import com.certacure.lis.interfaces.service.ElegabalityApprovalOrderService;
import com.certacure.lis.interfaces.service.ElegabalityApprovalService;
import com.certacure.lis.interfaces.service.LkpService;
import com.certacure.lis.interfaces.service.MachineOrderService;
import com.certacure.lis.interfaces.service.MessageTransactionService;
import com.certacure.lis.interfaces.service.OrderCoverageService;
import com.certacure.lis.interfaces.service.PCRRealTimeOrderService;
import com.certacure.lis.interfaces.service.PostDetailFinancialTransactionService;
import com.certacure.lis.interfaces.service.StockService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import akka.actor.Props;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.hl7v2.model.v25.message.OML_O33;
import freemarker.template.utility.UndeclaredThrowableException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneOffsetTransitionRule;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/services")
public class MachineOrderController {

	@Autowired
	private MessageTransactionService messageTransactionService;

	@Autowired
	private MachineOrderService machineOrderService;

	@Autowired
	private CoreEventLogService coreEventLogService;

	@Autowired
	private DataOrderInboundHL7MessageService dataOrderInboundHL7MessageService;

	@Autowired
	private PostDetailFinancialTransactionService postDetailFinancialTransactionService;

	@Autowired
	private DataInboundHL7MessageService dataInboundHL7MessageService;

	@Autowired
	private DataInboundJSONMessageService dataInboundJSONMessageService;

	@Autowired
	private ElegabalityApprovalService elegabalityApprovalService;

	@Autowired
	private ElegabalityApprovalOrderService elegabalityApprovalOrderService;

	@Autowired
	private LkpService lkpService;

	@Autowired
	private PCRRealTimeOrderService pcrRealTimeOrderService;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private OrderCoverageService orderCoverageService;

	@Autowired
	private SecUserService secUserService;

	@Autowired
	private CheckInService checkInService;

	@Autowired
	private StockService stockService;

	private Props props1;
	private static Gson gson = new Gson();;

	private PostDetailFinancialTransaction postDetailFinancialTransaction;

	@Value("${system.certacure.api.checkin.url}")
	private String CHECK_IN_API;

	@Value("${system.certacure.api.appointment.qms.url}")
	private String APPOINTMENT_QMS_API;
	@Value("${system.certacure.api.appointment.qms.auth}")
	private String APPOINTMENT_QMS_API_AUTH;

	@Value("${system.certacure.api.update.order.coverage.url}")
	private String UPDATE_ORDER_COVERAGE_API;
	@Value("${system.certacure.api.update.order.coverage.auth}")
	private String UPDATE_ORDER_COVERAGE_API_AUTH;

	// @Value("${system.certacure.api.cloverleaf.stock.issue.url}")
	// private String ISSUE_STOCK_API;
	// @Value("${system.certacure.api.cloverleaf.stock.auth}")
	// private String STOCK_API_AUTH;

	// @Value("${system.certacure.api.cloverleaf.stock.return.url}")
	// private String RETURN_STOCK_API;
	// @Value("${system.certacure.api.cloverleaf.stock.auth}")
	// private String RETURN_STOCK_API_AUTH;

	@RequestMapping(value = "/runTest.srvc", method = RequestMethod.POST)
	public void test() {
		System.out.println("run test success!");
	}

	@RequestMapping(value = "/patient", method = RequestMethod.GET)
	public ResponseEntity<String> getMachinePage(@RequestBody FilterablePageRequest filterablePageRequest) {

		Observation obs = new Observation();

		// These are all equivalent
		obs.setIssuedElement(new InstantType(new Date()));
		obs.setIssuedElement(new InstantType(new Date(), TemporalPrecisionEnum.MILLI));
		obs.setIssued(new Date());

		// The InstantType also lets you work with the instant as a Java Date
		// object or as a FHIR String.
		Date date = obs.getIssuedElement().getValue(); // A date object
		String dateString = obs.getIssuedElement().getValueAsString(); // "2014-03-08T12:59:58.068-05:00"

		// Create a patient
		Patient patient = new Patient();
		patient.addName().setFamily("Smith").addGiven("Rob").addGiven("Bruce");

		patient.setId("1333");
		patient.addIdentifier().setSystem("urn:mrns").setValue("253345");
		patient.getGenderElement().setValueAsString("male");

		String genderString = patient.getGenderElement().getValueAsString();
		Enumerations.AdministrativeGender genderEnum = patient.getGenderElement().getValue();

		// Create an Observation instance
		/*
		 * Observation observation = new Observation();
		 * 
		 * // Give the observation a status
		 * observation.setStatus(Observation.ObservationStatus.FINAL);
		 * 
		 * // Give the observation a code (what kind of observation is this) Coding
		 * coding = observation.getCode().addCoding();
		 * coding.setCode("29463-7").setSystem("http://loinc.org").
		 * setDisplay("Body Weight");
		 * 
		 * // Create a quantity datatype Quantity value = new Quantity();
		 * value.setValue(83.9).setSystem("http://unitsofmeasure.org").setCode("kg");
		 * observation.setValue(value);
		 * 
		 * // Set the reference range SimpleQuantity low = new SimpleQuantity();
		 * low.setValue(45).setSystem("http://unitsofmeasure.org").setCode("kg");
		 * observation.getReferenceRangeFirstRep().setLow(low); SimpleQuantity high =
		 * new SimpleQuantity();
		 * low.setValue(90).setSystem("http://unitsofmeasure.org").setCode("kg");
		 * observation.getReferenceRangeFirstRep().setHigh(high);
		 */

		// Create a FHIR context
		FhirContext ctx = FhirContext.forR4();

		// Instantiate a new JSON parser
		IParser parser = ctx.newJsonParser();

		// Serialize it
		String serialized = parser.encodeResourceToString(patient);
		System.out.println(serialized);

		// Using XML instead
		serialized = ctx.newXmlParser().encodeResourceToString(patient);
		System.out.println(serialized);

		// Using XML instead
		serialized = ctx.newJsonParser().encodeResourceToString(patient);
		System.out.println(serialized);

		return new ResponseEntity<String>(serialized, HttpStatus.OK);

	}

	@RequestMapping(value = "/addOrder.srvc", method = RequestMethod.POST)
	public void addOrder(@RequestBody MachineOrder machineOrder) {

		LkpMessageSourceType msgType = setMessageType();
		addMachineOrders(machineOrder, msgType);

		// ActorSystem.create(StringToLIS2A2Converter.create(), "iot-system");
		// Props props7 = Props.create(StringToLIS2A2Converter.class, () -> new
		// ActorWithArgs("arg"));

	}

	/*
	 * @RequestMapping(value = "/addElegabalityResponce.srvc", method =
	 * RequestMethod.POST) public void AddElegabalityResponce(@RequestBody
	 * ElegabalityApprovalEntity elegabalityApprovalEntity) { /*
	 * 
	 * 
	 * 1.OrderId 2.OrderActionId 3.OrderSource ///////////////////////// 4.ItemCode
	 * 5.ItemCategoryId ///////////////////////////////////// 6.IsEligible
	 * 7.Quantity 8.OrderSectionCode 9.Description ///////////////////////patient
	 * information 10.PatientInfoId 11.PatientInfoCode 12.PatientInfoNationalCode
	 * ///////////////////////patient information 13.VisitInfoId
	 * 14.VisitInfoPatientType 15.VisitInfoSectionCode 16.VisitInfoDoctorCode
	 * 17.visitInfoDoctorName 18.VisitInfoAdmissionReasonCode
	 * 19.VisitInfoDealingType
	 * 
	 * 
	 * insertEligibilityMessage(
	 * 
	 * elegabalityApprovalEntity.getOrderId()
	 * ,elegabalityApprovalEntity.getOrderActionId()
	 * ,elegabalityApprovalEntity.getOrderSource()
	 * 
	 * ,elegabalityApprovalEntity.getItemCode()
	 * ,elegabalityApprovalEntity.getItemCategoryId()
	 * ,elegabalityApprovalEntity.getIsEligible()
	 * ,elegabalityApprovalEntity.getQuantity()
	 * ,elegabalityApprovalEntity.getOrderSectionCode()
	 * ,elegabalityApprovalEntity.getDescription()
	 * 
	 * ,elegabalityApprovalEntity.getPatientInfoId()
	 * ,elegabalityApprovalEntity.getPatientInfoCode()
	 * ,elegabalityApprovalEntity.getPatientInfoNationalCode()
	 * 
	 * ,elegabalityApprovalEntity.getVisitInfoId()
	 * ,elegabalityApprovalEntity.getVisitInfoPatientType()
	 * ,elegabalityApprovalEntity.getVisitInfoSectionCode()
	 * ,elegabalityApprovalEntity.getVisitInfoDoctorCode()
	 * ,elegabalityApprovalEntity.getVisitInfoDoctorName()
	 * ,elegabalityApprovalEntity.getVisitInfoAdmissionReasonCode ()
	 * ,elegabalityApprovalEntity.getVisitInfoDealingType());
	 * 
	 * 
	 * System.out.println(elegabalityApprovalEntity); }
	 */

/////////////THIS WILL INSERT USING JPA /////////////////
	@RequestMapping(value = "/addElegabalityResponce.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> AddElegabalityResponce(
			@RequestBody ElegabalityApprovalEntity elegabalityApprovalEntity) {
		try {

			// elegabalityApprovalEntity.setAppointment_id(elegabalityApprovalEntity.getVisitInfoId());

			if (elegabalityApprovalEntity.getIsAppointment()) {
				elegabalityApprovalService.createElegabalityAppointmentRecord(elegabalityApprovalEntity);

			} else {

				ElegabalityApprovalOrderEntity elegabalityOrderEntity = new ElegabalityApprovalOrderEntity();

				elegabalityOrderEntity.setOrderId(elegabalityApprovalEntity.getOrderId());
				elegabalityOrderEntity.setOrderActionId(elegabalityApprovalEntity.getOrderActionId());
				elegabalityOrderEntity.setItemCode(elegabalityApprovalEntity.getItemCode());
				elegabalityOrderEntity.setOrderActionId(elegabalityOrderEntity.getOrderActionId());
				elegabalityOrderEntity.setOrderSource(elegabalityApprovalEntity.getOrderSource());
				elegabalityOrderEntity.setItemCategoryId(elegabalityApprovalEntity.getItemCategoryId());
				elegabalityOrderEntity.setIsEligible(elegabalityApprovalEntity.getIsEligible());
				elegabalityOrderEntity.setQuantity(elegabalityApprovalEntity.getQuantity());
				elegabalityOrderEntity.setOrderSectionCode(elegabalityApprovalEntity.getOrderSectionCode());
				elegabalityOrderEntity.setDescription(elegabalityApprovalEntity.getDescription());
				elegabalityOrderEntity.setDescription(elegabalityApprovalEntity.getDescription());
				elegabalityOrderEntity.setPatientInfoId(elegabalityApprovalEntity.getPatientInfoId());
				elegabalityOrderEntity.setPatientInfoCode(elegabalityApprovalEntity.getPatientInfoCode());
				elegabalityOrderEntity
						.setPatientInfoNationalCode(elegabalityApprovalEntity.getPatientInfoNationalCode());
				elegabalityOrderEntity.setVisitInfoId(elegabalityApprovalEntity.getVisitInfoId());
				elegabalityOrderEntity.setVisitInfoPatientType(elegabalityApprovalEntity.getVisitInfoPatientType());
				elegabalityOrderEntity.setVisitInfoSectionCode(elegabalityApprovalEntity.getVisitInfoSectionCode());
				elegabalityOrderEntity.setVisitInfoDoctorCode(elegabalityApprovalEntity.getVisitInfoDoctorCode());
				elegabalityOrderEntity.setVisitInfoDoctorName(elegabalityApprovalEntity.getVisitInfoDoctorName());
				elegabalityOrderEntity
						.setVisitInfoAdmissionReasonCode(elegabalityApprovalEntity.getVisitInfoAdmissionReasonCode());
				elegabalityOrderEntity.setVisitInfoDealingType(elegabalityApprovalEntity.getVisitInfoDealingType());
				elegabalityOrderEntity.setIsAppointment(elegabalityApprovalEntity.getIsAppointment());
				elegabalityOrderEntity.setIpPrinterAddress(elegabalityApprovalEntity.getIpPrinterAddress());

				elegabalityOrderEntity.setJsonBody(elegabalityOrderEntity.toString());

				elegabalityOrderEntity = elegabalityApprovalOrderService
						.createElegabalityOrderRecord(elegabalityOrderEntity);

			}

			return new ResponseEntity<String>("Response Code: 200, Elegability Approval Has been Added Successfully",
					HttpStatus.OK);

		} catch (Exception e) {
			System.out.println("Catch Statement");
			return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	private void addMachineOrders(List<MachineOrder> machineOrderList, LkpMessageSourceType msgType) {
		for (MachineOrder order : machineOrderList) {
			order.setSourceType(msgType);
			if (order.getResultReceived() == null) {
				order.setResultReceived(false);
			}

			this.machineOrderService.addOrder(order);

		}
	}

	private void addMachineOrders(MachineOrder machineOrder, LkpMessageSourceType msgType) {
		// for (MachineOrder order : machineOrderList) {
		machineOrder.setSourceType(msgType);
		if (machineOrder.getResultReceived() == null) {
			machineOrder.setResultReceived(false);
		}

		this.machineOrderService.addOrder(machineOrder);

		// }
	}

	// send check in API

	@RequestMapping(value = "/sendCheckIn.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> createCheckIn(

			@RequestBody CheckInEntity checkInEntity) {
		try {

			/*
			 * "appointmentId": 0, => Long "nationalId": "string", => String "patientCode":
			 * "string", => String "appointmentDate": "2024-01-10T16:35:25.328Z", =>
			 * DateTime "resourceId": 0 => Long
			 */
			String responseString = "";
			String strJson =

					"{ " + "\"" + "appointmentId" + "\"" + ":" + checkInEntity.getApptID() + "," + "\"" + "nationalId"
							+ "\"" + " : " + "\"" + checkInEntity.getNationalId() + "\"" + "," + "\"" + "patientCode"
							+ "\"" + " : " + "\"" + checkInEntity.getMRN() + "\"" + "," + "\"" + "appointmentDate"
							+ "\"" + " : " + "\"" + getStringDateTimeISO8601(checkInEntity.getApptDate()) + "\"" + ","
							+ "\"" + "resourceId" + "\"" + " : " + checkInEntity.getClinicIEN() + "}";

			checkInEntity.setIsSent(false);
			checkInEntity.setIsSuccess(false);

			try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
				HttpUriRequest request = null;

				request = RequestBuilder.post()
						// .setUri("http://192.168.21.114:6655/api/Appointment/ActiveBillForAppointment")
						// "http://192.168.21.90:6655/api/Appointment/ActiveBillForAppointment"
						.setUri(CHECK_IN_API).setHeader(HttpHeaders.ACCEPT, "application/json;charset=utf-8")
						.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8")
						// add request body
						.setEntity(new StringEntity(strJson))

						.build();

				System.out.println("/////////////////////////////////////////////////////////////////////////");
				System.out.println("Executing POST request... ");

				CloseableHttpResponse response = httpclient.execute(request);
				// httpRequest.setHttpResponse(response);
				// httpRequest.getMessageTransaction().setNotes(response.toString());
				HttpEntity entity = response.getEntity();
				// EntityUtils.consume(entity);

				response.setEntity(entity);
				responseString = EntityUtils.toString(entity, "UTF-8");

				System.out.println("ReasonPhrase: " + response.getStatusLine().getReasonPhrase().toString());
				System.out.println("Error Responce : " + responseString);
				System.out.println("Status code: " + response.getStatusLine().getStatusCode());

				// httpRequestTransObj.setErrorDesc(responseString);
				System.out.println("/////////////////////////////////////////////////////////////////////////");

				// HttpResponse<T> responseString = new
				// BasicResponseHandler().handleResponse(response);
				System.out.println("/////////////////////////////////////////////////////////////////////////");
				System.out.println("/////////////////////////////////////////////////////////////////////////");

				MessageTransaction messageTransaction = new MessageTransaction();
				SecUser user = secUserService.findById(SecurityUtil.getCurrentUser().getRid());
				messageTransaction.setTenantId(1l);
				messageTransaction.setBranchId(115l);
				messageTransaction.setMessageBody(strJson);

				checkInEntity.setJsonSourceInput(checkInEntity.toString());
				checkInEntity.setJsonSourceresponse(
						"Status Code : " + response.getStatusLine().getStatusCode() + " Body : " + responseString);
				checkInEntity.setJsonDestinationInput(strJson);
				checkInEntity.setJsonDestinationResponse(
						"Status Code : " + response.getStatusLine().getStatusCode() + " Body : " + responseString);

				checkInService.createCheckInRecord(checkInEntity);

				if (response.getStatusLine().getStatusCode() == 200) {
					// checkInEntity.setIsSent(true);
					// checkInEntity.setIsSuccess(true);

					messageTransaction.setIsSent(true);
					messageTransaction.setIsSuccess(true);
				} else {
					messageTransaction.setIsSent(false);
					messageTransaction.setIsSuccess(false);

					return new ResponseEntity<String>(
							"Response Code: 400, Check-In Record Addeed Failed :" + responseString,
							HttpStatus.BAD_REQUEST);
				}
			}

			return new ResponseEntity<String>(
					"Response Code: 200, Check-In Record Has been Added Successfully :" + responseString,
					HttpStatus.OK);

		} catch (Exception e) {
			System.out.println("Catch Statement");
			return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	public String getStringDateTimeISO8601(String strDateTime) throws Exception {

		int indexOfDash = strDateTime.indexOf('-');

		if (indexOfDash != -1) {

			strDateTime = strDateTime.substring(0, indexOfDash);
		}

		if (strDateTime.length() < 15) {
			String str = Strings.padEnd(strDateTime, 14, '0');
			strDateTime = str;
			System.out.println(strDateTime);
		}
		checkTimeFormat(strDateTime);
		String strDate = strDateTime;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime localDate = LocalDateTime.parse(strDate, formatter);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("GMT"));
		return localDate.format(formatter2);
	}

	public Date getDateTime(String strDateTime) throws Exception {

		int indexOfDash = strDateTime.indexOf('-');

		Date date = new Date();
		Instant instant = date.toInstant();

		if (indexOfDash != -1) {

			strDateTime = strDateTime.substring(0, indexOfDash);
		}

		if (strDateTime.length() < 15) {
			String str = Strings.padEnd(strDateTime, 14, '0');
			strDateTime = str;
			System.out.println(strDateTime);
		}
		checkTimeFormat(strDateTime);
		String strDate = strDateTime;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime localDate = LocalDateTime.parse(strDate, formatter);
		// DateTimeFormatter formatter2=
		// DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
		// .withZone(ZoneId.of("GMT"));

		Date output = Date.from(localDate.toInstant(ZoneOffset.UTC));
		return output;
	}

	private void checkTimeFormat(String dateTime) throws java.text.ParseException {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			format.parse(dateTime);
			System.out.println("Correct date");

		} catch (ParseException e) {
			System.out.println("Incorrect date");
		}

	}

	private LkpMessageSourceType setMessageType() {

		LkpMessageSourceType msgType = lkpService.findOneAnyLkp(
				java.util.Arrays
						.asList(new SearchCriterion("code", MessageSourceType.LIS.getValue(), FilterOperator.eq)),
				LkpMessageSourceType.class);
		return msgType;
	}

	@RequestMapping(value = "/addHL7Order.srvc", method = RequestMethod.POST)
	public void addHL7Order(@RequestBody String strHL7Order) {

	/*	if (machineOrderService.getIsEnabledReciveEhopeOrder() == "1") {

			try {
				
				HL7Parser parser = new HL7Parser(strHL7Order, "2.5");
				OML_O33 OML_O33_MSG = parser.getOrderOMLO33Message();

				//List<DataOrderInboundHL7Message> dataOrderInboundHL7List = parser.getOrderInboundHL7List(OML_O33_MSG);

				saveDataInboundHL7Message(dataOrderInboundHL7List.get(0).getInboundHl7Message());

				for (int index = 0; index < dataOrderInboundHL7List.size(); index++) {
					if (saveMachineOrder(dataOrderInboundHL7List.get(index).getMachineOrder()) != null) {
						saveDataOrderInboundHL7Message(dataOrderInboundHL7List.get(index));
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			String strError = "\n-------------------------------------------------------------------------------------------------------------\n"
					+ "setup not allow to Recive any order from eHoep"
					+ "\n-------------------------------------------------------------------------------------------------------------\n";
			System.out.println(strError);

			CoreEventLog eventLog = getEventLogObject(strError);
			coreEventLogService.addCoreEventLog(eventLog);
		}
	}

	@RequestMapping(value = "/addMessageTraansferOrder.srvc", method = RequestMethod.POST)
	public void addMessageTraansferOrder(@RequestBody String strHL7Order) {

		if (machineOrderService.getIsEnabledReciveEhopeOrder() == "1") {
			try {
				HL7Parser parser = new HL7Parser(strHL7Order, "2.5");
				OML_O33 OML_O33_MSG = parser.getOrderOMLO33Message();

				List<DataOrderInboundHL7Message> dataOrderInboundHL7List = parser.getOrderInboundHL7List(OML_O33_MSG);

				saveDataInboundHL7Message(dataOrderInboundHL7List.get(0).getInboundHl7Message());

				for (int index = 0; index < dataOrderInboundHL7List.size(); index++) {
					if (saveMachineOrder(dataOrderInboundHL7List.get(index).getMachineOrder()) != null) {
						saveDataOrderInboundHL7Message(dataOrderInboundHL7List.get(index));
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			String strError = "\n-------------------------------------------------------------------------------------------------------------\n"
					+ "setup not allow to Recive any order from "
					+ "\n-------------------------------------------------------------------------------------------------------------\n";
			System.out.println(strError);

			CoreEventLog eventLog = getEventLogObject(strError);
			coreEventLogService.addCoreEventLog(eventLog);
		}*/
	}

	@RequestMapping(value = "/addMessageTransactionEvent.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> addMessageTransactionEvent(@RequestBody DataInboundJSONMessage jsonMessage) {
		DataInboundJSONMessage dataInbound = new DataInboundJSONMessage();
		dataInbound.setMessageControllerId(jsonMessage.getMessageControllerId());
		dataInbound.setMessageBody(jsonMessage.getMessageBody());
		dataInbound.setPriority(jsonMessage.getPriority());
		dataInbound.setMessageType((Long) jsonMessage.getMessageType());
		dataInbound.setIsSent(jsonMessage.getIsSent());
		dataInbound.setIsSuccess(jsonMessage.getIsSuccess());
		dataInbound.setSource(jsonMessage.getSource());
		dataInbound.setBranchId(115l);
		// dataIbbound.setTenantId(1l);*/

		instertJsonMessage(dataInbound.getMessageControllerId(), dataInbound.getMessageBody());

		// dataInboundJSONMessageService.addInbound(dataInbound);
		System.out.println(jsonMessage);

		return ResponseEntity.ok(" Message ID : " + dataInbound.getMessageControllerId() + " , Added " + "Successfully"
				+ " Code: 200  ");
	}

	private static java.sql.Date getCurrentDate() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Date(today.getTime());
	}

	public static void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException) e).getSQLState());
				System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while (t != null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}

	// send appointment API

	@RequestMapping(value = "/sendAppointment.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> sendAppointment(@RequestBody AppointmentEntity Appointment) {
		/*
		 * 
		 * Appointment JSON will receive from Certacure Engine Should Re send this JSON
		 * to CL : {
		 * 
		 * "Appt_ID": Mandatory string "MRN": Mandatory string
		 * 
		 * "NATIONAL_ID": Mandatory string
		 * 
		 * "Clinic_IEN": Mandatory number
		 * 
		 * "Appt_Date": Mandatory Date
		 * 
		 * "Appt_From_Time": "03:00", Mandatory string
		 * 
		 * "Appt_To_Time": "04:00",-- Mandatory string
		 * 
		 * "Speciality_IEN": "21451", -- Mandatory number
		 * 
		 * "Appt_Status": "CR", ,-- Mandatory string
		 * 
		 * "Has_Coverage": " AA" Mandatory string
		 * 
		 * “Type”:”I” ,-- Mandatory string }
		 * 
		 */

		try {

			sendDataToQMS(Appointment);

			return new ResponseEntity<String>("Response Code: 200, Appointment  Has been Added Successfully",
					HttpStatus.OK);

		} catch (Exception ex) {
			return new ResponseEntity<String>("Response Code: 400, Appointment Has not been Added (Failed !!)",
					HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/updateOrderCoverageStatus.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> updateOrderCoverageStatus(@RequestBody OrderCoverageEntity orderCoverage) {

		/*
		 * 
		 * 
		 * Order Coverage Status , will receive from Certacure : { "Order_Id": Get Order
		 * ID from pharacy.order.id,-- Mandatory number "Action_id " Get id from
		 * pharacy.order_action --Mandatory number "item_ code " Get ITEM_CODE from
		 * PHARMACY.ORDER_ACTION --- Mandatory string "MRN": PATIENT_CODE from
		 * PHARMACY.[ORDER] ---Mandatory string "Has_Coverage": 0 or 1 --- Mandatory
		 * bool }
		 * 
		 * 
		 */

		try {

			List<PostDetailFinancialTransaction> listDFTOrders = postDetailFinancialTransactionService
					.getOrdersByCertaOrderIdAndActionAndItem(orderCoverage.getCertaOrderId(),
							orderCoverage.getCertaActionId(), orderCoverage.getItemCode());

			if (listDFTOrders.size() == 0) {
				return new ResponseEntity<String>("Response Code: 400, ORDER [" + orderCoverage.getCertaOrderId()
						+ "] , " + "ACTION [" + orderCoverage.getCertaActionId() + "] , " + "ITEM ["
						+ orderCoverage.getItemCode() + "]" + " NOT FOUND , UPDATE ORDER COVERAGE FAILED !! : ",
						HttpStatus.BAD_REQUEST);
			}

			// Check if assigned order date and time is for today otherwise request ignore
			// sending to CL
			/*
			 * if (!isSameDayOrder(orderCoverage, listDFTOrders.get(0))) { return new
			 * ResponseEntity<String>(
			 * "Response Code: 400, ORDER DATE IS NOT FOR TODAY , UPDATE ORDER COVERAGE FAILED !! : "
			 * , HttpStatus.BAD_REQUEST); }
			 */

			sendUpdateOrderCoverage(orderCoverage, listDFTOrders.get(0));

			return new ResponseEntity<String>("Response Code: 200, Updated Order Coverage  Has been done Successfully",
					HttpStatus.OK);

		} catch (Exception ex) {
			return new ResponseEntity<String>(
					"Response Code: 400, Update Coverage Order Record Failed !! : " + ex.getMessage(),
					HttpStatus.BAD_REQUEST);

		}

	}

	private boolean isSameDayOrder(OrderCoverageEntity orderCoverage, PostDetailFinancialTransaction postOrder)
			throws Exception {

		boolean sameDay = false;

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(getDateTime(postOrder.getTransactionDate()));

		sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
				&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);

		return sameDay;

	}

	private void sendDataToQMS(AppointmentEntity appointment) throws ParseException, IOException {

		HttpUriRequest request = null;

		List<AppointmentEntity> lstAppointments = appointmentService.getAppointment(appointment);

		String strJson =

				"{ " + "\"" + "Appt_ID" + "\"" + ":" + "\"" + appointment.getAppointmentId() + "\"" + "," + "\"" + "MRN"
						+ "\"" + " : " + "\"" + appointment.getPatientCode() + "\"" + "," + "\"" + "NATIONAL_ID" + "\""
						+ " : " + "\"" + appointment.getNationalId() + "\"" + "," + "\"" + "Clinic_IEN" + "\"" + " : "
						+ "\"" + appointment.getResourceId() + "\"" + "," + "\"" + "Appt_Date" + "\"" + " : " + "\""
						+ appointment.getAppointmentDate() + "\"" + "," + "\"" + "Appt_From_Time" + "\"" + " : " + "\""
						+ appointment.getStartTime() + "\"" + "," + "\"" + "Appt_To_Time" + "\"" + " : " + "\""
						+ appointment.getEndTime() + "\"" + "," + "\"" + "Speciality_IEN" + "\"" + " : " + "\""
						+ appointment.getSpecialityId() + "\"" + "," + "\"" + "Appt_Status" + "\"" + " : " + "\"" + "CR"
						+ "\"" + "," // + (appointment.getAppointmnetStatus()) + "\"" + ","
						+ "\"" + "Has_Coverage" + "\"" + " : " + "\""
						+ (appointment.getHasCoverage() == true ? "AA" : "AR") + "\"" + "," + "\"" + "Type" + "\""
						+ " : " + "\"" + (lstAppointments.size() > 0 ? "U" : "I") + "\"" + " }";

		appointment.setIsSent(false);
		appointment.setIsSuccess(false);

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			request = RequestBuilder.post().setUri(APPOINTMENT_QMS_API)// "http://192.168.21.226:3322/Patients_Appointments"
					.setHeader(HttpHeaders.ACCEPT, "application/json;charset=utf-8")
					.setHeader(HttpHeaders.AUTHORIZATION, APPOINTMENT_QMS_API_AUTH) // Basic
																					// UkNNQ0w6Njk2bGRpQXpLR3lXRkFU
					.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8")
					// add request body
					.setEntity(new StringEntity(strJson))

					.build();

			System.out.println("/////////////////////////////////////////////////////////////////////////");
			System.out.println("Executing POST request... ");

			CloseableHttpResponse response = httpclient.execute(request);
			// httpRequest.setHttpResponse(response);
			// httpRequest.getMessageTransaction().setNotes(response.toString());
			HttpEntity entity = response.getEntity();
			// EntityUtils.consume(entity);

			response.setEntity(entity);
			String responseString = EntityUtils.toString(entity, "UTF-8");

			System.out.println("ReasonPhrase: " + response.getStatusLine().getReasonPhrase().toString());
			System.out.println("Error Responce : " + responseString);
			System.out.println("Status code: " + response.getStatusLine().getStatusCode());

			// httpRequestTransObj.setErrorDesc(responseString);
			System.out.println("/////////////////////////////////////////////////////////////////////////");

			// HttpResponse<T> responseString = new
			// BasicResponseHandler().handleResponse(response);
			System.out.println("/////////////////////////////////////////////////////////////////////////");
			System.out.println("/////////////////////////////////////////////////////////////////////////");

			MessageTransaction messageTransaction = new MessageTransaction();
			SecUser user = secUserService.findById(SecurityUtil.getCurrentUser().getRid());
			messageTransaction.setTenantId(1l);
			messageTransaction.setBranchId(115l);
			messageTransaction.setMessageBody(strJson);

			if (response.getStatusLine().getStatusCode() == 200) {
				appointment.setIsSent(true);
				appointment.setIsSuccess(true);
				appointmentService.addAppointment(appointment);
				messageTransaction.setIsSent(true);
				messageTransaction.setIsSuccess(true);
			} else {
				messageTransaction.setIsSent(false);
				messageTransaction.setIsSuccess(false);
			}

			//appointment.setJsonSourceInput(appointment.toString());
			//appointment.setJsonSourceresponse(
			//		"Status Code : " + response.getStatusLine().getStatusCode() + " Body : " + responseString);
			//appointment.setJsonDestinationInput(strJson);
			//appointment.setJsonDestinationResponse(
			//		"Status Code : " + response.getStatusLine().getStatusCode() + " Body : " + responseString);

			appointmentService.addAppointment(appointment);

		}
//	catch(Exception e)
//	{
//	   e.printStackTrace(); 
//		appointment.setIsSent(false);
//		appointment.setIsSuccess(false);
//		appointmentService.addAppointment(appointment);
//	}
	}

	private void sendUpdateOrderCoverage(OrderCoverageEntity orderCoverage, PostDetailFinancialTransaction posOrder)
			throws ParseException, IOException {

		HttpUriRequest request = null;

		// Boolean isAppointmentExists =
		// appointmentService.checkAppointmentExists(appointment);

		String strJson =

				"{ " + "\"" + "Order_No" + "\"" + ":" + "\"" + posOrder.getVistaOrderID() + "\"" + "," + "\"" + "MRN"
						+ "\"" + " : " + "\"" + orderCoverage.getMRN() + "\"" + "," + "\"" + "Has_Coverage" + "\""
						+ " : " + "\"" + (orderCoverage.getHasCoverage() == true ? "AA" : "AR") + "\"" + " }";

		orderCoverage.setIsSent(false);
		orderCoverage.setIsSuccess(false);
		orderCoverage.setJSONBody(strJson);
		orderCoverageService.addOrderCoverage(orderCoverage);

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			request = RequestBuilder.post().setUri(UPDATE_ORDER_COVERAGE_API)
					// "http://192.168.21.226:3322/Update_Medications_Coverage"
					// "http://192.168.20.236:3322/Update_Medications_Coverage"
					.setHeader(HttpHeaders.ACCEPT, "application/json;charset=utf-8")
					.setHeader(HttpHeaders.AUTHORIZATION, UPDATE_ORDER_COVERAGE_API_AUTH)// Basic
																							// UkNNQ0w6Njk2bGRpQXpLR3lXRkFU
					.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8")
					// add request body
					.setEntity(new StringEntity(strJson))

					.build();

			System.out.println("/////////////////////////////////////////////////////////////////////////");
			System.out.println("Executing POST request... ");

			CloseableHttpResponse response = httpclient.execute(request);
			// httpRequest.setHttpResponse(response);
			// httpRequest.getMessageTransaction().setNotes(response.toString());
			HttpEntity entity = response.getEntity();
			// EntityUtils.consume(entity);

			response.setEntity(entity);
			String responseString = EntityUtils.toString(entity, "UTF-8");

			System.out.println("ReasonPhrase: " + response.getStatusLine().getReasonPhrase().toString());
			System.out.println("Error Responce : " + responseString);
			System.out.println("Status code: " + response.getStatusLine().getStatusCode());

			// httpRequestTransObj.setErrorDesc(responseString);
			System.out.println("/////////////////////////////////////////////////////////////////////////");

			// HttpResponse<T> responseString = new
			// BasicResponseHandler().handleResponse(response);
			System.out.println("/////////////////////////////////////////////////////////////////////////");
			System.out.println("/////////////////////////////////////////////////////////////////////////");

			MessageTransaction messageTransaction = new MessageTransaction();
			SecUser user = secUserService.findById(SecurityUtil.getCurrentUser().getRid());
			messageTransaction.setTenantId(1l);
			messageTransaction.setBranchId(115l);
			messageTransaction.setMessageBody(strJson);

			if (response.getStatusLine().getStatusCode() == 200) {
				orderCoverage.setIsSent(true);
				orderCoverage.setIsSuccess(true);
				orderCoverageService.addOrderCoverage(orderCoverage);
				messageTransaction.setIsSent(true);
				messageTransaction.setIsSuccess(true);
			} else {
				messageTransaction.setIsSent(false);
				messageTransaction.setIsSuccess(false);
			}

		}
//	catch(Exception e)
//	{
//	   e.printStackTrace(); 
//		appointment.setIsSent(false);
//		appointment.setIsSuccess(false);
//		appointmentService.addAppointment(appointment);
//	}
	}

	private void instertJsonMessage(String strMessageID, String strMessageBody) {
		String url = "jdbc:postgresql://localhost:5433/certacure_middleware2";
		String user = "postgres";
		String password = "root";

		String INSERT_USERS_SQL = "INSERT INTO mw_qms_temp_table" + "  (message_body, message_control_id) VALUES "
				+ " (?, ?);";

		System.out.println(INSERT_USERS_SQL);
		// Step 1: Establishing a Connection
		try (Connection connection = DriverManager.getConnection(url, user, password);

				// Step 2:Create a statement using connection object
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
			preparedStatement.setString(1, strMessageBody.replace("\'", "\""));
			preparedStatement.setInt(2, Integer.parseInt(strMessageID));

			System.out.println(preparedStatement);
			// Step 3: Execute the query or update query
			preparedStatement.executeUpdate();
		} catch (SQLException e) {

			// print SQL exception information
			printSQLException(e);
		}

	}

	private void insertEligibilityMessage(String OrderId, String OrderActionId, String OrderSource, String ItemCode,
			String ItemCategoryId, String IsEligible, String Quantity, String OrderSectionCode, String Description,
			/////////////////////// patient information
			String PatientInfoId, String PatientInfoCode, String PatientInfoNationalCode,
			/////////////////////// patient information
			String VisitInfoId, String VisitInfoPatientType, String VisitInfoSectionCode, String VisitInfoDoctorCode,
			String VisitInfoDoctorName, String VisitInfoAdmissionReasonCode, String VisitInfoDealingType) {

		String url = "jdbc:postgresql://localhost:5433/certacure_middleware2";
		String user = "postgres";
		String password = "root";

		String INSERT_USERS_SQL = "INSERT INTO mw_elegabality_approval_inbound_message" +

				"(order_Id," + "order_action_id," + "order_source," + "item_code," + "item_category_Id," +

				"is_eligible," + "quantity," + "order_section_code," + "description," +

				"patient_info_id," + "patient_info_code," + "patient_info_national_code," +

				"visit_info_id," + "visit_info_patient_type," + "visit_info_section_code," + "visit_info_doctor_code,"
				+ "visit_info_doctor_name," + "visit_info_admission_reason_Code," + "visit_Info_dealing_type)" +

				"VALUES " + " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

		System.out.println(INSERT_USERS_SQL);
		// Step 1: Establishing a Connection
		try (Connection connection = DriverManager.getConnection(url, user, password);

				// Step 2:Create a statement using connection object
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {

			preparedStatement.setString(1, OrderId);
			preparedStatement.setString(2, OrderActionId);
			preparedStatement.setString(3, OrderSource);
			preparedStatement.setString(4, ItemCode);
			preparedStatement.setString(5, ItemCategoryId);
			preparedStatement.setString(6, IsEligible);
			preparedStatement.setString(7, Quantity);
			preparedStatement.setString(8, OrderSectionCode);
			preparedStatement.setString(9, Description);
			preparedStatement.setString(10, PatientInfoId);
			preparedStatement.setString(11, PatientInfoCode);
			preparedStatement.setString(12, PatientInfoNationalCode);
			preparedStatement.setString(13, VisitInfoId);
			preparedStatement.setString(14, VisitInfoPatientType);
			preparedStatement.setString(15, VisitInfoSectionCode);
			preparedStatement.setString(16, VisitInfoDoctorCode);
			preparedStatement.setString(17, VisitInfoDoctorName);
			preparedStatement.setString(18, VisitInfoAdmissionReasonCode);
			preparedStatement.setString(19, VisitInfoDealingType);
			// preparedStatement.setTimestamp(20, new
			// Timestamp(System.currentTimeMillis()));
			// preparedStatement.setInt(21, 0);
			// preparedStatement.setTimestamp(22, new
			// Timestamp(System.currentTimeMillis()));

			System.out.println(preparedStatement);
			// Step 3: Execute the query or update query
			preparedStatement.executeUpdate();
		} catch (SQLException e) {

			// print SQL exception information
			printSQLException(e);
		}
	}

	private CoreEventLog getEventLogObject(String output) {
		CoreEventLog coreEventLog = new CoreEventLog();

		coreEventLog.setText(output);
		coreEventLog.setStatusId(4);
		Date date = new Date();
		coreEventLog.setSentDate(date);
		return coreEventLog;
	}

	private MachineOrder saveMachineOrder(MachineOrder machineOrder) {
		return this.machineOrderService.addOrder(machineOrder);
	}

	private DataOrderInboundHL7Message saveDataOrderInboundHL7Message(DataOrderInboundHL7Message inOrder) {
		return this.dataOrderInboundHL7MessageService.addOrder(inOrder);
	}

	private DataInboundHL7Message saveDataInboundHL7Message(DataInboundHL7Message inboundMessage) {
		return this.dataInboundHL7MessageService.addInbound(inboundMessage);
	}

	@RequestMapping(value = "/updateOrder.srvc", method = RequestMethod.POST)
	public ResponseEntity<MachineOrder> updateDriver(@RequestBody MachineOrder machineOrder) {

		this.machineOrderService.updateOrder(machineOrder);

		return new ResponseEntity<MachineOrder>(machineOrder, HttpStatus.OK);
	}

	@RequestMapping(value = "/getMachineOrderPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<MachineOrder>> getMachineOrderPage(
			@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<MachineOrder>>(machineOrderService.getMachineOrderPage(filterablePageRequest),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getOrderQueryResponse.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<MachineOrder>> getOrderQueryResponse(@RequestBody String barcode) {
		List<MachineOrder> machineOrderQueryResponse = machineOrderService.getOrderQueryResponseBySample(barcode);
		return new ResponseEntity<List<MachineOrder>>(machineOrderQueryResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/getOrderInfo.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<MachineOrder>> getOrderInfo(@RequestBody String barcode) {
		List<MachineOrder> machineOrderQueryResponse = machineOrderService.getOrderQueryByBarcode(barcode);
		return new ResponseEntity<List<MachineOrder>>(machineOrderQueryResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/getMessageTransactionPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<MessageTransaction>> getMessageTransactionPage(@RequestBody FilterablePageRequest fpr) {

		return new ResponseEntity<Page<MessageTransaction>>(messageTransactionService.getMessageTransactionPage(fpr),
				HttpStatus.OK);
	}

	// @RequestMapping(value = "/addPcrMachineOrder.srvc", method =
	// RequestMethod.POST)
	// public ResponseEntity<PCRRealTimeOrder> getPcrMachineOrder(@RequestBody
	// String barcode) {
	// MachineOrder machineOrder =
	// machineOrderService.getPcrMachineOrderByBarcode(barcode);
	// PCRRealTimeOrder savedPcrRealTimeOrder =
	// pcrRealTimeOrderService.addPcrRealTimeOrder(machineOrder);
	// return new ResponseEntity<PCRRealTimeOrder>(savedPcrRealTimeOrder,
	// HttpStatus.OK);
	// }

	/*
	 * @RequestMapping(value = "/issueStock.srvc", method = RequestMethod.POST)
	 * public ResponseEntity<Object> receiveStockItem(@RequestBody Map<String,
	 * Object> jsonObj) throws Exception { String jsonBody =
	 * convertMapToJsonString(jsonObj); StockEntity stockEntity = new StockEntity();
	 * 
	 * String strNewRequetString = valudationRequest(jsonBody);
	 * 
	 * stockEntity.setJsonBody(strNewRequetString);
	 * stockEntity.setSource("CERTACURE"); stockEntity.setDestination("INFOR");
	 * stockEntity.setOperationType("STOCK_ITEM");
	 * stockService.addStock(stockEntity);
	 * 
	 * Map<String, Object> response = excutePostRequest(ISSUE_STOCK_API, jsonBody,
	 * "issueStock");
	 * 
	 * // RootDeduction rootDeducation = new //
	 * RootDeduction(response.get("responseString").toString());
	 * 
	 * return new ResponseEntity<Object>(
	 * gson.fromJson(response.get("responseString").toString(), Object.class),
	 * HttpStatus.valueOf((Integer) response.get("statusCode")));
	 * 
	 * 
	 * }
	 */

	private String valudationRequest(String jsonBody) throws Exception {
		// check that all JSON items in correct format

		if (!jsonBody.contains("deduction"))
			throw new Exception("the request body not valid --deduction");
		else if (!jsonBody.contains("items"))
			throw new Exception("the request body not valid --items");
		else if (!jsonBody.contains("company"))
			throw new Exception("the request body not valid --company");
		else if (!jsonBody.contains("fromCompany"))
			throw new Exception("the request body not valid --fromCompany");
		else if (!jsonBody.contains("fromLocation"))
			throw new Exception("the request body not valid --fromLocation");
		/*
		 * else if (!jsonBody.contains("toCompany")) throw new
		 * Exception("the request body not valid --toCompany");
		 */
		else if (!jsonBody.contains("requestingLocation"))
			throw new Exception("the request body not valid -- requestingLocation");
		else if (!jsonBody.contains("actionId"))
			throw new Exception("the request body not valid --actionId");
//		else if (!jsonBody.contains("orderId"))
//			throw new Exception("the request body not valid --orderId");
		else if (!jsonBody.contains("staFlag"))
			throw new Exception("the request body not valid --staFlag");
		else if (!jsonBody.contains("item"))
			throw new Exception("the request body not valid --item");
		else if (!jsonBody.contains("quantity"))
			throw new Exception("the request body not valid --quantity");
		else if (!jsonBody.contains("uom"))
			throw new Exception("the request body not valid --uom");

		return jsonBody;

	}

	/*
	 * @RequestMapping(value = "/returnStock.srvc", method = RequestMethod.POST)
	 * public ResponseEntity<Object> receiveReturnStock(@RequestBody Map<String,
	 * Object> jsonObj) throws Exception { String jsonBody =
	 * convertMapToJsonString(jsonObj); StockEntity stockEntity = new StockEntity();
	 * validateReturnRequest(jsonBody); String strNewRequetString =
	 * returnRequestMapperString(jsonBody);
	 * 
	 * stockEntity.setJsonBody(strNewRequetString);
	 * stockEntity.setSource("CERTACURE"); stockEntity.setDestination("INFOR");
	 * stockEntity.setOperationType("RETURN_STOCK");
	 * stockService.addStock(stockEntity); Map<String, Object> response =
	 * excutePostRequest(RETURN_STOCK_API, strNewRequetString, "returnStock");
	 * return new
	 * ResponseEntity<Object>(gson.fromJson(response.get("responseString").toString(
	 * ), Object.class), HttpStatus.valueOf((Integer) response.get("statusCode")));
	 * 
	 * }
	 */

	private void validateReturnRequest(String jsonBody) throws Exception {
		// TODO Auto-generated method stub

		if (!jsonBody.contains("return"))
			throw new Exception("the request body not valid ---return");
		else if (!jsonBody.contains("items"))
			throw new Exception("the request body not valid ---items");
		else if (!jsonBody.contains("company"))
			throw new Exception("the request body not valid ---company");
		else if (!jsonBody.contains("fromCompany"))
			throw new Exception("the request body not valid ---fromCompany");
		else if (!jsonBody.contains("fromLocation"))
			throw new Exception("the request body not valid ---fromLocation");
		else if (!jsonBody.contains("toCompany"))
			throw new Exception("the request body not valid ---toCompany");
		else if (!jsonBody.contains("requestingLocation"))
			throw new Exception("the request body not valid ---requestingLocation");
		else if (!jsonBody.contains("actionId"))
			throw new Exception("the request body not valid ---actionId");
		else if (!jsonBody.contains("orderId"))
			throw new Exception("the request body not valid ---orderId");
		else if (!jsonBody.contains("staFlag"))
			throw new Exception("the request body not valid ---staFlag");
		else if (!jsonBody.contains("item"))
			throw new Exception("the request body not valid ---item");
		else if (!jsonBody.contains("quantity"))
			throw new Exception("the request body not valid ---quantity");
		else if (!jsonBody.contains("uom"))
			throw new Exception("the request body not valid ---uom");
		else if (!jsonBody.contains("lots"))
			throw new Exception("the request body not valid ---lots");
		else if (!jsonBody.contains("lotNumber"))
			throw new Exception("the request body not valid ---lotNumber");

	}

	/*
	 * @SuppressWarnings({ "finally" }) private Map<String, Object>
	 * excutePostRequest(String url, String jsonBody, String flag) throws
	 * ParseException, IOException {
	 * 
	 * HttpUriRequest request = null; String strInfoResponseBodyString = ""; String
	 * strRCMRecivedResponse = ""; MessageTransaction messageTransaction = new
	 * MessageTransaction(); CloseableHttpResponse response = null; HttpEntity
	 * entity = null; String responseString = ""; Map<String, Object>
	 * responseDataMap = new HashMap<>();
	 * 
	 * 
	 * try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
	 * 
	 * request = RequestBuilder.post().setUri(url) .setHeader(HttpHeaders.ACCEPT,
	 * "application/json;charset=utf-8") .setHeader(HttpHeaders.AUTHORIZATION,
	 * STOCK_API_AUTH) //Basic UkNNU0NNOjU5NmxkaUF6S0d5V0ZBTg==
	 * .setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8") // add
	 * request body .setEntity(new StringEntity(jsonBody)).build();
	 * System.out.println(
	 * "/////////////////////////////////////////////////////////////////////////");
	 * System.out.println("Executing POST request... "); response =
	 * httpclient.execute(request); // httpRequest.setHttpResponse(response); //
	 * httpRequest.getMessageTransaction().setNotes(response.toString()); entity =
	 * response.getEntity(); // EntityUtils.consume(entity);
	 * 
	 * response.setEntity(entity);
	 * 
	 * responseString = EntityUtils.toString(entity, "UTF-8");
	 * 
	 * 
	 * if (responseString.startsWith("[") && responseString.endsWith("]")){
	 * 
	 * responseString = responseString.substring(1, responseString.length() - 1); }
	 * 
	 * responseDataMap.put("responseString", responseString);
	 * responseDataMap.put("statusCode", response.getStatusLine().getStatusCode());
	 * System.out.println("ReasonPhrase: " +
	 * response.getStatusLine().getReasonPhrase().toString());
	 * System.out.println("Error Responce : " + responseString);
	 * System.out.println("Status code: " +
	 * response.getStatusLine().getStatusCode()); System.out.println("Body" +
	 * response.getEntity().getContentEncoding()); System.out.println(
	 * "/////////////////////////////////////////////////////////////////////////");
	 * System.out.println(
	 * "/////////////////////////////////////////////////////////////////////////");
	 * System.out.println(
	 * "/////////////////////////////////////////////////////////////////////////");
	 * 
	 * // strInfoResponseBodyString= //
	 * response.getEntity().getContentEncoding().getValue(); if
	 * (flag.equals("returnStock")) { strRCMRecivedResponse =
	 * returnResponseMapperString(responseString);
	 * 
	 * responseDataMap.replace("responseString", strRCMRecivedResponse);
	 * responseDataMap.replace("statusCode",
	 * response.getStatusLine().getStatusCode());
	 * 
	 * } else if (flag.equals("issueStock")) { strRCMRecivedResponse =
	 * issueStockResponseValidate(responseString);
	 * 
	 * responseDataMap.replace("responseString", strRCMRecivedResponse);
	 * responseDataMap.replace("statusCode",
	 * response.getStatusLine().getStatusCode());
	 * 
	 * }
	 * 
	 * SecUser user =
	 * secUserService.findById(SecurityUtil.getCurrentUser().getRid());
	 * messageTransaction.setTenantId(1L); messageTransaction.setBranchId(115L);
	 * messageTransaction.setMessageBody(jsonBody); if
	 * (response.getStatusLine().getStatusCode() == 200) {
	 * 
	 * messageTransaction.setIsSent(true); messageTransaction.setIsSuccuss(true);
	 * 
	 * } } catch (Exception e) { e.printStackTrace();
	 * messageTransaction.setIsSent(false); messageTransaction.setIsSuccuss(false);
	 * } finally { messageTransactionService.add(messageTransaction); return
	 * responseDataMap; } }
	 */

	private String returnResponseMapperString(String responseString) {

		///////////////////////////////////////////////////////////

		// responseString =
		/*
		 * "{\r\n" + "   \"return\":[\r\n" + "      {\r\n" +
		 * "         \"orderId\":\"RCM-2342380130\",\r\n" +
		 * "         \"errorCode\":\"1\",\r\n" +
		 * "         \"errorMessage\":\"Invalid Header, Action ID value must be provided\",\r\n"
		 * + "         \"actionId\":\"RCM-2342380125\",\r\n" +
		 * "         \"items\":[\r\n" + "            {\r\n" +
		 * "               \"item\":\"238109\",\r\n" +
		 * "               \"uom\":\"PCE\",\r\n" +
		 * "               \"quantity\":\"1.0\",\r\n" + "               \"lots\":[\r\n"
		 * + "                  {\r\n" +
		 * "                     \"quantity\":\"1.0\",\r\n" +
		 * "                     \"lotNumber\":\"10\"\r\n" + "                  }\r\n" +
		 * "               ]\r\n" + "            },\r\n" + "            {\r\n" +
		 * "               \"item\":\"276265\",\r\n" +
		 * "               \"uom\":\"PCE\",\r\n" +
		 * "               \"quantity\":\"1.0\",\r\n" + "               \"lots\":[\r\n"
		 * + "                  {\r\n" +
		 * "                     \"quantity\":\"1.0\",\r\n" +
		 * "                     \"lotNumber\":\"POI\"\r\n" + "                  }\r\n"
		 * + "               ]\r\n" + "            }\r\n" + "         ]\r\n" +
		 * "      },\r\n" + "      {\r\n" +
		 * "         \"orderId\":\"RCM-2342380130\",\r\n" +
		 * "         \"errorCode\":\"1\",\r\n" +
		 * "         \"errorMessage\":\"Invalid Header, Action ID value must be provided\",\r\n"
		 * + "         \"actionId\":\"RCM-2342380125\",\r\n" +
		 * "         \"items\":[\r\n" + "            {\r\n" +
		 * "               \"item\":\"238109\",\r\n" +
		 * "               \"uom\":\"PCE\",\r\n" +
		 * "               \"quantity\":\"1.0\",\r\n" + "               \"lots\":[\r\n"
		 * + "                  {\r\n" +
		 * "                     \"quantity\":\"1.0\",\r\n" +
		 * "                     \"lotNumber\":\"10\"\r\n" + "                  }\r\n" +
		 * "               ]\r\n" + "            },\r\n" + "            {\r\n" +
		 * "               \"item\":\"276265\",\r\n" +
		 * "               \"uom\":\"PCE\",\r\n" +
		 * "               \"quantity\":\"1.0\",\r\n" + "               \"lots\":[\r\n"
		 * + "                  {\r\n" +
		 * "                     \"quantity\":\"1.0\",\r\n" +
		 * "                     \"lotNumber\":\"POI\"\r\n" + "                  }\r\n"
		 * + "               ]\r\n" + "            }\r\n" + "         ]\r\n" +
		 * "      }\r\n" + "   ]\r\n" + "}";
		 */

		///////////////////////////////////////////////////////////

		// TODO Auto-generated method stub
		String newJsonString = "";
		try {

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			ResponseWrapperClass wrapper = objectMapper.readValue(responseString, ResponseWrapperClass.class);
			ObjectNode newObject = objectMapper.createObjectNode();
			ObjectNode extractedAttributes = objectMapper.createObjectNode();
			ArrayNode newArray = objectMapper.createArrayNode();

			// for (ResponseWrapperClass wrapper : wrapperArray) {
			ArrayNode newReturnArray = objectMapper.createArrayNode();
			for (ReturnResponse ret : wrapper.getReturns()) {

				extractedAttributes.put("orderId", ret.orderId);

				// extractedAttributes.put("toCompany", ret.toCompany);
				extractedAttributes.put("errorCode", ret.errorCode);
				extractedAttributes.put("errorMessage", ret.errorMessage);
				extractedAttributes.put("actionId", ret.actionId);
				extractedAttributes.put("errorCode", ret.errorCode);
				extractedAttributes.put("transactionNumber", ret.transactionNumber);

				// extractedAttributes.put("company", ret.company);
				// extractedAttributes.put("fromLocation", ret.fromLocation);
				// extractedAttributes.put("staFlag", ret.staFlag.equals("N")? false : true);
				// extractedAttributes.put("fromCompany", ret.fromCompany);
				newReturnArray.add(extractedAttributes);
				ArrayNode newItemsArray = objectMapper.createArrayNode();

				if (ret.items != null) {
					for (Item item : ret.items) {

						ObjectNode newItemObject = objectMapper.createObjectNode();
						newItemObject.put("item", item.item);
						newItemObject.put("uom", item.uom);
						newItemObject.put("quantity", getTotalItemLotsQuantity(item.lots));
						ArrayNode newLotsArray = objectMapper.createArrayNode();

						if (item.lots != null) {
							for (Lot lot : item.lots) {

								ObjectNode newLotObject = objectMapper.createObjectNode();
								newLotObject.put("lotNumber", lot.lotNumber);
								newLotObject.put("quantity", lot.quantity);
								newLotsArray.add(newLotObject);
							}
						}

						newItemObject.set("lots", newLotsArray);
						newItemsArray.add(newItemObject);

					}
					extractedAttributes.set("items", newItemsArray);
					newArray.add(extractedAttributes);

				}

			}
			// }

			newObject.set("return", newArray);
			newJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newObject);
			System.out.println(newJsonString);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return newJsonString;
	}

	@SuppressWarnings("null")
	private String returnRequestMapperString(String requestString) {
		// TODO Auto-generated method stub
		String newJsonString = "";
		try {
			int iTotalOfQuantity = 0;

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			// false);
			RequestWrapperClass wrapperReturnArray = objectMapper.readValue(requestString, RequestWrapperClass.class);
			ObjectNode newObject = objectMapper.createObjectNode();
			ObjectNode extractedAttributes = objectMapper.createObjectNode();
			ArrayNode newArray = objectMapper.createArrayNode();
			ArrayNode newReturnArray = objectMapper.createArrayNode();

			for (ReturnRequest ret : wrapperReturnArray.getReturns()) {

				iTotalOfQuantity = getTotalOfItemQuantity(ret);
				extractedAttributes.put("company", ret.company);
				extractedAttributes.put("fromCompany", ret.fromCompany);
				extractedAttributes.put("toCompany", ret.toCompany);
				extractedAttributes.put("fromLocation", ret.fromLocation);
				extractedAttributes.put("requestingLocation", ret.requestingLocation);
				extractedAttributes.put("staFlag", ret.staFlag);
				extractedAttributes.put("orderId", ret.orderId);
				extractedAttributes.put("actionId", ret.actionId);
				extractedAttributes.put("returnId", ret.returnId);
				newReturnArray.add(extractedAttributes);
				ArrayNode newItemsArray = objectMapper.createArrayNode();
				ObjectNode newItemObject = null;

				if (ret.items != null) {
					for (Item item : ret.items) {

						newItemObject = objectMapper.createObjectNode();
						newItemObject.put("item", item.item);
						newItemObject.put("uom", item.uom);
						// newItemObject.put("quantity", iTotalOfQuantity);

						ArrayNode newLotsArray = objectMapper.createArrayNode();

						if (item.lots != null) {
							for (Lot lot : item.lots) {

								ObjectNode newLotObject = objectMapper.createObjectNode();
								newLotObject.put("lotNumber", lot.lotNumber);
								newLotObject.put("quantity", (lot.quantity == null ? "0" : lot.quantity));
								newLotsArray.add(newLotObject);
								// iTotalOfQuantity += (int) Float.parseFloat( lot.quantity);
							}

							// newItemObject.put("quantity", getTotalItemLotsQuantity(item.lots));
						}

						newItemObject.set("lots", newLotsArray);

						newItemsArray.add(newItemObject);

					}

					ObjectNode objectNode = (ObjectNode) newItemsArray.get(0);

					ObjectNode copyOfObjectNode = objectNode.deepCopy();

					copyOfObjectNode.put("quantity", iTotalOfQuantity);

					newItemsArray.set(0, copyOfObjectNode);

				}

				// newItemsQuantity.put("quantity", iTotalOfQuantity);
				// newItemsArray.add(newItemsQuantity);
				extractedAttributes.set("items", newItemsArray);
				newArray.add(extractedAttributes);

			}

			newObject.set("return", newArray);
			newJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newObject);
			System.out.println(newJsonString);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return newJsonString;
	}

	private int getTotalOfItemQuantity(ReturnRequest ret) {
		int iTotalOfItemsLotQuantity = 0;

		if (ret.items != null) {
			for (Item item : ret.items) {
				if (item.lots != null) {
					for (Lot lot : item.lots) {
						iTotalOfItemsLotQuantity += (int) Float.parseFloat(lot.quantity);
					}
				}

			}

		}

		return iTotalOfItemsLotQuantity;
	}

	private int getTotalItemLotsQuantity(ArrayList<Lot> lots) {
		int iTotalofQuantityForItem = 0;

		for (Lot l : lots) {
			iTotalofQuantityForItem = (int) (iTotalofQuantityForItem + Float.parseFloat(l.quantity));
		}

		return iTotalofQuantityForItem;
	}

	private String issueStockResponseValidate(String strInfoResponseBodyString) {
		// TODO Auto-generated method stub
		String newJsonString = "";
		try {

			/*
			 * 
			 * 
			 * "acquisitionCost": "0.075", "jFDACostPrice": "0.075", "quantity": "2.0",
			 * "tenderFlag": "N", "lotNumber": "EK0103", "expiry": "2025-04-30 00:00:00",
			 * "jFDAPublicPrice": "0.098", "order": "1"
			 * 
			 */

			ObjectMapper objectMapper = new ObjectMapper();
			WrapperClass wrapper = objectMapper.readValue(strInfoResponseBodyString, WrapperClass.class);
			ObjectNode newObject = objectMapper.createObjectNode();
			ObjectNode extractedAttributes = objectMapper.createObjectNode();
			ArrayNode newArray = objectMapper.createArrayNode();
			// for (WrapperClass wrapperr : wrapper) {
			ArrayNode newDeductionArray = objectMapper.createArrayNode();
			for (Deduction deduction : wrapper.getDeduction()) {
				ObjectNode newDeductionObject = objectMapper.createObjectNode();
				newDeductionObject.put("orderId", deduction.orderId);
				newDeductionObject.put("transactionNumber", deduction.transactionNumber);
				newDeductionObject.put("errorMessage", deduction.errorMessage);
				newDeductionObject.put("actionId", deduction.actionId);
				newDeductionObject.put("staFlag", deduction.staFlag);
				newDeductionObject.put("errorCode", deduction.errorCode);
				newDeductionArray.add(newDeductionObject);

				ArrayNode newItemsArray = objectMapper.createArrayNode();
				for (Item item : deduction.items) {
					ObjectNode newItemObject = objectMapper.createObjectNode();
					newItemObject.put("item", item.item);
					newItemObject.put("uom", item.uom);
					newItemObject.put("quantity", item.quantity);
					ArrayNode newLotsArray = objectMapper.createArrayNode();

					if (item.lots != null) {
						for (Lot lot : item.lots) {
							ObjectNode newLotObject = objectMapper.createObjectNode();
							newLotObject.put("lotNumber", lot.lotNumber);
							newLotObject.put("acquisitionCost", lot.acquisitionCost);
							newLotObject.put("jFDAPublicPrice", lot.jFDAPublicPrice);
							newLotObject.put("jFDACostPrice", lot.jFDACostPrice);
							newLotObject.put("quantity", lot.quantity);
							if (lot.tenderFlag.equals("Y")) {
								newLotObject.put("tenderFlag", true);
							} else if (lot.tenderFlag.equals("N")) {
								newLotObject.put("tenderFlag", false);
							}

							newLotObject.put("expiry", lot.expiry);
							newLotObject.put("jFDAPublicPrice", lot.jFDAPublicPrice);
							newLotObject.put("order", lot.order);
							newLotsArray.add(newLotObject);
						}
					}

					newItemObject.set("lots", newLotsArray);
					newItemsArray.add(newItemObject);

				}

				newDeductionObject.set("items", newItemsArray);
				newObject.set("deduction", newDeductionArray);
			}
			// }
			// create new JSON object

			// ArrayNode newDeductionArray = objectMapper.createArrayNode();

			// Convert the new JSON structure to a String
			newJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newObject);
			System.out.println(newJsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newJsonString;
	}

	private static String convertMapToJsonString(Map<String, Object> map) throws Exception {
		// Create an ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();
		// Convert the Map to a JSON string
		return objectMapper.writeValueAsString(map);
	}

	public JsonNode convertStringToJson(String jsonString) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readTree(jsonString);
		} catch (Exception e) {
			e.printStackTrace(); // Handle the exception as needed
			return null;
		}
	}
}
