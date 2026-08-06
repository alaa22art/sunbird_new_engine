package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.PCRRealTimeOrderResult;

@Repository("PCRRealTimeOrderResultRepo")
public interface PCRRealTimeOrderResultRepo extends GenericRepository<PCRRealTimeOrderResult> {

}
