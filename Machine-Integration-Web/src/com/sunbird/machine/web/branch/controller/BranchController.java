package com.sunbird.machine.web.branch.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.entities.LabBranch;
import com.sunbird.lis.interfaces.service.LabBranchService;

@RestController
@RequestMapping("/services")
public class BranchController {

	@Autowired
	private LabBranchService branchService;

	
	//lov 
	@RequestMapping(value = "/getLabBranchList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabBranch>> getLabBranchList(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<List<LabBranch>>(branchService.findBranchList(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/getBranches.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabBranch>> getBranches() {
		return new ResponseEntity<List<LabBranch>>(branchService.getBranches(), HttpStatus.OK);
	}

	@RequestMapping(value = "/createBranch.srvc", method = RequestMethod.POST)
	public ResponseEntity<LabBranch> createBranch(@RequestBody LabBranch branch) {
		return new ResponseEntity<LabBranch>(branchService.createBranch(branch), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateBranch.srvc", method = RequestMethod.POST)
	public ResponseEntity<LabBranch> updateBranch(@RequestBody LabBranch branch) {
		return new ResponseEntity<LabBranch>(branchService.updateBranch(branch), HttpStatus.OK);
	}

	@RequestMapping(value = "/activateBranch.srvc", method = RequestMethod.POST)
	public ResponseEntity<LabBranch> activateBranch(@RequestBody Long rid) {
		return new ResponseEntity<LabBranch>(branchService.activateBranch(rid), HttpStatus.OK);
	}

	@RequestMapping(value = "/deactivateBranch.srvc", method = RequestMethod.POST)
	public ResponseEntity<LabBranch> deactivateBranch(@RequestBody Long rid) {
		return new ResponseEntity<LabBranch>(branchService.deactivateBranch(rid), HttpStatus.OK);
	}
}
