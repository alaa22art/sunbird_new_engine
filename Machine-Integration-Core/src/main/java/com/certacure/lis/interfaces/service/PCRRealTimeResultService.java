package com.certacure.lis.interfaces.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.entities.PCRRealTimeResult;
import com.certacure.lis.interfaces.repo.PCRRealTimeResultRepo;

@Service("PCRRealTimeResultService")
public class PCRRealTimeResultService extends GenericService<PCRRealTimeResult, PCRRealTimeResultRepo> {

	@Autowired
	private PCRRealTimeResultRepo repo;

	@Override
	protected PCRRealTimeResultRepo getRepository() {
		return repo;
	}

	public PCRRealTimeResult addPcrRealTimeResult(PCRRealTimeResult pcrRealTimeResult) {
		return getRepository().save(pcrRealTimeResult);
	}

	public void updateResults(List<PCRRealTimeResult> lstResultsSent) {
		getRepository().saveAll(lstResultsSent);

	}

}
