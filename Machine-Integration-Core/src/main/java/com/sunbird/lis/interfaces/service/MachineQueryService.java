package com.sunbird.lis.interfaces.service;

import java.util.Set;

/**
*

* 
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MachineQuery;
import com.sunbird.lis.interfaces.repo.MachineQueryRepo;

@Service("MachineQueryService")
public class MachineQueryService extends GenericService<MachineQuery, MachineQueryRepo> {

	@Autowired
	private MachineQueryRepo repo;

	@Override
	protected MachineQueryRepo getRepository() {
		return repo;
	}

	@InterceptorFree
	public MachineQuery addQuery(MachineQuery query) {

		return repo.save(query);
	}

	public MachineQuery updateQuery(MachineQuery query) {
		return repo.save(query);
	}

	public void deleteQuery(Long rId) {
		repo.deleteById(rId);
	}

	@InterceptorFree
	public MachineQuery getQueryBySampleAndMachine(String specimenId, Machine machine) {
		MachineQuery machineQuery = repo.findTop1BySampleNoAndMachineOrderByRidDesc(specimenId, machine);
		return machineQuery;
	}

	public Set<MachineQuery> getQueryResult(Long rid, String testCode) {
		Set<MachineQuery> machineQueryInfo = repo.findQueryResult(rid, testCode);
		return machineQueryInfo;
	}

}
