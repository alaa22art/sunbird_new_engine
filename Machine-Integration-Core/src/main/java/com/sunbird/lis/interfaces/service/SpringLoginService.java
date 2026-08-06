package com.certacure.lis.interfaces.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.common.business.exception.BusinessException;
import com.certacure.core.common.business.exception.BusinessException.ErrorSeverity;
import com.certacure.core.common.util.CollectionUtil;
import com.certacure.lis.interfaces.admin.model.SecGroupRole;
import com.certacure.lis.interfaces.admin.model.SecGroupUser;
import com.certacure.lis.interfaces.admin.model.SecRoleRight;
import com.certacure.lis.interfaces.admin.model.SecTenant;
import com.certacure.lis.interfaces.admin.model.SecUser;
import com.certacure.lis.interfaces.admin.model.SecUserRole;
import com.certacure.lis.interfaces.admin.repo.SecGroupUserRepo;
import com.certacure.lis.interfaces.admin.repo.SecTenantRepo;
import com.certacure.lis.interfaces.admin.repo.SecUserRepo;
import com.certacure.lis.interfaces.admin.repo.SecUserRoleRepo;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.LabBranch;
import com.certacure.lis.interfaces.repo.ComTenantLanguageRepo;
import com.certacure.lis.interfaces.repo.LabBranchRepo;
import com.certacure.lis.interfaces.security.ClientType;

/**
 *
 * SpringLoginService.java, Used to implement spring security loadUserByUsername
 * method, to check for user authentication
 *
 */
@Service("SpringLoginService")
@InterceptorFree
public class SpringLoginService implements UserDetailsService {

	@Autowired
	private SecUserRepo userRepo;
	@Autowired
	private SecUserRoleRepo userRoleRepo;
	@Autowired
	private SecGroupUserRepo groupUserRepo;
	@Autowired
	private ComTenantLanguageRepo tenantLanguageRepo;
	@Autowired
	private SecTenantRepo tenantRepo;
	@Autowired
	private LabBranchRepo branchRepo;
	@Autowired
	private EntityManager entityManager;

	/**
	 * get the User data from database by user name and throws an exception when
	 * user not found
	 *
	 * @param username
	 * @throws UsernameNotFoundException
	 */
	@Override
	@Transactional(readOnly = false)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		Map<String, String[]> paramMap = request.getParameterMap();
		String clientId = paramMap.get("client_id")[0].toString();
		if (!ClientType.ACCULINK.toString().equals(clientId)) {
			throw new UsernameNotFoundException("User " + username + " was not found !!");
		}
		SecUser user = userRepo.loadUserByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User " + username + " was not found !!");
		}
		Set<SecUserRole> userRoles = userRoleRepo.getBySecUser(user.getRid());
		Set<SecGroupUser> userGroups = groupUserRepo.getBySecUser(user.getRid());
		List<String> userRights = new ArrayList<>(getRights(userRoles, userGroups));
		if (CollectionUtil.isCollectionEmpty(userRights)) {
			// The message of the error is the same as the code because spring wraps the exception in another way and in exception handler we read Exceptions
			// by getting the message if the cause is not null(BusinessException is not null here ) so we get the message which is the errorCode
			throw new BusinessException("userNoRights", "userNoRights", ErrorSeverity.ERROR);
		}
		SecTenant tenant = tenantRepo.fetchTenantDataById(user.getTenantId());
		boolean canLogin = tenant.getIsActive();
		user.setTenantLanguages(tenantLanguageRepo.fetchTenantLanguages(user.getTenantId()));
		user.setTenant(tenant);
		user.setCountry(tenant.getCountry());
		if (user.getBranchId() != null) {
			LabBranch branch = branchRepo.findOne(Arrays.asList(new SearchCriterion("rid", user.getBranchId(), FilterOperator.eq)),
					LabBranch.class);
			user.setBranch(branch);
			canLogin = canLogin && branch.getIsActive();
		}
		user.setIsActive(canLogin);

		user.setUserGroups(new HashSet<>());
		for (SecGroupUser sgu : userGroups) {
			user.getUserGroups().add(sgu.getSecGroup());
		}
		user.setPrivileges(userRights);

		entityManager.clear();
		userRepo.updateLastLoginTime(user.getRid(), new Date());

		return user;
	}

	private Set<String> getRights(Set<SecUserRole> userRoles, Set<SecGroupUser> userGroups) {
		Set<String> rights = new HashSet<>();
		for (SecUserRole sur : userRoles) {
			for (SecRoleRight srr : sur.getSecRole().getSecRoleRights()) {
				rights.add(srr.getSecRight().getCode().trim());
			}
		}
		for (SecGroupUser sgu : userGroups) {
			for (SecGroupRole sgr : sgu.getSecGroup().getSecGroupRoles()) {
				for (SecRoleRight srr : sgr.getSecRole().getSecRoleRights()) {
					rights.add(srr.getSecRight().getCode().trim());
				}
			}
		}
		return rights;
	}

}
