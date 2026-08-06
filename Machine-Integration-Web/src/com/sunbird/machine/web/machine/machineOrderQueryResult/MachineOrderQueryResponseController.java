package com.sunbird.machine.web.machine.machineOrderQueryResult;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.lis.interfaces.entities.MachineOrderQueryResponse;
import com.sunbird.lis.interfaces.service.MachineOrderQueryResponceService;

@RestController
@RequestMapping("/services")

public class MachineOrderQueryResponseController {

	@Autowired
	private MachineOrderQueryResponceService machineOrderQueryResponceService;

	@RequestMapping(value = "/getQueryResultResponse.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<MachineOrderQueryResponse>> getOrderQueryResponse(@RequestBody Long rid) {
		Set<MachineOrderQueryResponse> machineOrderQueryResponse = machineOrderQueryResponceService.getQueryResponseByOrderId(rid);
		return new ResponseEntity<Set<MachineOrderQueryResponse>>(machineOrderQueryResponse, HttpStatus.OK);
	}

}
