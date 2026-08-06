package com.sunbird.lis.interfaces.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.entities.MachineTypeTest;
import com.sunbird.lis.interfaces.entities.TestCatalog;
import com.sunbird.lis.interfaces.helper.MachineIntegrationRights;
import com.sunbird.lis.interfaces.repo.MachineTypeTestRepo;

@Service("MachineTypeTestService")
public class MachineTypeTestService extends GenericService<MachineTypeTest, MachineTypeTestRepo> {

	@Autowired
	private MachineTypeTestRepo repo;

	@Override
	protected MachineTypeTestRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_MACHINE_TYPE_TEST_MAPPING + "')")
	public Page<MachineTypeTest> getMachineTypeTestsPage(FilterablePageRequest filterablePageRequest) {

		String[] joins = new String[] { "machineTypeId" };
		Page<MachineTypeTest> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				MachineTypeTest.class, joins);

		return page;
	}

	public List<MachineTypeTest> getTestsListByMachineType(String searchValue, Long mahcineTypeId) {
		return getRepository().find(
				Arrays.asList(new SearchCriterion("machineTypeId", mahcineTypeId, FilterOperator.eq),
						new SearchCriterion("testId.name", searchValue, FilterOperator.contains),
						new SearchCriterion("isActive", true, FilterOperator.eq)),
				MachineTypeTest.class, "testId");
	}

	public List<MachineTypeTest> getTestsListByMachineType(Long mahcineTypeId) {

		return repo.getAllTestsByMachineTypeId(mahcineTypeId);

		/*
		 * return getRepository().find(
		 * Arrays.asList(new SearchCriterion("machineTypeId", mahcineTypeId, FilterOperator.eq),
		 * new SearchCriterion("isActive", true, FilterOperator.eq)),
		 * MachineTypeTest.class, "testId");
		 */
	}

	public MachineTypeTest addMachineTypeTest(MachineTypeTest testMap) {

		MachineTypeTest tempTestMap = getRepository().findByTestAndMachineType(testMap.getTestId().getRid(),
				testMap.getMachineTypeId().getRid());

		if (testMap.getDefultHostCode() == null || testMap.getName() == null) {
			throw new BusinessException("insert all data", "insertTestWithEmptyData", ErrorSeverity.ERROR);
		} else if (tempTestMap != null) {
			throw new BusinessException(" already exists", "testAlreadyExist", ErrorSeverity.ERROR);
		} else {

			return repo.save(testMap);
		}

	}

	public MachineTypeTest updateMachineTypeTest(MachineTypeTest testMap) {

		MachineTypeTest tempTestMap = getRepository().findByTestAndMachineTypeNotId(testMap.getRid(), testMap.getTestId().getRid(),
				testMap.getMachineTypeId().getRid());
		if (tempTestMap != null) {
			throw new BusinessException(" already exists", "testAlreadyExist", ErrorSeverity.ERROR);
		} else {

			return repo.save(testMap);
		}

	}

	public MachineTypeTest deleteMachineTypeTest(MachineTypeTest testMap) {

		repo.deleteById(testMap.getRid());

		return testMap;

	}

	public void deleteAllByTestId(TestCatalog test) {
		repo.deleteByTestId(test);

	}

}
