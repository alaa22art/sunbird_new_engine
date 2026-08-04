package com.certacure.lis.interfaces.middleware.flow_component.astm_to_mylis;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MachineTypePanel;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Analysis;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Container;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.LabOrderMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.astm.*;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.astm.LIS2A2OrderASTM;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.CommentRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HeaderRecordAstm;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.QueryRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.TerminationRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.HeaderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.OrderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.PatientASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.*;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.certacure.lis.interfaces.middleware.util.CodesEnumAll;
import com.certacure.lis.interfaces.middleware.util.CodesEnumBechman;
import com.certacure.lis.interfaces.middleware.util.CodesEnumCobas;
import com.certacure.lis.interfaces.middleware.util.CodesEnumSysmexCS2000;
import com.certacure.lis.interfaces.middleware.util.MachineTypeEnum;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.MachineTypePanelService;
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

				.build();
	}

	private Config config = null;

	private TerminationRecord terminationRecord;
	private OrderASTMRecord orderASTMRecord;
	private OrderRecord orderRecord;

	private QueryRecord queryRecord;
	private LIS2A2OrderASTM testSelectionMessage;
	private LIS2A2OrderMsg testSelectionHL7Message;
	private CommentRecord commentRecord;
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private Date date = new Date();

	/*private void convertAndForwardOrderMsg(LabOrderMsg LabOrderMsg) {
		config = ConfigFactory.load();

		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());

		
			LIS2A2OrderASTM astmOrderMsg = labOrderMsgToAstmOrderMsg(LabOrderMsg, machine);
			conf.recipient.tell(astmOrderMsg, self());
		

	}*/
	
	private void convertAndForwardOrderMsg(LabOrderMsg labOrderMsg) {
		config = ConfigFactory.load();
		
		List<Analysis> lstAnalysis =  labOrderMsg.getOrder().container.analyses;

		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
		LIS2A2OrderMsg lis2OrderMsg ;
		
		switch(machineType)
		{
		case ROCHE_COBAS_PURE:
		case ROCHE_COBAS_PRO_HL7_251:
			
				lis2OrderMsg = labOrderMsgToHl7OrderMsg(labOrderMsg, machine);
				conf.recipient.tell(lis2OrderMsg, self());
				
			
	
			break;
			
		case SIEMENS_ATELLICA_HL7_251:
		
			lis2OrderMsg = labOrderMsgToHl7OrderMsg(labOrderMsg, machine);
			conf.recipient.tell(lis2OrderMsg, self());
			break;
			
			default : 
				LIS2A2OrderASTM astmOrderMsg = labOrderMsgToAstmOrderMsg(labOrderMsg, machine);
				conf.recipient.tell(astmOrderMsg, self());
				
				break ;
			
		}
	}

	
	private LIS2A2OrderMsg labEmptyMsgOrder(LabOrderMsg labOrderMsg, Machine machine) {
		// TODO Auto-generated method stub
		return null;
	}


	private LIS2A2OrderMsg labOrderMsgToHl7OrderMsg(LabOrderMsg labOrderMsg, Machine machine) 
	{
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
		terminationRecord = new TerminationRecord();
		testSelectionHL7Message = new LIS2A2OrderMsg();
		int recordCounter = 0;

		
		testSelectionHL7Message = testSelectionHL7Message

					.addRecord(HeaderRecord.create().setSendingApp(labOrderMsg.order.senderName)// 5
							.setReceivingFacility(labOrderMsg.order.machineName)// 10
							.setDateTimeOfMessage(formatDatatimeNow().toString())// 14
							.setVersion(CodesEnumAll.VersionNumber.getValue())
							.setProcessingID(CodesEnumAll.ProccessingID.getValue())
					// .setSpe(CodesEnumCobas.SpecialInstruction.getValue())
					)
					.addRecord(PatientRecord.create(1).setPatientID(
							labOrderMsg.order.patient.patientId != null ? labOrderMsg.order.patient.patientId : "")
							.setDateTimeOfBirth(labOrderMsg.order.patient.dateOfBirth != null
									? formatDateofBirth(labOrderMsg.order.patient.dateOfBirth)
									: new Date().toString())
							.setGender(getGender(
									labOrderMsg.order.patient.gender != null ? labOrderMsg.order.patient.gender : "")));
			recordCounter = 1;
			orderRecord = OrderRecord.create(recordCounter++)
					//.setSpecimenId(rightPadding(labOrderMsg.order.container.specimenId.toString(), ' ', 22)
					.setSpecimenId(labOrderMsg.order.container.specimenId.toString())
					.setSpecimenDetails(labOrderMsg.order.container.queryTag.toString(), 
							labOrderMsg.order.container.queryControlID ,
							labOrderMsg.order.container.specimenRackID , 
							labOrderMsg.order.container.specimenPosition,
							labOrderMsg.order.container.barcodeMode)
					//.setSpecimnPositionInfo(labOrderMsg.order.container.specimenPosition)
					//.setSpecimentRackNumber(labOrderMsg.order.container.specimenRackID)
					.addPriorityCode(getPriority("STAT"))// myLabOrderMsg.order.container.priority
					
					.setActionCode(CodesEnumAll.NEW.getValue())
					
					//labOrderMsg.order.container.equals("Serum")? orderRecord.setSpecimenDescriptor("SER") : orderRecord.setSpecimenDescriptor("WB")
					.setSpecimenDescriptor(labOrderMsg.order.container.specimenDescriptor.equals("Serum")? "SER" : "WB")
					.setReportType(CodesEnumAll.ReportTypeO.getValue())
					.addReciveDatetime(formatDatatimeNow().toString());
					//.setQueryMessageContrlID(labOrderMsg.order.queryMessageContrlID)
					//.setQueryTag(labOrderMsg.order.queryTag);
		
					
			
					
			if (labOrderMsg.order.container.analyses != null) {
				for (Analysis analysis : labOrderMsg.order.container.analyses)
					orderRecord = orderRecord.addAnalysis(analysis.code);
			}
			testSelectionHL7Message = testSelectionHL7Message.addRecord(orderRecord);

			commentRecord = (CommentRecord) CommentRecord.create(1).setComponent(3, 1, "L").setComponent(4, 1,
					/* String.format("%1$-" + 30 + "s", */
					labOrderMsg.order.patient.firstName + "." + labOrderMsg.order.patient.secondName)
					.setComponent(4, 2, "").setComponent(4, 3, "").setComponent(4, 4, "").setComponent(4, 5, "")
					.setComponent(5, 1, "G");
			testSelectionHL7Message = testSelectionHL7Message.addRecord(commentRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("N");

			testSelectionHL7Message = testSelectionHL7Message.addRecord(terminationRecord);
		return testSelectionHL7Message  ;
		
	}
	
	

	private void convertAndForward(String strJSON) {
		System.out.println(strJSON);

	}

	public String getPriority(Machine machine) {

		if (machine.getName().equals("UNL_MGL_ROCHE_COBAS_C311")) {
			return CodesEnumAll.STAT.getValue();
		} else {

			return CodesEnumAll.ROUTINE.getValue();
		}
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
		case WB:
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
		// CodesEnumCobas code = CodesEnumCobas.SERUM;
		// parseValues(specimen, CodesEnumCobas.class);
		// if (contains(specimen, CodesEnumCobas.class) == true) {
		// code = CodesEnumCobas.valueOf(specimen);
		// } else {
		// code = CodesEnumCobas.OTHERS;
		// }
		CodesEnumCobas code = CodesEnumCobas.valueOf(specimen);

		switch (code) {
		case SERUM:
		case Serum:
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

	public LIS2A2OrderASTM labOrderMsgToAstmOrderMsg(LabOrderMsg myLabOrderMsg, Machine machine) {

		resetSegmentions();

		/*
		 * if (myLabOrderMsg.order == null) { return testSelectionMessage; }
		 */

		// MachineService machineService = (MachineService)
		// SpringUtil.getBean("MachineService");
		// Machine machine =
		// machineService.getMachineByActorPath(getContext().parent().toString());
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
		switch (machineType) {

		case ROCHE_COBAS_6000:

			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setSenderId(myLabOrderMsg.order.senderName)// 5
							.setReceiverId(myLabOrderMsg.order.machineName)// 10
							.setDateTime(new Date())// 14
							.setVersionNumber(CodesEnumAll.VersionNumber.getValue())
							.setProcessingId(CodesEnumAll.ProccessingID.getValue())
							.setSpecialInstruction(CodesEnumCobas.SpecialInstruction.getValue()))
					.addRecord(PatientASTMRecord.create(1).setPatientId(
							myLabOrderMsg.order.patient.patientId != null ? myLabOrderMsg.order.patient.patientId : "")
							.setSpecialField1(getAge(myLabOrderMsg.order.patient.dateOfBirth != null
									? myLabOrderMsg.order.patient.dateOfBirth
									: new Date()))
							.setGender(getGender(
									myLabOrderMsg.order.patient.gender != null ? myLabOrderMsg.order.patient.gender
											: ""))
							.setBirthDate(formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth != null
									? myLabOrderMsg.order.patient.dateOfBirth
									: new Date())));
			int recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(rightPadding(myLabOrderMsg.order.container.specimenId.toString(), ' ', 22)

					).setSpecimnPositionInfo(myLabOrderMsg.order.container.specimenPosition)
					.addPriorityCode(getPriority("STAT"))// myLabOrderMsg.order.container.priority
					.setActionCode(CodesEnumAll.NEW.getValue())
					.setSpecimenDescriptor(
							getSpecimenDescriptorCobas6000(myLabOrderMsg.order.container.specimenDescriptor))
					.setReportType(CodesEnumAll.ReportTypeO.getValue())
					.addReciveDatetime(formatDatatimeNow().toString());

			if (myLabOrderMsg.order.container.analyses != null) {
				for (Analysis analysis : myLabOrderMsg.order.container.analyses)
					orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);
			}
			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			commentRecord = (CommentRecord) CommentRecord.create(1).setComponent(3, 1, "L").setComponent(4, 1,
					/* String.format("%1$-" + 30 + "s", */
					myLabOrderMsg.order.patient.firstName + "." + myLabOrderMsg.order.patient.secondName)
					.setComponent(4, 2, "").setComponent(4, 3, "").setComponent(4, 4, "").setComponent(4, 5, "")
					.setComponent(5, 1, "G");
			testSelectionMessage = testSelectionMessage.addRecord(commentRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("N");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);

			break;

		case ROCHE_COBAS_C311:
			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setSenderId(myLabOrderMsg.order.senderName)// 5
							.setReceiverId(myLabOrderMsg.order.machineName)// 10
							.setDateTime(new Date())// 14
							.setVersionNumber(CodesEnumAll.VersionNumber.getValue())
							.setProcessingId(CodesEnumAll.ProccessingID.getValue())
							.setSpecialInstruction(CodesEnumCobas.SpecialInstruction.getValue()))
					.addRecord(PatientASTMRecord.create(1).setPatientId(
							myLabOrderMsg.order.patient.patientId != null ? myLabOrderMsg.order.patient.patientId : "")
							.setSpecialField1(getAge(myLabOrderMsg.order.patient.dateOfBirth != null
									? myLabOrderMsg.order.patient.dateOfBirth
									: new Date()))
							.setGender(getGender(
									myLabOrderMsg.order.patient.gender != null ? myLabOrderMsg.order.patient.gender
											: ""))
							.setBirthDate(formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth != null
									? myLabOrderMsg.order.patient.dateOfBirth
									: new Date())));
			int rCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(rCounter++)
					.setSpecimenId(rightPadding(myLabOrderMsg.order.container.specimenId.toString(), ' ', 22)

					).setSpecimnPositionInfo(myLabOrderMsg.order.container.specimenPosition)

					.addPriorityCode(getPriority(machine))// myLabOrderMsg.order.container.priority

					.setActionCode(CodesEnumAll.ADD.getValue())
					.setSpecimenDescriptor(
							getSpecimenDescriptorCobas6000(myLabOrderMsg.order.container.specimenDescriptor))
					.setReportType(CodesEnumAll.ReportTypeO.getValue())
					.addReciveDatetime(formatDatatimeNow().toString());

			if (myLabOrderMsg.order.container.analyses != null) {
				for (Analysis analysis : myLabOrderMsg.order.container.analyses)
					orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);
			}
			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			commentRecord = (CommentRecord) CommentRecord.create(1).setComponent(3, 1, "L").setComponent(4, 1,
					/* String.format("%1$-" + 30 + "s", */
					myLabOrderMsg.order.patient.firstName + "." + myLabOrderMsg.order.patient.secondName)
					.setComponent(4, 2, "").setComponent(4, 3, "").setComponent(4, 4, "").setComponent(4, 5, "")
					.setComponent(5, 1, "G");
			
		//	testSelectionMessage = testSelectionMessage.addRecord(commentRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("N");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);

			break;

		case ROCHE_COBAS_E411:
			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setSenderId(myLabOrderMsg.order.senderName)
							.setReceiverId(myLabOrderMsg.order.machineName).setDateTime(new Date())
							.setVersionNumber(CodesEnumAll.VersionNumber.getValue())
							.setProcessingId(CodesEnumAll.ProccessingID.getValue())
							.setSpecialInstruction(CodesEnumCobas.SpecialInstruction.getValue()))
					.addRecord(PatientASTMRecord.create(1).setPatientId(myLabOrderMsg.order.patient.patientId)
							.setSpecialField1(getAge(myLabOrderMsg.order.patient.dateOfBirth))
							.setGender(getGender(myLabOrderMsg.order.patient.gender)));
			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId)
					.setSpecimnPositionInfo(myLabOrderMsg.order.container.specimenPosition)
					.addPriorityCode(getPriority(myLabOrderMsg.order.container.priority))
					.setActionCode(CodesEnumAll.NEW.getValue())
					.setSpecimenDescriptor(getSpecimenDescriptorCobas(myLabOrderMsg.order.container.specimenDescriptor))
					.setReportType(CodesEnumAll.ReportTypeO.getValue())
					.addCollectionDate(formatDatatimeNow().toString());
			for (Analysis analysis : myLabOrderMsg.order.container.analyses)
				orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);

			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;

		case BECHMAN_ACCESS_200_DXI:
			testSelectionMessage = testSelectionMessage.addRecord(HeaderASTMRecord.create()
					// H|\^&|||Access|||||LIS||P|1|19970901085833
					// .setSenderId("Aculink-LIS")
					// .setReceiverId("Access")
					// .setVersionNumber(
					// CodesEnumAll.VersionNumber.getValue())
					// .setProcessingId(
					// CodesEnumAll.ProccessingID.getValue(),
					// 12)
					.setDateTime(new Date()))

					.addRecord(PatientASTMRecord.create(1).setPatientId(myLabOrderMsg.order.patient.patientId, 3));
			// .setAttendingPhysician(myLabOrderMsg.order.patient.doctorName));
			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId)
					.setSpecimnPositionInfo(myLabOrderMsg.order.container.specimenPosition)
					.addPriorityCode(getPriority(myLabOrderMsg.order.container.priority))
					.addCollectionDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
					.setActionCode(CodesEnumAll.ADD.getValue())
					.setSpecimenDescriptor(getSpecimenDescriptor(myLabOrderMsg.order.container.specimenDescriptor));
			// .setReportType(CodesEnumAll.NULL.getValue());

			for (Analysis analysis : myLabOrderMsg.order.container.analyses)
				orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);
			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);

			break;

		case ABBOTT_ARCHITECT_CI4100:
		case ABBOTT_ALINITY_CI:

			// check if it is not empty TS
			if (!myLabOrderMsg.order.container.analyses.isEmpty()) {
				testSelectionMessage = testSelectionMessage.addRecord(HeaderASTMRecord.create()
						// .setSenderId(myLabOrderMsg.order.machineName)
						.setVersionNumber(CodesEnumAll.VersionNumber.getValue())
						.setProcessingId(CodesEnumAll.ProccessingID.getValue()).setDateTime(new Date()))
						.addRecord(PatientASTMRecord.create(1)
								.setPatientId(myLabOrderMsg.order.patient.patientId != null
										&& myLabOrderMsg.order.patient.patientId != ""
												? myLabOrderMsg.order.patient.patientId
												: "")
								.setFirstName(myLabOrderMsg.order.patient.firstName != null
										&& myLabOrderMsg.order.patient.firstName != ""
												? myLabOrderMsg.order.patient.firstName + "^"
												: "",
										"")
								.setSecondName(myLabOrderMsg.order.patient.secondName != null
										&& myLabOrderMsg.order.patient.secondName != ""
												? myLabOrderMsg.order.patient.secondName
												: "",
										"")
								.setSurname(myLabOrderMsg.order.patient.surname != null
										&& myLabOrderMsg.order.patient.surname != ""
												? myLabOrderMsg.order.patient.surname
												: "",
										"")
								.setGender(getGender(myLabOrderMsg.order.patient.gender != null
										&& myLabOrderMsg.order.patient.gender != "" ? myLabOrderMsg.order.patient.gender
												: ""))
								.setBirthDate(myLabOrderMsg.order.patient.dateOfBirth != null
										? formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth)
										: ""));
