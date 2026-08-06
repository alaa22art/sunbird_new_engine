package com.certacure.lis.interfaces.middleware.flow_component.hl724ServerOverTcp;

import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.CR;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.stream.IntStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.enums.Enums.API_URL_PREFEX;
import com.certacure.lis.interfaces.middleware.enums.Enums.REQUEST_RESULT_TYPE;
import com.certacure.lis.interfaces.middleware.enums.Enums.VALUDATION_RESULT_TYPE;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194archi.AstmE138194ArchiProtocol;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageSourceType;
import com.certacure.lis.interfaces.middleware.util.LowLevelUtils;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.MessageTransactionService;
import com.google.common.base.Strings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.DefaultHapiContext;

public class Hl724ServerOverTcpController extends FlowComponent<Hl724ServerOverTcpControllerConf> {

	private PartialFunction<Object, BoxedUnit> idleState;
	private PartialFunction<Object, BoxedUnit> receivingState;
	private PartialFunction<Object, BoxedUnit> sendingState;

	private final List<String> msgBeingSent = new ArrayList<>();
	private int sendingFrameNumber;
	private final StringBuilder msgBeingReceived = new StringBuilder();
	private HttpUriRequest request = null;
	private String strActivationRequest = "";
	private String strActivationResponse = "";
	private String strActivationRefNumberString = "";
    private MachineService machineService;
    private MessageTransaction messageTransaction;
    private MessageTransactionService messageTransactionService;
    private Machine machine;

	Config config = ConfigFactory.load();
	private String SUFFIX_API_URL_PATH = config.getString("system.certacure.api.suffix.url.path");
	private String SENDING_ACK_FIRST_STRATEGY = config.getString("system.ack.sending.first");
	private String IS_AUTO_ACTIVATE_APPOINTMENT_ENABLED = config.getString("system.appointment.auto.activate.enabled");

