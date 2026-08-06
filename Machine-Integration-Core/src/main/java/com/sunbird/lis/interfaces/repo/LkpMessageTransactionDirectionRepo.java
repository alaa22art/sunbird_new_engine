package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.sunbird.lis.interfaces.entities.MessageTransaction;

/**
 * LkpMasterRepo.java
 * 
 **/

@Repository("LkpMessageTransactionDirectionRepo")
public interface LkpMessageTransactionDirectionRepo extends GenericRepository<LkpMessageTransactionDirection> {

}
