package com.sunbird.lis.interfaces.admin.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.admin.model.SecRight;
import com.sunbird.lis.interfaces.admin.model.SecRole;
import com.sunbird.lis.interfaces.admin.model.SecRoleRight;

/**
 * SecRoleRightRepo.java
 * 
 **/

@Repository("SecRoleRightRepo")
public interface SecRoleRightRepo extends GenericRepository<SecRoleRight> {

	void deleteAllBySecRole(SecRole secRole);

	void deleteAllBySecRight(SecRight secRight);
}
