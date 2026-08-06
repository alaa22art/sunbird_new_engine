package com.sunbird.lis.interfaces.admin.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.admin.model.SecRight;

/**
 * SecRightRepo.java
 * 
 **/

@Repository("SecRightRepo")
public interface SecRightRepo extends GenericRepository<SecRight> {

}
