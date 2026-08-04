package com.certacure.lis.interfaces.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.admin.model.SecGroup;
import com.certacure.lis.interfaces.admin.model.SecGroupRole;
import com.certacure.lis.interfaces.admin.model.SecRole;
import com.certacure.lis.interfaces.admin.repo.SecGroupRoleRepo;
import com.certacure.lis.interfaces.annotation.InterceptorFree;

/**
 * SecGroupRoleService.java
 * 
 **/

@Service("SecGroupRoleService")
public class SecGroupRoleService extends GenericService<SecGroupRole, SecGroupRoleRepo> {

	@Autowired
	private SecGroupRoleRepo groupRoleRepo;

	public List<SecGroupRole> createGroupRole(List<SecGroupRole> secGroupRoleList) {
		return getRepository().saveAll(secGroupRoleList);
	}

	public void deleteAllGroupRoleByRole(SecRole secRole) {
		getRepository().deleteAllBySecRole(secRole);
	}

	public void deleteAllGroupRoleByGroup(SecGroup secGroup) {
		getRepository().deleteAllBySecGroup(secGroup);
	}

	@InterceptorFree
	public List<SecGroupRole> findGroupRolesExcluded(List<SearchCriterion> filters, String... joins) {
		return getRepository().find(filters, SecGroupRole.class, joins);
	}

	@Override
	protected SecGroupRoleRepo getRepository() {
		return groupRoleRepo;
	}

}
