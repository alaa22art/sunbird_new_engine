package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.CheckInEntity;
import com.sunbird.lis.interfaces.entities.StockEntity;

@Repository("StockRepo")
public interface StockRepo extends GenericRepository<StockEntity> {

}
