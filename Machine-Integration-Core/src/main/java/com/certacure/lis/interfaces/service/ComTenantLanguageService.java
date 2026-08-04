package com.certacure.lis.interfaces.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.service.GenericService;
import com.certacure.core.common.util.CollectionUtil;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.ComTenantLanguage;
import com.certacure.lis.interfaces.repo.ComTenantLanguageRepo;

/**
 * ComTenantLanguageService.java
 * 
 **/

@Service("ComTenantLanguageService")
public class ComTenantLanguageService extends GenericService<ComTenantLanguage, ComTenantLanguageRepo> {

	@Autowired
	private ComTenantLanguageRepo comTenantLanguageRepo;

	@InterceptorFree
	public List<ComTenantLanguage> createTenantLanguages(List<ComTenantLanguage> tenantLanguages) {
		return getRepository().saveAll(tenantLanguages);
	}

	public List<ComTenantLanguage> updateTenantLanguages(List<ComTenantLanguage> tenantLanguages) {
		if (CollectionUtil.isCollectionEmpty(tenantLanguages)) {
			return new ArrayList<>();
		}
		getRepository().deleteAllByTenantId(tenantLanguages.get(0).getTenantId());
		getRepository().flush();//it causes an exception for unique key because save occurs before delete
		return getRepository().saveAll(tenantLanguages);
	}

	public List<ComTenantLanguage> findTenantLanguages(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, ComTenantLanguage.class, sort, joins);
	}

	public List<ComTenantLanguage> findTenantExcelLanguages() {
		return getRepository().find(new ArrayList<>(), ComTenantLanguage.class, Sort.by(Direction.DESC, "isPrimary"), "comLanguage");
	}

	@InterceptorFree
	public List<ComTenantLanguage> findTenantLanguagesExcluded(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, ComTenantLanguage.class, sort, joins);
	}

	public String getTenantNamePrimary() {
		return getRepository().findOne(Arrays.asList(new SearchCriterion("isNamePrimary", Boolean.TRUE, FilterOperator.eq)),
				ComTenantLanguage.class, "comLanguage").getComLanguage().getLocale();
	}

	@Override
	protected ComTenantLanguageRepo getRepository() {
		return comTenantLanguageRepo;
	}

}
