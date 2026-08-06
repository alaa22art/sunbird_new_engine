package com.sunbird.lis.interfaces.service;

/**
*

* 
*/
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MachineResult;
import com.sunbird.lis.interfaces.repo.MachineResultRepo;

@Service("MachineResultService")
public class MachineResultService extends GenericService<MachineResult, MachineResultRepo> {

	@Autowired
	private MachineResultRepo repo;

	@Override
	protected MachineResultRepo getRepository() {
		return repo;
	}

	public MachineResult addResult(MachineResult result) {
		MachineResult saveResultInfo = repo.save(result);
		return saveResultInfo;
	}

	@InterceptorFree
	public void addListResult(List<MachineResult> machineResultList) {
		repo.saveAll(machineResultList);
	}

	public MachineResult updateResult(MachineResult result) {
		return repo.save(result);
	}

	public void deleteResult(Long rId) {
		repo.deleteById(rId);
	}

	public List<MachineResult> getAllResultList() {
		return repo.findAll();
	}

	public List<MachineResult> getResultBySampleAndMachine(String sampleNo, Machine machine) {
		List<MachineResult> machineResult = repo.findTop1BySampleNoAndMachineOrderByRidDesc(sampleNo, machine);
		return machineResult;
	}

	public List<MachineResult> getMachineResultList(FilterablePageRequest fpr) {
		return getRepository().find(fpr.getFilters(), MachineResult.class, fpr.getSortObject(), "machine", "machineOrder", "machineTest",
				"actionCode");
	}

	public Page<MachineResult> getResultByMachine(FilterablePageRequest filterablePageRequest) {
		Page<MachineResult> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				MachineResult.class, "machine");
		return page;
	}

}
