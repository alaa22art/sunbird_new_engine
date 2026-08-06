package com.sunbird.lis.interfaces.middleware.flow_component.astm_to_mylis;

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

import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MachineTypePanel;
import com.sunbird.lis.interfaces.middleware.core.FlowComponent;
import com.sunbird.lis.interfaces.middleware.core.RecipientConf;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.Analysis;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.Container;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.LabOrderMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.CommentRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.QueryRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.TerminationRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.sunbird.lis.interfaces.middleware.util.CodesEnumAll;
import com.sunbird.lis.interfaces.middleware.util.CodesEnumBechman;
import com.sunbird.lis.interfaces.middleware.util.CodesEnumCobas;
import com.sunbird.lis.interfaces.middleware.util.CodesEnumSysmexCS2000;
import com.sunbird.lis.interfaces.middleware.util.MachineTypeEnum;
import com.sunbird.lis.interfaces.service.MachineService;
import com.sunbird.lis.interfaces.service.MachineTypePanelService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class LabToLIS2A2Converter extends FlowComponent<RecipientConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
								.match(LabOrderMsg.class, this::convertAndForwardOrderMsg)
								.match(String.class, this::convertAndForward)
								.match(LabOrderMsg[].class, this::convertAndForwardOrderMsg)

								.build();
	}

	private Config config = null;

	private TerminationRecord terminationRecord;
	private OrderRecord orderRecord;
	private QueryRecord queryRecord;
	private LIS2A2OrderMsg testSelectionMessage;
	private CommentRecord commentRecord;

	private void convertAndForwardOrderMsg(LabOrderMsg LabOrderMsg) {
		config = ConfigFactory.load();
		LIS2A2OrderMsg astmOrderMsg = labOrderMsgToAstmOrderMsg(LabOrderMsg);
		conf.recipient.tell(astmOrderMsg, self());
	}

	private void convertAndForwardOrderMsg(LabOrderMsg[] arrLabOrderMsg) {

	}
	
	
	private void convertAndForward(String strJSON)
	{
		System.out.println(strJSON);

	}

	public String formatDate(String strDate) {
		String formatDateTime = config.getString("local.datetimeformat");
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

	public String getPriorityBechman(String priority) {
		if (priority == "") {
			return "";
		}
		CodesEnumBechman code = CodesEnumBechman.valueOf(priority);
		switch (code) {
			case ROUTINE:
				return CodesEnumBechman.ROUTINE.getValue();
			case STAT:
				return CodesEnumBechman.STAT.getValue();
			default:
				return CodesEnumAll.Others.getValue();
		}
	}

	public String getPriorityArchiticate(String priority) {
		if (priority == "") {
			return "";
		}
		CodesEnumAll code = CodesEnumAll.valueOf(priority);
		switch (code) {
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

	public String getSpecimenDescriptorCobas(String specimen) {
		if (specimen == "") {
			return "";
		}

		CodesEnumCobas code = CodesEnumCobas.valueOf(specimen);

		switch (code) {
			case SERUM:
				return CodesEnumAll.Serum.getValue();
			case PLASMA:
				return CodesEnumAll.Plasma.getValue();
			case URINE:
				return CodesEnumAll.Urine.getValue();
			case CSF:
				return CodesEnumAll.CSF.getValue();
			case BLOOD:
				return CodesEnumAll.Blood.getValue();
			case OTHERS:
				return CodesEnumAll.Others.getValue();
			default:
				return CodesEnumAll.Others.getValue();
		}
	}

	public String getSpecimenDescriptorCobas6000(String specimen) {
		if (specimen == "") {
			return "";
		}
		//		CodesEnumCobas code = CodesEnumCobas.SERUM;
		//		parseValues(specimen, CodesEnumCobas.class);
		//		if (contains(specimen, CodesEnumCobas.class) == true) {
		//			code = CodesEnumCobas.valueOf(specimen);
		//		} else {
		//			code = CodesEnumCobas.OTHERS;
		//		}
		CodesEnumCobas code = CodesEnumCobas.valueOf(specimen);

		switch (code) {
			case SERUM:
				return CodesEnumCobas.SERUM.getValue();
			case PLASMA:
				return CodesEnumCobas.PLASMA.getValue();
			case URINE:
				return CodesEnumCobas.URINE.getValue();
			case CSF:
				return CodesEnumCobas.CSF.getValue();
			case BLOOD:
				return CodesEnumCobas.BLOOD.getValue();
			case OTHERS:
				return CodesEnumCobas.OTHERS.getValue();
			default:
				return CodesEnumAll.Others.getValue();
		}
	}

	public String getSpecimenDescriptorBechman(String specimen) {
		if (specimen == "") {
			return "";
		}
		CodesEnumAll code = CodesEnumAll.valueOf(specimen);
		switch (code) {
			case Serum:
				return CodesEnumAll.Serum.getValue();
			case Plasma:
				return CodesEnumAll.Plasma.getValue();
			case Urine:
				return CodesEnumAll.Urine.getValue();
			case CSF:
				return CodesEnumAll.CSF.getValue();
			case Amniotic:
				return CodesEnumAll.Amniotic.getValue();
			case TimedUrine:
				return CodesEnumAll.TimedUrine.getValue();
			case Blood:
				return CodesEnumAll.Blood.getValue();
			case Cervical:
				return CodesEnumAll.Cervical.getValue();
			case Saliva:
				return CodesEnumAll.Saliva.getValue();
			case Synovial:
				return CodesEnumAll.Synovial.getValue();
			case Urethral:
				return CodesEnumAll.Urethral.getValue();
			case Others:
				return CodesEnumAll.Others.getValue();
			default:
				return CodesEnumAll.Others.getValue();
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

		return null;

	}

	private void resetSegmentions() {
		terminationRecord = new TerminationRecord();
		orderRecord = new OrderRecord();
		testSelectionMessage = new LIS2A2OrderMsg();
		commentRecord = new CommentRecord();
	}
}
