package com.sunbird.lis.interfaces.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;
import com.sunbird.core.common.util.JSONUtil;
import com.sunbird.core.common.util.StringUtil;
import com.sunbird.lis.interfaces.entities.CoreEventLog;
import com.sunbird.lis.interfaces.entities.LabBranch;
import com.sunbird.lis.interfaces.entities.PCRRealTimeActualResultValue;
import com.sunbird.lis.interfaces.entities.PCRRealTimeOrderResult;
import com.sunbird.lis.interfaces.entities.PCRRealTimeResult;
import com.sunbird.lis.interfaces.entities.PCRRealTimeResultTemplate;
import com.sunbird.lis.interfaces.entities.PCRRealTimeResultTemplateLine;
import com.sunbird.lis.interfaces.entities.PCRRealTimeWorkListOrder;
import com.sunbird.lis.interfaces.middleware.util.PcrOperationEnum;
import com.sunbird.lis.interfaces.middleware.util.WorkListStatusEnum;
import com.sunbird.lis.interfaces.repo.PCRRealTimeActualResultValueRepo;
import com.sunbird.lis.interfaces.wrapper.PCRActualResultValueWrapper;
import com.sunbird.lis.interfaces.wrapper.PCRRealTimeActualResultWrapper;

@Service("PCRRealTimeActualResultValueService")
public class PCRRealTimeActualResultValueService
		extends GenericService<PCRRealTimeActualResultValue, PCRRealTimeActualResultValueRepo> {

	@Autowired
	private PCRRealTimeActualResultValueRepo repo;

	@Autowired
	private PCRRealTimeResultService pcrRealTimeResultService;

	@Autowired
	private PCRRealTimeOrderResultService pcrRealTimeOrderResultService;

	@Autowired
	private PCRRealTimeResultTemplateLineService pcrRealTimeResultTemplateLineService;

	@Autowired
	private PCRRealTimeWorkListService pcrRealTimeWorkListService;

	@Autowired
	private PCRRealTimeWorkListOrderService pcrRealTimeWorkListOrderService;

	@Autowired
	private LabBranchService branchService;

	@Override
	protected PCRRealTimeActualResultValueRepo getRepository() {
		return repo;
	}

	public PCRRealTimeActualResultValue addPcrRealTimeActualResultValue(String actualValue,
			PCRRealTimeResultTemplateLine pcrRealTimeResultTemplateLine,
			PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder) {

		boolean canDoOperation = pcrRealTimeWorkListService.canDoOperation(
				pcrRealTimeWorkListOrder.getPcrRealTimeWorkList().getWorkListStatus().getCode(),
				PcrOperationEnum.CHANGE_ORDER_RESULT);
		if (!canDoOperation) {
			throw new BusinessException("Can Not Add Edit Order Result In This WorkList Status", "canntEditOrderResultInThisStatus",
					ErrorSeverity.ERROR);
		}

		PCRRealTimeResult pcrRealTimeResult = new PCRRealTimeResult();
		PCRRealTimeOrderResult pcrRealTimeOrderResult = new PCRRealTimeOrderResult();
		PCRRealTimeActualResultValue pcrRealTimeActualResultValue = new PCRRealTimeActualResultValue();
		PCRRealTimeResultTemplate pcrRealTimeResultTemplate = pcrRealTimeResultTemplateLine
																							.getPcrRealTimeResultTemplate();

		pcrRealTimeResult.setPcrRealTimeResultTemplate(pcrRealTimeResultTemplate);
		pcrRealTimeResultService.addPcrRealTimeResult(pcrRealTimeResult);

		pcrRealTimeActualResultValue.setValue(actualValue);
		pcrRealTimeActualResultValue.setPcrRealTimeResultTemplateLine(pcrRealTimeResultTemplateLine);
		pcrRealTimeActualResultValue.setPcrRealTimeResult(pcrRealTimeResult);

		pcrRealTimeOrderResult.setPcrRealTimeResult(pcrRealTimeResult);
		pcrRealTimeOrderResult.setPcrRealTimeWorkListOrder(pcrRealTimeWorkListOrder);

		pcrRealTimeOrderResultService.addOrderResult(pcrRealTimeOrderResult);

		return getRepository().save(pcrRealTimeActualResultValue);

	}

	public PCRRealTimeActualResultValue addPcrRealTimeActualResultValue(
			PCRActualResultValueWrapper pcrActualResultValueWrapper) {

		boolean canDoOperation = pcrRealTimeWorkListService.canDoOperation(
				pcrActualResultValueWrapper.getPcrRealTimeWorkListOrder().getPcrRealTimeWorkList().getWorkListStatus().getCode(),
				PcrOperationEnum.CHANGE_ORDER_RESULT);
		if (!canDoOperation) {
			throw new BusinessException("Can Not Add Edit Order Result In This WorkList Status", "canntEditOrderResultInThisStatus",
					ErrorSeverity.ERROR);
		}

		PCRRealTimeResult pcrRealTimeResult = new PCRRealTimeResult();
		PCRRealTimeOrderResult pcrRealTimeOrderResult = new PCRRealTimeOrderResult();
		PCRRealTimeActualResultValue pcrRealTimeActualResultValue = new PCRRealTimeActualResultValue();
		PCRRealTimeResultTemplateLine pcrRealTimeResultTemplateLine = pcrRealTimeResultTemplateLineService.findOne(
				Arrays.asList(
						new SearchCriterion("code", pcrActualResultValueWrapper.getLineCode(), FilterOperator.eq)),
				PCRRealTimeResultTemplateLine.class);
		PCRRealTimeResultTemplate pcrRealTimeResultTemplate = pcrRealTimeResultTemplateLine
																							.getPcrRealTimeResultTemplate();

		pcrRealTimeResult.setPcrRealTimeResultTemplate(pcrRealTimeResultTemplate);
		pcrRealTimeResult = pcrRealTimeResultService.addPcrRealTimeResult(pcrRealTimeResult);

		pcrRealTimeOrderResult.setPcrRealTimeResult(pcrRealTimeResult);
		pcrRealTimeOrderResult.setPcrRealTimeWorkListOrder(pcrActualResultValueWrapper.getPcrRealTimeWorkListOrder());
		pcrRealTimeOrderResultService.addOrderResult(pcrRealTimeOrderResult);

		pcrRealTimeActualResultValue.setValue(pcrActualResultValueWrapper.getActualValue());
		pcrRealTimeActualResultValue.setPcrRealTimeResultTemplateLine(pcrRealTimeResultTemplateLine);
		pcrRealTimeActualResultValue.setPcrRealTimeResult(pcrRealTimeResult);
		return getRepository().save(pcrRealTimeActualResultValue);

	}

	public PCRRealTimeActualResultValue updatePcrRealTimeActualResultValue(
			PCRRealTimeActualResultValue pcrRealTimeActualResultValue) {
		return getRepository().save(pcrRealTimeActualResultValue);
	}

	public List<PCRRealTimeActualResultValue> addPcrRealTimeActualResultValues(
			PCRRealTimeActualResultWrapper pcrRealTimeActualResultWrapper) {

		boolean canDoOperation = pcrRealTimeWorkListService.canDoOperation(
				pcrRealTimeActualResultWrapper.getPcrRealTimeWorkListOrder().getPcrRealTimeWorkList().getWorkListStatus().getCode(),
				PcrOperationEnum.CHANGE_ORDER_RESULT);
		if (!canDoOperation) {
			throw new BusinessException("Can Not Add Edit Order Result In This WorkList Status", "canntEditOrderResultInThisStatus",
					ErrorSeverity.ERROR);
		}

		PCRRealTimeOrderResult pcrRealTimeOrderResult = pcrRealTimeOrderResultService.getPcrRealTimeOrderResult(
				pcrRealTimeActualResultWrapper.getPcrRealTimeWorkListOrder().getRid());
		PCRRealTimeResult pcrRealTimeResult = new PCRRealTimeResult();
		List<PCRRealTimeActualResultValue> savedActualResultValues = new ArrayList<PCRRealTimeActualResultValue>();
		if (pcrRealTimeOrderResult == null) {
			pcrRealTimeResult = new PCRRealTimeResult();
			pcrRealTimeResultService.addPcrRealTimeResult(pcrRealTimeResult);

			pcrRealTimeOrderResult = new PCRRealTimeOrderResult();
			pcrRealTimeOrderResult.setPcrRealTimeWorkListOrder(pcrRealTimeActualResultWrapper.getPcrRealTimeWorkListOrder());
			;
			pcrRealTimeOrderResult.setPcrRealTimeResult(pcrRealTimeResult);
			pcrRealTimeOrderResultService.addOrderResult(pcrRealTimeOrderResult);
		} else {
			pcrRealTimeResult = pcrRealTimeOrderResult.getPcrRealTimeResult();
		}

		for (PCRRealTimeActualResultValue pcrRealTimeActualResultValue : pcrRealTimeActualResultWrapper
																										.getPcrRealTimeActualValues()) {
			pcrRealTimeActualResultValue.setPcrRealTimeResult(pcrRealTimeResult);
			savedActualResultValues.add(getRepository().save(pcrRealTimeActualResultValue));
		}
		return savedActualResultValues;

	}

	public List<PCRRealTimeActualResultValue> getPcrRealTimeActualResultValues(Long worklistOrderId) {
		List<PCRRealTimeActualResultValue> pcrRealTimeActualResultValues = new ArrayList<PCRRealTimeActualResultValue>();
		PCRRealTimeOrderResult pcrRealTimeOrderResult = pcrRealTimeOrderResultService.getPcrRealTimeOrderResult(worklistOrderId);
		if (pcrRealTimeOrderResult == null) {
			return pcrRealTimeActualResultValues;
		}
		pcrRealTimeActualResultValues = getRepository().find(
				Arrays.asList(new SearchCriterion("pcrRealTimeResult.rid",
						pcrRealTimeOrderResult.getPcrRealTimeResult().getRid(), FilterOperator.eq)),
				PCRRealTimeActualResultValue.class, "pcrRealTimeResultTemplateLine", "pcrRealTimeResult");

		return pcrRealTimeActualResultValues;
	}

	public List<PCRRealTimeActualResultValue> updateActualResultValues(
			PCRRealTimeActualResultWrapper pcrRealTimeActualResultWrapper) {

		boolean canDoOperation = pcrRealTimeWorkListService.canDoOperation(
				pcrRealTimeActualResultWrapper.getPcrRealTimeWorkListOrder().getPcrRealTimeWorkList().getWorkListStatus().getCode(),
				PcrOperationEnum.CHANGE_ORDER_RESULT);
		if (!canDoOperation) {
			throw new BusinessException("Can Not Add Edit Order Result In This WorkList Status", "canntEditOrderResultInThisStatus",
					ErrorSeverity.ERROR);
		}

		List<PCRRealTimeActualResultValue> savedActualResultValues = new ArrayList<PCRRealTimeActualResultValue>();

		for (PCRRealTimeActualResultValue pcrRealTimeActualResultValue : pcrRealTimeActualResultWrapper
																										.getPcrRealTimeActualValues()) {
			savedActualResultValues.add(getRepository().save(pcrRealTimeActualResultValue));
		}

		return savedActualResultValues;
	}

	public List<PCRRealTimeActualResultValue> addActualResultValues(
			PCRRealTimeActualResultWrapper pcrRealTimeActualResultWrapper) {

		boolean canDoOperation = pcrRealTimeWorkListService.canDoOperation(
				pcrRealTimeActualResultWrapper.getPcrRealTimeWorkListOrder().getPcrRealTimeWorkList().getWorkListStatus().getCode(),
				PcrOperationEnum.CHANGE_ORDER_RESULT);
		if (!canDoOperation) {
			throw new BusinessException("Can Not Add Edit Order Result In This WorkList Status", "canntEditOrderResultInThisStatus",
					ErrorSeverity.ERROR);
		}

		List<PCRRealTimeActualResultValue> savedActualResultValues = new ArrayList<PCRRealTimeActualResultValue>();

		for (PCRRealTimeActualResultValue pcrRealTimeActualResultValue : pcrRealTimeActualResultWrapper
																										.getPcrRealTimeActualValues()) {
			savedActualResultValues.add(addPcrRealTimeActualResultValue(pcrRealTimeActualResultValue.getValue(),
					pcrRealTimeActualResultValue.getPcrRealTimeResultTemplateLine(),
					pcrRealTimeActualResultWrapper.getPcrRealTimeWorkListOrder()));
		}

		return savedActualResultValues;
	}

	public void saveActualResultValues(PCRRealTimeActualResultWrapper pcrRealTimeActualResultWrapper) {
		List<PCRRealTimeActualResultValue> resultsToAdd = new ArrayList<PCRRealTimeActualResultValue>();
		List<PCRRealTimeActualResultValue> resultsToUpdate = new ArrayList<PCRRealTimeActualResultValue>();

		pcrRealTimeWorkListOrderService.updatePcrRealTimeWorkListOrder(pcrRealTimeActualResultWrapper.getPcrRealTimeWorkListOrder());

		for (PCRRealTimeActualResultValue pcrRealTimeActualResultValue : pcrRealTimeActualResultWrapper.getPcrRealTimeActualValues()) {
			if (pcrRealTimeActualResultValue.getPcrRealTimeResult() != null) {
				resultsToUpdate.add(pcrRealTimeActualResultValue);
			} else {
				resultsToAdd.add(pcrRealTimeActualResultValue);
			}
		}

		if (resultsToUpdate.size() > 0) {
			PCRRealTimeActualResultWrapper updateResultValueWrapper = new PCRRealTimeActualResultWrapper();
			updateResultValueWrapper.setPcrRealTimeActualValues(resultsToUpdate);
			updateResultValueWrapper.setPcrRealTimeWorkListOrder(pcrRealTimeActualResultWrapper.getPcrRealTimeWorkListOrder());
			updateActualResultValues(updateResultValueWrapper);
		}

		if (resultsToAdd.size() > 0) {
			PCRRealTimeActualResultWrapper addResultValueWrapper = new PCRRealTimeActualResultWrapper();
			addResultValueWrapper.setPcrRealTimeActualValues(resultsToAdd);
			addResultValueWrapper.setPcrRealTimeWorkListOrder(pcrRealTimeActualResultWrapper.getPcrRealTimeWorkListOrder());
			addPcrRealTimeActualResultValues(addResultValueWrapper);
		}

	}

	public boolean submitActualResultValues(List<PCRRealTimeWorkListOrder> lstWorklistOrder)
			throws JsonParseException, JsonMappingException, IOException {
		String workListStatus;
		try {
			workListStatus = lstWorklistOrder.get(0).getPcrRealTimeWorkList().getWorkListStatus().getCode();
		} catch (Exception e) {
			throw new BusinessException(); // customize the excpetion later
		}
		if (!WorkListStatusEnum.FINALIZED.getValue().equals(workListStatus)
				&& !WorkListStatusEnum.RESULTS_ENTRY.getValue().equals(workListStatus)) {
			throw new BusinessException("You Can't Send Results In This Worklist Status", "canntSendResultsInThisStatus",
					ErrorSeverity.ERROR);
		}

		PCRRealTimeWorkListOrder tempWorklistOrder = null;
		PCRRealTimeOrderResult tempOrderResult = null;
		PCRRealTimeResult tempResult = null;
		List<PCRRealTimeResult> lstResultsSent = new ArrayList<PCRRealTimeResult>();
		Long branchID = lstWorklistOrder.get(0).getBranchId();
		List<Map<String, String>> resultMapList;
		//		Map<String, String> resultMap;
		//		resultMap = new HashMap<String, String>();
		resultMapList = new ArrayList<Map<String, String>>();

		for (int index = 0; index < lstWorklistOrder.size(); index++) {
			tempWorklistOrder = lstWorklistOrder.get(index);
			if (!tempWorklistOrder.getIsConfirmed()) {
				continue;
			}
			tempOrderResult = tempWorklistOrder.getPcrRealTimeOrderResults().stream().findFirst().orElse(null);
			if (tempOrderResult == null) {
				continue;
			}
			tempOrderResult.getPcrRealTimeResult().setIsResultSent(true);
			lstResultsSent.add(tempOrderResult.getPcrRealTimeResult());
			tempResult = tempOrderResult.getPcrRealTimeResult();
			for (PCRRealTimeActualResultValue actualResultValue : tempResult.getPcrRealTimeActualResultValues()) {
				Map<String, String> resultMap = new HashMap<String, String>();
				resultMap.put("barcode", tempWorklistOrder.getPcrRealTimeOrder().getMachineOrder().getBarcode());
				resultMap.put("testCode", actualResultValue.getPcrRealTimeResultTemplateLine().getCode());
				resultMap.put("result", actualResultValue.getValue().toLowerCase());
				resultMapList.add(resultMap);
			}

		}

		sendRecivedResultListToLIS(resultMapList, branchID, lstResultsSent);

		return true;
	}

	private List<PCRRealTimeActualResultValue> convertSetToList(Set<PCRRealTimeActualResultValue> tempSet) {
		List<PCRRealTimeActualResultValue> lstData = new ArrayList<PCRRealTimeActualResultValue>(tempSet);

		return lstData;
	}

	private LabBranch getMachineBranch(Long BranchID) {
		return branchService.findById(BranchID);
	}

	private CoreEventLog getEventLogObject(String output, long branchID, long tenantID, Exception ex) {
		CoreEventLog coreEventLog = new CoreEventLog();
		coreEventLog.setBranchId(branchID);
		coreEventLog.setTenantId(tenantID);
		coreEventLog.setSource(ex.toString());
		coreEventLog.setText(output);
		coreEventLog.setMachine(null);
		coreEventLog.setStatusId(4);
		Date date = new Date();
		coreEventLog.setSentDate(date);
		return coreEventLog;
	}

	private void sendRecivedResultListToLIS(List<Map<String, String>> resultMapList, long branchID, List<PCRRealTimeResult> lstResultsSent)
			throws IOException, JsonParseException, JsonMappingException {

		LabBranch branch = getMachineBranch(branchID);
		String resultAsJason = JSONUtil.convertObjectToJSON(resultMapList);
		//		ObjectMapper objectMapper;

		try {

			if (branch != null && StringUtil.isNotEmpty(resultAsJason)) {
				String apiResponse = executePOST(resultAsJason, branch.getIntegrationUrl(), branch.getIntegrationToken());
				pcrRealTimeResultService.updateResults(lstResultsSent);
			}

		} catch (Exception e) {
			throw new BusinessException(e.toString(), "canntSendResults", ErrorSeverity.ERROR);
		}
		//objectMapper = new ObjectMapper();
		//List<Map<String, String>> responseMap = objectMapper.readValue(apiResponse, new TypeReference<List<Map<String, String>>>() {
		//});

		//for (Map<String, String> mapResponse : responseMap) {
		//boolean isAccepted = Boolean.parseBoolean(mapResponse.get("isAccepted"));
		//if (isAccepted == false) {
		//String output = MapUtil.mapToString(mapResponse);
		//CoreEventLog coreEventLog = getEventLogObject(output);
		//coreEventLogService.addCoreEventLog(coreEventLog);
		//}
		//}
		//} else {
		//	String strError = "\n------------------------------------------------------------------------------------------------------------\n"
		//					+ "------------------------------------------------------------------------------------------------------------\n"
		//
		//					+ " [Error On Sending Result ] : Sending error happed when sending data from LIS to MIW for " + "\n" + " Machine : "
		//					+ machine.getName() + "\n"
		//					+ " For Order with Sample ID : " + "\n"
		//					+ barcode + "\n"
		//					+ " For The Follwing Reason : " + "\n"
		//					+ "check dependencies reference if null " + "\n"
		//
		//					+ "\n----------------------------------------------------------------------------------------------------------------------\n"
		//					+ "------------------------------------------------------------------------------------------------------------\n";
		//
		//			if (runMode == RUN_MODE.DEBUG) {
		//				System.out.println(strError);
		//			}
		//
		//			CoreEventLog eventLog = getEventLogObject(strError);
		//			coreEventLogService.addCoreEventLog(eventLog);
		//		}

	}

	private String executePOST(String requestBody, String targetUrl, String token) throws IOException {
		HttpURLConnection connection = null;
		String authorizationToken = token;
		try {
			URL url = new URL(targetUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", authorizationToken);
			//			DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
			OutputStreamWriter outStream = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
			outStream.write(requestBody);
			outStream.flush();
			outStream.close();
			InputStream inStream = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
			System.out.print("#########################SENT SUCCESSFULLY##########################");
			return br.lines().collect(Collectors.joining());
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

}
