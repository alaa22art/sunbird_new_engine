package com.sunbird.lis.interfaces.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.entities.CheckInEntity;
import com.sunbird.lis.interfaces.entities.StockEntity;
import com.sunbird.lis.interfaces.repo.CheckInRepo;
import com.sunbird.lis.interfaces.repo.StockRepo;

@Service("StockService")
public class StockService extends GenericService<StockEntity, StockRepo> {
	
	
	@Autowired
	private StockRepo repo;

	@Override
	protected StockRepo getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}

	public StockEntity addStock(StockEntity stockEntity) {
		return getRepository().save(stockEntity);
		
	}


}
