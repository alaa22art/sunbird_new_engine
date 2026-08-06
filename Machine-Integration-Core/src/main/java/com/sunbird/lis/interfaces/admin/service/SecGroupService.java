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
import com.sunbird.lis.interfaces.admin.model.SecGroup;
import com.sunbird.lis.interfaces.admin.model.SecGroupRole;
import com.sunbird.lis.interfaces.admin.model.SecGroupUser;
import com.sunbird.lis.interfaces.admin.repo.SecGroupRepo;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.helper.MachineIntegrationRights;

/**
 * SecGroupService.java
 * 
 **/

@Service("SecGroupService")
public class SecGroupService extends GenericService<SecGroup, SecGroupRepo> {

	@Autowired
	private SecGroupRepo secGroupRepo;

	@Autowired
	private SecGroupRoleService secGroupRoleService;

	@Autowired
	private SecGroupUserService secGroupUserService;

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.ADD_GROUP + "')")
	public SecGroup createGroup(SecGroup secGroup, List<SecGroupRole> secGroupRoleList, List<SecGroupUser> secGroupUserList) {

		SecGroup newSecGroup = getRepository().save(secGroup);

		if (!CollectionUtil.isCollectionEmpty(secGroupRoleList)) {
			secGroupRoleList.stream().forEach(sgu -> sgu.setSecGroup(newSecGroup));
			secGroupRoleService.createGroupRole(secGroupRoleList);
		}

		if (!CollectionUtil.isCollectionEmpty(secGroupUserList)) {
			secGroupUserList.stream().forEach(sur -> sur.setSecGroup(newSecGroup));
			secGroupUserService.createGroupUser(secGroupUserList);
		}

		return newSecGroup;
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.DEL_GROUP + "')")
	public void deleteGroup(SecGroup secGroup) {

		secGroupRoleService.deleteAllGroupRoleByGroup(secGroup);
		secGroupUserService.deleteGroupUserByGroup(secGroup);
		getRepository().delete(secGroup);
	}

	public List<SecGroup> findGroups() {
		return getRepository().find(new ArrayList<>(), SecGroup.class);
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_GROUPS_MANAGEMENT + "')")
	public Set<SecGroup> findGroupJoinRoles() {

		Set<SecGroup> groupWithRoles = new HashSet<>(getRepository().find(new ArrayList<>(), SecGroup.class, "secGroupRoles.secRole"));
		for (SecGroup sg : groupWithRoles) {
			for (SecGroupRole sgr : sg.getSecGroupRoles()) {
				sg.getGroupRoles().add(sgr.getSecRole());
			}
		}

		return groupWithRoles;
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.UPD_GROUP + "')")
	public SecGroup updateGroup(SecGroup secGroup, List<SecGroupRole> secGroupRoleList, List<SecGroupUser> secGroupUserList) {
		SecGroup newSecGroup = getRepository().save(secGroup);

		secGroupRoleService.deleteAllGroupRoleByGroup(secGroup);
		if (!CollectionUtil.isCollectionEmpty(secGroupRoleList)) {
			secGroupRoleList.stream().forEach(sgu -> sgu.setSecGroup(newSecGroup));
			secGroupRoleService.createGroupRole(secGroupRoleList);
		}

		secGroupUserService.deleteGroupUserByGroup(secGroup);
		if (!CollectionUtil.isCollectionEmpty(secGroupUserList)) {
			secGroupUserList.stream().forEach(sur -> sur.setSecGroup(newSecGroup));
			secGroupUserService.createGroupUser(secGroupUserList);
		}

		return newSecGroup;
	}

	@InterceptorFree
	public List<SecGroup> findGroupsExcluded(List<SearchCriterion> filters, String... joins) {
		return getRepository().find(filters, SecGroup.class, joins);
	}

	@Override
	protected SecGroupRepo getRepository() {
		return secGroupRepo;
	}

}
