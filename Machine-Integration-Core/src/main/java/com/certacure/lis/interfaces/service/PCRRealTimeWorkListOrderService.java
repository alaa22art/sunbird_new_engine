package com.certacure.lis.interfaces.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.service.GenericService;
import com.certacure.core.common.business.exception.BusinessException;
import com.certacure.core.common.business.exception.BusinessException.ErrorSeverity;
import com.certacure.lis.interfaces.entities.MachineOrder;
import com.certacure.lis.interfaces.entities.PCRRealTimeOrder;
import com.certacure.lis.interfaces.entities.PCRRealTimeWorkList;
import com.certacure.lis.interfaces.entities.PCRRealTimeWorkListOrder;
import com.certacure.lis.interfaces.middleware.util.PcrOperationEnum;
import com.certacure.lis.interfaces.repo.PCRRealTimeWorkListOrderRepo;
import com.certacure.lis.interfaces.wrapper.PCRWorkListOrderWrapper;

@Service("PCRRealTimeWorkListOrderService")
public class PCRRealTimeWorkListOrderService extends GenericService<PCRRealTimeWorkListOrder, PCRRealTimeWorkListOrderRepo> {

	@Autowired
	private PCRRealTimeWorkListOrderRepo repo;

	@Autowired
	private MachineOrderService machineOrderService;

	@Autowired
	private PCRRealTimeOrderService pcrRealTimeOrderService;

	@Autowired
	private PCRRealTimeWorkListService pcrRealTimeWorkListService;

	@Override
	protected PCRRealTimeWorkListOrderRepo getRepository() {
		return repo;
	}

	public PCRRealTimeWorkListOrder addPcrRealTimeWorkListOrder(PCRWorkListOrderWrapper pcrWorkListOrderWrapper) {
		boolean canDoOperation = pcrRealTimeWorkListService.canDoOperation(
				pcrWorkListOrderWrapper.getPcrRealTimeWorkList().getWorkListStatus().getCode(), PcrOperationEnum.ADD_ORDER);
		if (!canDoOperation) {
			throw new BusinessException("Can Not Add Order In This WorkList Status", "canntAddOrderInThisStatus", ErrorSeverity.ERROR);
		}
		PCRRealTimeOrder pcrRealTimeOrder = new PCRRealTimeOrder();
		PCRRealTimeWorkListOrder savedPcrRealTimeWorkListOrder = new PCRRealTimeWorkListOrder();

		List<MachineOrder> machineOrders = machineOrderService.getPcrMachineOrderByBarcode(pcrWorkListOrderWrapper.getBarcode(), "COV-19");
		if (machineOrders.size() > 0) {
			pcrRealTimeOrder = pcrRealTimeOrderService.addPcrRealTimeOrder(machineOrders.get(0));
		} else {
			throw new BusinessException("Patient Doesn't Exist", "patientNotExist", ErrorSeverity.ERROR);
		}

		savedPcrRealTimeWorkListOrder = addPcrRealTimeWorkListOrder(
				pcrRealTimeOrder, pcrWorkListOrderWrapper.getPcrRealTimeWorkList(), pcrWorkListOrderWrapper.getOrderRowIndex(),
				pcrWorkListOrderWrapper.getOrderColumnIndex());

		return savedPcrRealTimeWorkListOrder;
	}

	public PCRRealTimeWorkListOrder addPcrRealTimeWorkListOrder(PCRRealTimeOrder pcrRealTimeOrder,
			PCRRealTimeWorkList pcrRealTimeWorkList, String orderRowIndex, String orderColumnIndex) {
		PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder = new PCRRealTimeWorkListOrder();
		pcrRealTimeWorkListOrder.setPcrRealTimeOrder(pcrRealTimeOrder);
		pcrRealTimeWorkListOrder.setPcrRealTimeWorkList(pcrRealTimeWorkList);
		pcrRealTimeWorkListOrder.setOrderRowIndex(orderRowIndex);
		pcrRealTimeWorkListOrder.setOrderColumnIndex(orderColumnIndex);

		return getRepository().save(pcrRealTimeWorkListOrder);
	}

