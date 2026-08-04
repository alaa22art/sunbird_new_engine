package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.LkpOrderActionCode;

/**
 * LkpOrderActionCodeRepo.java
 * 
 **/
@Repository("LkpOrderActionCodeRepo")
public interface LkpOrderActionCodeRepo extends GenericRepository<LkpOrderActionCode> 
{

}
