package com.sunbird.lis.interfaces.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.admin.service.SystemSettingService;
import com.sunbird.lis.interfaces.entities.LkpPCRRealRimeWorkListStatus;
import com.sunbird.lis.interfaces.entities.PCRRealTimeWorkList;
import com.sunbird.lis.interfaces.entities.PCRRealTimeWorkListOrder;
import com.sunbird.lis.interfaces.middleware.util.PcrOperationEnum;
import com.sunbird.lis.interfaces.middleware.util.WorkListStatusEnum;
import com.sunbird.lis.interfaces.repo.PCRRealTimeWorkListRepo;
import com.sunbird.lis.interfaces.wrapper.WorkListStatusWrapper;

@Service("PCRRealTimeWorkListService")
public class PCRRealTimeWorkListService extends GenericService<PCRRealTimeWorkList, PCRRealTimeWorkListRepo> {

	@Autowired
	private PCRRealTimeWorkListRepo repo;

	@Autowired
	private PCRRealTimeWorkListOrderService pcrRealTimeWorkListOrderService;

	@Autowired
	private LkpService lkpService;

	@Autowired
	private SystemSettingService systemSettingService;

	@Override
	protected PCRRealTimeWorkListRepo getRepository() {
		return repo;
	}

	public PCRRealTimeWorkList addPcrRealTimeWorkList(PCRRealTimeWorkList pcrRealTimeWorkList) {
		LkpPCRRealRimeWorkListStatus lkpPCRRealRimeWorkListStatus = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", WorkListStatusEnum.OPEN.getValue(), FilterOperator.eq)),
				LkpPCRRealRimeWorkListStatus.class);

		pcrRealTimeWorkList.setWorkListStatus(lkpPCRRealRimeWorkListStatus);
		PCRRealTimeWorkList savedPcrRealTimeWorkList = getRepository().save(pcrRealTimeWorkList);

		return savedPcrRealTimeWorkList;
	}

	public PCRRealTimeWorkList updatePcrRealTimeWorkList(PCRRealTimeWorkList pcrRealTimeWorkList) {
		return getRepository().save(pcrRealTimeWorkList);
	}

	public void deletePcrRealTimeWorkList(Long rid) {
		try {
			getRepository().deleteById(rid);
		} catch (Exception e) {
			throw new BusinessException("You Can't Delete A WorkList that Has Orders", "cantDeleteWorkList", ErrorSeverity.ERROR);
		}
	}

	public List<PCRRealTimeWorkListOrder> getPcrRealTimeWorkListOrders(Long rid) {
		PCRRealTimeWorkList pcrRealTimeWorkList = getRepository().findOne(Arrays.asList(new SearchCriterion("rid", rid, FilterOperator.eq)),
				PCRRealTimeWorkList.class);
		return pcrRealTimeWorkList.getWorkListOrders();
	}

	public PCRRealTimeWorkList getPcrRealTimeWorkListById(Long rid) {

		return getRepository().findOne(Arrays.asList(new SearchCriterion("rid", rid, FilterOperator.eq)), PCRRealTimeWorkList.class);
	}

	//	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_MACHINE_TYPE_SETUP + "')")
	public Page<PCRRealTimeWorkList> getPcrWorkListPage(FilterablePageRequest filterablePageRequest) {

		//check if the the branch is authorized to to access pcr module
		if (!systemSettingService.getIsEnabledToViewPCRModule()) {
			throw new BusinessException("Tenant not Authorized to use this module ! ", "tenatNotAuthorized", ErrorSeverity.ERROR);
		}

		Page<PCRRealTimeWorkList> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				PCRRealTimeWorkList.class, "workListStatus");

		return page;
	}

	public List<PCRRealTimeWorkList> getPcrRealTimeWorkLists() {
		return getRepository().findAll(Sort.by("creationDate").descending());
	}

	public PCRRealTimeWorkList changeStatus(WorkListStatusWrapper workListStatusWrapper) {

		if (workListStatusWrapper.getWorkList() == null)
			throw new BusinessException("No WorkList Selected", "noWorkListSelected", ErrorSeverity.ERROR);

		WorkListStatusEnum wantedWorkListStatus = WorkListStatusEnum.getWorkListStatusByCode(workListStatusWrapper.getStatusCode());
		WorkListStatusEnum currentlyWorkListStatus = WorkListStatusEnum.getWorkListStatusByCode(
				workListStatusWrapper.getWorkList().getWorkListStatus().getCode());
		LkpPCRRealRimeWorkListStatus lkpWorkListStatus = new LkpPCRRealRimeWorkListStatus();
		PCRRealTimeWorkList workList = workListStatusWrapper.getWorkList();

		if (wantedWorkListStatus.isAfterByOneLevel(currentlyWorkListStatus)) {
			lkpWorkListStatus = lkpService.findOneAnyLkp(
					Arrays.asList(new SearchCriterion("code", wantedWorkListStatus.getValue(), FilterOperator.eq)),
					LkpPCRRealRimeWorkListStatus.class);
			workList.setWorkListStatus(lkpWorkListStatus);
			if (WorkListStatusEnum.FINALIZED.equals(wantedWorkListStatus)) {
				setWorkListOrdersResultsConfirmed(workList);
			}
			return updatePcrRealTimeWorkList(workList);
		} else {
			throw new BusinessException("You Can Not Change To This Status", "canntChangeStatus", ErrorSeverity.ERROR);
		}

	}

	private void setWorkListOrdersResultsConfirmed(PCRRealTimeWorkList workList) {
		Set<PCRRealTimeWorkListOrder> workListOrders = pcrRealTimeWorkListOrderService.getPcrRealTimeWorkListOrders(workList.getRid());
		for (PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder : workListOrders) {
			pcrRealTimeWorkListOrder.setIsConfirmed(true);
		}

		pcrRealTimeWorkListOrderService.updateWorkListOrders(new ArrayList<PCRRealTimeWorkListOrder>(workListOrders));

	}

	public boolean canDoOperation(String workListStatus, PcrOperationEnum operation) {
		if (WorkListStatusEnum.OPEN.getValue().equals(workListStatus)) {
			if (PcrOperationEnum.ADD_ORDER.equals(operation)) {
				return true;
			}

			if (PcrOperationEnum.UNLOAD_ORDER.equals(operation)) {
				return true;
			}

			return false;
		}

		if (WorkListStatusEnum.IN_PRGRESS.getValue().equals(workListStatus)) {
			return false;
		}

		if (WorkListStatusEnum.RESULTS_ENTRY.getValue().equals(workListStatus)) {
			if (PcrOperationEnum.CHANGE_ORDER_RESULT.equals(operation)) {
				return true;
			}
			return false;
		}

		if (WorkListStatusEnum.FINALIZED.getValue().equals(workListStatus)) {
			if (PcrOperationEnum.SEND_RESULTS.equals(operation)) {
				return true;
			}
			return false;
		}

		return false;
	}

}
