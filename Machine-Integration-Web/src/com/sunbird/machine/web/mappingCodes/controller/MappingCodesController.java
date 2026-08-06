package com.sunbird.machine.web.mappingCodes.controller;

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
import com.sunbird.lis.interfaces.entities.MappingCodes;
import com.sunbird.lis.interfaces.entities.TestCatalog;
import com.sunbird.lis.interfaces.service.MappingCodesService;
import com.sunbird.lis.interfaces.service.TestCatalogService;

@RestController
@RequestMapping("/services")
public class MappingCodesController {

	@Autowired
	private TestCatalogService testCatalogeService;
	
	@Autowired
	private MappingCodesService mappingCodesService;

	@RequestMapping(value = "/getMappingCodesById.srvc", method = RequestMethod.POST)
	public ResponseEntity<MappingCodes> getMappingCodesById(@RequestBody Long rId) {
		MappingCodes mappingCodes = mappingCodesService.getMappingCodesById(rId);
		return new ResponseEntity<MappingCodes>(mappingCodes, HttpStatus.OK);

	}

	@RequestMapping(value = "/getMappingCodesPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getTestCatalogPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		Map<String, Object> result = new HashMap<>();
		Page<MappingCodes> pageable = mappingCodesService.getMappingCodesPage(filterablePageRequest);
		result.put("data", pageable.getContent());
		result.put("total", pageable.getTotalElements());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/addMappingCodes.srvc", method = RequestMethod.POST)
	public ResponseEntity<MappingCodes> addMappingCodes(@RequestBody MappingCodes mappingCodes) {

		mappingCodesService.addMappingCode(mappingCodes);

		return new ResponseEntity<MappingCodes>(mappingCodes, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateMappingCodes.srvc", method = RequestMethod.POST)
	public ResponseEntity<MappingCodes> updateMappingCodes(@RequestBody MappingCodes mappingCodes) {

		mappingCodesService.updateMappingCodes(mappingCodes);

		return new ResponseEntity<MappingCodes>(mappingCodes, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteMappingCodes.srvc", method = RequestMethod.POST)
	public ResponseEntity<MappingCodes> deleteTest(@RequestBody MappingCodes mappingCodes) {

		mappingCodesService.deleteMappingCodes(mappingCodes);

		return new ResponseEntity<MappingCodes>(mappingCodes, HttpStatus.OK);
	}

//	@RequestMapping(value = "/deleteAllTest.srvc", method = RequestMethod.POST)
//	public ResponseEntity<TestCatalog> deleteAllTest(@RequestBody TestCatalog test) {
//
//		this.testCatalogeService.deleteAllTest(test);
//
//		return new ResponseEntity<TestCatalog>(test, HttpStatus.OK);
//	}

}
