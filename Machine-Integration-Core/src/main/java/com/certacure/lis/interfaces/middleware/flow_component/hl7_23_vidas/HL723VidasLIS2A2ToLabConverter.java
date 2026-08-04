package com.certacure.lis.interfaces.middleware.flow_component.hl7_23_vidas;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Analysis;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Container;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.LabQueryMsg;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.LabResultMsg;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Order;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Patient;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.Result;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultAstmMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.OrderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.PatientASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_PatientRecord;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class HL723VidasLIS2A2ToLabConverter extends FlowComponent<RecipientConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour()
	{
		return ReceiveBuilder
								.match(LIS2A2ResultMsg.class, this::convertAndForwardResult)
								.match(LIS2A2ResultAstmMsg.class, this::convertAndForwardResult)
								.match(LIS2A2QueryMsg.class, this::convertAndForwardQuery)
								.build();
	}

	private void convertAndForwardResult(LIS2A2ResultMsg Lis2A2ResultMsg) 
	{
		for (LabResultMsg LabResultMsg : lis2a2ResultMsgToLabResultMsgs(Lis2A2ResultMsg))
			conf.recipient.tell(LabResultMsg, self());
	}
	
	private void convertAndForwardResult(LIS2A2ResultAstmMsg Lis2A2ResultMsg) {
		for (LabResultMsg LabResultMsg : lis2a2ResultMsgToLabResultMsgs(Lis2A2ResultMsg))
			conf.recipient.tell(LabResultMsg, self());
	}
	
	
	public List<LabResultMsg> lis2a2ResultMsgToLabResultMsgs(LIS2A2ResultMsg Lis2A2ResultMsg) {
		log.debug("Converting LIS2A2 message to LabResultMsg");
		List<LabResultMsg> result = new ArrayList<>();
		for (HL7_V24_PatientRecord patientRecord : Lis2A2ResultMsg.getPatientRecords()) {
			log.debug("Finding O records for " + patientRecord.asString());
			for (HL7_V24_OrderRecord orderRecord : Lis2A2ResultMsg.getOrderRecords(patientRecord)) {
				log.debug("Finding R records for " + orderRecord.asString());
				
				List<Analysis> analyses  = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(
						resultRecord ->
							{
								log.debug("Converting Result record to Analysis - " + resultRecord.toString());
								return new Analysis(
										resultRecord.getHL7AnalysisCode(4, 1, "^"),
										"",
										"",
										new Result(
												resultRecord.getHL7ResultValue(6, 1 , "^"),
												resultRecord.getHL7Unit(),
												resultRecord.getHL7AbnormalFlag()));
							}).collect(toList());

				LabResultMsg labResultMsg = new LabResultMsg(
						new Order(
								new Patient(
										"",
										"",
										"",
										null,
										"",
										""),
								(new Container(
										orderRecord.getSpecimenId(3,1 , "^").toString().trim(),
										analyses, "", "", "", "", "", "")),
								"", "", null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
				result.add(labResultMsg);


			}

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
				List<Analysis> analyses  = Lis2A2ResultMsg.getResultRecords(orderRecord).stream().map(
						resultRecord ->
							{
								log.debug("Converting Result record to Analysis - " + resultRecord.toString());
								return new Analysis(
										resultRecord.getAnalysisCode(2, 5),
										"",
										"",
										new Result(
												resultRecord.getResultValue(3, 1),
												resultRecord.getUnit(),
												resultRecord.getAbnormalFlag()));
							}).collect(toList());

				LabResultMsg labResultMsg = new LabResultMsg(
						new Order(
								new Patient(
										"",
										"",
										"",
										null,
										"",
										""),
								(new Container(
										orderRecord.getSpecimenIds(3, 4).toString().trim(),
										analyses, "", "", "", "", "", "")),
								"", "", null, Lis2A2ResultMsg.getMessageTransactionID(),"",""));
				result.add(labResultMsg);


			}

		} 
		return result;
	}


	///Store Query
	private void convertAndForwardQuery(LIS2A2QueryMsg lIS2A2QueryMsg) {
		for (LabQueryMsg myLabQueryMsg : astmQueryMsgToMyLabQueryMsgs(lIS2A2QueryMsg))
			conf.recipient.tell(myLabQueryMsg, self());
	}
	
	public List<LabQueryMsg> astmQueryMsgToMyLabQueryMsgs(LIS2A2QueryMsg LIS2A2QueryMsg) {
		List<LabQueryMsg> lstMyLabQueryMsg = null;
		
				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(4).toString(),
															queryRecord.getAnalysisCodes(),
															new String(), new String()))
													.collect(toList());
				return lstMyLabQueryMsg;
	}
}