	Timer timer;

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return receivingState;
	}

	private Hl724ServerOverTcpController() {
		idleState = getIdleState();
		receivingState = getReceivingState();
		sendingState = getSendingState();
	}

	private PartialFunction<Object, BoxedUnit> getIdleState() {
		return ReceiveBuilder.match(httpRequstTransaction.class, this::goToSendingState)
				.matchAny(__ -> goToReceivingState()).build();
	}

	private void goToSendingState(httpRequstTransaction httpRequstTransactionObj) {
		if (httpRequstTransactionObj.getRequest_result_type() == REQUEST_RESULT_TYPE.FAILED
				|| httpRequstTransactionObj.getValudation_result_type() == VALUDATION_RESULT_TYPE.FAILED) {
			// httpRequstTransactionObj.getMessageTransaction().setIsValidated(false);

			if (SENDING_ACK_FIRST_STRATEGY.equals("0")) {
				sendNAK(httpRequstTransactionObj);
			}

			httpRequstTransactionObj.getMessageTransaction().setIsSuccuss(false);
		} else if (httpRequstTransactionObj.getRequest_result_type() == REQUEST_RESULT_TYPE.SUCCUSS
				|| httpRequstTransactionObj.getValudation_result_type() == VALUDATION_RESULT_TYPE.SUCCUSS) {
			httpRequstTransactionObj.getMessageTransaction().setIsValidated(true);

			if (SENDING_ACK_FIRST_STRATEGY.equals("0")) {
				sendACK(httpRequstTransactionObj);
			}

			httpRequstTransactionObj.getMessageTransaction().setIsSent(true);
			httpRequstTransactionObj.getMessageTransaction().setIsSuccuss(true);

		}

		MessageTransactionService messageTransactionService = (MessageTransactionService) SpringUtil
				.getBean("MessageTransactionService");

		messageTransactionService.addMessageTransaction(httpRequstTransactionObj.getMessageTransaction().getMachine(),
				httpRequstTransactionObj.getMessageTransaction().getMessageBody(),
				httpRequstTransactionObj.getMessageTransaction().getMessageDirection(),
				httpRequstTransactionObj.getMessageTransaction().getMessageType(),
				httpRequstTransactionObj.getMessageTransaction().getIsSuccuss(),
				httpRequstTransactionObj.getMessageTransaction().getIsValidated(),
				httpRequstTransactionObj.getMessageTransaction().getIsSent(),
				httpRequstTransactionObj.getMessageTransaction().getNotes(),
				httpRequstTransactionObj.getMessageTransaction().getAckmessageText(),
				httpRequstTransactionObj.getMessageTransaction().getMessageControlID(),
	            httpRequstTransactionObj.getMessageTransaction().getPatientID(),
	            httpRequstTransactionObj.getMessageTransaction().getNationalID(),
	            httpRequstTransactionObj.getMessageTransaction().getResponse(),
	            httpRequstTransactionObj.getMessageTransaction().getAdtOperationType());

		activatePharmacyOrder(httpRequstTransactionObj);
		
		if (IS_AUTO_ACTIVATE_APPOINTMENT_ENABLED.equals("1"))
		{
			activateAppointmentVisit(httpRequstTransactionObj);
		}

	}

	private boolean activateVisit(httpRequstTransaction httpRequstTransactionObj) {

		String str = "";
		try {

			// String SUFFIX_API_URL_PATH = "http://192.168.21.114:6607/";
			// String SUFFIX_API_URL_PATH = "http://192.168.21.90:6607/";
			String API_AUTH_KEY = "793f8156-dfca-4693-940a-ac87feca305e";
			String strURLString = null;
			str = "{" + "\r\n" + "\"" + "facility" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{"
					+ "\r\n" + "\"" + "value" + "\"" + ":" + "\""
					+ httpRequstTransactionObj.getScheduleAppointment().getFacility() + "\"" + "\r\n" + "}" + "\r\n"
					+ "}," + "\r\n" + "\"" + "recordedBy" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{"
					+ "\r\n" + "\"" + "value" + "\"" + ":" + "\""
					+ httpRequstTransactionObj.getScheduleAppointment().getRecordedBy() + "\"" + "\r\n" + "}" + "\r\n"
					+ "}," + "\r\n" + "\"" + "recordedDate" + "\"" + ":" + "\""
					+ getDateTimeISO8601(httpRequstTransactionObj.getScheduleAppointment().getRecordedDate()) + "\""
					+ "\r\n" + "}";

			System.out.println(str);
			strURLString = SUFFIX_API_URL_PATH + String.format(API_URL_PREFEX.APPOINTMENT_VISIT_ACTIVATE.getValue(),
					httpRequstTransactionObj.getScheduleAppointment().getAppointmentId());
			
			httpRequstTransactionObj.getScheduleAppointment().setMessageBody(str);
			
		
			try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

				request = RequestBuilder.post().setUri(strURLString)

						.setHeader("API-KEY", API_AUTH_KEY)
						.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
						.setHeader(HttpHeaders.ACCEPT, "application/json").setEntity(new StringEntity(str, "UTF-8"))

						.build();

				CloseableHttpResponse response = httpclient.execute(request);

				System.out.println(response);

				HttpEntity entity = response.getEntity();
				response.setEntity(entity);
				String responseString = EntityUtils.toString(entity, "UTF-8");
				System.out.println("ReasonPhrase: " + response.getStatusLine().getReasonPhrase().toString());
				System.out.println("Error Responce : " + responseString);
				System.out.println("Status code: " + response.getStatusLine().getStatusCode());

				System.out.println("/////////////////////////////////////////////////////////////////////////");
				System.out.println("/////////////////////////////////////////////////////////////////////////");

				strActivationResponse = responseString;

				if (response.getStatusLine().getStatusCode() == 200) 
				{
					
					
					MessageTransactionService messageTransactionService = (MessageTransactionService) SpringUtil
							.getBean("MessageTransactionService");
					
					machine = getMachineInfoByPath();
					
					messageTransaction = messageTransactionService.addMessageTransaction(
							machine
							,str.toString()
	        				,MessageDirection.OUT.toString()
	        				, MessageSourceType.ACTIVATE_APPOINTMRNT.toString()
	        				, MessageSourceType.ACTIVATE_APPOINTMRNT.getValue()
	        				,true, true,true
	        				,responseString.toString() 
	        				,httpRequstTransactionObj.getMessageTransaction().getMessageControlID());
					
					return true;
				} else {
					return false;
				}

			}

		} catch (Exception e) {
			return false;
		}

	}

	public String getDateTimeISO8601(String strDateTime) throws Exception {

		int indexOfDash = strDateTime.indexOf('-');

		if (indexOfDash != -1) {

			strDateTime = strDateTime.substring(0, indexOfDash);
		}

		if (strDateTime.length() < 15) {
			String str = Strings.padEnd(strDateTime, 14, '0');
			strDateTime = str;
			System.out.println(strDateTime);
		}
		if (strDateTime.length() > 14) {
			System.out.println("data time length more than 14");
			System.out.println(strDateTime.substring(0, 14));
			strDateTime = strDateTime.substring(0, 14);

		}
		checkTimeFormat(strDateTime);
		String strDate = strDateTime;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime localDate = LocalDateTime.parse(strDate, formatter);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("UTC"));
		return localDate.format(formatter2);
	}

	private void activateAppointmentVisit(httpRequstTransaction httpRequstTransactionObj) {
		try {
			boolean isActivated = false;

			if (httpRequstTransactionObj.getRequest_result_type().equals(REQUEST_RESULT_TYPE.SUCCUSS)
					&& httpRequstTransactionObj.getMSG_Type().equals("SIU_S12")) {

				if (isSameDayOrder(httpRequstTransactionObj.getScheduleAppointment().getAppointmentDate())) {

					isActivated = activateVisit(httpRequstTransactionObj);
					if (isActivated) {
						httpRequstTransactionObj.getMessageTransaction().setIsSuccuss(true);
						httpRequstTransactionObj.getMessageTransaction().setIsSent(true);

					} else {
						httpRequstTransactionObj.getMessageTransaction().setIsSuccuss(false);
						httpRequstTransactionObj.getMessageTransaction().setIsSent(true);

					}

					httpRequstTransactionObj.getScheduleAppointment().setNotes(strActivationResponse);
				
					//httpRequstTransactionObj.getMessageTransaction().setNotes(strActivationResponse);
				} else {
					strActivationResponse = "APPOINTMENT ["
							+ httpRequstTransactionObj.getScheduleAppointment().getAppointmentId() +"] WITH DATE ["
							+ httpRequstTransactionObj.getScheduleAppointment().getAppointmentDate()
							+ "] IS NOT APPLICABLE TO ACTIVEATE , ACTIVATION FAILED!!! ," + strActivationResponse;
					httpRequstTransactionObj.getMessageTransaction().setIsSuccuss(false);

					httpRequstTransactionObj.getMessageTransaction().setNotes(strActivationResponse);
					
					strActivationResponse = "";
				}

				MessageTransactionService messageTransactionService = (MessageTransactionService) SpringUtil
						.getBean("MessageTransactionService");

				messageTransactionService.addMessageTransaction(
						httpRequstTransactionObj.getMessageTransaction().getMachine(),
						httpRequstTransactionObj.getMessageTransaction().getMessageBody(),
						httpRequstTransactionObj.getMessageTransaction().getMessageDirection(),
						httpRequstTransactionObj.getMessageTransaction().getMessageType(),
						httpRequstTransactionObj.getMessageTransaction().getIsSuccuss(),
						httpRequstTransactionObj.getMessageTransaction().getIsValidated(),
						httpRequstTransactionObj.getMessageTransaction().getIsSent(),
						httpRequstTransactionObj.getMessageTransaction().getNotes(),
						httpRequstTransactionObj.getMessageTransaction().getAckmessageText(),
						httpRequstTransactionObj.getMessageTransaction().getMessageControlID());

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	}

	private void sendACK(httpRequstTransaction httpRequstTransactionObj) {

		try {

			// MSH|~^\&|myCare|KHCC|VistA|KHCC|20231224104441||ACK|A0175447953|P|2.4|
			// MSA|AA|0175447953|success|

			String ACK = "MSH|~^\\&|myCare" + "|KHCC|" + "VistA" + "|KHCC|" + getCurrentLocalDateTimeStamp() + "||"
					+ "ACK" + "|" + httpRequstTransactionObj.getMessageID() // :
					+ "|" + "|" + "2.4|" + "\r" + "MSA|" + "AA|" + httpRequstTransactionObj.getMessageID() + "|success";

			ACK = LowLevelUtils.VT + ACK + LowLevelUtils.CR + LowLevelUtils.FS + LowLevelUtils.CR;

			conf.lowLevelRecipient.tell(new BytesMessage(ACK), self());
			System.out.println(ACK);
			httpRequstTransactionObj.getMessageTransaction().setAckMessageText(ACK);

		} catch (Exception e) {
			httpRequstTransactionObj.getMessageTransaction().setIsSuccuss(false);
			
			 messageTransactionService.addMessageTransaction(
	                    httpRequstTransactionObj.getMessageTransaction().getMachine(),
	                    httpRequstTransactionObj.getMessageTransaction().getMessageBody(),
	                    httpRequstTransactionObj.getMessageTransaction().getMessageDirection(),
	                    httpRequstTransactionObj.getMessageTransaction().getMessageType(),
	                    httpRequstTransactionObj.getMessageTransaction().getIsSuccuss(),
	                    httpRequstTransactionObj.getMessageTransaction().getIsValidated(),
	                    httpRequstTransactionObj.getMessageTransaction().getIsSent(),
	                    httpRequstTransactionObj.getMessageTransaction().getNotes(),
	                    httpRequstTransactionObj.getMessageTransaction().getAckmessageText(),
	                    httpRequstTransactionObj.getMessageTransaction().getMessageControlID());

			
			
		} finally

		{
			MessageTransactionService messageTransactionService = (MessageTransactionService) SpringUtil
					.getBean("MessageTransactionService");

			/*messageTransactionService.addMessageTransaction(
					httpRequstTransactionObj.getMessageTransaction().getMachine(),
					httpRequstTransactionObj.getMessageTransaction().getMessageBody(),
					httpRequstTransactionObj.getMessageTransaction().getMessageDirection(),
					httpRequstTransactionObj.getMessageTransaction().getMessageType(),
					httpRequstTransactionObj.getMessageTransaction().getIsSuccuss(),
					httpRequstTransactionObj.getMessageTransaction().getIsValidated(),
					httpRequstTransactionObj.getMessageTransaction().getIsSent(),
					httpRequstTransactionObj.getMessageTransaction().getNotes(),
					httpRequstTransactionObj.getMessageTransaction().getAckmessageText(),
					httpRequstTransactionObj.getMessageTransaction().getMessageControlID());*/

		}

	}

	private void sendACK(String strMsgControlId, httpRequstTransaction httpRequstTransactionObj) {

		try {

			// MSH|~^\&|myCare|KHCC|VistA|KHCC|20231224104441||ACK|A0175447953|P|2.4|
			// MSA|AA|0175447953|success|

			String ACK = "MSH|~^\\&|myCare" + "|KHCC|" + "VistA" + "|KHCC|" + getCurrentLocalDateTimeStamp() + "||"
					+ "ACK" + "|" + strMsgControlId// :
					+ "|" + "|" + "2.4|" + "\r" + "MSA|" + "AA|" + strMsgControlId + "|success";

			ACK = LowLevelUtils.VT + ACK + LowLevelUtils.CR + LowLevelUtils.FS + LowLevelUtils.CR;

			conf.lowLevelRecipient.tell(new BytesMessage(ACK), self());
			System.out.println(ACK);
			httpRequstTransactionObj.getMessageTransaction().setAckMessageText(ACK);

		} catch (Exception e) {
			// httpRequstTransactionObj.getMessageTransaction().setIsSuccuss(false);
		} finally

		{
			/*
			 * MessageTransactionService messageTransactionService =
			 * (MessageTransactionService) SpringUtil .getBean("MessageTransactionService");
			 * 
			 * messageTransactionService.addMessageTransaction(
			 * httpRequstTransactionObj.getMessageTransaction().getMachine(),
			 * httpRequstTransactionObj.getMessageTransaction().getMessageBody(),
			 * httpRequstTransactionObj.getMessageTransaction().getMessageDirection(),
			 * httpRequstTransactionObj.getMessageTransaction().getMessageType(),
			 * httpRequstTransactionObj.getMessageTransaction().getIsSuccuss(),
			 * httpRequstTransactionObj.getMessageTransaction().getIsValidated(),
			 * httpRequstTransactionObj.getMessageTransaction().getIsSent(),
			 * httpRequstTransactionObj.getMessageTransaction().getNotes(),
			 * httpRequstTransactionObj.getMessageTransaction().getAckmessageText(),
			 * httpRequstTransactionObj.getMessageTransaction().getMessageControlID());
			 */

		}

	}

	private void activatePharmacyOrder(httpRequstTransaction httpRequstTransactionObj) {

		try {

			boolean isActivated;
			if (httpRequstTransactionObj.getRequest_result_type().equals(REQUEST_RESULT_TYPE.SUCCUSS)
					&& httpRequstTransactionObj.getMSG_Type().equals("DFT_P03")
					&& (httpRequstTransactionObj.getPostDetailFinancialTransaction().getServSection().equals("PSJ")
							|| httpRequstTransactionObj.getPostDetailFinancialTransaction().getServSection()
									.equals("PSO"))) {
				if (!httpRequstTransactionObj.getPostDetailFinancialTransaction().getTransactionType().equals("DC")
						|| !httpRequstTransactionObj.getPostDetailFinancialTransaction().getTransactionType()
								.equals("CR")
						|| !httpRequstTransactionObj.getPostDetailFinancialTransaction().getTransactionType()
								.equals("CO"))

				{ // Check if order date is today

					if (isSameDayOrder(
							httpRequstTransactionObj.getPostDetailFinancialTransaction().getTransactionDate())) {
						isActivated = activatePendingOrtder(
								httpRequstTransactionObj.getPostDetailFinancialTransaction().getServSection(),
								httpRequstTransactionObj.getPostDetailFinancialTransaction().getOrderByDoctorId(),
								httpRequstTransactionObj.getPostDetailFinancialTransaction().getRCMOrderActionID());
						if (isActivated) {
							httpRequstTransactionObj.getMessageTransaction().setIsSuccuss(true);
						} else {
							httpRequstTransactionObj.getMessageTransaction().setIsSuccuss(false);
						}

					} else {
						strActivationResponse = "PHARMACY ORDER ["
								+ httpRequstTransactionObj.getPostDetailFinancialTransaction().getServSection() + ","
								+ httpRequstTransactionObj.getPostDetailFinancialTransaction().getRCMOrderActionID()
								+ "] WITH DATE ["
								+ httpRequstTransactionObj.getPostDetailFinancialTransaction().getTransactionDate()
								+ "] IS NOT APPLICABLE TO ACTIVEATE , ACTIVATION FAILED!!!";
						httpRequstTransactionObj.getMessageTransaction().setIsSuccuss(false);
					}
				}

				MessageTransactionService messageTransactionService = (MessageTransactionService) SpringUtil
						.getBean("MessageTransactionService");

				messageTransactionService.addMessageTransaction(
						httpRequstTransactionObj.getMessageTransaction().getMachine(), strActivationRequest,
						httpRequstTransactionObj.getMessageTransaction().getMessageDirection(),
						httpRequstTransactionObj.getMessageTransaction().getMessageType(),
						httpRequstTransactionObj.getMessageTransaction().getIsSuccuss(),
						httpRequstTransactionObj.getMessageTransaction().getIsValidated(),
						httpRequstTransactionObj.getMessageTransaction().getIsSent(), strActivationResponse,
						httpRequstTransactionObj.getMessageTransaction().getAckmessageText(),
						strActivationRefNumberString);

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private boolean activatePendingOrtder(String servSection, String strOrderByDoctorID, String certa_order_action_id)
			throws IOException {

		// String SUFFIX_API_URL_PATH = "http://192.168.21.90:6607/";
		// String SUFFIX_API_URL_PATH = "http://192.168.21.114:6607/";
		String API_AUTH_KEY = "793f8156-dfca-4693-940a-ac87feca305e";
		String strURLString = null;
		String strRequeString = "{ " + "\"" + "facility" + "\"" + " : {" + "\"" + "code" + "\"" + " : " + "{" + "\""
				+ "value" + "\"" + " : " + "\"" + "KHCC" + "\"" + "}" + "} , " + "\"" + "recordedBy" + "\"" + ": {"
				+ "\"" + "code" + "\"" + " : {" + "\"" + "value" + "\"" + " : " + "\"" + strOrderByDoctorID + "\"" + "}"
				+ "}," + "\"" + "recordedDate" + "\"" + ":" + "\"" + getCurrentDateTime() + "\"" + "}";

		strActivationRequest = strRequeString;

		strActivationRefNumberString = servSection + " - " + certa_order_action_id;

		if (servSection.equals("PSJ") || servSection.equals("PSO")) {

			// strURLString = SUFFIX_API_URL_PATH +
			// "api/PharmacyOrderItems/157/ActivatePending";

			strURLString = SUFFIX_API_URL_PATH
					+ String.format(API_URL_PREFEX.DETAIL_FINANCIAL_TRANSACTION_PHARMACY_ACTIVATE_ORDER.getValue(),
							certa_order_action_id);

		}

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			request = RequestBuilder.post().setUri(strURLString)

					.setHeader("API-KEY", API_AUTH_KEY)
					.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
					.setHeader(HttpHeaders.ACCEPT, "application/json")
					.setEntity(new StringEntity(strRequeString, "UTF-8"))

					.build();

			CloseableHttpResponse response = httpclient.execute(request);

			System.out.println(response);

			HttpEntity entity = response.getEntity();
			response.setEntity(entity);
			String responseString = EntityUtils.toString(entity, "UTF-8");
			System.out.println("ReasonPhrase: " + response.getStatusLine().getReasonPhrase().toString());
			System.out.println("Error Responce : " + responseString);
			System.out.println("Status code: " + response.getStatusLine().getStatusCode());

			System.out.println("/////////////////////////////////////////////////////////////////////////");
			System.out.println("/////////////////////////////////////////////////////////////////////////");

			strActivationResponse = responseString;

			if (response.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
				return false;
			}

		}

	}

	public String getCurrentDateTime() {

		DateTimeFormatter currentDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(currentDateTime.format(now));

		return currentDateTime.format(now);

	}

	/*private boolean isSameDayOrder(String strTransactionDate) throws Exception {

		boolean sameDay = false;

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(getDateTime(strTransactionDate));

		sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
				&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);

		return sameDay;

	}*/
	
	/*private boolean isSameDayOrder(String strTransactionDate) throws Exception {
        boolean sameDay= false;
        DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now= LocalDateTime.now();
        LocalDateTime visitDate= LocalDateTime.parse("20240228223001", dtf);
        System.out.println(dtf.format(now));
        System.out.println(dtf.format(visitDate));
        System.out.println("now day of month" + now.getDayOfMonth() + "now month" +
            now.getMonthValue() + "now year" + now.getYear());
        System.out.println("visitDate day of month" + visitDate.getDayOfMonth() +
            "visitDate month" + visitDate.getMonthValue() + "visitDate year" + visitDate.getYear());
        sameDay= now.getDayOfMonth() == visitDate.getDayOfMonth() &&
            now.getMonthValue() == visitDate.getMonthValue() &&
            now.getYear() == visitDate.getYear();
        return sameDay;
    }*/
	
	private boolean isSameDayOrder(String strTransactionDate) throws Exception 
	{
		
		 DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		 // Create two LocalDate objects representing dates
		// Parse the string into a LocalDateTime object
         LocalDateTime dat1 = LocalDateTime.parse(strTransactionDate, dtf);
         LocalDateTime dat2 = LocalDateTime.now();
         
			if ((dat1.getYear() == dat2.getYear()) && (dat1.getMonth() == dat2.getMonth())
					&& (dat1.getDayOfMonth() == dat2.getDayOfMonth()))
         {
        	 return true;
         }else {
			return false;
		}
         
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

	private void goToReceivingState() {
		log.debug("Transitioning to receiving state");
		// msgBeingReceived.setLength(0);
		// conf.lowLevelRecipient.tell(ACKBytes, self());
		context().become(receivingState);
	}

	private PartialFunction<Object, BoxedUnit> getSendingState() {
		return ReceiveBuilder.matchEquals(AstmE138194ArchiProtocol.ACKBytes, __ -> {
			if (msgBeingSent.isEmpty()) {
				conf.lowLevelRecipient.tell(AstmE138194ArchiProtocol.EOTBytes, self());
				goToIdleState();
			} else {
				// sendNextFrameInBuffer();
			}
		}).matchAny(__ -> stash()).build();
	}

	private PartialFunction<Object, BoxedUnit> getReceivingState() {
		return ReceiveBuilder.match(BytesMessage.class, bytesMessage -> {
			System.out.println("Data Arrive >>>>>>>>>>>>>>>>>>" + extractPayload(bytesMessage.bytes));
			msgBeingReceived.append(extractPayload(bytesMessage.bytes));
			changeStateAndProcessCompletedMessage();
		}).match(httpRequstTransaction.class, this::goToSendingState)

				.matchAny(__ -> {
					stash();
				}).build();
	}

	private void sendNAK(httpRequstTransaction httpRequstTransactionObj) {
		// MSH|~^\&|myCare||KHCC|VistA|20231224133929|ACK|0240473023|D|2.4
		// MSA|AE|0240473023||||207
		// ERR|Error:400:."title":"One.or.more.validation.errors.occurred.":."status":400

		// DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		String strMsgID = httpRequstTransactionObj.getMessageID();

		if (strMsgID == null) {
			strMsgID = "";
		}
		String NAK = "MSH|~^\\&|myCare"
				/// httpRequstTransactionObj.getSendingFacility() != null ?
				// httpRequstTransactionObj.getSendingFacility() +
				+ "|KHCC|"
				// httpRequstTransactionObj.getReceivingFacility() != null ?
				// httpRequstTransactionObj.getReceivingFacility() // :
				+ "VistA" + "|KHCC|" +
				// httpRequstTransactionObj.getResponseDateTime() != null ?
				// httpRequstTransactionObj.getResponseDateTime()// :
				// "KHCC" + "|"+
				getCurrentLocalDateTimeStamp() + "||"
				// httpRequstTransactionObj.getACKMSG_Type() != null ?
				+ "ACK"// httpRequstTransactionObj.getACKMSG_Type()// :
				+ "|" +
				// httpRequstTransactionObj.getMessageID() != null ?
				httpRequstTransactionObj.getMessageID() // :
				+ "|" + "|" + "2.4" + "\r" + "MSA" + "|" + "AE" + "|" + strMsgID + "||||" + "ERROR" +
				// httpRequstTransactionObj.getMSA6_Error_Condition() +
				"\r" + "ERR|" + "CHECK TRANS LOG";
		// httpRequstTransactionObj
		// .getErrorDesc().replaceAll(" ", ".");*/

		NAK = LowLevelUtils.VT + NAK + LowLevelUtils.CR + LowLevelUtils.FS + LowLevelUtils.CR;

		conf.lowLevelRecipient.tell(new BytesMessage(NAK), self());
		System.out.println(httpRequstTransactionObj.getErrorDesc());
		System.out.println(NAK);
		httpRequstTransactionObj.getMessageTransaction().setAckMessageText(NAK);

		System.out.println("----------------------------Dead End-----------------------------------------");
	}

	private void changeStateAndProcessCompletedMessage() {
		httpRequstTransaction httpRequstTransactionObj = new httpRequstTransaction();
		httpRequstTransactionObj.setHL7MsgText(msgBeingReceived.toString());

		// goToIdleState();
		if (SENDING_ACK_FIRST_STRATEGY.equals("1")) {
			sendResponseToSource(msgBeingReceived.toString(), httpRequstTransactionObj);
		} else {
			conf.highLevelRecipient.tell(msgBeingReceived.toString(), self());
		}
		msgBeingReceived.setLength(0);
		// goToIdleState();
	}

	private void sendResponseToSource(String strMsg, httpRequstTransaction httpRequstTransaction) {
		try {

			httpRequstTransaction.setHL7MsgText(strMsg);
			String strMessageControlId = getMessageControlId(strMsg);

			if ((strMsg.indexOf(LowLevelUtils.VT) != -1) && (strMsg.indexOf(LowLevelUtils.FS) != -1)
					&& strMsg.contains("MSH")) {
				sendACK(strMessageControlId, httpRequstTransaction);
				conf.highLevelRecipient.tell(httpRequstTransaction, self());
			} else {
				sendNAK(strMessageControlId , httpRequstTransaction);
			}

		} catch (Exception e) {
			//sendNAK("000000" , httpRequstTransaction);
		} finally

		{
			/*
			 * MessageTransactionService messageTransactionService =
			 * (MessageTransactionService) SpringUtil .getBean("MessageTransactionService");
			 * 
			 * messageTransactionService.addMessageTransaction(
			 * httpRequstTransactionObj.getMessageTransaction().getMachine(),
			 * httpRequstTransactionObj.getMessageTransaction().getMessageBody(),
			 * httpRequstTransactionObj.getMessageTransaction().getMessageDirection(),
			 * httpRequstTransactionObj.getMessageTransaction().getMessageType(),
			 * httpRequstTransactionObj.getMessageTransaction().getIsSuccuss(),
			 * httpRequstTransactionObj.getMessageTransaction().getIsValidated(),
			 * httpRequstTransactionObj.getMessageTransaction().getIsSent(),
			 * httpRequstTransactionObj.getMessageTransaction().getNotes(),
			 * httpRequstTransactionObj.getMessageTransaction().getAckmessageText(),
			 * httpRequstTransactionObj.getMessageTransaction().getMessageControlID());
			 */

		}

	}

	private void sendNAK(String strMessageControlId, httpRequstTransaction httpRequstTransactionObj) {
		// MSH|~^\&|myCare||KHCC|VistA|20231224133929|ACK|0240473023|D|2.4
		// MSA|AE|0240473023||||207
		// ERR|Error:400:."title":"One.or.more.validation.errors.occurred.":."status":400

		// DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		String strMsgID = strMessageControlId;
		machine= machineService.getMachineByActorPath(getContext().parent().toString());
		if (strMsgID == null) {
			strMsgID = "";
		}
		String NAK = "MSH|~^\\&|myCare"
				/// httpRequstTransactionObj.getSendingFacility() != null ?
				// httpRequstTransactionObj.getSendingFacility() +
				+ "|KHCC|"
				// httpRequstTransactionObj.getReceivingFacility() != null ?
				// httpRequstTransactionObj.getReceivingFacility() // :
				+ "VistA" + "|KHCC|" +
				// httpRequstTransactionObj.getResponseDateTime() != null ?
				// httpRequstTransactionObj.getResponseDateTime()// :
				// "KHCC" + "|"+
				getCurrentLocalDateTimeStamp() + "||"
				// httpRequstTransactionObj.getACKMSG_Type() != null ?
				+ "ACK"// httpRequstTransactionObj.getACKMSG_Type()// :
				+ "|" +
				// httpRequstTransactionObj.getMessageID() != null ?
				strMsgID // :
				+ "|" + "|" + "2.4" + "\r" + "MSA" + "|" + "AE" + "|" + strMsgID + "||||" + "ERROR" +
				// httpRequstTransactionObj.getMSA6_Error_Condition() +
				"\r" + "ERR|" + "CHECK TRANS LOG";
		// httpRequstTransactionObj
		// .getErrorDesc().replaceAll(" ", ".");*/

		NAK = LowLevelUtils.VT + NAK + LowLevelUtils.CR + LowLevelUtils.FS + LowLevelUtils.CR;

		// conf.lowLevelRecipient.tell(new BytesMessage(NAK), self());
		System.out.println("----------------------------Dead End-----------------------------------------");
		httpRequstTransactionObj.getMessageTransaction().setAckMessageText(NAK);
		  messageTransaction = messageTransactionService.addMessageTransaction(machine,
	                NAK,
	                MessageDirection.IN.toString(), MessageSourceType.HL7.toString(),MessageSourceType.HL7.getValue(), false, false,false,
	                "Arrive new Message" , strMessageControlId);
		  httpRequstTransactionObj.setMessageTransaction(messageTransaction);
		

	}

	private String getMessageControlId(String strMsg) {
		String messageControllId = "";
		Class<? extends LIS2A2Msg> msgType;
		List<LIS2A2Record> records = new ArrayList<>();
		String[] recordLines = strMsg.split("(?<=" + CR + ")");
		for (String record : recordLines) {

			if (record.length() < 3) {
				continue;

			}

			log.debug("Converting String '" + record + "' into LIS2A2Record");
			LIS2A2Record lis2a2Record = LIS2A2Record.fromString(record);

			messageControllId = lis2a2Record.getFieldValue(9);
			records.clear();
			break;
		}

		return messageControllId;
	}

	private void goToIdleState() {
		log.debug("Transitioning to idle state");
		unstashAll();
		context().become(idleState);
	}

	private String extractPayload(List<Byte> frameBytes) {

		// List<Byte> payloadBytes = frameBytes.subList(2, index);
		List<Byte> payloadBytes = frameBytes;
		byte[] bytes = new byte[payloadBytes.size()];
		IntStream.range(0, payloadBytes.size()).forEach(i -> bytes[i] = payloadBytes.get(i));
		return new String(bytes);
	}
	
	 public Machine getMachineInfoByPath() {
	        MachineService machineService= (MachineService) SpringUtil.getBean("MachineService");
	        Machine machine= machineService.getMachineByActorPath(getContext().parent().toString());
	        return machine;
	    }


}