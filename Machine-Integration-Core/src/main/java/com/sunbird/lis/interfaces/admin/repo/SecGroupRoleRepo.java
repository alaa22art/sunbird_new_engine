package com.certacure.lis.interfaces.admin.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.admin.model.SecGroup;
import com.certacure.lis.interfaces.admin.model.SecGroupRole;
import com.certacure.lis.interfaces.admin.model.SecRole;

/**
 * SecGroupRoleRepo.java
 * 
 **/

@Repository("SecGroupRoleRepo")
public interface SecGroupRoleRepo extends GenericRepository<SecGroupRole> {

	void deleteAllBySecRole(SecRole secRole);

	void deleteAllBySecGroup(SecGroup secGroup);

}
