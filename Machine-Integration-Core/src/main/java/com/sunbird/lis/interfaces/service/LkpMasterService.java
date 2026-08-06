package com.sunbird.lis.interfaces.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.entity.BaseAuditableTenantedEntity;
import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.util.ReflectionUtil;
import com.sunbird.core.common.util.SecurityUtil;
import com.sunbird.lis.interfaces.entities.LkpMaster;
import com.sunbird.lis.interfaces.helper.MachineIntegrationRights;
import com.sunbird.lis.interfaces.repo.LkpMasterRepo;

/**
 * LkpMasterService.java
 * 
 **/

@Service("LkpMasterService")
public class LkpMasterService extends GenericService<LkpMaster, LkpMasterRepo> {

	@Autowired
	private LkpMasterRepo lkpMasterRepo;

	@Autowired
	private EntityManager entityManager;

	public LkpMaster createLkpMaster(LkpMaster lkpMaster) {
		return getRepository().save(lkpMaster);
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_LKP_MANAGEMENT + "')")
	public List<LkpMaster> findLkpMasters() {
		List<LkpMaster> lkpMasters = getRepository().find(Arrays.asList(new SearchCriterion("entity", null, FilterOperator.isnotnull)),
				LkpMaster.class, Sort.by(Direction.ASC, "entity"));
		if (SecurityUtil.isTenantedApplicationAdmin()) {
			return lkpMasters;
		} else {
			List<LkpMaster> result = new ArrayList<>();
			for (LkpMaster master : lkpMasters) {
				Class<?> masterClass = ReflectionUtil.getEntityClassByName(master.getEntity(), entityManager);
				if (masterClass != null && masterClass.getSuperclass().equals(BaseAuditableTenantedEntity.class)) {
					result.add(master);
				}
			}
			return result;
		}

	}

	@Override
	protected LkpMasterRepo getRepository() {
		return lkpMasterRepo;
	}

}
