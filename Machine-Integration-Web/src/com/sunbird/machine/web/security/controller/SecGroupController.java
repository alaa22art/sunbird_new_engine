package com.sunbird.machine.web.security.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.lis.interfaces.admin.model.SecGroup;
import com.sunbird.lis.interfaces.admin.model.SecGroupRole;
import com.sunbird.lis.interfaces.admin.model.SecGroupUser;
import com.sunbird.lis.interfaces.admin.service.SecGroupService;
import com.sunbird.machine.web.security.wrapper.SecRelationWrapper;

/**
 * SecGroupController.java
 * 

 **/
@RestController
@RequestMapping("/services")
public class SecGroupController {

	@Autowired
	private SecGroupService secGroupService;

	@RequestMapping(value = "/createSecGroup.srvc", method = RequestMethod.POST)
	public ResponseEntity<SecGroup> createSecGroup(
			@RequestBody SecRelationWrapper<SecGroup, SecGroupRole, SecGroupUser, SecGroup> secRelationWrapper) {

		SecGroup secGroup = secRelationWrapper.getMaster();
		List<SecGroupRole> secGroupRoleList = secRelationWrapper.getRelationTableOne();
		List<SecGroupUser> secGroupUserList = secRelationWrapper.getRelationTableTwo();

		return new ResponseEntity<SecGroup>(secGroupService.createGroup(secGroup, secGroupRoleList, secGroupUserList), HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteSecGroup.srvc", method = RequestMethod.POST)
	public ResponseEntity<Void> deleteSecGroup(@RequestBody SecGroup secGroup) {
		this.secGroupService.deleteGroup(secGroup);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/updateSecGroup.srvc", method = RequestMethod.POST)
	public ResponseEntity<SecGroup> updateSecGroup(
			@RequestBody SecRelationWrapper<SecGroup, SecGroupRole, SecGroupUser, SecGroup> secRelationWrapper) {

		SecGroup secGroup = secRelationWrapper.getMaster();
		List<SecGroupRole> secGroupRoleList = secRelationWrapper.getRelationTableOne();
		List<SecGroupUser> secGroupUserList = secRelationWrapper.getRelationTableTwo();

		return new ResponseEntity<SecGroup>(secGroupService.updateGroup(secGroup, secGroupRoleList, secGroupUserList), HttpStatus.OK);
	}

	@RequestMapping(value = "/getSecGroupList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<SecGroup>> getSecGroupList() {
		return new ResponseEntity<List<SecGroup>>(secGroupService.findGroups(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getSecGroupWithRolesList.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<SecGroup>> getSecGroupWithRolesList() {
		return new ResponseEntity<Set<SecGroup>>(secGroupService.findGroupJoinRoles(), HttpStatus.OK);
	}

}
