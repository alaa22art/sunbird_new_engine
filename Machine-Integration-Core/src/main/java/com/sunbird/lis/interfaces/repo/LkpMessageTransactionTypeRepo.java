package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionType;
import com.certacure.lis.interfaces.entities.MessageTransaction;

/**
 * LkpMasterRepo.java
 * 
 **/

@Repository("LkpMessageTransactionTypeRepo")
public interface LkpMessageTransactionTypeRepo extends GenericRepository<LkpMessageTransactionType> {

}
