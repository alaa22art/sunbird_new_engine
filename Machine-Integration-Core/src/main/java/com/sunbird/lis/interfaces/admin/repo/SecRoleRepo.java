package com.sunbird.lis.interfaces.admin.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.admin.model.SecRole;

/**
 * SecRoleRepo.java
 * 
 **/

@Repository("SecRoleRepo")
public interface SecRoleRepo extends GenericRepository<SecRole> {

}
