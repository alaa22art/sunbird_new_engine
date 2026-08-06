package com.sunbird.machine.web.machine.log.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.entities.CoreEventLog;
import com.sunbird.lis.interfaces.service.CoreEventLogService;;

@RestController
@RequestMapping("/services")
public class CoreEventLogController {

	@Autowired
	private CoreEventLogService coreEventLogService;

	@RequestMapping(value = "/getEvenLogPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getMachinePage(@RequestBody FilterablePageRequest filterablePageRequest) {
		Map<String, Object> result = new HashMap<>();
		Page<CoreEventLog> pageable = coreEventLogService.getCoreEventLog(filterablePageRequest);
		result.put("data", pageable.getContent());
		result.put("total", pageable.getTotalElements());

		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

}
