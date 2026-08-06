package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.MessageTransaction;

/**
 * LkpMasterRepo.java
 * 
 **/

@Repository("MessageTransactionRepo")
public interface MessageTransactionRepo extends GenericRepository<MessageTransaction> {

}
