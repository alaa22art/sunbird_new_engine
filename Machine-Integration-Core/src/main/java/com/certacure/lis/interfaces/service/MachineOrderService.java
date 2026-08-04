package com.certacure.lis.interfaces.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
/**
*

* 
*/
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import org.joda.time.DurationFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.service.GenericService;
import com.certacure.core.common.helper.FilterablePageRequest;
import com.certacure.core.common.util.CollectionUtil;
import com.certacure.core.common.util.DateUtil;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.LabBranch;
import com.certacure.lis.interfaces.entities.MachineOrder;
import com.certacure.lis.interfaces.entities.MachineResult;
import com.certacure.lis.interfaces.entities.MachineTypePanel;
import com.certacure.lis.interfaces.repo.MachineOrderRepo;

@Service("MachineOrderService")
public class MachineOrderService extends GenericService<MachineOrder, MachineOrderRepo> {

	@Autowired
	private MachineOrderRepo repo;
	@Autowired
	private LabBranchService branchService;
	@Autowired
	private Environment env;
	@Autowired
	private MachineResultService machineResultService;
	@Autowired
	private EntityManager entityManager;

	@Override
	protected MachineOrderRepo getRepository() {
		return repo;
	}

	public MachineOrder addOrder(MachineOrder order) {

		return repo.save(order);
	}

	public void addOrderList(List<MachineOrder> machineOrderList) {
		for (MachineOrder machineOrder : machineOrderList) {
			repo.save(machineOrder);
		}
	}

	@InterceptorFree
	public MachineOrder updateOrder(MachineOrder order) {
		return repo.save(order);
	}

	public void deleteOrder(Long rId) {
		repo.deleteById(rId);
	}

	public List<MachineOrder> getOrderBySample(String specimenId) {
		List<MachineOrder> machineOrder = repo.findTop1ByBarcodeOrderByRidDesc(specimenId);
		return machineOrder;
	}

	@InterceptorFree
	public List<MachineOrder> getOrderInformationBySampleNo(String barcode, long tenantId, long branchId) {

		List<MachineOrder> machineOrderInfo = null;
		List<MachineOrder> machineOrderList = null;

		if (!barcode.isEmpty()) {
			machineOrderInfo = repo.getBySample(barcode, tenantId, branchId);
			

			Map<String, Optional<MachineOrder>> machineOrderMap = machineOrderInfo	.stream()
																					.collect(
																							Collectors.groupingBy(MachineOrder::getTestCode,
																									Collectors.maxBy(Comparator.comparing(
																											MachineOrder::getRid))));
			machineOrderList = new ArrayList<MachineOrder>();
			
			for (Optional<MachineOrder> mo : machineOrderMap.values()) {
				machineOrderList.add(mo.get());
			}

		} else {

			System.out.println("The sample can not be identified by\r\n" +
					"the host. Recived a Query without Uniqe Barcode from Branch : " + branchId + " And " + " Tenant ID : " + tenantId);

		}

		return machineOrderList;

	}

	public List<MachineOrder> getOrderInformationBySampleNoWithOptions(String barcode, boolean resultNotReceived, boolean newTestOnly) {
	
		List<MachineOrder> machineOrderInfo = new ArrayList<MachineOrder>();
		if (resultNotReceived == true && newTestOnly == false) {
			machineOrderInfo = repo.getBySampleAndResultNotReceived(barcode);
		} else if (newTestOnly == true && resultNotReceived == false) {
			machineOrderInfo = repo.getBySampleAndNewTestOnly(barcode);
		} else if (newTestOnly == true && resultNotReceived == true) {
			machineOrderInfo = repo.getBySampleAndNewTestOnlyAndResultNotReceived(barcode);
		}
		return machineOrderInfo;
	}

