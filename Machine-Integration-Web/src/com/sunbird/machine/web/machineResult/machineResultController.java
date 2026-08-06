package com.sunbird.machine.web.machineResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.entities.LabBranch;
import com.sunbird.lis.interfaces.entities.MachineResult;
import com.sunbird.lis.interfaces.service.LabBranchService;
import com.sunbird.lis.interfaces.service.MachineResultService;

@RestController
@RequestMapping("/services")
public class machineResultController {

	@Autowired
	private MachineResultService machineResultService;

	@Autowired
	private LabBranchService branchService;

	@RequestMapping(value = "/getMachineResultList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<MachineResult>> findMachineResultByMachine(@RequestBody FilterablePageRequest fpr) {
		return new ResponseEntity<List<MachineResult>>(machineResultService.getMachineResultList(fpr), HttpStatus.OK);
	}

	@RequestMapping(value = "/getMachineResultPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<MachineResult>> find(@RequestBody FilterablePageRequest machineResultFilter) {
		Page<MachineResult> machineList = machineResultService.getResultByMachine(machineResultFilter);
		return new ResponseEntity<Page<MachineResult>>(machineList, HttpStatus.OK);
	}

	@RequestMapping(value = "/resendMachineResults.srvc", method = RequestMethod.POST)
	public void resendMachineResults(@RequestBody List<MachineResult> machineResultList) {
		LabBranch branch = branchService.findById(machineResultList.get(0).getMachine().getBranchId());
		List<Map<String, String>> resultMapList = new ArrayList<Map<String, String>>();

		for (MachineResult machineResult : machineResultList) {
			Map<String, String> resultMap = new HashMap<String, String>();
			if (machineResult != null) {
				resultMap.put("testCode", machineResult.getTestCode());
			}
			resultMap.put("sampleNo", machineResult.getSampleNo());
			resultMap.put("result", machineResult.getDataOrMeasurementValue());
			machineResult.setIsSentToLIS(true);
			machineResultService.updateResult(machineResult);
			resultMapList.add(resultMap);
		}

		performRequest(branch.getIntegrationUrl(), resultMapList, Void.class, branch.getIntegrationToken());
	}

	private <T> T performRequest(String url, Object objectToSend, Class<T> responseType, String token) {
		HttpHeaders headers = new HttpHeaders();
		//headers.set("Authorization", token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> request = new HttpEntity<Object>(objectToSend, headers);

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.postForObject(url, request, responseType);
	}

	// public List<Map<String, String>> resultMapList = new ArrayList<Map<String, String>>();

}
