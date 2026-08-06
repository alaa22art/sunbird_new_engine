package com.sunbird.machine.web.rtpcr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.lis.interfaces.entities.PCRRealTimeWorkListOrder;
import com.sunbird.lis.interfaces.service.PCRRealTimeWorkListOrderService;
import com.sunbird.lis.interfaces.wrapper.PCRWorkListOrderWrapper;

@RestController
@RequestMapping("/services")
public class PCRRealTimeWorkListOrderController {

	@Autowired
	private PCRRealTimeWorkListOrderService pcrRealTimeWorkListOrderService;

	@RequestMapping(value = "/addPcrWorkListOrder.srvc", method = RequestMethod.POST)
	public ResponseEntity<PCRRealTimeWorkListOrder> addPcrWorkListOrder(@RequestBody PCRWorkListOrderWrapper pcrWorkListOrderWrapper) {
		return new ResponseEntity<PCRRealTimeWorkListOrder>(
				pcrRealTimeWorkListOrderService.addPcrRealTimeWorkListOrder(pcrWorkListOrderWrapper), HttpStatus.OK);
	}

	@RequestMapping(value = "/unloadPcrWorkListOrder.srvc", method = RequestMethod.POST)
	public ResponseEntity<PCRRealTimeWorkListOrder> unloadPcrWorkListOrder(@RequestBody PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder) {
		return new ResponseEntity<PCRRealTimeWorkListOrder>(
				pcrRealTimeWorkListOrderService.unloadPcrWorkListOrder(pcrRealTimeWorkListOrder), HttpStatus.OK);

	}

	@RequestMapping(value = "/updateWorkListOrder.srvc", method = RequestMethod.POST)
	public ResponseEntity<PCRRealTimeWorkListOrder> updateWorkListOrder(@RequestBody PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder) {
		return new ResponseEntity<PCRRealTimeWorkListOrder>(
				pcrRealTimeWorkListOrderService.updatePcrRealTimeWorkListOrder(pcrRealTimeWorkListOrder), HttpStatus.OK);
	}

}
