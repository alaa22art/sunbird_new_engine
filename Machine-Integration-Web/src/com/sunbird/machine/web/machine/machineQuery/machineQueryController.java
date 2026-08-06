package com.sunbird.machine.web.machine.machineQuery;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.lis.interfaces.entities.MachineQuery;
import com.sunbird.lis.interfaces.service.MachineQueryService;

@RestController
@RequestMapping("/services")
public class machineQueryController {

	@Autowired
	private MachineQueryService machineQueryService;

	@RequestMapping(value = "/getQueryResult.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<MachineQuery>> getQueryResultInfo(@RequestBody Map<String, String> queryResultMap) {
		Long rid = Long.parseLong(queryResultMap.get("rid"));

		String testCode = queryResultMap.get("testCode");
		Set<MachineQuery> machineQueryResponse = machineQueryService.getQueryResult(rid, testCode);
		return new ResponseEntity<Set<MachineQuery>>(machineQueryResponse, HttpStatus.OK);
	}

}
