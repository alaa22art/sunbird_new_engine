package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.LkpUserStatus;

/**
 * LkpUserStatusRepo.java
 * 
 **/

@Repository("LkpUserStatusRepo")
public interface LkpUserStatusRepo extends GenericRepository<LkpUserStatus> {

}
