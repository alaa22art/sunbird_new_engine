package com.certacure.lis.interfaces.admin.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.helper.SearchCriterion.JunctionOperator;
import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.admin.model.SecGroup;
import com.certacure.lis.interfaces.admin.model.SecGroupUser;
import com.certacure.lis.interfaces.admin.model.SecUser;
import com.certacure.lis.interfaces.admin.repo.SecGroupUserRepo;

/**
 * SecGroupUserService.java
 * 
 **/

@Service("SecGroupUserService")
public class SecGroupUserService extends GenericService<SecGroupUser, SecGroupUserRepo> {

	@Autowired
	private SecGroupUserRepo secGroupUserRepo;

	public List<SecGroupUser> createGroupUser(List<SecGroupUser> secGroupUserList) {
		return getRepository().saveAll(secGroupUserList);
	}

	public void deleteGroupUserByGroup(SecGroup secGroup) {
		getRepository().deleteAllBySecGroup(secGroup);
	}

	public void deleteGroupUserByUser(SecUser secUser) {
		getRepository().deleteAllBySecUser(secUser);
	}

	public List<SecGroupUser> findGroupUserByGroup(SecGroup secGroup) {

		return getRepository().find(
				Arrays.asList(new SearchCriterion("secGroup", secGroup.getRid(), FilterOperator.eq, JunctionOperator.And)),
				SecGroupUser.class, "secGroup", "secUser");
	}

	public List<SecGroupUser> findGroupUserByUser(SecUser secUser) {
		return getRepository().find(
				Arrays.asList(new SearchCriterion("secUser", secUser.getRid(), FilterOperator.eq, JunctionOperator.And)),
				SecGroupUser.class, "secGroup", "secUser");
	}

	@Override
	protected SecGroupUserRepo getRepository() {
		return secGroupUserRepo;
	}

}
