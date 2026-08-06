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
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2ResultMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.PatientRecord;
import com.certacure.lis.interfaces.middleware.util.MachineTypeEnum;
import com.certacure.lis.interfaces.service.MachineService;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class LIS2A2ToLabConverter extends FlowComponent<RecipientConf> {

	@Override
	protected PartialFunction<Object, BoxedUnit> getBehaviour() {
		return ReceiveBuilder
								.match(LIS2A2ResultMsg.class, this::convertAndForwardResult)
								.match(String[].class, this::convertAndForwardResult)
								.match(LIS2A2QueryMsg.class, this::convertAndForwardQuery)
								.build();
	}

	
	
	private void convertAndForwardResult(LIS2A2ResultMsg Lis2A2ResultMsg) {
		for (LabResultMsg LabResultMsg : lis2a2ResultMsgToLabResultMsgs(Lis2A2ResultMsg))
			conf.recipient.tell(LabResultMsg, self());
	}

	private void convertAndForwardResult(String[] resultArray) {
		//for (LabResultMsg LabResultMsg : lis2a2ResultMsgToLabResultMsgs(Lis2A2ResultMsg))
		//conf.recipient.tell(LabResultMsg, self());
	}

	public List<LabResultMsg> lis2a2ResultMsgToLabResultMsgs(LIS2A2ResultMsg Lis2A2ResultMsg) {
		
		return null;
	}

	///Store Query
	private void convertAndForwardQuery(LIS2A2QueryMsg lIS2A2QueryMsg) {
		for (LabQueryMsg myLabQueryMsg : astmQueryMsgToMyLabQueryMsgs(lIS2A2QueryMsg))
			conf.recipient.tell(myLabQueryMsg, self());
	}

	public List<LabQueryMsg> astmQueryMsgToMyLabQueryMsgs(LIS2A2QueryMsg LIS2A2QueryMsg) {
		List<LabQueryMsg> lstMyLabQueryMsg = null;
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(
				getContext().parent().toString());
		MachineTypeEnum machineType = MachineTypeEnum.valueOf(machine.getMachineType().getCode());
		switch (machineType) {
			case ROCHE_COBAS_6000:
			case ROCHE_COBAS_C311:
				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(3, 3).toString(),
															queryRecord.getAnalysisCodes(),
															new String(), queryRecord.getSpecimenPositionInfo()))
													.collect(toList());
				break;
			case ROCHE_COBAS_E411:
			case SYSMEX_XP_300:
				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(3).toString(),
															queryRecord.getAnalysisCodes(),
															new String(), queryRecord.getSpecimenPositionInfo()))
													.collect(toList());
				break;
			case BECHMAN_COULTER_AU:
				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(2).toString(),
															queryRecord.getAnalysisCodes(),
															new String(), queryRecord.getSpecimenPositionInfoAU()))
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

				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(2).toString(),
															queryRecord.getAnalysisCodes(),
															new String(), new String()))
													.collect(toList());
				break;

			case ABBOTT_CELL_DYN_RUBY:

				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getComponentValue(3, 2, "^").toString(),
															new String(), new String()))
													.collect(toList());
				break;
			case SYSMEX_CS_2000I:
				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getSysmexSpecimenId().toString(),
															queryRecord.getAnalysisCodes(),
															new String(), new String()))
													.collect(toList());
				break;
			case SYSMEX_SUITE:
				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(4).toString(),
															queryRecord.getAnalysisCodes(),
															new String(), new String()))
													.collect(toList());
				break;
			case ROCHE_ELECSYS_2010:
				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenIds(2).toString(),
															queryRecord.getAnalysisCodes(),
															new String(), queryRecord.getElecsysSpecimenPositionInfo()))
													.collect(toList());
				break;
			case BECHMAN_ACCESS_200_DXI:
				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getComponentValue(3, 2, "^").toString(),
															queryRecord.getAnalysisCodes(),
															new String(), new String()))
													.collect(toList());

				break;

			case DIASORIN_LIAISON_XL:
				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(3).toString(),
															queryRecord.getAnalysisCodes(),
															new String(), new String()))
													.collect(toList());
				break;

			case BECKMAN_UNICEL_800_DXH:
				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(3, 2, "!").toString(),
															queryRecord.getAnalysisCodes("!"),
															new String(), queryRecord.getElecsysSpecimenPositionInfoASTM02()))
													.collect(toList());
				break;

			case BECHMAN_COULTER_800_DXH:
			case BECHMAN_COULTER_AU480_DXC:
				lstMyLabQueryMsg = LIS2A2QueryMsg	.getQueryRecords().stream()
													.map(queryRecord -> new LabQueryMsg(queryRecord.getSpecimenId(3, 2, "!").toString(),
															queryRecord.getAnalysisCodes("!"),
															new String(), queryRecord.getElecsysSpecimenPositionInfoASTM02()))
													.collect(toList());
			default:
				break;

		}
		return lstMyLabQueryMsg;

	}
}