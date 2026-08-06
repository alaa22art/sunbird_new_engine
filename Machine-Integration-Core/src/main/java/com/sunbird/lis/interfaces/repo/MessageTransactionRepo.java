package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.MessageTransaction;

/**
 * LkpMasterRepo.java
 * 
 **/

@Repository("MessageTransactionRepo")
public interface MessageTransactionRepo extends GenericRepository<MessageTransaction> {

}
