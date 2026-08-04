package com.certacure.machine.web.security.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.certacure.lis.interfaces.admin.model.SecTenant;
import com.certacure.lis.interfaces.admin.service.SecTenantService;
import com.certacure.lis.interfaces.entities.ComTenantLanguage;
import com.certacure.lis.interfaces.service.ComTenantLanguageService;
import com.certacure.machine.web.util.ImageUtil;




/**
 * SecTenantController.java
 * 

 **/
@RestController
@RequestMapping("/services")
public class SecTenantController {

	@Autowired
	private SecTenantService tenantService;
	@Autowired
	private ComTenantLanguageService tenantLanguageService;

	@RequestMapping(value = "/createTenant.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<SecTenant> createTenant(@RequestParam(value = "logo", required = false) MultipartFile logo,
			@RequestParam(value = "tenant") String tenant,
			@RequestParam(value = "tenantLanguages") String tenantLanguages) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SecTenant secTenant = mapper.readValue(tenant, SecTenant.class);
		if (logo != null) {
			secTenant.setLogo(ImageUtil.reduceSize(logo, 500, 500));
		}

		secTenant = tenantService.createTenant(secTenant, Boolean.TRUE);
		List<ComTenantLanguage> comTenantLanguages = mapper.readValue(tenantLanguages, new TypeReference<List<ComTenantLanguage>>() {
		});
		for (ComTenantLanguage ctl : comTenantLanguages) {
			ctl.setTenantId(secTenant.getRid());
		}
		comTenantLanguages = tenantLanguageService.createTenantLanguages(comTenantLanguages);
		return new ResponseEntity<SecTenant>(tenantService.createTenant(secTenant, Boolean.TRUE), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateTenant.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<SecTenant> updateTenant(@RequestParam(value = "logo", required = false) MultipartFile logo,
			@RequestParam(value = "tenant") String tenant,
			@RequestParam(value = "tenantLanguages") String tenantLanguages) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SecTenant secTenant = mapper.readValue(tenant, SecTenant.class);
		if (logo != null) {
			secTenant.setLogo(ImageUtil.reduceSize(logo, 500, 500));
		}

		List<ComTenantLanguage> comTenantLanguages = mapper.readValue(tenantLanguages, new TypeReference<List<ComTenantLanguage>>() {
		});
		tenantLanguageService.updateTenantLanguages(comTenantLanguages);
		return new ResponseEntity<SecTenant>(tenantService.updateTenant(secTenant), HttpStatus.OK);
	}

	@RequestMapping(value = "/getTenantData.srvc", method = RequestMethod.POST)
	public ResponseEntity<SecTenant> getTenantData(@RequestBody Long rid) {
		return new ResponseEntity<SecTenant>(tenantService.getTenantData(rid), HttpStatus.OK);
	}

	@RequestMapping(value = "/getTenants.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<SecTenant>> getTenants() {
		return new ResponseEntity<List<SecTenant>>(tenantService.findTenants(new ArrayList<>(), null), HttpStatus.OK);
	}

}
