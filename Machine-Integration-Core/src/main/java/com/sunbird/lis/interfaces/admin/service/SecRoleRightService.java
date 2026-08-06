package com.sunbird.lis.interfaces.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.admin.model.SecRole;
import com.sunbird.lis.interfaces.admin.model.SecRoleRight;
import com.sunbird.lis.interfaces.admin.repo.SecRoleRightRepo;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;

/**
 * SecRoleRightService.java
 * 

 **/

@Service("SecRoleRightService")
public class SecRoleRightService extends GenericService<SecRoleRight, SecRoleRightRepo> {

	@Autowired
	private SecRoleRightRepo roleRightRepo;

	public List<SecRoleRight> createRoleRight(List<SecRoleRight> secRoleRight) {
		return getRepository().saveAll(secRoleRight);
	}

	public void deleteAllRoleRightByRole(SecRole secRole) {
		getRepository().deleteAllBySecRole(secRole);
	}

	@InterceptorFree
	public List<SecRoleRight> findRoleRightsExcluded(List<SearchCriterion> filters, String... joins) {
		return getRepository().find(filters, SecRoleRight.class, joins);
	}

	@Override
	protected SecRoleRightRepo getRepository() {
		return roleRightRepo;
	}

}