	public void addPcrRealTimeWorkListOrdersList(List<PCRRealTimeWorkListOrder> pcrRealTimeWorkListOrders) {
		for (PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder : pcrRealTimeWorkListOrders) {
			getRepository().save(pcrRealTimeWorkListOrder);
		}
	}

	public PCRRealTimeWorkListOrder updatePcrRealTimeWorkListOrder(PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder) {
		boolean canDoOperation = pcrRealTimeWorkListService.canDoOperation(
				pcrRealTimeWorkListOrder.getPcrRealTimeWorkList().getWorkListStatus().getCode(), PcrOperationEnum.CHANGE_ORDER_RESULT);
		if (!canDoOperation) {
			throw new BusinessException("Can Not Update Order Result In This WorkList Status", "canntUpdateOrderResultInThisStatus",
					ErrorSeverity.ERROR);
		}
		return getRepository().save(pcrRealTimeWorkListOrder);
	}

	public PCRRealTimeOrder getPcrRealTimeOrder(Long rid) {
		PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder = getRepository().findOne(
				Arrays.asList(new SearchCriterion("rid", rid, FilterOperator.eq)), PCRRealTimeWorkListOrder.class);
		return pcrRealTimeWorkListOrder.getPcrRealTimeOrder();
	}

	public PCRRealTimeWorkList getPcrRealTimeWorkList(Long rid) {
		PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder = getRepository().findOne(
				Arrays.asList(new SearchCriterion("rid", rid, FilterOperator.eq)), PCRRealTimeWorkListOrder.class);
		return pcrRealTimeWorkListOrder.getPcrRealTimeWorkList();
	}

	public Set<PCRRealTimeWorkListOrder> getPcrRealTimeWorkListOrders(Long worklistId) {
		List<PCRRealTimeWorkListOrder> tempList = getRepository().find(
				Arrays.asList(new SearchCriterion("pcrRealTimeWorkList.rid", worklistId, FilterOperator.eq)),
				PCRRealTimeWorkListOrder.class,
				"pcrRealTimeOrder",
				"pcrRealTimeWorkList.workListStatus",
				"pcrRealTimeOrderResults.pcrRealTimeResult.pcrRealTimeActualResultValues.pcrRealTimeResultTemplateLine");

		Set<PCRRealTimeWorkListOrder> pcrRealTimeWorkListOrders = new HashSet<PCRRealTimeWorkListOrder>(tempList);

		return pcrRealTimeWorkListOrders;
	}

	public void deletePcrRealTimeWorkListOrder(Long rid) {
		getRepository().deleteById(rid);
	}

	public void deletePcrRealTimeWorkListOrder(PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder) {
		getRepository().delete(pcrRealTimeWorkListOrder);
	}

	public PCRRealTimeWorkListOrder unloadPcrWorkListOrder(PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder) {
		boolean canDoOperation = pcrRealTimeWorkListService.canDoOperation(
				pcrRealTimeWorkListOrder.getPcrRealTimeWorkList().getWorkListStatus().getCode(), PcrOperationEnum.UNLOAD_ORDER);
		if (!canDoOperation) {
			throw new BusinessException("Can Not UnLoad PCR Order In This WorkList Status", "canntUnloadPcrOrderInThisStatus",
					ErrorSeverity.ERROR);
		}
		deletePcrRealTimeWorkListOrder(pcrRealTimeWorkListOrder);
		pcrRealTimeOrderService.deletePcrRealTimeOrder(pcrRealTimeWorkListOrder.getPcrRealTimeOrder());
		return pcrRealTimeWorkListOrder;
	}

	public void updateWorkListOrders(List<PCRRealTimeWorkListOrder> workListOrders) {
		getRepository().saveAll(workListOrders);
	}

}
