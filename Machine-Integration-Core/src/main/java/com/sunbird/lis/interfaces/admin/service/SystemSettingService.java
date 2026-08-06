package com.sunbird.lis.interfaces.admin.service;


import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.Finder;
import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.core.common.util.SecurityUtil;
import com.sunbird.lis.interfaces.admin.model.SystemSetting;
import com.sunbird.lis.interfaces.admin.repo.SystemSettingRepo;
import com.sunbird.lis.interfaces.helper.SystemSettingType;

/**
 * SystemSettingService.java
 *
 * DO NOT USE ANY FUNCTION NOT IN THIS CLASS, SINCE TENANT/BRANCH FILTER WILL NOT WORK
 * WHICH MAY CAUSE A SECURITY BREACH
 *
 * @author Ala'a Himour <ahimour@certacuresolutions.com>
 * @since Mar/13/2022
 *
 */
@Service("SystemSettingService")
public class SystemSettingService extends GenericService<SystemSetting, SystemSettingRepo> {

	@Autowired
	private SystemSettingRepo repo;

	@Autowired
	private EntityManager em;

	@Override
	protected SystemSettingRepo getRepository() {
		return repo;
	}

	public Page<SystemSetting> findPage(FilterablePageRequest fpr) {
		SecurityUtil.authorizeTenantedApplicationAdmin();
		Finder<SystemSetting> finder = new Finder<>(repo);
		finder.setFilterablePageRequest(fpr);
		finder.addJoins("tenant", "branch");

		Page<SystemSetting> page = finder.findPage();

		page.getContent().stream().forEach(ss ->
			{
				switch (ss.getType()) {
					case ENABLE_REALTIME_PCR_VIEW:
						ss.setValue(SecurityUtil.decrypt(ss.getValue()));
						break;
					default:
						break;
				}
			});
		em.clear();

		return page;
	}





	/**
	 * Returns a boolean representing the value of the setting.
	 * Depends on tenantId of the current user.
	 *
	 * @param booleanSystemSettingType must be a setting of type boolean
	 */
	public boolean getBooleanSystemSetting(SystemSettingType booleanSystemSettingType) {
		return Boolean.valueOf(getStringSystemSetting(booleanSystemSettingType));
	}


	/**
	 * Returns a [nullable] string representing the VALUE of the SETTING.
	 * Will be called by other getTYPEDSystemSetting functions in this class since the raw value of all settings is a string.
	 * Depends on tenantId of the current user.
	 *
	 * @param systemSettingType
	 */
	public String getStringSystemSetting(SystemSettingType systemSettingType) {
		Set<SystemSetting> allSystemSettings = findAllSystemSettings();

		Optional<SystemSetting> systemSetting = allSystemSettings.stream().filter(ss ->
			{
				return ss.getType().equals(systemSettingType) &&
						ss.getTenant().getRid().equals(SecurityUtil.getCurrentUser().getTenantId());
			}).findFirst();

		return systemSetting.isPresent() ? systemSetting.get().getValue() : null;
	}

	private Set<SystemSetting> findAllSystemSettings() {
		Finder<SystemSetting> finder = new Finder<>(repo);
		finder.addJoins("tenant", "branch");

		Set<SystemSetting> allSystemSettings = finder.findSet();

		em.clear();
		return allSystemSettings;
	}

	public Boolean getIsEnabledToViewModule(String viewAttribute) {
		Finder<SystemSetting> finder = new Finder<>(repo);
		finder.addJoins("tenant", "branch").addFilter(new SearchCriterion("type", viewAttribute, FilterOperator.eq));
		

		Set<SystemSetting> allSystemSettings = finder.findSet();
		
		for(SystemSetting ss : allSystemSettings)
		{
			
			return Boolean.valueOf(ss.getValue());
		}

		em.clear();
		return false;
	}

	public Boolean getIsEnabledToViewPCRModule() {
		
		return getIsEnabledToViewModule(SystemSettingType.ENABLE_REALTIME_PCR_VIEW.getValue());
		
	}


}