	@InterceptorFree
	public MachineOrder getBySampleAndTestCode(String barcode, String testCode, long tenantId, long branchId) {
		List<MachineOrder> machineOrderInfoList = repo.getBySampleAndTestCode(barcode, testCode, tenantId, branchId);
		MachineOrder machineOrderInfo = null;
		/*
		 * List<SearchCriterion> filters = new ArrayList<>();
		 * filters.add(new SearchCriterion("barcode", barcode, FilterOperator.eq));
		 * filters.add(new SearchCriterion("testCode", testCode, FilterOperator.eq));
		 * filters.add(new SearchCriterion("tenantId", tenantId, FilterOperator.eq));
		 * filters.add(new SearchCriterion("branchId", branchId, FilterOperator.eq));
		 * MachineOrder machineOrderInfo = getRepository() .find(filters, MachineOrder.class, new Sort(new Order(Direction.DESC, "rid")))
		 * .get(0);
		 */
		if (!CollectionUtil.isCollectionEmpty(machineOrderInfoList)) {
			machineOrderInfo = machineOrderInfoList.get(0);

		} else {
			System.out.println("The sample can not be identified by\r\n" +
					"the host. Recived a result without mapped order Barcode from Branch : " + branchId + " And " + " Tenant ID : "
					+ tenantId + "barcode" + barcode + "test code" + testCode);

		}
		return machineOrderInfo;
	}

	@InterceptorFree
	public String getIsEnabledReciveEhopeOrder() {
		return env.getProperty("enable.recive.data.ehope");
	}

	@InterceptorFree
	public String getIsEnabledSendEhopeResult() {
		return env.getProperty("enable.send.data.ehope");
	}

	public Page<MachineOrder> getMachineOrderPage(FilterablePageRequest fpr) {
		Page<MachineOrder> page = getRepository().find(fpr.getFilters(), fpr.getPageRequest(), MachineOrder.class,
				"sourceType");
		if (CollectionUtil.isCollectionEmpty(page.getContent())) {
			return page;
		}
		List<LabBranch> branches = branchService.find(Arrays.asList(new SearchCriterion("rid",
				page.getContent().stream().map(m -> m.getBranchId()).distinct().collect(Collectors.toList()), FilterOperator.in)),
				LabBranch.class);
		List<MachineResult> machineResults = machineResultService.find(
				Arrays.asList(new SearchCriterion("machineOrder.rid", CollectionUtil.getRidAsList(page.getContent()), FilterOperator.in)),
				MachineResult.class, Sort.by(Direction.DESC, "rid"), "machine", "machineOrder");
		for (MachineOrder mo : page.getContent()) {
			mo.addTransient("branch", branches.stream().filter(b -> b.getRid().equals(mo.getBranchId())).findFirst().orElse(null));
			if (mo.getDateOfBirth() != null) {
				mo.addTransient("patientAge", DateUtil.getPeriod(mo.getDateOfBirth(), new Date(), DurationFieldType.years(),
						DurationFieldType.months(), DurationFieldType.days()));
			}
			List<MachineResult> machineResultsByOrder = machineResults	.stream()
																		.filter(r -> r.getMachineOrder() != null &&
																				r.getMachineOrder().getRid().equals(mo.getRid()))
																		//																		.sorted((a, b) -> b.getRid().compareTo(a.getRid()))
																		.collect(Collectors.toList());
			if (CollectionUtil.isCollectionEmpty(machineResultsByOrder)) {
				mo.addTransient("resultsCount", 0);
				mo.addTransient("latestResult", null);
			} else {
				mo.addTransient("resultsCount", machineResultsByOrder.size());
				MachineResult latestResult = machineResultsByOrder.get(0);
				latestResult.setMachineOrder(null);
				mo.addTransient("latestResult", latestResult);
			}
		}
		entityManager.clear();//in case we changed any field
		return page;
	}

	public List<MachineOrder> getOrderQueryResponseBySample(String barcode) {
		List<MachineOrder> machineOrderQuertyResponse = new ArrayList<>(repo.findOrderQueryResponseByBarcode(barcode));
		return machineOrderQuertyResponse;
	}

