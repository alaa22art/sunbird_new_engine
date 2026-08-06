package com.certacure.lis.interfaces.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.certacure.core.base.entity.BaseEntity;
import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.helper.SearchCriterion.JunctionOperator;
import com.certacure.core.base.service.GenericService;
import com.certacure.core.common.business.exception.BusinessException;
import com.certacure.core.common.business.exception.BusinessException.ErrorSeverity;
import com.certacure.core.common.util.SecurityUtil;
import com.certacure.lis.interfaces.admin.model.SecTenant;
import com.certacure.lis.interfaces.admin.service.SecTenantService;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.ComTenantMessage;
import com.certacure.lis.interfaces.helper.CacheType;
import com.certacure.lis.interfaces.helper.MachineIntegrationRights;
import com.certacure.lis.interfaces.repo.ComTenantMessageRepo;

/**
 * ComTenantMessageService.java
 * 

 **/

@Service("ComTenantMessageService")
public class ComTenantMessageService extends GenericService<ComTenantMessage, ComTenantMessageRepo> {

	@Autowired
	private ComTenantMessageRepo comTenantMessagesRepo;

	@Autowired
	private SecTenantService tenantService;

	public boolean isDuplicate(ComTenantMessage comTenantMessage) {
		return getRepository().findOneByCodeIgnoreCase(comTenantMessage.getCode()) != null;
	}

	//TODO: Evict cache for all tenants?
	public void createTenantMessage(ComTenantMessage tenantMessage) {
		SecurityUtil.authorizeTenantedApplicationAdmin();
		if (isDuplicate(tenantMessage)) {
			throw new BusinessException(
					"Duplication Message with Code: " + tenantMessage.getCode(), "messageNotUnique",
					ErrorSeverity.ERROR);
		}

		//Create new label for all tenants
		List<SecTenant> tenants = tenantService.find(new ArrayList<>(), SecTenant.class);
		for (SecTenant tenant : tenants) {
			ComTenantMessage msg = new ComTenantMessage();
			msg.setCode(tenantMessage.getCode());
			msg.setLkpMessagesType(tenantMessage.getLkpMessagesType());
			msg.setDescription(tenantMessage.getDescription());
			msg.setTenantId(tenant.getRid());
			getRepository().save(msg);
		}

	}

	//TODO: Evict cache for all tenants?
	@InterceptorFree
	public void deleteTenantMessage(ComTenantMessage tenantMessage) {
		SecurityUtil.authorizeTenantedApplicationAdmin();
		//Delete label from all tenants
		List<SearchCriterion> filters = tenantService	.find(new ArrayList<>(), SecTenant.class).stream().map(
																t -> new SearchCriterion("tenantId", t.getRid(), FilterOperator.eq,
																		JunctionOperator.Or))
														.collect(Collectors.toList());
		filters.add(new SearchCriterion("code", tenantMessage.getCode(), FilterOperator.eq, JunctionOperator.And));
		List<ComTenantMessage> toDeleteMsgs = getRepository().find(filters, ComTenantMessage.class);
		for (ComTenantMessage msg : toDeleteMsgs) {
			getRepository().delete(msg);
		}
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_TENANT_MESSAGES + "')")
	public List<ComTenantMessage> findTenantMessagesList() {
		return getRepository().find(new ArrayList<>(), ComTenantMessage.class, "lkpMessagesType");
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.UPD_TENANT_MESSAGES + "')")
	@CacheEvict(cacheNames = "ComTenantMessage", allEntries = false, keyGenerator = "customCacheKeyGenerator")
	public ComTenantMessage updateTenantMessage(ComTenantMessage comTenantMessage, Class<? extends BaseEntity> entityClass) {
		return getRepository().save(comTenantMessage);
	}

	@Cacheable(cacheNames = CacheType.ComTenantMessage, key = "#entityClass.getSimpleName().concat(\"-\" + #tenantId)")
	@InterceptorFree
	public List<ComTenantMessage> findLabels(Class<? extends BaseEntity> entityClass, Long tenantId) {
		return getRepository().find(Arrays.asList(new SearchCriterion("tenantId", tenantId, FilterOperator.eq)),
				ComTenantMessage.class);
	}

	@Cacheable(cacheNames = CacheType.ComTenantMessage, key = "#entityClass.getSimpleName()")
	@InterceptorFree
	public List<ComTenantMessage> findDefaultLabels(Class<? extends BaseEntity> entityClass) {
		return findLabels(entityClass, SecurityUtil.DEFAULT_TENANT);
	}

	@Override
	protected ComTenantMessageRepo getRepository() {
		return comTenantMessagesRepo;
	}

}
