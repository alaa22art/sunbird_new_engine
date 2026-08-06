package com.certacure.lis.interfaces.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.MachineOrderQueryResponse;
import com.certacure.lis.interfaces.repo.MachineOrderQueryResponceRepo;

@Service("MachineOrderQueryResponceService")
public class MachineOrderQueryResponceService extends GenericService<MachineOrderQueryResponse, MachineOrderQueryResponceRepo> {

	@Autowired
	private MachineOrderQueryResponceRepo repo;

	@Override
	protected MachineOrderQueryResponceRepo getRepository() {
		return repo;
	}

	@InterceptorFree
	public MachineOrderQueryResponse addMachineOrderQueryResponse(MachineOrderQueryResponse machineOrderQueryResponce) {

		return repo.save(machineOrderQueryResponce);
	}

	public MachineOrderQueryResponse updateMachineOrderQueryResponse(MachineOrderQueryResponse machineOrderQueryResponce) {
		return repo.save(machineOrderQueryResponce);
	}

	public void deleteMachineOrderQueryResponce(Long rId) {
		repo.deleteById(rId);
	}

	public Set<MachineOrderQueryResponse> getQueryResponseByOrderId(Long rid) {
		Set<MachineOrderQueryResponse> machineOrderQuertyResponse = repo.findQueryResponseByOrderId(rid);
		return machineOrderQuertyResponse;
	}

}