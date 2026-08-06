/**
 * 
 */
package com.sunbird.lis.interfaces.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.core.common.util.CollectionUtil;
import com.sunbird.lis.interfaces.entities.LabBranch;
import com.sunbird.lis.interfaces.helper.MachineIntegrationRights;
import com.sunbird.lis.interfaces.repo.LabBranchRepo;

/**
 *

 * 
 */

@Service("LabBranchService")
public class LabBranchService extends GenericService<LabBranch, LabBranchRepo> {

	@Autowired
	private LabBranchRepo repo;

	@Override
	protected LabBranchRepo getRepository() {
		return repo;
	}

	public void uniqueCodeValidation(LabBranch branch) {
		List<SearchCriterion> filters = new ArrayList<>();
		filters.add(new SearchCriterion("code", branch.getCode(), FilterOperator.eq));
		if (branch.getRid() != null) {
			filters.add(new SearchCriterion("rid", branch.getRid(), FilterOperator.neq));
		}
		List<LabBranch> otherBranches = getRepository().find(filters, LabBranch.class);
		if (!CollectionUtil.isCollectionEmpty(otherBranches)) {
			throw new BusinessException("Branch code: " + branch.getCode() + " already being used", "codeExist", ErrorSeverity.ERROR);
		}
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.ADD_BRANCH + "')")
	public LabBranch createBranch(LabBranch branch) {
		uniqueCodeValidation(branch);
		return getRepository().save(branch);
	}

	/**
	 * FOR UI.
	 * 
	 * @param branches
	 * @return
	 */
	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.UPD_BRANCH + "')")
	public LabBranch updateBranch(LabBranch branch) {
		uniqueCodeValidation(branch);
		branch.setIntegrationToken(getRepository().fetchIntegrationTokenById(branch.getRid()));
		return getRepository().save(branch);
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.ACTIVATE_BRANCH + "')")
	public LabBranch activateBranch(Long rid) {
		LabBranch branch = getRepository().findById(rid).get();
		branch.setIsActive(Boolean.TRUE);
		branch = getRepository().save(branch);
		return branch;

	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.DEACTIVATE_BRANCH + "')")
	public LabBranch deactivateBranch(Long rid) {
		LabBranch branch = getRepository().findById(rid).get();
		branch.setIsActive(Boolean.FALSE);
		branch = getRepository().save(branch);
		return branch;
	}

	/**
	 * for LOV
	 * 
	 * @param filterablePageRequest
	 * @return
	 */
	public List<LabBranch> findBranchList(FilterablePageRequest filterablePageRequest) {
		return getRepository().find(filterablePageRequest.getFilters(), LabBranch.class, filterablePageRequest.getSortObject());
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_BRANCH + "')")
	public List<LabBranch> getBranches() {
		return getRepository().find(new ArrayList<>(), LabBranch.class, Sort.by(Direction.ASC, "rid"), "city", "country");
	}

}
