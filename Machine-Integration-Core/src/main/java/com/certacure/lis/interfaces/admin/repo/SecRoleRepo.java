package com.certacure.lis.interfaces.admin.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.admin.model.SecRole;

/**
 * SecRoleRepo.java
 * 
 **/

@Repository("SecRoleRepo")
public interface SecRoleRepo extends GenericRepository<SecRole> {

}
