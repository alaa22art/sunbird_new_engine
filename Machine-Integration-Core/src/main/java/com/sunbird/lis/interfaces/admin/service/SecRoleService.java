package com.sunbird.lis.interfaces.admin.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.util.CollectionUtil;
import com.sunbird.lis.interfaces.admin.model.SecGroupRole;
import com.sunbird.lis.interfaces.admin.model.SecRole;
import com.sunbird.lis.interfaces.admin.model.SecRoleRight;
import com.sunbird.lis.interfaces.admin.model.SecUserRole;
import com.sunbird.lis.interfaces.admin.repo.SecRoleRepo;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.helper.MachineIntegrationRights;

/**
 * SecRoleService.java
 * 
 * @author Abdullah Imran <aImran@certacuresolutions.com>
 * @since Sep/27/2017
 **/

@Service("SecRoleService")
public class SecRoleService extends GenericService<SecRole, SecRoleRepo> {

	@Autowired
	private SecRoleRepo roleRepo;

	@Autowired
	private SecGroupRoleService secGroupRoleService;

	@Autowired
	private SecRoleRightService secRoleRightService;

	@Autowired
	private SecUserRoleService secUserRoleService;

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.ADD_ROLE + "')")
	public SecRole createRole(SecRole secRole, List<SecGroupRole> secGroupRoleList, List<SecRoleRight> secRoleRightList,
			List<SecUserRole> secUserRoleList) {

		SecRole newSecRole = getRepository().save(secRole);

		if (!CollectionUtil.isCollectionEmpty(secGroupRoleList)) {
			secGroupRoleList.stream().forEach(sgr -> sgr.setSecRole(newSecRole));
			secGroupRoleService.createGroupRole(secGroupRoleList);
		}
		if (!CollectionUtil.isCollectionEmpty(secRoleRightList)) {
			secRoleRightList.stream().forEach(srr -> srr.setSecRole(newSecRole));
			secRoleRightService.createRoleRight(secRoleRightList);
		}
		if (!CollectionUtil.isCollectionEmpty(secUserRoleList)) {
			secUserRoleList.stream().forEach(sur -> sur.setSecRole(newSecRole));
			secUserRoleService.createUserRole(secUserRoleList);
		}

		return newSecRole;
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.DEL_ROLE + "')")
	public void deleteRole(SecRole secRole) {

		secGroupRoleService.deleteAllGroupRoleByRole(secRole);
		secRoleRightService.deleteAllRoleRightByRole(secRole);
		secUserRoleService.deleteUserRoleByRole(secRole);
		getRepository().delete(secRole);
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.UPD_ROLE + "')")
	public SecRole updateRole(SecRole secRole, List<SecGroupRole> secGroupRoleList, List<SecRoleRight> secRoleRightList,
			List<SecUserRole> secUserRoleList) {

		SecRole updatedSecRole = getRepository().save(secRole);

		secGroupRoleService.deleteAllGroupRoleByRole(secRole);
		if (!CollectionUtil.isCollectionEmpty(secGroupRoleList)) {
			secGroupRoleList.stream().forEach(sgr -> sgr.setSecRole(updatedSecRole));
			secGroupRoleService.createGroupRole(secGroupRoleList);
		}
		secRoleRightService.deleteAllRoleRightByRole(secRole);
		if (!CollectionUtil.isCollectionEmpty(secRoleRightList)) {
			secRoleRightList.stream().forEach(srr -> srr.setSecRole(updatedSecRole));
			secRoleRightService.createRoleRight(secRoleRightList);
		}

		secUserRoleService.deleteUserRoleByRole(secRole);
		if (!CollectionUtil.isCollectionEmpty(secUserRoleList)) {
			secUserRoleList.stream().forEach(sur -> sur.setSecRole(updatedSecRole));
			secUserRoleService.createUserRole(secUserRoleList);
		}

		return updatedSecRole;
	}

	@InterceptorFree
	public List<SecRole> findRolesExcluded(List<SearchCriterion> filters, String... joins) {
		return getRepository().find(filters, SecRole.class, joins);
	}

	public List<SecRole> findRoles() {
		return getRepository().find(new ArrayList<>(), SecRole.class);
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_ROLES_MANAGEMENT + "')")
	public List<SecRole> findRoleJoinRightsGroups() {

		Set<SecRole> rolesWithRights = new HashSet<>(
				getRepository().find(new ArrayList<>(), SecRole.class, "secRoleRights.secRight", "secGroupRoles.secGroup"));
		for (SecRole sr : rolesWithRights) {
			sr.setRoleRights(new HashSet<>());
			sr.setRoleGroups(new HashSet<>());
			for (SecRoleRight srr : sr.getSecRoleRights()) {
				sr.getRoleRights().add(srr.getSecRight());
			}
			for (SecGroupRole sgr : sr.getSecGroupRoles()) {
				sr.getRoleGroups().add(sgr.getSecGroup());
			}
		}
		return new ArrayList<>(rolesWithRights);
	}

	@Override
	protected SecRoleRepo getRepository() {
		return roleRepo;
	}

}
