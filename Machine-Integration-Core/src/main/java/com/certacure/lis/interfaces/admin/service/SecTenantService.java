package com.certacure.lis.interfaces.admin.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.service.GenericService;
import com.certacure.core.common.business.exception.BusinessException;
import com.certacure.core.common.business.exception.BusinessException.ErrorSeverity;
import com.certacure.core.common.util.SecurityUtil;
import com.certacure.lis.interfaces.admin.model.SecTenant;
import com.certacure.lis.interfaces.admin.repo.SecTenantRepo;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.helper.MachineIntegrationRights;

/**
 * SecTenantService.class
 * 

 **/
@Service("SecTenantService")
@InterceptorFree
public class SecTenantService extends GenericService<SecTenant, SecTenantRepo> {

	@Autowired
	private SecTenantRepo secTenantRepo;

	@Override
	protected SecTenantRepo getRepository() {
		return this.secTenantRepo;
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_TENANT_MANAGEMENT + "')")
	public SecTenant getTenantData(Long tenantRid) {
		return getRepository().findOne(SearchCriterion.generateRidFilter(tenantRid, FilterOperator.eq), SecTenant.class,
				"country", "city");
	}

	public SecTenant createTenant(SecTenant tenant, Boolean isActive) {
		checkIfDuplicatedTenant(true, tenant.getEmail(), tenant.getCode(), null);
		tenant.setIsActive(isActive);
		tenant.setUpdatedBy(SecurityUtil.APPLICATION_ADMIN);// since the webhook updates the tenant and no user is set there
		return getRepository().save(tenant);
	}

	/**
	 * Checks if new/updated tenant is duplicated in email and code.
	 * 
	 * @param isNewTenant
	 * @param tenantEmail
	 * @param tenantCode
	 * @param updatedTenantRid
	 */
	public void checkIfDuplicatedTenant(boolean isNewTenant, String tenantEmail, String tenantCode, Long updatedTenantRid) {
		BusinessException usedEmail = new BusinessException("Email is used", "emailExist", ErrorSeverity.ERROR);
		BusinessException usedCode = new BusinessException("Code is used", "codeExist", ErrorSeverity.ERROR);
		SecTenant tenantByEmail = getRepository().findByEmail(tenantEmail);
		SecTenant tenantByCode = getRepository().findByCode(tenantCode);
		if (isNewTenant) {
			if (tenantByEmail != null) {
				throw usedEmail;
			} else if (tenantByCode != null) {
				throw usedCode;
			}
		} else {
			if (tenantByEmail != null && !tenantByEmail.getRid().equals(updatedTenantRid)) {
				throw usedEmail;
			} else if (tenantByCode != null && !tenantByCode.getRid().equals(updatedTenantRid)) {
				throw usedCode;
			}
		}

	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.UPD_TENANT + "')")
	public SecTenant updateTenant(SecTenant tenant) {
		checkIfDuplicatedTenant(false, tenant.getEmail(), tenant.getCode(), tenant.getRid());
		return getRepository().save(tenant);
	}

	public SecTenant updateTenantExcluded(SecTenant tenant) {
		tenant.setUpdatedBy(SecurityUtil.APPLICATION_ADMIN);// since the webhook updates the tenant and no user is set there
		return getRepository().save(tenant);
	}

	public List<SecTenant> findTenants(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, SecTenant.class, sort);
	}

	public SecTenant findTenantByPayerIdExcluded(String payerId, String... joins) {
		return getRepository().findOne(Arrays.asList(new SearchCriterion("payerId", payerId, FilterOperator.eq)), SecTenant.class, joins);
	}

}
