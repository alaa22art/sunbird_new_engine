package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.CheckInEntity;
import com.certacure.lis.interfaces.entities.StockEntity;

@Repository("StockRepo")
public interface StockRepo extends GenericRepository<StockEntity> {

}
