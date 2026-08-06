package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.PCRRealTimeOrderResult;

@Repository("PCRRealTimeOrderResultRepo")
public interface PCRRealTimeOrderResultRepo extends GenericRepository<PCRRealTimeOrderResult> {

}
