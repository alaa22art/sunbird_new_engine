package com.sunbird.machine.web.security.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.lis.interfaces.admin.model.SecRight;
import com.sunbird.lis.interfaces.admin.service.SecRightService;

/**
 * SecRightController.java
 * 

 **/
@RestController
@RequestMapping("/services")
public class SecRightController {

	@Autowired
	private SecRightService secRightService;

	@RequestMapping(value = "/getSecRightList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<SecRight>> getSecRightList() {
		return new ResponseEntity<List<SecRight>>(secRightService.findRights(), HttpStatus.OK);
	}

}
