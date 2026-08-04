package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.ComTenantMessage;

/**
 * ComTenantMessageRepo.java
 * 

 **/

@Repository("ComTenantMessageRepo")
public interface ComTenantMessageRepo extends GenericRepository<ComTenantMessage> {

	ComTenantMessage findOneByCodeIgnoreCase(String code);

}
