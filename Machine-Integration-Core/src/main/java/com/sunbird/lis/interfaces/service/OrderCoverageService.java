package com.sunbird.lis.interfaces.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.entities.AckMessageSequance;
import com.sunbird.lis.interfaces.entities.OrderCoverageEntity;
import com.sunbird.lis.interfaces.repo.AckMessageSequanceRepo;
import com.sunbird.lis.interfaces.repo.OrderCoverageRepo;



@Service("OrderCoverageService")
public class OrderCoverageService extends GenericService<OrderCoverageEntity, OrderCoverageRepo> {

	@Autowired
	private OrderCoverageRepo repo;

	@Override
	protected OrderCoverageRepo getRepository() {
		return repo;
	}

	@InterceptorFree
	public void addOrderCoverage(OrderCoverageEntity orderCoverage) {
		
		getRepository() .save(orderCoverage);
		
	}
	
	

}
