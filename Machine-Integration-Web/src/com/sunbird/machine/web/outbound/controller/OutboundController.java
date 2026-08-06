package com.sunbird.machine.web.outbound.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.lis.interfaces.entities.OutboundInformation;
import com.sunbird.lis.interfaces.service.OutboundService;

@RestController
@RequestMapping("/services")
public class OutboundController {

	@Autowired
	private OutboundService outbounService;

	@RequestMapping(value = "/getOutboundList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<OutboundInformation>> getOutboundList() {

		List<OutboundInformation> outboundInformationList = this.outbounService.findAll();

		return new ResponseEntity<List<OutboundInformation>>(outboundInformationList, HttpStatus.OK);
	}

	@RequestMapping(value = "/addOutboundOrder.srvc", method = RequestMethod.POST)
	public ResponseEntity<OutboundInformation> addOutboundOrder(@RequestBody OutboundInformation outBoundInformation) {

		outbounService.addOutbound(outBoundInformation);

		return new ResponseEntity<OutboundInformation>(outBoundInformation, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateOutboundOrder.srvc", method = RequestMethod.POST)
	public ResponseEntity<OutboundInformation> updateOutboundOrder(@RequestBody OutboundInformation outBoundInformation) {

		outbounService.updateOutbound(outBoundInformation);

		return new ResponseEntity<OutboundInformation>(outBoundInformation, HttpStatus.OK);
	}

}
