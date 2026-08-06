package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.PCRRealTimeOrder;

@Repository("PCRRealTimeOrderRepo")
public interface PCRRealTimeOrderRepo extends GenericRepository<PCRRealTimeOrder> {

}
