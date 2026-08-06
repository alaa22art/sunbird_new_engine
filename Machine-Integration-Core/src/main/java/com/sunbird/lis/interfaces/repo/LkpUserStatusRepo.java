package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.LkpUserStatus;

/**
 * LkpUserStatusRepo.java
 * 
 **/

@Repository("LkpUserStatusRepo")
public interface LkpUserStatusRepo extends GenericRepository<LkpUserStatus> {

}
