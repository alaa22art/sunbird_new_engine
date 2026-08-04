package com.certacure.lis.interfaces.middleware.flow_component.astm_to_mylis;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Analysis;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Container;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.LabQueryMsg;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.LabResultMsg;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Order;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Patient;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Result;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryAstmMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultAstmMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_QBP_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7_V24_SpecimenRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.QueryPatientDemographicRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.QueryRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.OrderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.PatientASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_PatientDemographicQueryRecord;
import com.certacure.lis.interfaces.middleware.util.MachineTypeEnum;
import com.certacure.lis.interfaces.service.MachineService;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class LIS2A2ToLabConverter extends FlowComponent<RecipientConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder

				.match(LIS2A2ResultAstmMsg.class, this::convertAndForwardResult)
				.match(LIS2A2ResultMsg.class, this::convertAndForwardResult)
				.match(LIS2A2ResultMsg[].class, this::convertAndForwardResult)
				.match(String[].class, this::convertAndForwardResult)
				.match(LIS2A2QueryMsg.class, this::convertAndForwardQuery)
				.match(LIS2A2QueryAstmMsg.class, this::convertAndForwardQuery)
				.match(LIS2A2_QBP_Msg.class, this::convertAndForwardQuery).build();

	}

	private void convertAndForwardResult(LIS2A2ResultMsg[] arrLIS2A2ResultMsg) {

	}

	private void convertAndForwardQuery(LIS2A2_QBP_Msg Lis2a_QBP_Msg) {
		LabQueryMsg LabQueryMsg = hl7QueryMsgToMyLabQueryMsgs(Lis2a_QBP_Msg);
		conf.recipient.tell(LabQueryMsg, self());
	}

	private void convertAndForwardResult(LIS2A2ResultAstmMsg Lis2A2ResultMsg) {
		for (LabResultMsg LabResultMsg : lis2a2ResultMsgToLabResultMsgs(Lis2A2ResultMsg))
			conf.recipient.tell(LabResultMsg, self());
	}

	private void convertAndForwardResult(LIS2A2ResultMsg lis2A2ResultMsg) {
		for (LabResultMsg LabResultMsg : lis2a2ResultMsgToHL7v25LabResultMsgs(lis2A2ResultMsg))
			conf.recipient.tell(LabResultMsg, self());
	}

	private void convertAndForwardResult(String[] resultArray) {
		// for (LabResultMsg LabResultMsg :
		// lis2a2ResultMsgToLabResultMsgs(Lis2A2ResultMsg))
		// conf.recipient.tell(LabResultMsg, self());
	}

	public List<LabResultMsg> lis2a2ResultMsgToLabResultMsgs(LIS2A2ResultMsg Lis2A2ResultMsg) {
		log.debug("Converting LIS2A2 message to LabResultMsg");
		List<LabResultMsg> result = new ArrayList<>();
		for (HL7_V24_PatientRecord patientRecord : Lis2A2ResultMsg.getPatientRecords()) {
			log.debug("Finding O records for " + patientRecord.asString());
			for (HL7_V24_OrderRecord orderRecord : Lis2A2ResultMsg.getOrderRecords(patientRecord)) {
				log.debug("Finding R records for " + orderRecord.asString());

				List<Analysis> analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
					log.debug("Converting Result record to Analysis - " + resultRecord.toString());
					return new Analysis(resultRecord.getHL7AnalysisCode(4, 2, "^"), "", "",
							new Result(resultRecord.getHL7ResultValue(6, 1, "^"), resultRecord.getHL7Unit(),
									resultRecord.getHL7AbnormalFlag()));
				}).collect(toList());

				LabResultMsg labResultMsg = new LabResultMsg(new Order(new Patient("", "", "", null, "", ""),
						(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "", null,
						Lis2A2ResultMsg.getMessageTransactionID(),"",""));
				result.add(labResultMsg);

			}

		}
		return result;
	}

	public List<LabResultMsg> lis2a2ResultMsgToHL7v25LabResultMsgs(LIS2A2ResultMsg Lis2A2ResultMsg) {
		log.debug("Converting LIS2A2 message to LabResultMsg");
		List<LabResultMsg> result = new ArrayList<>();
		
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());

		switch (machineType) {
		case ROCHE_COBAS_PRO_HL7_251:

			for (HL7_V24_SpecimenRecord hl7v24SpecimenRecord : Lis2A2ResultMsg.getSpecimenRecord()) {
				for (HL7_V24_OrderRecord orderRecord : Lis2A2ResultMsg.getOrderRecords()) {
					log.debug("Finding R records for " + orderRecord.asString());

					List<Analysis> analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream()
							.map(resultRecord -> {
								log.debug("Converting Result record to Analysis - " + resultRecord.toString());
								return new Analysis(resultRecord.getHL7AnalysisCode(4, 2, "^"),
										resultRecord.getResultCode(11), "",
										new Result(resultRecord.getHL7ResultValue(6, 1, "^"), resultRecord.getHL7Unit(),
												resultRecord.getHL7AbnormalFlag()));
							}).collect(toList());

					LabResultMsg labResultMsg = new LabResultMsg(new Order(new Patient("", "", "", null, "", ""),
							(new Container(hl7v24SpecimenRecord.getSpecimenId(3,1), analyses, "", "", "", "", "", "")), "",
							"", null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);

				}
			}

			break;
		default:

			for (HL7_V24_SpecimenRecord hl7v24SpecimenRecord : Lis2A2ResultMsg.getSpecimenRecord()) {
				for (HL7_V24_OrderRecord orderRecord : Lis2A2ResultMsg.getOrderRecords()) {
					log.debug("Finding R records for " + orderRecord.asString());

					List<Analysis> analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream()
							.map(resultRecord -> {
								log.debug("Converting Result record to Analysis - " + resultRecord.toString());
								return new Analysis(resultRecord.getHL7AnalysisCode(4, 2, "^"),
										resultRecord.getResultCode(4, 1), "",
										new Result(resultRecord.getHL7ResultValue(6, 1, "^"), resultRecord.getHL7Unit(),
												resultRecord.getHL7AbnormalFlag()));
							}).collect(toList());

					LabResultMsg labResultMsg = new LabResultMsg(new Order(new Patient("", "", "", null, "", ""),
							(new Container(hl7v24SpecimenRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "",
							"", null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);

				}
			}
		break;
	}
		
		

		return result;
	}

	public List<LabResultMsg> lis2a2ResultMsgToLabResultMsgs(LIS2A2ResultAstmMsg Lis2A2ResultMsg) {
		log.debug("Converting LIS2A2 message to LabResultMsg");
		List<LabResultMsg> result = new ArrayList<>();
		for (PatientASTMRecord patientRecord : Lis2A2ResultMsg.getPatientASTMRecords()) {
			log.debug("Finding O records for " + patientRecord.asString());
			for (OrderASTMRecord orderRecord : Lis2A2ResultMsg.getOrderRecords(patientRecord)) {
				log.debug("Finding R records for " + orderRecord.asString());
				MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
				Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
				MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
				switch (machineType) {
				case ROCHE_COBAS_6000:
				case ROCHE_COBAS_C311:
					List<Analysis> analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord ->

					{
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCodeRepeted(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					LabResultMsg labResultMsg = new LabResultMsg(
							new Order(new Patient("", "", patientRecord.getPatientId(4), null, "", ""),
									(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "",
									"", null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case ROCHE_COBAS_PURE:

					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCodeRepeted(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(
							new Order(new Patient("", "", patientRecord.getPatientId(4), null, "", ""),
									(new Container(orderRecord.getSpecimenId(4), analyses, "", "", "", "", "", "")), "",
									"", null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);

					break;

				case SIEMENS_ADVIA_560:

					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCodeRepeted(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(
							new Order(new Patient("", "", patientRecord.getPatientId(4), null, "", ""),
									(new Container(orderRecord.getSpecimenId(4), analyses, "", "", "", "", "", "")), "",
									"", null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);

					break;

				case BECHMAN_COULTER_AU:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(4, 1), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValueComponent(4, 2), ""));
					}).collect(toList());

					labResultMsg = new LabResultMsg(
							new Order(new Patient("", "", patientRecord.getPatientId(4), null, "", ""),
									(new Container(orderRecord.getSpecimenIds(2), analyses, "", "", "", "", "", "")),
									"", "", null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case ABBOTT_CELL_DYN_RUBY:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {

						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCodeRuby(), resultRecord.getResultCode(),
								resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit(),
										resultRecord.getAbnormalFlag(), resultRecord.getStatus(),
										resultRecord.getReferanceRanges()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, "", ""),
							(new Container(orderRecord.getSpecimenId(2), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case ABBOTT_ALINITY_CI:

					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), resultRecord.getResultCode(3, 7),
								resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit(),
										resultRecord.getAbnormalFlag(), resultRecord.getStatus(),
										resultRecord.getReferanceRanges()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case ABBOTT_ARCHITECT_CI4100:

					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), resultRecord.getResultCode(3, 11),
								resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit(),
										resultRecord.getAbnormalFlag(), resultRecord.getStatus(),
										resultRecord.getReferanceRanges()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case ABBOTT_CELL_DYN_EMERALD_22:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), resultRecord.getResultCode(),
								resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit(),
										resultRecord.getAbnormalFlag(), resultRecord.getStatus(),
										resultRecord.getReferanceRanges()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, "", ""),
							(new Container(orderRecord.getSpecimenId02(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case BECHMAN_ACCESS_200_DXI:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), resultRecord.getResultCode(),
								resultRecord.getAnalysisName(), new Result(resultRecord.getResultValue(),
										resultRecord.getUnit(), resultRecord.getAbnormalFlag()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case BECKMAN_UNICEL_800_DXH:
				case BECHMAN_COULTER_800_DXH:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), resultRecord.getResultCode(),
								resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(4, 1), resultRecord.getUnit(),
										resultRecord.getAbnormalFlag(), resultRecord.getStatus(),
										resultRecord.getReferanceRangesASTM02()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case BECHMAN_COULTER_500_DXH:

					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), resultRecord.getResultCode(),
								resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(4, 1), resultRecord.getUnit(),
										resultRecord.getAbnormalFlag(4, 2), resultRecord.getStatus(),
										resultRecord.getReferanceRanges(7, 1)));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case ROCHE_COBAS_E411:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCodeRepeted(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());
					labResultMsg = new LabResultMsg(new Order(new Patient("", "", "", // patientRecord.getPatientId(4),
							null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case ROCHE_COBAS_U_411:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCodeRepeted(3,1), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(3,1), resultRecord.getUnit()));
					}).collect(toList());
					labResultMsg = new LabResultMsg(new Order(new Patient("", "", "", // patientRecord.getPatientId(4),
							null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case BECHMAN_COULTER_X800:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValueComponent(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(
							new Order(new Patient("", "", patientRecord.getPatientId(4), null, "", ""),
									(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")),

									"", "", null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case SIEMENS_ADVIA_CENTAUR_XPT:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), resultRecord.getAnalysisResultCode(3, 8),
								resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case COBASP312:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case SIEMENS_IMMULITE_2000_XPI:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case D10:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), resultRecord.getAnalysisResultCode(),
								resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(new Patient("", "", "", null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case ROCHE_COBAS_C111_ETB:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(new Patient("", "", "", null, "", ""),
							(new Container(orderRecord.getSpecimenIds(1, 4), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case ROCHE_COBAS_C111:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(new Patient("", "", "", null, "", ""),
							(new Container(orderRecord.getSpecimenIds(1, 4), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case SYSMEX_XP_300:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(new Patient("", "", "", null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case SYSMEX_CS_2000I:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, "", ""),
							(new Container(orderRecord.getSpecimenId(3), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case GRIFOLS:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(3, 5), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValueComponent(4, 5), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(
							new Order(new Patient("", "", patientRecord.getPatientId(3), null, "", ""),
									(new Container(orderRecord.getSpecimenId(3), analyses, "", "", "", "", "", "")), "",
									"", null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;
				case ROCHE_ELECSYS_2010:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getFieldValue(3), resultRecord.getUnit(),
										resultRecord.getAbnormalFlag(), resultRecord.getStatus(),
										resultRecord.getReferanceRanges()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(3), null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case SIEMENS_ATELLICA:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());

						System.out.println("=================================================================");
						System.out.println("Converting Result record to Analysis - " + resultRecord.toString());
						System.out.println("=================================================================");

						return new Analysis(resultRecord.getAnalysisCode(), resultRecord.getResultCode(),
								resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit(),
										resultRecord.getAbnormalFlag(), resultRecord.getStatus(),
										resultRecord.getReferanceRanges()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(3), null, "", ""),
							(new Container(orderRecord.getSpecimenId(), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case DIASORIN_LIAISON_XL:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), resultRecord.getUnit()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(
							new Patient(patientRecord.getFirstName(), patientRecord.getSurname(),
									patientRecord.getPatientId(4), null, patientRecord.getGender(9), ""),
							(new Container(orderRecord.getSpecimenId(3), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case SYSMEX_KX_21:
				case BECHMAN_COULTER_AU480_DXC:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(), "", resultRecord.getAnalysisName(),
								new Result(resultRecord.getResultValue(), ""));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(new Patient("", "", "", null, "", ""),
							(new Container(orderRecord.getSpecimenId(4), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);
					break;

				case SYSMEX_SUITE:
					analyses = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(resultRecord -> {
						log.debug("Converting Result record to Analysis - " + resultRecord.toString());
						return new Analysis(resultRecord.getAnalysisCode(4, 1), "", "",
								new Result(resultRecord.getResultValue(6, 1), resultRecord.getUnit(),
										resultRecord.getAbnormalFlag()));
					}).collect(toList());

					labResultMsg = new LabResultMsg(new Order(new Patient("", "", "", null, "", ""),
							(new Container(orderRecord.getSpecimenId(4), analyses, "", "", "", "", "", "")), "", "",
							null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
					result.add(labResultMsg);

					break;

				default:
					break;
				}

			}

		}
		return result;
	}

	/// Store Query
	private void convertAndForwardQuery(LIS2A2QueryMsg lIS2A2QueryMsg) {

		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
		

		switch (machineType) {
		case SIEMENS_ATELLICA_HL7_251:
		case ROCHE_COBAS_PRO_HL7_251:
			for (LabQueryMsg myLabQueryMsg : hl7QueryMsgToMyLabQueryMsgs(lIS2A2QueryMsg))
				conf.recipient.tell(myLabQueryMsg, self());
			break;
		default:
			for (LabQueryMsg myLabQueryMsg : astmQueryMsgToMyLabQueryMsgs(lIS2A2QueryMsg))
				conf.recipient.tell(myLabQueryMsg, self());
			break;

		}

	}

	/// Store Query
	private void convertAndForwardQuery(LIS2A2QueryAstmMsg lIS2A2QueryMsg) {
		for (LabQueryMsg myLabQueryMsg : astmQueryMsgToMyLabQueryMsgs(lIS2A2QueryMsg))
			conf.recipient.tell(myLabQueryMsg, self());
	}

	public List<LabQueryMsg> astmQueryMsgToMyLabQueryMsgs(LIS2A2QueryAstmMsg LIS2A2QueryMsg) {
		List<LabQueryMsg> lstMyLabQueryMsg = null;
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
		switch (machineType) {
		case ROCHE_COBAS_6000:
		case ROCHE_COBAS_C311:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(3, 3).toString(),
							queryRecord.getAnalysisCodes(), new String(), queryRecord.getSpecimenPositionInfo()))
					.collect(toList());
			break;
		case ROCHE_COBAS_E411:
		case SYSMEX_XP_300:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(3).toString(),
							queryRecord.getAnalysisCodes(), new String(), queryRecord.getSpecimenPositionInfo()))
					.collect(toList());
			break;
		case BECHMAN_COULTER_AU:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(2).toString(),
							queryRecord.getAnalysisCodes(), new String(), queryRecord.getSpecimenPositionInfoAU()))
					.collect(toList());
			break;
		case ABBOTT_ARCHITECT_CI4100:
		case ABBOTT_ALINITY_CI:
		case BECHMAN_COULTER_X800:
		case SIEMENS_ADVIA_CENTAUR_XPT:
		case COBASP312:
		case SIEMENS_IMMULITE_2000_XPI:
		case D10:
		case ROCHE_COBAS_C111_ETB:
		case ROCHE_COBAS_C111:
		case GRIFOLS:

			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(2).toString(),
							queryRecord.getAnalysisCodes(), new String(), new String()))
					.collect(toList());
			break;

		case ABBOTT_CELL_DYN_RUBY:

			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getComponentValue(3, 2, "^").toString(),
							new String(), new String()))
					.collect(toList());
			break;
		case SYSMEX_CS_2000I:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSysmexSpecimenId().toString(),
							queryRecord.getAnalysisCodes(), new String(), new String()))
					.collect(toList());
			break;
		case SYSMEX_SUITE:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(4).toString(),
							queryRecord.getAnalysisCodes(), new String(), new String()))
					.collect(toList());
			break;
		case ROCHE_ELECSYS_2010:
		case SIEMENS_ATELLICA:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(2).toString(),
							queryRecord.getAnalysisCodes(), new String(), queryRecord.getElecsysSpecimenPositionInfo()))
					.collect(toList());
			break;

		case BECHMAN_ACCESS_200_DXI:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getComponentValue(3, 2, "^").toString(),
							queryRecord.getAnalysisCodes(), new String(), new String()))
					.collect(toList());

			break;

		case DIASORIN_LIAISON_XL:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(3).toString(),
							queryRecord.getAnalysisCodes(), new String(), new String()))
					.collect(toList());
			break;

		case BECKMAN_UNICEL_800_DXH:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(3, 2, "!").toString(),
							queryRecord.getAnalysisCodes("!"), new String(),
							queryRecord.getElecsysSpecimenPositionInfoASTM02()))
					.collect(toList());
			break;

		case BECHMAN_COULTER_800_DXH:
		case BECHMAN_COULTER_AU480_DXC:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(3, 2, "!").toString(),
							queryRecord.getAnalysisCodes("!"), new String(),
							queryRecord.getElecsysSpecimenPositionInfoASTM02()))
					.collect(toList());
		default:
			break;

		}
		return lstMyLabQueryMsg;

	}

	public LabQueryMsg hl7QueryMsgToMyLabQueryMsgs(LIS2A2_QBP_Msg lis2a2_QBP_Msg) {

		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		String strBarcode = lis2a2_QBP_Msg.getQueryPatientDemographicRecords().get(0).getSpecimenId(3);
		String strPosition = lis2a2_QBP_Msg.getQueryPatientDemographicRecords().get(0).getSpecimenId(10);
		LabQueryMsg lstMyLabQueryMsg = new LabQueryMsg(strBarcode, machine.getName(), strPosition , "","");
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());

		return lstMyLabQueryMsg;

	}

	public List<LabQueryMsg> hl7QueryMsgToMyLabQueryMsgs(LIS2A2QueryMsg LIS2A2QueryMsg) {
		List<LabQueryMsg> lstMyLabQueryMsg = null;
		HL7_V25_PatientDemographicQueryRecord queryRecord ;
		HL7_V24_HeaderRecord headerRecord;
		LabQueryMsg labQueryMsg;
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
		switch (machineType) {
		case SIEMENS_ATELLICA_HL7_251:
			lstMyLabQueryMsg = new ArrayList<LabQueryMsg>();
			queryRecord = LIS2A2QueryMsg.getQueryParametrDefinitionRecord().get(0);
			labQueryMsg = new LabQueryMsg(queryRecord.getSpecimenId(3).toString(), machine.getName(),
					queryRecord.getSpecimenPositionInfo(),"","");
			lstMyLabQueryMsg.add(labQueryMsg);
		case ROCHE_COBAS_PRO_HL7_251:
			lstMyLabQueryMsg = new ArrayList<LabQueryMsg>();
			headerRecord = LIS2A2QueryMsg.getHeaderRecords().get(0);
			queryRecord = LIS2A2QueryMsg.getQueryParametrDefinitionRecord().get(0);
			labQueryMsg = new LabQueryMsg(queryRecord.getSpecimenId(3).toString(),
					machine.getName(),
					queryRecord.getContainerRackPositionID() ,
					headerRecord.getMessageControlId(),
					queryRecord.getQueryTag() ,
					queryRecord.getContainerRackID() , 
					queryRecord.getBarcodeMode(2,1).toString());
			lstMyLabQueryMsg.add(labQueryMsg);
			break;
		default:
			break;
		}

		return lstMyLabQueryMsg;
	}

	public List<LabQueryMsg> astmQueryMsgToMyLabQueryMsgs(LIS2A2QueryMsg LIS2A2QueryMsg) {
		List<LabQueryMsg> lstMyLabQueryMsg = null;
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
		switch (machineType) {
		case ROCHE_COBAS_6000:
		case ROCHE_COBAS_C311:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(3, 3).toString(),
							queryRecord.getAnalysisCodes(), new String(), queryRecord.getSpecimenPositionInfo()))
					.collect(toList());
			break;
		case ROCHE_COBAS_E411:
		case SYSMEX_XP_300:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(3).toString(),
							queryRecord.getAnalysisCodes(), new String(), queryRecord.getSpecimenPositionInfo()))
					.collect(toList());
			break;
		case BECHMAN_COULTER_AU:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(2).toString(),
							queryRecord.getAnalysisCodes(), new String(), queryRecord.getSpecimenPositionInfoAU()))
					.collect(toList());
			break;
		case ABBOTT_ARCHITECT_CI4100:
		case ABBOTT_ALINITY_CI:
		case BECHMAN_COULTER_X800:
		case SIEMENS_ADVIA_CENTAUR_XPT:
		case COBASP312:
		case SIEMENS_IMMULITE_2000_XPI:
		case D10:
		case ROCHE_COBAS_C111_ETB:
		case ROCHE_COBAS_C111:
		case GRIFOLS:

			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(2).toString(),
							queryRecord.getAnalysisCodes(), new String(), new String()))
					.collect(toList());
			break;

		case ABBOTT_CELL_DYN_RUBY:

			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getComponentValue(3, 2, "^").toString(),
							new String(), new String(),"",""))
					.collect(toList());
			break;
		case SYSMEX_CS_2000I:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSysmexSpecimenId().toString(),
							queryRecord.getAnalysisCodes(), new String(), new String()))
					.collect(toList());
			break;
		case SYSMEX_SUITE:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(4).toString(),
							queryRecord.getAnalysisCodes(), new String(), new String()))
					.collect(toList());
			break;
		case ROCHE_ELECSYS_2010:
		case SIEMENS_ATELLICA:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(2).toString(),
							queryRecord.getAnalysisCodes(), new String(), queryRecord.getElecsysSpecimenPositionInfo()))
					.collect(toList());
			break;
		case BECHMAN_ACCESS_200_DXI:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getComponentValue(3, 2, "^").toString(),
							queryRecord.getAnalysisCodes(), new String(), new String()))
					.collect(toList());

			break;

		case DIASORIN_LIAISON_XL:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(3).toString(),
							queryRecord.getAnalysisCodes(), new String(), new String()))
					.collect(toList());
			break;

		case BECKMAN_UNICEL_800_DXH:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(3, 2, "!").toString(),
							queryRecord.getAnalysisCodes("!"), new String(),
							queryRecord.getElecsysSpecimenPositionInfoASTM02()))
					.collect(toList());
			break;

		case BECHMAN_COULTER_800_DXH:
		case BECHMAN_COULTER_AU480_DXC:
			lstMyLabQueryMsg = LIS2A2QueryMsg.getQueryRecords().stream()
					.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(3, 2, "!").toString(),
							queryRecord.getAnalysisCodes("!"), new String(),
							queryRecord.getElecsysSpecimenPositionInfoASTM02()))
					.collect(toList());
			break;
		default:
			break;

		}
		return lstMyLabQueryMsg;

	}
}