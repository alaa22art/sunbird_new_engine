package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.ComTenantMessage;

/**
 * ComTenantMessageRepo.java
 * 

 **/

@Repository("ComTenantMessageRepo")
public interface ComTenantMessageRepo extends GenericRepository<ComTenantMessage> {

	ComTenantMessage findOneByCodeIgnoreCase(String code);

}
