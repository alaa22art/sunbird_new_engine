package com.certacure.machine.web.protocol.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.certacure.lis.interfaces.entities.LkpProtocol;
import com.certacure.lis.interfaces.service.ProtocolService;

@RestController
@RequestMapping("/services")
public class ProtocolController {

	@Autowired
	private ProtocolService protocolService;

	@RequestMapping(value = "/addProtocol.srvc", method = RequestMethod.POST)
	public ResponseEntity<LkpProtocol> addProtocol(@RequestBody LkpProtocol protocol) {

		this.protocolService.addProtocol(protocol);

		return new ResponseEntity<LkpProtocol>(protocol, HttpStatus.OK);
	}

	@RequestMapping(value = "/getProtocolList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LkpProtocol>> getProtocolList() {

		List<LkpProtocol> protocolList = this.protocolService.findAll();

		return new ResponseEntity<List<LkpProtocol>>(protocolList, HttpStatus.OK);

	}

	@RequestMapping(value = "/deleteProtocol.srvc", method = RequestMethod.POST)
	public ResponseEntity<LkpProtocol> deleteProtocol(@RequestBody LkpProtocol protocol) {

		this.protocolService.deleteProtocol(protocol.getRid());

		return new ResponseEntity<LkpProtocol>(protocol, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateProtocol.srvc", method = RequestMethod.POST)
	public ResponseEntity<LkpProtocol> updateProtocol(@RequestBody LkpProtocol protocol) {

		this.protocolService.updateProtocol(protocol);

		return new ResponseEntity<LkpProtocol>(protocol, HttpStatus.OK);

	}

}
