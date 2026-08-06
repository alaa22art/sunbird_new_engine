package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionType;
import com.sunbird.lis.interfaces.entities.MessageTransaction;

/**
 * LkpMasterRepo.java
 * 
 **/

@Repository("LkpMessageTransactionTypeRepo")
public interface LkpMessageTransactionTypeRepo extends GenericRepository<LkpMessageTransactionType> {

}
