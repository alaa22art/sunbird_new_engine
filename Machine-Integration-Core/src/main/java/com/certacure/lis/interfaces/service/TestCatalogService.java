package com.certacure.lis.interfaces.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.service.GenericService;
import com.certacure.core.common.business.exception.BusinessException;
import com.certacure.core.common.business.exception.BusinessException.ErrorSeverity;
import com.certacure.core.common.helper.FilterablePageRequest;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MachineTypeTest;
import com.certacure.lis.interfaces.entities.TestCatalog;
import com.certacure.lis.interfaces.helper.MachineIntegrationRights;
import com.certacure.lis.interfaces.repo.TestCatalogRepo;

@Service("TestCatalogService")
public class TestCatalogService extends GenericService<TestCatalog, TestCatalogRepo> {

	@Autowired
	private TestCatalogRepo repo;

	@Autowired
	private MachineTypeTestService machinTypeTestService;

	@Autowired
	private MachineTestsService machinTestService;

	@Autowired
	private MachineTypeTestService machineTypeTestService;

	@Override
	protected TestCatalogRepo getRepository() {
		return repo;
	}

	@InterceptorFree
	public TestCatalog getMachineTests(String hostCode, Machine machine) {
		TestCatalog testCatalog = repo.getMachineTests(hostCode, machine);
		return testCatalog;
	}

	@InterceptorFree
	public List<TestCatalog> getMachineTestsList(String hostCode, Machine machine) {
		List<TestCatalog> testCatalogList = repo.getMachineTestsList(hostCode, machine);
		return testCatalogList;
	}

	public List<TestCatalog> getTestCatalogList() {
		return repo.getTestCatalogList();

	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_TEST_CATALOG + "')")
	public List<MachineTypeTest> getMachineTypeTestsList(Long machineTypeId) {

		return machineTypeTestService.find(
				Arrays.asList(new SearchCriterion("machineTypeId.rid", machineTypeId, FilterOperator.eq)),
				MachineTypeTest.class, "machineTypeId", "testId");

	}

	/**
	 * Get all test catalogs depending on searchValue
	 * 
	 * @param searchValue
	 * @return List
	 */

	public List<TestCatalog> getNotMappedTestCatalogList(Map<String, String> map) {
		String searchValue = map.get("searchValue");
		Long machineTypeId = new Long(map.get("machineTypeId"));

		//get all machine type tests from TestCatalog and machineType tests tables 
		List<MachineTypeTest> machineTypeTest = getMachineTypeTestsList(machineTypeId);

		//mapped data from machineTypeTest to TestCatalog to remove duplication in case happened 
		Set<TestCatalog> testCatalogSet = ConvertListToSet(machineTypeTest);

		//Create Temp Filter
		List<SearchCriterion> filters = new ArrayList<>();

		//add new search criteria to filter [find by a part of name]
		filters.add(new SearchCriterion("name", searchValue, FilterOperator.contains));

		//add search criteria for each excluded row 
		//for (TestCatalog tc : testCatalogSet) {
		//filters.add(new SearchCriterion("rid", tc.getRid(), FilterOperator.neq, JunctionOperator.And));
		//}
		return getRepository().find(filters, TestCatalog.class);
	}

	private Set<TestCatalog> ConvertListToSet(List<MachineTypeTest> machineTypeTest) {
		return machineTypeTest.stream().map(MachineTypeTest::getTestId).collect(Collectors.toSet());
	}

	/**
	 * Get one test depending on rid
	 * 
	 * @param rid
	 * @return List
	 */

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_TEST_CATALOG + "')")
	public TestCatalog getTestCatalogById(Long rId) {

		List<TestCatalog> tempTestList = getRepository().find(Arrays.asList(new SearchCriterion("rid", rId, FilterOperator.eq)),
				TestCatalog.class);

		return tempTestList.get(0);
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_TEST_CATALOG + "')")
	public Page<TestCatalog> getTestCatalogPage(FilterablePageRequest filterablePageRequest) {

		Page<TestCatalog> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				TestCatalog.class, "specimenType");

		return page;
	}

	public Set<TestCatalog> getTestCatalogListByMachineType(String searchValue, Long mahcineTypeId) {
		List<MachineTypeTest> machineTypeTest = machinTypeTestService.getTestsListByMachineType(searchValue, mahcineTypeId);
		return ConvertListToSet(machineTypeTest);
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.UPD_TEST_CATALOG + "')")
	public TestCatalog updateTest(TestCatalog test) {

		if (test.getName().trim() == "" || test.getRequesterTestCode().trim() == "") {
			throw new BusinessException("insert all data", "insertTestWithEmptyData", ErrorSeverity.ERROR);
		} else

		{
			return repo.save(test);
		}

	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.ADD_TEST_CATALOG + "')")
	public TestCatalog addTest(TestCatalog test) {

		if (test.getName().trim() == "" || test.getRequesterTestCode().trim() == "") {
			throw new BusinessException("insert all data", "insertTestWithEmptyData", ErrorSeverity.ERROR);
		} else

		{
			return repo.save(test);
		}

	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.DEL_TEST_CATALOG + "')")
	public TestCatalog deleteTest(TestCatalog test) {
		getRepository().delete(test);

		return test;

	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.DEL_MAP_MACHINE_TYPE_WITH_TEST + "')")
	public TestCatalog deleteAllTest(TestCatalog test) {

		machinTestService.deleteAllByTestId(test);
		machinTypeTestService.deleteAllByTestId(test);
		getRepository().delete(test);

		return test;

	}

}
