package com.certacure.machine.web.machine.test;

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

import com.certacure.core.common.helper.FilterablePageRequest;
import com.certacure.lis.interfaces.entities.TestCatalog;
import com.certacure.lis.interfaces.service.TestCatalogService;

@RestController
@RequestMapping("/services")
public class TestCatalogController {

	@Autowired
	private TestCatalogService testCatalogeService;

	@RequestMapping(value = "/getTestById.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestCatalog> getTestCatalogById(@RequestBody Long rId) {
		TestCatalog testObj = this.testCatalogeService.getTestCatalogById(rId);
		return new ResponseEntity<TestCatalog>(testObj, HttpStatus.OK);

	}

	@RequestMapping(value = "/getTestCatalogPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getTestCatalogPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		Map<String, Object> result = new HashMap<>();
		Page<TestCatalog> pageable = this.testCatalogeService.getTestCatalogPage(filterablePageRequest);
		result.put("data", pageable.getContent());
		result.put("total", pageable.getTotalElements());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/addTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestCatalog> addTest(@RequestBody TestCatalog test) {

		this.testCatalogeService.addTest(test);

		return new ResponseEntity<TestCatalog>(test, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestCatalog> saveTest(@RequestBody TestCatalog test) {

		this.testCatalogeService.updateTest(test);

		return new ResponseEntity<TestCatalog>(test, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestCatalog> deleteTest(@RequestBody TestCatalog test) {

		this.testCatalogeService.deleteTest(test);

		return new ResponseEntity<TestCatalog>(test, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteAllTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestCatalog> deleteAllTest(@RequestBody TestCatalog test) {

		this.testCatalogeService.deleteAllTest(test);

		return new ResponseEntity<TestCatalog>(test, HttpStatus.OK);
	}

}
