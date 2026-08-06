package com.sunbird.machine.web.rtpcr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.entities.PCRRealTimeOrder;
import com.sunbird.lis.interfaces.service.PCRRealTimeOrderService;

@RestController
@RequestMapping("/services")
public class PCRRealTimeOrderController {

	@Autowired
	private PCRRealTimeOrderService PCRRealTimeOrderService;

	@RequestMapping(value = "/getPcrOrdersPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<PCRRealTimeOrder>> getPcrOrdersPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<PCRRealTimeOrder>>(
				PCRRealTimeOrderService.getPcrOrdersPage(filterablePageRequest),
				HttpStatus.OK);
	}

}
