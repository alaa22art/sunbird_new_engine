package com.sunbird.machine.web.common.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.lis.interfaces.admin.model.SystemSetting;
import com.sunbird.lis.interfaces.admin.service.SystemSettingService;
import com.sunbird.lis.interfaces.entities.ComLanguage;
import com.sunbird.lis.interfaces.entities.ComTenantLanguage;
import com.sunbird.lis.interfaces.entities.ComTenantMessage;
import com.sunbird.lis.interfaces.service.ComLanguageService;
import com.sunbird.lis.interfaces.service.ComTenantLanguageService;
import com.sunbird.lis.interfaces.service.ComTenantMessageService;

/**
 * CommonsController.java
 * 
 **/
@RestController
@RequestMapping("/services")
public class CommonsController {

	@Autowired
	private ComTenantMessageService comTenantMessageService;

	@Autowired
	private ComLanguageService languageService;

	@Autowired
	private ComTenantLanguageService tenantLanguageService;
	
	@Autowired
	private SystemSettingService systemSettingService;


	@RequestMapping(value = "/getTenantMessagesList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<ComTenantMessage>> getTenantMessagesList() {

		return new ResponseEntity<List<ComTenantMessage>>(comTenantMessageService.findTenantMessagesList(),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getLabels.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<ComTenantMessage>> getLabels(@RequestBody(required = false) Long tenantId) {
		List<ComTenantMessage> messages = new ArrayList<>();
		//if tenant id exists then get the labels otherwise get defaults labels 
		if (tenantId != null) {
			messages = comTenantMessageService.findLabels(ComTenantMessage.class, tenantId);
		} else {
			messages = comTenantMessageService.findDefaultLabels(ComTenantMessage.class);
		}
		return new ResponseEntity<List<ComTenantMessage>>(messages, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateTenantMessage.srvc", method = RequestMethod.POST)
	public ResponseEntity<ComTenantMessage> updateTenantMessage(@RequestBody ComTenantMessage tenantMessage) {

		return new ResponseEntity<ComTenantMessage>(
				comTenantMessageService.updateTenantMessage(tenantMessage, ComTenantMessage.class),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/createTenantMessage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Void> createTenantMessage(@RequestBody ComTenantMessage tenantMessage) {
		comTenantMessageService.createTenantMessage(tenantMessage);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteTenantMessage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Void> deleteTenantMessage(@RequestBody ComTenantMessage tenantMessage) {
		comTenantMessageService.deleteTenantMessage(tenantMessage);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/getSupportedLanguages.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<ComLanguage>> getSupportedLanguages() {
		return new ResponseEntity<List<ComLanguage>>(languageService.find(new ArrayList<>(), ComLanguage.class), HttpStatus.OK);
	}

	@RequestMapping(value = "/getTenantLanguages.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<ComTenantLanguage>> getTenantLanguages() {
		return new ResponseEntity<List<ComTenantLanguage>>(
				tenantLanguageService.findTenantLanguages(new ArrayList<>(), null, "comLanguage"), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getIsEnabledToViewPCR.srvc", method = RequestMethod.POST)
	public ResponseEntity<Boolean> getIsEnabledToViewPCR(@RequestBody String viewAttribute) {
		return new ResponseEntity<Boolean>(
				systemSettingService.getIsEnabledToViewModule(viewAttribute), HttpStatus.OK);
	}


	@RequestMapping(value = "/setTenantLanguages.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<ComTenantLanguage>> setTenantLanguages(@RequestBody List<ComTenantLanguage> tenantLanguages) {
		return new ResponseEntity<List<ComTenantLanguage>>(tenantLanguageService.updateTenantLanguages(tenantLanguages), HttpStatus.OK);
	}

}
