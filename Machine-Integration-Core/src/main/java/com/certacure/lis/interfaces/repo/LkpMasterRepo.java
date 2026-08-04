package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.LkpMaster;

/**
 * LkpMasterRepo.java
 * 
 **/

@Repository("LkpMasterRepo")
public interface LkpMasterRepo extends GenericRepository<LkpMaster> {

}
