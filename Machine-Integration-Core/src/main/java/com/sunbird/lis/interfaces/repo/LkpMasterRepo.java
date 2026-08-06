package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.LkpMaster;

/**
 * LkpMasterRepo.java
 * 
 **/

@Repository("LkpMasterRepo")
public interface LkpMasterRepo extends GenericRepository<LkpMaster> {

}
