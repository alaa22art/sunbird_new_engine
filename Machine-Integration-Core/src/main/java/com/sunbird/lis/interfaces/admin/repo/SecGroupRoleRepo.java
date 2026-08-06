package com.sunbird.lis.interfaces.admin.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.admin.model.SecGroup;
import com.sunbird.lis.interfaces.admin.model.SecGroupRole;
import com.sunbird.lis.interfaces.admin.model.SecRole;

/**
 * SecGroupRoleRepo.java
 * 
 **/

@Repository("SecGroupRoleRepo")
public interface SecGroupRoleRepo extends GenericRepository<SecGroupRole> {

	void deleteAllBySecRole(SecRole secRole);

	void deleteAllBySecGroup(SecGroup secGroup);

}
