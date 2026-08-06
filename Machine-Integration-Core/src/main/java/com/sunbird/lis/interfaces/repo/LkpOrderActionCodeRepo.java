package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.LkpOrderActionCode;

/**
 * LkpOrderActionCodeRepo.java
 * 
 **/
@Repository("LkpOrderActionCodeRepo")
public interface LkpOrderActionCodeRepo extends GenericRepository<LkpOrderActionCode> 
{

}
