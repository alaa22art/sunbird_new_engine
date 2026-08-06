package com.sunbird.machine.web.machine.test;

import java.util.HashMap;
import java.util.List;
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
import com.sunbird.lis.interfaces.entities.LabBranch;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MachineTest;
import com.sunbird.lis.interfaces.entities.TestCatalog;
import com.sunbird.lis.interfaces.service.MachineTestsService;
import com.sunbird.lis.interfaces.service.TestCatalogService;

@RestController
@RequestMapping("/services")
public class MachineTestController {

	@Autowired
	private MachineTestsService machineTestsService;

	@Autowired
	private TestCatalogService testCatalogeService;

	@RequestMapping(value = "/getMachineTestCatalogList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<MachineTest>> getMachineTestCatalogList(@RequestBody Long rId) {

		List<MachineTest> TestList = this.machineTestsService.getMachineTestCatalogList(rId);

		return new ResponseEntity<List<MachineTest>>(TestList, HttpStatus.OK);

	}

	@RequestMapping(value = "/getAllBranchesList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabBranch>> getAllBranchesList(@RequestBody Long rId) {

		List<LabBranch> BranchList = this.machineTestsService.getAllBranchesList(rId);

		return new ResponseEntity<List<LabBranch>>(BranchList, HttpStatus.OK);

	}

	@RequestMapping(value = "/getMachineTestsPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getMachineTestsPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		Map<String, Object> result = new HashMap<>();
		Page<MachineTest> pageable = this.machineTestsService.getMachineTestsPage(filterablePageRequest);
		result.put("data", pageable.getContent());
		result.put("total", pageable.getTotalElements());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteMachineTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<MachineTest> deleteMachineTest(@RequestBody MachineTest machineTest) {

		this.machineTestsService.deleteMachineTest(machineTest);

		return new ResponseEntity<MachineTest>(machineTest, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteAllByTestId.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestCatalog> deleteAllByTestId(@RequestBody TestCatalog testCatalog) {

		this.machineTestsService.deleteAllByTestId(testCatalog);

		return new ResponseEntity<TestCatalog>(testCatalog, HttpStatus.OK);
	}

	@RequestMapping(value = "/insertMachineTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<MachineTest> insertMachineTest(@RequestBody MachineTest machineTest) {

		this.machineTestsService.addMachineTest(machineTest);

		return new ResponseEntity<MachineTest>(machineTest, HttpStatus.OK);
	}

	/*
	 * @RequestMapping(value = "/insertMachineTests.srvc", method = RequestMethod.POST)
	 * public ResponseEntity<Machine> insertMachineTests(@RequestBody Machine machine) {
	 * 
	 * this.machineTestsService.addMachineTests(machine);
	 * 
	 * return new ResponseEntity<Machine>(machine, HttpStatus.OK);
	 * }
	 */

	@RequestMapping(value = "/updateMachineTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<MachineTest> updateMachineTest(@RequestBody MachineTest machineTest) {

		this.machineTestsService.updateMachineTest(machineTest);

		return new ResponseEntity<MachineTest>(machineTest, HttpStatus.OK);
	}

	@RequestMapping(value = "/activeAllTests.srvc", method = RequestMethod.POST)
	public ResponseEntity<Machine> activateAllTest(@RequestBody Machine machine) {

		this.machineTestsService.activateAllTests(machine);

		return new ResponseEntity<Machine>(machine, HttpStatus.OK);
	}

	@RequestMapping(value = "/deactivateAllTests.srvc", method = RequestMethod.POST)
	public ResponseEntity<Machine> deactivateAllTest(@RequestBody Machine machine) {

		this.machineTestsService.deactivateAllTests(machine);

		return new ResponseEntity<Machine>(machine, HttpStatus.OK);
	}

}
