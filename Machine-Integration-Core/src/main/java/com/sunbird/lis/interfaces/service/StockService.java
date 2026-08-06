package com.certacure.lis.interfaces.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.entities.CheckInEntity;
import com.certacure.lis.interfaces.entities.StockEntity;
import com.certacure.lis.interfaces.repo.CheckInRepo;
import com.certacure.lis.interfaces.repo.StockRepo;

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
