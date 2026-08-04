package com.certacure.lis.interfaces.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.AckMessageSequance;
import com.certacure.lis.interfaces.entities.OrderCoverageEntity;
import com.certacure.lis.interfaces.repo.AckMessageSequanceRepo;
import com.certacure.lis.interfaces.repo.OrderCoverageRepo;



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