	public List<MachineOrder> getOrderQueryByBarcode(String barcode) {
		Set<MachineOrder> machineOrderInfo = repo.findOrderQuery(barcode);

		Map<String, Optional<MachineOrder>> machineOrderMap = machineOrderInfo	.stream()
																				.collect(
																						Collectors.groupingBy(MachineOrder::getTestCode,
																								Collectors.maxBy(Comparator.comparing(
																										MachineOrder::getRid))));
		List<MachineOrder> machineOrderList = new ArrayList<MachineOrder>();
		for (Optional<MachineOrder> mo : machineOrderMap.values()) {
			machineOrderList.add(mo.get());
		}

		return machineOrderList;
	}

	@InterceptorFree
	public List<MachineOrder> getPanelOrderInformation(String barcode, Long tenantId, Long branchId) {
		List<MachineOrder> machineOrderList = new ArrayList<>();
		MachineOrder machineOrder = new MachineOrder();

		if (!barcode.isEmpty()) {
			machineOrderList = repo.getByPanelCode(barcode, tenantId, branchId);

			if (machineOrderList.size() > 1) {
				machineOrder = machineOrderList.get(0);
				machineOrderList.clear();
				machineOrderList.add(machineOrder);

			}

		} else {

			System.out.println("The sample can not be identified by\r\n" +
					"the host. Recived a Query without Uniqe Barcode from Branch : " + branchId + " And " + " Tenant ID : " + tenantId);

		}

		return machineOrderList;
	}

	@InterceptorFree
	public List<MachineOrder> getAllOrdersByPanelName(List<MachineTypePanel> machineTypePanelList, Long tenantId, Long branchId) {

		List<MachineOrder> machineOrderList = new ArrayList<>();

		List<Tuple> tupleMachineOrderList;

		for (MachineTypePanel machineTypePanel : machineTypePanelList) {

			tupleMachineOrderList = repo.getAllByPanelCode(machineTypePanel.getPanelHostCode(), tenantId, branchId);

			for (int iRow = 0; iRow < tupleMachineOrderList.size(); iRow++) {

				String barcode = tupleMachineOrderList.get(iRow).get(0, String.class);
				String patientFirstName = tupleMachineOrderList.get(iRow).get(1, String.class);
				String patientSecondName = tupleMachineOrderList.get(iRow).get(2, String.class);
				String patientId = tupleMachineOrderList.get(iRow).get(3, String.class);
				Date dOB = tupleMachineOrderList.get(iRow).get(4, Date.class);
				String gender = tupleMachineOrderList.get(iRow).get(5, String.class);
				String panelCode = tupleMachineOrderList.get(iRow).get(6, String.class);

				MachineOrder order = new MachineOrder(barcode, patientFirstName, patientSecondName, patientId, dOB, gender, panelCode);
				machineOrderList.add(order);
			}

		}

		return machineOrderList;
	}

	@InterceptorFree
	public MachineOrder getOrderInformationByPanelName(String barcode, Long tenantId, Long branchId, String panelCode) {

		List<MachineOrder> machineOrderList = new ArrayList<>();
		machineOrderList = repo.getSampleByPanelCode(barcode, panelCode, tenantId, branchId);

		if (machineOrderList.size() > 0) {

			return machineOrderList.get(0);
		} else {
			return null;
		}
	}

	@InterceptorFree
	public void updateAllOrdersIsSentToMachine(String barcode, String panelCode, boolean b, Long tenantId, Long branchId) {
		repo.updateAllOrdersIsSentToMachine(barcode, panelCode, b, tenantId, branchId);
	}

	public List<MachineOrder> getPcrMachineOrderByBarcode(String barcode, String panelCode) {
		return getRepository().getPcrRealTimeOrderBySampleAndPanelCode(barcode, panelCode);
	}

}
