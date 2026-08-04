package com.certacure.lis.interfaces.middleware.flow_component.hl7_23_vidas;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Analysis;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.LabOrderMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.CommentRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HeaderRecordAstm;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.TerminationRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.PatientASTMRecord;
import com.certacure.lis.interfaces.middleware.util.CodesEnumAll;
import com.certacure.lis.interfaces.middleware.util.CodesEnumCobas;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class HL723VidassLabToLIS2A2Converter extends FlowComponent<RecipientConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
								.match(LabOrderMsg.class, this::convertAndForwardOrderMsg)
								.build();
	}

	private Config config = null;

	private TerminationRecord terminationRecord;
	private LIS2A2OrderMsg testSelectionMessage;
	
	private void convertAndForwardOrderMsg(LabOrderMsg LabOrderMsg) {
		config = ConfigFactory.load();
	
			LIS2A2OrderMsg astmOrderMsg = labOrderMsgToAstmOrderMsg(LabOrderMsg);
			conf.recipient.tell(astmOrderMsg, self());
		
	}

	public String formatDate(String strDate) {
		String machineDateFormat = config.getString("machine.datetimeformat");
		DateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		Date date = null;
		String formattedDate = "";
		SimpleDateFormat simpleFormat = new SimpleDateFormat(machineDateFormat);
		try {
			date = dateFormat.parse(strDate);
			formattedDate = simpleFormat.format(date.getTime());
		} catch (

		ParseException e) {
			e.printStackTrace();
		}
		return formattedDate;
	}


	public String formatDateofBirth(Date strDateOfBirth) {
		String format = config.getString("dob.dateformat");
		String formattedDate = "";
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
		formattedDate = simpleFormat.format(strDateOfBirth.getTime());
		return formattedDate;
	}

	public String formatDataNow() {
		LocalDate nowDate = LocalDate.now();
		String format = config.getString("dob.dateformat");
		String formattedDate = "";
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
		formattedDate = simpleFormat.format(nowDate);
		return formattedDate;
	}

	public String formatDatatimeNow() {
		Date inDate = new Date();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(inDate.toInstant(), ZoneId.systemDefault());
		Date outDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		String localDateFormat = config.getString("local.datetimeformat");
		DateFormat format = new SimpleDateFormat(localDateFormat, Locale.ENGLISH);
		String formattedDate = "";
		formattedDate = format.format(outDate.getTime());
		String machineDateFormat = config.getString("machine.datetimeformat");
		SimpleDateFormat newFormat = new SimpleDateFormat(machineDateFormat);
		formattedDate = newFormat.format(outDate.getTime());
		return formattedDate.toString();
	}

	public String formatDatatimeNowShort() {
		Date inDate = new Date();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(inDate.toInstant(), ZoneId.systemDefault());
		Date outDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		String localDateFormat = config.getString("local.datetimeformat");
		DateFormat format = new SimpleDateFormat(localDateFormat, Locale.ENGLISH);
		String formattedDate = "";
		formattedDate = format.format(outDate.getTime());
		String machineDateFormat = config.getString("machine.datetimeformatNoSec");
		SimpleDateFormat newFormat = new SimpleDateFormat(machineDateFormat);
		formattedDate = newFormat.format(outDate.getTime());
		return formattedDate.toString();
	}

	public String getAge(Date strDateOfBirth) {
		int age = 0;
		String ageAndUnit = "";
		Date date = null;
		date = strDateOfBirth;
		ZoneId defaultZoneId = ZoneId.systemDefault();
		Instant instant = date.toInstant();
		LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
		LocalDate curDate = LocalDate.now();
		age = Period.between(localDate, curDate).getYears();
		if (age == 0) {
			age = Period.between(localDate, curDate).getMonths();
			if (age == 0) {
				age = Period.between(localDate, curDate).getDays();
				ageAndUnit = String.valueOf(age) + CodesEnumAll.Day.getValue();
			} else {
				ageAndUnit = String.valueOf(age) + CodesEnumAll.Month.getValue();
			}
		} else {
			ageAndUnit = String.valueOf(age) + CodesEnumAll.Year.getValue();
		}
		return ageAndUnit;
	}

	public String getGender(String gender) {
		if (gender == "") {
			return "";
		}
		CodesEnumAll code = CodesEnumAll.valueOf(gender);
		switch (code) {
			case M:
				return CodesEnumAll.M.getValue();
			case F:
				return CodesEnumAll.F.getValue();
			case U:
				return CodesEnumAll.U.getValue();
			default:
				return "";
		}
	}

	public String getPriority(String priority) {
		if (null == priority || "".equals(priority)) {
			return "";
		}
		CodesEnumAll code = CodesEnumAll.valueOf(priority);
		switch (code) {
			case ROUTINE:
				return CodesEnumAll.ROUTINE.getValue();
			case STAT:
				return CodesEnumAll.STAT.getValue();
			default:
				return "";
		}
	}
	
	
	public String getSpecimenDescriptor(String specimen) {
		if (specimen == "") {
			return "";
		}

		CodesEnumCobas code = CodesEnumCobas.valueOf(specimen);
		switch (code) {
			case SERUM:
				return CodesEnumAll.Serum.getValue();
			default:
				return "";
		}
	}

	// Function to perform right padding 
	public static String rightPadding(String input, char ch, int L) {

		String result = String

								// First right pad the string 
								// with space up to length L 
								.format("%" + (L) + "s", input)

								// Then replace all the spaces 
								// with the given character ch 
								.replace(' ', ch);

		// Return the resultant string 
		return result;
	}
	
	

public LIS2A2OrderMsg labOrderMsgToAstmOrderMsg(LabOrderMsg myLabOrderMsg) {

		resetSegmentions();

				testSelectionMessage = testSelectionMessage
															.addRecord(HeaderRecordAstm	.create()
																					.setVersionNumber(
																							CodesEnumAll.VersionNumberSysmexSuit.getValue())
																					.setDateTimeWithoutSecond(new Date()))
															.addRecord(PatientASTMRecord.create(1)
																					.setPracticePatientId(
																							myLabOrderMsg.order.patient.patientId)
																					.setFirstName(myLabOrderMsg.order.patient.firstName)
																					.setSurname(myLabOrderMsg.order.patient.surname)
																					.setGender(
																							getGender(
																									myLabOrderMsg.order.patient.gender))
																					.setBirthDate(formatDateofBirth(
																							myLabOrderMsg.order.patient.dateOfBirth))
																					.setRegistrationDate(
																							formatDateofBirth(new Date())));
				int recordCounter = 1;
				OBRRecord obrRecord = OBRRecord	.create(recordCounter++)
												.setSpecimenId(myLabOrderMsg.order.container.specimenId)
												.addPriorityCode(getPriority(myLabOrderMsg.order.container.priority))
												.setActionCode(CodesEnumAll.ADD.getValue())
												.addRequestDate(formatDatatimeNowShort().toString())
												.addCollectionDate(formatDatatimeNowShort().toString());
				for (Analysis analysis : myLabOrderMsg.order.container.analyses)
					obrRecord = obrRecord.addAnalysis(null, analysis.name, null,
							analysis.code);
				testSelectionMessage = testSelectionMessage.addRecord(obrRecord);

				terminationRecord = (TerminationRecord) TerminationRecord	.create(1)
																			.setTerminationCode("F");

				testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);

		return testSelectionMessage;

	}

	private void resetSegmentions() {
		terminationRecord = new TerminationRecord();
		new OrderRecord();
		testSelectionMessage = new LIS2A2OrderMsg();
		new CommentRecord();
	}
}
