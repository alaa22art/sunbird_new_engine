package com.sunbird.lis.interfaces.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.admin.service.SystemSettingService;
import com.sunbird.lis.interfaces.entities.MachineOrder;
import com.sunbird.lis.interfaces.entities.PCRRealTimeOrder;
import com.sunbird.lis.interfaces.entities.PCRRealTimeWorkListOrder;
import com.sunbird.lis.interfaces.repo.PCRRealTimeOrderRepo;

@Service("PCRRealTimeOrderService")
public class PCRRealTimeOrderService extends GenericService<PCRRealTimeOrder, PCRRealTimeOrderRepo> {

	@Autowired
	private PCRRealTimeOrderRepo repo;
	
	@Autowired
	private SystemSettingService systemSettingService;

	@Override
	protected PCRRealTimeOrderRepo getRepository() {
		return repo;
	}

	public Set<PCRRealTimeWorkListOrder> gePcrRealTimeWorkListOrders(Long rid) {
		PCRRealTimeOrder pcrRealTimeOrder = getRepository().findOne(Arrays.asList(new SearchCriterion("rid", rid, FilterOperator.eq)),
				PCRRealTimeOrder.class);
		return pcrRealTimeOrder.getWorkListOrders();
	}

	public PCRRealTimeOrder addPcrRealTimeOrder(MachineOrder machineOrder) {
		PCRRealTimeOrder pcrRealTimeOrder = new PCRRealTimeOrder();
		pcrRealTimeOrder.setMachineOrder(machineOrder);
		return getRepository().save(pcrRealTimeOrder);
	}

	public List<PCRRealTimeOrder> addPcrRealTimeOrders(List<MachineOrder> machineOrders) {
		List<PCRRealTimeOrder> pcrRealTimeOrders = new ArrayList<PCRRealTimeOrder>();
		for (MachineOrder machineOrder : machineOrders) {
			PCRRealTimeOrder pcrRealTimeOrder = new PCRRealTimeOrder();
			pcrRealTimeOrder.setMachineOrder(machineOrder);
			pcrRealTimeOrders.add(pcrRealTimeOrder);
		}

		return getRepository().saveAll(pcrRealTimeOrders);
	}

	public void deletePcrRealTimeOrder(PCRRealTimeOrder pcrRealTimeOrder) {
		getRepository().delete(pcrRealTimeOrder);
	}

	public Page<PCRRealTimeOrder> getPcrOrdersPage(FilterablePageRequest filterablePageRequest) {

		//check if the the branch is authorized to to access pcr module
				if(!systemSettingService.getIsEnabledToViewPCRModule())
				{
					throw new BusinessException("Tenant not Authorized to use this module ! ", "tenatNotAuthorized", ErrorSeverity.ERROR);
				}
				
		
		Page<PCRRealTimeOrder> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				PCRRealTimeOrder.class, "machineOrder", "workListOrders.pcrRealTimeWorkList", "workListOrders.orderStatus",
				"workListOrders.pcrRealTimeWorkList.workListStatus",
				"workListOrders.pcrRealTimeOrderResults.pcrRealTimeResult.pcrRealTimeActualResultValues.pcrRealTimeResultTemplateLine");

		return page;
	}

}
