package com.certacure.lis.interfaces.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.service.GenericService;
import com.certacure.core.common.business.exception.BusinessException;
import com.certacure.core.common.business.exception.BusinessException.ErrorSeverity;
import com.certacure.core.common.helper.FilterablePageRequest;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.LabBranch;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MachineOrder;
import com.certacure.lis.interfaces.entities.MachineTest;
import com.certacure.lis.interfaces.entities.MachineTypeTest;
import com.certacure.lis.interfaces.entities.TestCatalog;
import com.certacure.lis.interfaces.repo.MachineTestRepo;

@Service("MachineTestsService")
public class MachineTestsService extends GenericService<MachineTest, MachineTestRepo> {

	@Autowired
	private MachineTestRepo repo;

	@Autowired
	private MachineTypeTestService machineTypeTestService;

	@Override
	protected MachineTestRepo getRepository() {
		return repo;
	}

	@InterceptorFree
	public List<MachineTest> getMachineTests(List<String> requesterTestCodeList, Machine machine) {

		List<MachineTest> machineTests = new ArrayList<>();

		machineTests =  repo.getMachineTests(requesterTestCodeList, machine);

		return machineTests;

	}

	@InterceptorFree
	public List<MachineTest> getMachinePanel(MachineOrder order, Machine machine) {

		List<MachineTest> machineTests = null;

		//machineTests = repo.getMachinePanel(order.getBarcode(),order.getPanelCode(), machine);

		return null;

	}

	public List<MachineTest> getMachineTestCatalogList(Long rid) {
		List<MachineTest> machineTests = repo.getMachineTestCatalogList(rid);
		return machineTests;
	}

	public Page<MachineTest> getMachineTestsPage(FilterablePageRequest filterablePageRequest) {

		String[] joins = new String[] { "testCatalog", "machine" };
		Page<MachineTest> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				MachineTest.class, joins);

		return page;
	}

	public List<MachineTest> getMachineTestsByCodeAndMachine(String code, Machine machine) {
		List<MachineTest> mappedMachineTestsList = repo.getMachineTestsByHostCode(code, machine);
		return mappedMachineTestsList;
	}

	public MachineTest updateMachineTest(MachineTest machineTest) {

		MachineTest tempTestMap = getRepository().findByTestAndMachineNotId(machineTest.getRid(), machineTest.getTestCatalog().getRid(),
				machineTest.getMachine().getRid());
		if (tempTestMap != null) {
			throw new BusinessException(" already exists", "testAlreadyExist", ErrorSeverity.ERROR);
		} else {

			return repo.save(machineTest);
		}

	}

	public MachineTest addMachineTest(MachineTest machineTest) {

		MachineTest tempTestMap = getRepository().findByTestAndMachine(machineTest.getTestCatalog().getRid(),
				machineTest.getMachine().getRid());

		if (machineTest.getTestCatalog() == null || machineTest.getHostCode() == null) {
			throw new BusinessException(" insert all data", "insertTestWithEmptyData", ErrorSeverity.ERROR);
		}

		if (tempTestMap != null) {
			throw new BusinessException(" already exists", "testAlreadyExist", ErrorSeverity.ERROR);
		} else {

			return repo.save(machineTest);
		}

	}

	public MachineTest deleteMachineTest(MachineTest machineTest) {

		repo.deleteById(machineTest.getRid());

		return machineTest;

	}

	public List<MachineTest> filterMachineTypeTestsList(String searchValue) {
		if (StringUtils.isEmpty(searchValue)) {
			return getRepository().find(new ArrayList<>(), MachineTest.class);
		}
		return getRepository().find(Arrays.asList(new SearchCriterion("name", searchValue, FilterOperator.contains)),
				MachineTest.class);

	}

	public void deleteAllByTestId(TestCatalog tc) {

		repo.deleteAllByTestCatalog(tc);

	}

	public void addMachineTests(Machine machine) {
		try {

			List<MachineTypeTest> machineTypeTestList = machineTypeTestService.getTestsListByMachineType(machine.getMachineType().getRid());

			for (int index = 0; index < machineTypeTestList.size(); index++) {
				MachineTest machineTest = new MachineTest(machine,
						machineTypeTestList.get(index).getTestId(), machineTypeTestList.get(index).getDefultHostCode(), true);

				if (machineTest != null) {
					repo.save(machineTest);
				}
			}

		} catch (Exception ex) {

		}

	}

	public List<LabBranch> getAllBranchesList(Long rId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Machine activateAllTests(Machine machine) {
		repo.activateAllTests(machine.getRid());

		return machine;

	}

	public Machine deactivateAllTests(Machine machine) {
		repo.deactivateAllTests(machine.getRid());
		return machine;
	}

}