//.setAttendingPhysician(myLabOrderMsg.order.patient.doctorName));
				recordCounter = 1;

				orderASTMRecord = OrderASTMRecord.create(recordCounter++)
						.setSpecimenId(myLabOrderMsg.order.container.specimenId != null
								? myLabOrderMsg.order.container.specimenId
								: "")
						.setSpecimnPositionInfo(myLabOrderMsg.order.container.specimenPosition != null
								? myLabOrderMsg.order.container.specimenPosition
								: "")
						.addPriorityCode(getPriority(
								myLabOrderMsg.order.container.priority != null ? myLabOrderMsg.order.container.priority
										: ""))

						.addCollectionDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
						.setActionCode(myLabOrderMsg.order.container.actionCode != null
								&& !myLabOrderMsg.order.container.actionCode.equals("") ? CodesEnumAll.NEW.getValue()
										: CodesEnumAll.NEW.getValue())
						.setSpecimenDescriptor(getSpecimenDescriptor(myLabOrderMsg.order.container.specimenDescriptor))
						.setReportType(myLabOrderMsg.order.container.reportType != null
								&& myLabOrderMsg.order.container.reportType != "" ? "Q" : "Q");

				if (myLabOrderMsg.order.container.analyses != null) {
					for (Analysis analysis : myLabOrderMsg.order.container.analyses)
						orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);

				}

				testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

				terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("N");

				testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);

			} else {
				testSelectionMessage =

						testSelectionMessage.addRecord(HeaderASTMRecord.create()
								// .setSenderId(myLabOrderMsg.order.machineName)
								.setVersionNumber(CodesEnumAll.VersionNumber.getValue())
								.setProcessingId(CodesEnumAll.ProccessingID.getValue()).setDateTime(new Date()));

				queryRecord = (QueryRecord) QueryRecord.create(1)
						.setComponent(3, 2, myLabOrderMsg.order.container.specimenId).setComponent(5, 4, "ALL")
						.setComponent(13, 1, "X");

				testSelectionMessage = testSelectionMessage.addRecord(queryRecord);
				terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("N");
				testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			}
			break;

		case ABBOTT_CELL_DYN_RUBY:
			testSelectionMessage = testSelectionMessage.addRecord(HeaderASTMRecord.create()
					// .setSenderId(myLabOrderMsg.order.machineName)
					.setVersionNumber(CodesEnumAll.VersionNumber.getValue())
					.setProcessingId(CodesEnumAll.ProccessingID.getValue()).setDateTime(new Date()))
					.addRecord(PatientASTMRecord.create(1)
							.setPatientId(myLabOrderMsg.order.patient.patientId != null
									&& myLabOrderMsg.order.patient.patientId != ""
											? myLabOrderMsg.order.patient.patientId
											: "")
							.setFirstName(myLabOrderMsg.order.patient.firstName != null
									&& myLabOrderMsg.order.patient.firstName != ""
											? myLabOrderMsg.order.patient.firstName + "^"
											: "",
									"")
							.setSecondName(myLabOrderMsg.order.patient.secondName != null
									&& myLabOrderMsg.order.patient.secondName != ""
											? myLabOrderMsg.order.patient.secondName
											: "",
									"")
							.setSurname(myLabOrderMsg.order.patient.surname != null
									&& myLabOrderMsg.order.patient.surname != "" ? myLabOrderMsg.order.patient.surname
											: "",
									"")
							.setGender(getGender(myLabOrderMsg.order.patient.gender != null
									&& myLabOrderMsg.order.patient.gender != "" ? myLabOrderMsg.order.patient.gender
											: ""))
							.setBirthDate(myLabOrderMsg.order.patient.dateOfBirth != null
									? formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth)
									: ""));
			// .setAttendingPhysician(myLabOrderMsg.order.patient.doctorName));
			recordCounter = 1;

			orderASTMRecord = OrderASTMRecord.create(recordCounter++).setSpecimenId(
					myLabOrderMsg.order.container.specimenId != null ? myLabOrderMsg.order.container.specimenId : "")
					.setSpecimnPositionInfo(myLabOrderMsg.order.container.specimenPosition != null
							? myLabOrderMsg.order.container.specimenPosition
							: "")
					.addPriorityCode(getPriority(
							myLabOrderMsg.order.container.priority != null ? myLabOrderMsg.order.container.priority
									: ""))

					.addCollectionDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
					.setActionCode(myLabOrderMsg.order.container.actionCode != null
							&& !myLabOrderMsg.order.container.actionCode.equals("") ? CodesEnumAll.NEW.getValue()
									: CodesEnumAll.NEW.getValue())
					.setSpecimenDescriptor(getSpecimenDescriptor(myLabOrderMsg.order.container.specimenDescriptor))
					.setReportType(myLabOrderMsg.order.container.reportType != null
							&& myLabOrderMsg.order.container.reportType != "" ? "O" : "O");

			if (myLabOrderMsg.order.container.analyses != null) {
				for (Analysis analysis : myLabOrderMsg.order.container.analyses)
					orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);

			}

			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("N");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;

		case DIASORIN_LIAISON_XL:
			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setSenderId("LIAISON_XL")
							.setVersionNumber(CodesEnumAll.VersionNumber.getValue())
							.setProcessingId(CodesEnumAll.ProccessingID.getValue()).setDateTime(new Date()))
					.addRecord(PatientASTMRecord.create(1).setPatientId(myLabOrderMsg.order.patient.patientId, 3)
							.setFirstName(myLabOrderMsg.order.patient.firstName, "^", 5, 1)
							.setSecondName(myLabOrderMsg.order.patient.surname, "^", 5, 2)
							// .setSurname(myLabOrderMsg.order.patient.surname, 5 , 1)
							.setGender(getGender(myLabOrderMsg.order.patient.gender))
							.setBirthDate(formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth))
							.setAttendingPhysician(""));// name set from DB in arabic
			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId)
					.setSpecimnPositionInfo(myLabOrderMsg.order.container.specimenPosition)
					.addPriorityCode(getPriority(myLabOrderMsg.order.container.priority))
					.addCollectionDate(dateFormat.format(date))
					// .addCollectionDate(formatDate(myLabOrderMsg.order.container.collectionDate))
					.setActionCode(CodesEnumAll.NULL.getValue()).setSpecimenDescriptor(CodesEnumAll.NULL.getValue())
					.setReportType(CodesEnumAll.ReportTypeF.getValue());

			for (Analysis analysis : myLabOrderMsg.order.container.analyses)
				orderASTMRecord = orderASTMRecord.addAnalysisAstmE138194(null, analysis.name, null, analysis.code,
						null);
			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;
		case BECHMAN_COULTER_X800:
			testSelectionMessage = testSelectionMessage.addRecord(HeaderASTMRecord.create())
					.addRecord(PatientASTMRecord.create(1).setPatientId(myLabOrderMsg.order.patient.patientId)
							.setFirstName(myLabOrderMsg.order.patient.firstName)
							.setSurname(myLabOrderMsg.order.patient.surname)
							.setGender(getGender(myLabOrderMsg.order.patient.gender))
							.setBirthDate(formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth) + "^"
									+ getAge(myLabOrderMsg.order.patient.dateOfBirth))
							.setAttendingPhysician(myLabOrderMsg.order.patient.doctorName));
			recordCounter = 1;
			for (Container container : myLabOrderMsg.order.containers) {
				orderASTMRecord = OrderASTMRecord.create(recordCounter++).setSpecimenId(container.specimenId)
						.setSpecimnPositionInfo(container.specimenPosition)
						.addPriorityCode(getPriorityBechman(container.priority))
						.addCollectionDate(formatDate(container.collectionDate))
						.setActionCode(CodesEnumAll.NEW.getValue())
						.setSpecimenDescriptor(getSpecimenDescriptorBechman(container.specimenDescriptor))
						.setOfflineDilution(CodesEnumBechman.DilutionFactor.getValue());
				for (Analysis analysis : container.analyses)
					orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null,
							analysis.code + CodesEnumBechman.Dilution.getValue());
				testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

				terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

				testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			}
			break;

		case SIEMENS_ADVIA_CENTAUR_XPT:
			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setSenderId(myLabOrderMsg.order.senderName)
							.setReceiverId(myLabOrderMsg.order.machineName)
							.setVersionNumber(CodesEnumAll.VersionNumber.getValue())
							.setProcessingId(CodesEnumAll.ProccessingID.getValue()).setDateTime(new Date()))
					.addRecord(PatientASTMRecord.create(1).setPracticePatientId(myLabOrderMsg.order.patient.patientId)
							.setFirstName(myLabOrderMsg.order.patient.firstName)
							.setSurname(myLabOrderMsg.order.patient.surname)
							.setGender(getGender(myLabOrderMsg.order.patient.gender))
							.setBirthDate(formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth))
							.setAttendingPhysician(myLabOrderMsg.order.patient.doctorName));
			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId)
					.addPriorityCode(getPriority(myLabOrderMsg.order.container.priority))
					.setReportType(CodesEnumAll.ReportTypeOQ.getValue());
			for (Analysis analysis : myLabOrderMsg.order.container.analyses)
				orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);
			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;
		case COBASP312:
			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setProcessingId(CodesEnumAll.ProccessingID.getValue()))
					.addRecord(PatientASTMRecord.create(1).setPatientId(myLabOrderMsg.order.patient.patientId)
							.setFirstName(myLabOrderMsg.order.patient.firstName)
							.setSurname(myLabOrderMsg.order.patient.surname)
							.setGender(getGender(myLabOrderMsg.order.patient.gender))
							.setBirthDate(formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth))
							.setAttendingPhysician(myLabOrderMsg.order.patient.doctorName));
			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId)
					.addPriorityCode(getPriority(myLabOrderMsg.order.container.priority))
					.setActionCode(CodesEnumAll.NEW.getValue()).setReportType(CodesEnumAll.ReportTypeQ.getValue());
			for (Analysis analysis : myLabOrderMsg.order.container.analyses)
				orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);
			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;
		case SIEMENS_IMMULITE_2000_XPI:
			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setSenderId(myLabOrderMsg.order.senderName)
							.setPassword(myLabOrderMsg.order.password).setReceiverId("Analyzer")
							.setVersionNumber(CodesEnumAll.VersionNumber.getValue())
							.setProcessingId(CodesEnumAll.ProccessingID.getValue()).setDateTime(new Date()))
					.addRecord(PatientASTMRecord.create(1).setPracticePatientId(myLabOrderMsg.order.patient.patientId)
							.setFirstName(myLabOrderMsg.order.patient.firstName)
							.setSurname(myLabOrderMsg.order.patient.surname)
							.setGender(getGender(myLabOrderMsg.order.patient.gender))
							.setBirthDate(formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth))
							.setAttendingPhysician(myLabOrderMsg.order.patient.doctorName));
			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId);
			for (Analysis analysis : myLabOrderMsg.order.container.analyses)
				orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);
			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;
		case SYSMEX_CS_2000I:
			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setVersionNumber(CodesEnumAll.VersionNumber.getValue()))
					.addRecord(PatientASTMRecord.create(1).setFirstName(myLabOrderMsg.order.patient.firstName)
							.setSurname(myLabOrderMsg.order.patient.surname));
			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenPosition)
					.addPriorityCode(getPriority(myLabOrderMsg.order.container.priority))
					.setActionCode(CodesEnumAll.NEW.getValue()).addRequestDate(formatDatatimeNow().toString());
			for (Analysis analysis : myLabOrderMsg.order.container.analyses)
				orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null,
						analysis.code + CodesEnumSysmexCS2000.DilutionSysCS2000.getValue());
			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;
		case ROCHE_COBAS_C111_ETB:
			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setSenderId("ASTM_SIM").setReceiverId("c111")
							.setDateTime(new Date()).setVersionNumber(CodesEnumAll.VersionNumber.getValue())
							.setProcessingId(CodesEnumAll.ProccessingID.getValue())
							.setSpecialInstruction(CodesEnumCobas.SpecialInstructionC111.getValue()))
					.addRecord(PatientRecord.create(1));
			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId)
					.addPriorityCode(getPriority(myLabOrderMsg.order.container.priority))
					.setActionCode(CodesEnumAll.ADD.getValue())
					.setSpecimenDescriptor(getSpecimenDescriptorCobas(myLabOrderMsg.order.container.specimenDescriptor))
					.setReportType(CodesEnumAll.ReportTypeO.getValue())
					.addReciveDatetime(formatDatatimeNow().toString());
			for (Analysis analysis : myLabOrderMsg.order.container.analyses)
				orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null,
						analysis.code + CodesEnumCobas.DilutionC111.getValue());
			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;
		case ROCHE_COBAS_C111:
			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setSenderId("ASTM_SIM").setReceiverId("c111")
							.setDateTime(new Date()).setVersionNumber(CodesEnumAll.VersionNumber.getValue())
							.setProcessingId(CodesEnumAll.ProccessingID.getValue())
							.setSpecialInstruction(CodesEnumCobas.SpecialInstructionC111.getValue()))
					.addRecord(PatientRecord.create(1));
			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId)
					.addPriorityCode(getPriority(myLabOrderMsg.order.container.priority))
					.setActionCode(CodesEnumAll.ADD.getValue())
					.setSpecimenDescriptor(getSpecimenDescriptorCobas(myLabOrderMsg.order.container.specimenDescriptor))
					.setReportType(CodesEnumAll.ReportTypeO.getValue())
					.addReciveDatetime(formatDatatimeNow().toString());

			if (myLabOrderMsg.order.container.analyses != null) {
				for (Analysis analysis : myLabOrderMsg.order.container.analyses)
					orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null,
							analysis.code + CodesEnumCobas.DilutionC111.getValue());

			}

			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;
		case SYSMEX_SUITE:
			testSelectionMessage = testSelectionMessage
					.addRecord(
							HeaderASTMRecord.create().setVersionNumber(CodesEnumAll.VersionNumberSysmexSuit.getValue())
									.setDateTimeWithoutSecond(new Date()))
					.addRecord(PatientASTMRecord.create(1).setPracticePatientId(myLabOrderMsg.order.patient.patientId)
							.setFirstName(myLabOrderMsg.order.patient.firstName)
							.setSurname(myLabOrderMsg.order.patient.surname)
							.setGender(getGender(myLabOrderMsg.order.patient.gender))
							.setBirthDate(formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth))
							.setRegistrationDate(formatDateofBirth(new Date())));
			recordCounter = 1;
			OBRRecord obrRecord = OBRRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId)
					.addPriorityCode(getPriority(myLabOrderMsg.order.container.priority))
					.setActionCode(CodesEnumAll.ADD.getValue()).addRequestDate(formatDatatimeNowShort().toString())
					.addCollectionDate(formatDatatimeNowShort().toString());
			for (Analysis analysis : myLabOrderMsg.order.container.analyses)
				obrRecord = obrRecord.addAnalysis(null, analysis.name, null, analysis.code);
			testSelectionMessage = testSelectionMessage.addRecord(obrRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;

		case BECHMAN_COULTER_500_DXH:
		case BECHMAN_COULTER_800_DXH:
		case BECHMAN_COULTER_AU480_DXC:
			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create("\\!~")
							.setVersionNumber(CodesEnumAll.VersionNumberSysmexSuit.getValue())
							.setDateTimeWithoutSecond(new Date()))
					.addRecord(PatientASTMRecord.create(1).setPracticePatientId(myLabOrderMsg.order.patient.patientId)
							.setFirstName(myLabOrderMsg.order.patient.firstName, "!")
							.setSurname(myLabOrderMsg.order.patient.surname, "!")
							.setSecondName(myLabOrderMsg.order.patient.secondName, "!")
							.setGender(getGender(myLabOrderMsg.order.patient.gender))
							.setBirthDate(formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth))
							.setGender(myLabOrderMsg.order.patient.gender)
							.setRegistrationDate(formatDateofBirth(new Date())));
			recordCounter = 1;
			OrderRecord oRecord = OrderRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId)
					.addPriorityCode(getPriority(myLabOrderMsg.order.container.priority))
					.setActionCode(CodesEnumAll.ADD.getValue()).addRequestDate(formatDatatimeNowShort().toString())
					.addCollectionDate(formatDatatimeNowShort().toString())
					.setSpecimenDescriptor(myLabOrderMsg.order.container.specimenDescriptor);

			// for (Analysis analysis : myLabOrderMsg.order.container.analyses)
			if (machine.getIsPanelOrder()) {

				MachineTypePanelService machineTypePanelService = (MachineTypePanelService) SpringUtil
						.getBean("MachineTypePanelService");

				MachineTypePanel machinePanel = machineTypePanelService.getPanelByMachineTypeAndPanelName(
						machine.getMachineType().getRid(), myLabOrderMsg.order.container.analyses.get(0).code);

				oRecord = oRecord.addAnalysisAstmE138102(null, "", null, machinePanel.getPanelHostCode());
				testSelectionMessage = testSelectionMessage.addRecord(oRecord);

			} else {
				for (Analysis analysis : myLabOrderMsg.order.container.analyses) {
					oRecord = oRecord.addAnalysisAstmE138102(null, "", null, analysis.code);
					testSelectionMessage = testSelectionMessage.addRecord(oRecord);
				}
			}

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;
		case GRIFOLS:
			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setSenderId(myLabOrderMsg.order.senderName)
							.setVersionNumber(CodesEnumAll.VersionNumber.getValue())
							.setProcessingId(CodesEnumAll.ProccessingID.getValue()).setDateTime(new Date()))
					.addRecord(PatientASTMRecord.create(1).setPracticePatientId(myLabOrderMsg.order.patient.patientId));
			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId);
			for (Analysis analysis : myLabOrderMsg.order.container.analyses)
				orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);

			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);
			break;
		case ROCHE_ELECSYS_2010:

			testSelectionMessage = testSelectionMessage
					.addRecord(HeaderASTMRecord.create().setProcessingId(CodesEnumAll.ProccessingID.getValue()))
					.addRecord(PatientRecord.create(1));
			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++).setSpecimenId(
					myLabOrderMsg.order.container.specimenId != null ? myLabOrderMsg.order.container.specimenId : "")
					.setSpecimnPositionInfo(myLabOrderMsg.order.container.specimenPosition != null
							? myLabOrderMsg.order.container.specimenPosition
							: "")
					.addPriorityCode(getPriority(
							myLabOrderMsg.order.container.priority != null ? myLabOrderMsg.order.container.priority
									: null))
					.setActionCode(CodesEnumAll.NEW.getValue())
					.setReportType(myLabOrderMsg.order.container.analyses != null ? CodesEnumAll.ReportTypeQ.getValue()
							: CodesEnumAll.ReportTypeZ.getValue())
					.addRequestDate(formatDatatimeNow().toString());

			if (myLabOrderMsg.order.container.analyses != null) {
				for (Analysis analysis : myLabOrderMsg.order.container.analyses)
					orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);
			}

			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);
			terminationRecord = TerminationRecord.create(1);
			// .setTerminationCode("");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;

		case SIEMENS_ATELLICA:

			testSelectionMessage = testSelectionMessage.addRecord(HeaderASTMRecord.create()
					// .setSenderId(myLabOrderMsg.order.senderName)
					// .setReceiverId(myLabOrderMsg.order.machineName)
					// .setVersionNumber(CodesEnumAll.VersionNumber.getValue())
					// .setProcessingId(CodesEnumAll.ProccessingID.getValue())
					.setDateTime(new Date()))
					.addRecord(PatientASTMRecord.create(1).setPracticePatientId(
							myLabOrderMsg.order.patient.patientId != null ? myLabOrderMsg.order.patient.patientId : "")
							// .setFirstName(myLabOrderMsg.order.patient.firstName)
							// .setSurname(myLabOrderMsg.order.patient.surname)
							.setGender(getGender(myLabOrderMsg.order.patient.gender))
							.setBirthDate(formatDateofBirth(myLabOrderMsg.order.patient.dateOfBirth != null
									? myLabOrderMsg.order.patient.dateOfBirth
									: new Date()))
							.setAttendingPhysician(myLabOrderMsg.order.patient.doctorName));

			recordCounter = 1;
			orderASTMRecord = OrderASTMRecord.create(recordCounter++)
					.setSpecimenId(myLabOrderMsg.order.container.specimenId)
					.addPriorityCode(getPriority(
							myLabOrderMsg.order.container.priority != null ? myLabOrderMsg.order.container.priority
									: "ROUTINE"))
					.setSpecimenDescriptor(myLabOrderMsg.order.container.specimenDescriptor)
					.setReportType(CodesEnumAll.ReportTypeO.getValue());
			for (Analysis analysis : myLabOrderMsg.order.container.analyses)
				orderASTMRecord = orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code);
			testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);

			terminationRecord = (TerminationRecord) TerminationRecord.create(1).setTerminationCode("F");

			testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
			break;

		/*
		 * testSelectionMessage = testSelectionMessage
		 * .addRecord(HeaderASTMRecord.create().setProcessingId(CodesEnumAll.
		 * ProccessingID.getValue())) .addRecord(PatientRecord.create(1)); recordCounter
		 * = 1; orderASTMRecord = OrderASTMRecord.create(recordCounter++).setSpecimenId(
		 * myLabOrderMsg.order.container.specimenId != null ?
		 * myLabOrderMsg.order.container.specimenId : "")
		 * .setSpecimnPositionInfo(myLabOrderMsg.order.container.specimenPosition !=
		 * null ? myLabOrderMsg.order.container.specimenPosition : "")
		 * .addPriorityCode(getPriority( myLabOrderMsg.order.container.priority != null
		 * ? myLabOrderMsg.order.container.priority : "ROUTINE"))
		 * //.setActionCode(CodesEnumAll.NEW.getValue())
		 * .setReportType(myLabOrderMsg.order.container.analyses != null ?
		 * CodesEnumAll.ReportTypeO.getValue() : "")
		 * 
		 * .sets
		 * 
		 * .setSpecimenDescriptor("SERUM")
		 * 
		 * .addRequestDate(formatDatatimeNow().toString());
		 * 
		 * if (myLabOrderMsg.order.container.analyses != null) { for (Analysis analysis
		 * : myLabOrderMsg.order.container.analyses) orderASTMRecord =
		 * orderASTMRecord.addAnalysis(null, analysis.name, null, analysis.code); }
		 * 
		 * testSelectionMessage = testSelectionMessage.addRecord(orderASTMRecord);
		 * terminationRecord = TerminationRecord.create(1);
		 * terminationRecord.setTerminationCode("F");
		 * 
		 * testSelectionMessage = testSelectionMessage.addRecord(terminationRecord);
		 */
		// break;

		default:
			break;
		}

		/*
		 * result = result.addRecord(TerminationRecord .create(1)
		 * .setTerminationCode("N"));
		 */

		return testSelectionMessage;

	}

	private void resetSegmentions() {
		terminationRecord = new TerminationRecord();
		orderASTMRecord = new OrderASTMRecord();
		testSelectionMessage = new LIS2A2OrderASTM();
		commentRecord = new CommentRecord();
	}

}
