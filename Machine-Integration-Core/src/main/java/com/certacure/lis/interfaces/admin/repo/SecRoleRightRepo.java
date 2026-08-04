package com.certacure.lis.interfaces.admin.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.admin.model.SecRight;
import com.certacure.lis.interfaces.admin.model.SecRole;
import com.certacure.lis.interfaces.admin.model.SecRoleRight;

/**
 * SecRoleRightRepo.java
 * 
 **/

@Repository("SecRoleRightRepo")
public interface SecRoleRightRepo extends GenericRepository<SecRoleRight> {

	void deleteAllBySecRole(SecRole secRole);

	void deleteAllBySecRight(SecRight secRight);
}
