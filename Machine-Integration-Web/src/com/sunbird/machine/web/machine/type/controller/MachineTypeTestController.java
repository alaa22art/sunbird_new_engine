package com.sunbird.machine.web.machine.type.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.entities.MachineTypeTest;
import com.sunbird.lis.interfaces.entities.TestCatalog;
import com.sunbird.lis.interfaces.service.MachineTypeTestService;
import com.sunbird.lis.interfaces.service.TestCatalogService;

@RestController
@RequestMapping("/services")
public class MachineTypeTestController {

	@Autowired
	private MachineTypeTestService machineTypeTestService;

	@Autowired
	private TestCatalogService testCatalogeService;

	@RequestMapping(value = "/getMachineTypeTestsPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getMachineTypeTestsPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		Map<String, Object> result = new HashMap<>();
		Page<MachineTypeTest> pageable = machineTypeTestService.getMachineTypeTestsPage(filterablePageRequest);
		result.put("data", pageable.getContent());
		result.put("total", pageable.getTotalElements());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteMachineTypeTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<MachineTypeTest> deleteMachineType(@RequestBody MachineTypeTest machineTypeTest) {

		this.machineTypeTestService.deleteMachineTypeTest(machineTypeTest);

		return new ResponseEntity<MachineTypeTest>(machineTypeTest, HttpStatus.OK);
	}

	@RequestMapping(value = "/insertMachineTypeTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<MachineTypeTest> insertMachineType(@RequestBody MachineTypeTest machineTypeTest) {

		this.machineTypeTestService.addMachineTypeTest(machineTypeTest);

		return new ResponseEntity<MachineTypeTest>(machineTypeTest, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateMachineTypeTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<MachineTypeTest> updateMachineType(@RequestBody MachineTypeTest machineTypeTest) {

		this.machineTypeTestService.updateMachineTypeTest(machineTypeTest);

		return new ResponseEntity<MachineTypeTest>(machineTypeTest, HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestCatalogList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<TestCatalog>> getTestCatalogList() {

		List<TestCatalog> testCatalogList = this.testCatalogeService.findAll();

		return new ResponseEntity<List<TestCatalog>>(testCatalogList, HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestCatalogListByName.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<TestCatalog>> getTestCatalogListByName(@RequestBody Map<String, String> map) {

		List<TestCatalog> testCatalogList = this.testCatalogeService.getNotMappedTestCatalogList(map);

		return new ResponseEntity<List<TestCatalog>>(testCatalogList, HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestCatalogListByMachineType.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<TestCatalog>> getTestCatalogListByMachineType(@RequestBody Map<String, String> map) {

		Set<TestCatalog> testCatalogList = this.testCatalogeService.getTestCatalogListByMachineType(map.get("searchValue"),
				Long.valueOf(map.get("machineTypeId")));

		return new ResponseEntity<Set<TestCatalog>>(testCatalogList, HttpStatus.OK);
	}

	/*
	 * @RequestMapping(value = "/deleteAllByTypeAndTestId.srvc", method = RequestMethod.POST)
	 * public ResponseEntity<TestCatalog> deleteAllByTypeAndTestId(@RequestBody TestCatalog testCatalog) {
	 * 
	 * this.machineTypeTestService.deleteAllByTypeAndTestId(testCatalog.getRid());
	 * 
	 * return new ResponseEntity<TestCatalog>(testCatalog, HttpStatus.OK);
	 * }
	 */

}
