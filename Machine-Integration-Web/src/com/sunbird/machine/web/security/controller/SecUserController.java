package com.sunbird.machine.web.security.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.admin.model.SecGroup;
import com.sunbird.lis.interfaces.admin.model.SecGroupUser;
import com.sunbird.lis.interfaces.admin.model.SecRole;
import com.sunbird.lis.interfaces.admin.model.SecUser;
import com.sunbird.lis.interfaces.admin.model.SecUserRole;
import com.sunbird.lis.interfaces.admin.service.SecGroupUserService;
import com.sunbird.lis.interfaces.admin.service.SecUserRoleService;
import com.sunbird.lis.interfaces.admin.service.SecUserService;
import com.sunbird.machine.web.security.wrapper.SecRelationWrapper;

/**
 * SecUserController.java
 * 
 * @author Abdullah Imran <aImran@certacuresolutions.com>
 * @since Oct/17/2017
 **/
@RestController
@RequestMapping("/services")
public class SecUserController {

	@Autowired
	private SecUserService secUserService;

	@Autowired
	private SecGroupUserService secGroupUserService;

	@Autowired
	private SecUserRoleService secUserRoleService;

	@RequestMapping(value = "/createSecUser.srvc", method = RequestMethod.POST)
	public ResponseEntity<SecUser> createSecUser(
			@RequestBody SecRelationWrapper<SecUser, SecGroupUser, SecUserRole, SecUser> secRelationWrapper) {

		SecUser secUser = secRelationWrapper.getMaster();
		if (secUserService.findUserByUsername(secUser.getUsername()) != null) {
			throw new BusinessException("Username already exists", "usernameExist", ErrorSeverity.ERROR);
		}
		List<SecGroupUser> secGroupUserList = secRelationWrapper.getRelationTableOne();
		List<SecUserRole> secUserRoleList = secRelationWrapper.getRelationTableTwo();

		return new ResponseEntity<SecUser>(secUserService.createUser(secUser, secGroupUserList, secUserRoleList), HttpStatus.OK);
	}

	@RequestMapping(value = "/deactivateSecUser.srvc", method = RequestMethod.POST)
	public ResponseEntity<Void> deactivateSecUser(@RequestBody SecUser secUser) {
		this.secUserService.deactivateUser(secUser);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/activateSecUser.srvc", method = RequestMethod.POST)
	public ResponseEntity<Void> activateSecUser(@RequestBody SecUser secUser) {
		this.secUserService.activateUser(secUser);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/updateSecUser.srvc", method = RequestMethod.POST)
	public ResponseEntity<SecUser> updateSecUser(
			@RequestBody SecRelationWrapper<SecUser, SecGroupUser, SecUserRole, SecUser> secRelationWrapper) {

		SecUser secUser = secRelationWrapper.getMaster();
		List<SecGroupUser> secGroupUserList = secRelationWrapper.getRelationTableOne();
		List<SecUserRole> secUserRoleList = secRelationWrapper.getRelationTableTwo();

		return new ResponseEntity<SecUser>(secUserService.updateUser(secUser, secGroupUserList, secUserRoleList), HttpStatus.OK);
	}

	@RequestMapping(value = "/getSecUserPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<SecUser>> getSecUserPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<SecUser>>(secUserService.findUserPage(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/getSecUserPageWithGroups.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<SecUser>> getSecUserPageWithGroups(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<SecUser>>(secUserService.findUserPageJoinGroups(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/getSecUserPageWithRolesGroups.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<SecUser>> getSecUserPageWithRolesGroups(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<SecUser>>(secUserService.findUserPageJoinRolesGroups(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/resetSecUserPassword.srvc", method = RequestMethod.POST)
	public ResponseEntity<Void> resetSecUserPassword(@RequestBody SecUser secUser) {
		secUserService.resetUserPassword(secUser);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/forgotPassword.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<Void> forgotPassword(@RequestBody String username) {
		secUserService.forgotPassword(username);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/changeForgottenPassword.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<Void> changeForgottenPassword(@RequestBody Map<String, String> map) {
		secUserService.changeForgottenPassword(map.get("token"), map.get("password"));
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/updateEmailPassword.srvc", method = RequestMethod.POST)
	public ResponseEntity<Void> updateEmailPassword(@RequestBody Map<String, String> map) {

		secUserService.updateUserEmailAndPass(map.get("currentPassword"),
				map.get("newPassword"), map.get("newEmail"));
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/updateSecUserProfile.srvc", method = RequestMethod.POST)
	public ResponseEntity<Void> updateSecUserProfile(@RequestBody SecUser secUser) {
		secUserService.updateUserProfile(secUser);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/getSecUserRoleBySecUser.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<SecUserRole>> getSecUserRoleBySecUser(@RequestBody SecUser secUser) {

		return new ResponseEntity<List<SecUserRole>>(secUserRoleService.findUserRolesByUser(secUser), HttpStatus.OK);
	}

	@RequestMapping(value = "/getSecUsersListByRole.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<SecUser>> getSecUsersListByRole(@RequestBody SecRole secRole) {
		return new ResponseEntity<List<SecUser>>(secUserRoleService	.findUserRolesByRole(secRole).stream()
																	.map(SecUserRole::getSecUser).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getSecGroupUserBySecUser.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<SecGroupUser>> getSecGroupUserBySecUser(@RequestBody SecUser secUser) {

		return new ResponseEntity<List<SecGroupUser>>(secGroupUserService.findGroupUserByUser(secUser), HttpStatus.OK);
	}

	@RequestMapping(value = "/getSecUsersListByGroup.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<SecUser>> getSecUsersListByGroup(@RequestBody SecGroup secGroup) {
		return new ResponseEntity<List<SecUser>>(secGroupUserService.findGroupUserByGroup(secGroup).stream()
																	.map(SecGroupUser::getSecUser).collect(Collectors.toList()),
				HttpStatus.OK);
	}

}
