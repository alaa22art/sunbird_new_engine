package com.sunbird.lis.interfaces.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.entities.PCRRealTimeOrderResult;
import com.sunbird.lis.interfaces.repo.PCRRealTimeOrderResultRepo;

@Service("PCRRealTimeOrderResultService")
public class PCRRealTimeOrderResultService extends GenericService<PCRRealTimeOrderResult, PCRRealTimeOrderResultRepo> {

	@Autowired
	private PCRRealTimeOrderResultRepo repo;

	@Override
	protected PCRRealTimeOrderResultRepo getRepository() {
		return repo;
	}

	public List<PCRRealTimeOrderResult> getPcrRealTimeOrderResults(Long worklistOrderId) {
		return getRepository().find(Arrays.asList(
				new SearchCriterion("pcrRealTimeWorkListOrder.rid", worklistOrderId, FilterOperator.eq)),
				PCRRealTimeOrderResult.class, "pcrRealTimeResult");
	}

	public PCRRealTimeOrderResult addOrderResult(PCRRealTimeOrderResult pcrRealTimeOrderResult) {
		return getRepository().save(pcrRealTimeOrderResult);
	}

	public PCRRealTimeOrderResult getPcrRealTimeOrderResult(Long workListOrderRid) {
		return getRepository().findOne(
				Arrays.asList(new SearchCriterion("pcrRealTimeWorkListOrder.rid", workListOrderRid, FilterOperator.eq)),
				PCRRealTimeOrderResult.class);

	}
}
