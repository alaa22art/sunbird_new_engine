package com.sunbird.lis.interfaces.admin.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.admin.model.SecRole;
import com.sunbird.lis.interfaces.admin.model.SecUser;
import com.sunbird.lis.interfaces.admin.model.SecUserRole;

/**
 * SecUserRoleRepo.java
 * 
 **/

@Repository("SecUserRoleRepo")
public interface SecUserRoleRepo extends GenericRepository<SecUserRole> {

	void deleteAllBySecUser(SecUser secUser);

	void deleteAllBySecRole(SecRole secRole);

	@Query("SELECT DISTINCT sur FROM SecUserRole sur "
			+ "LEFT JOIN sur.secUser user "
			+ "LEFT JOIN FETCH sur.secRole r "
			+ "LEFT JOIN FETCH r.secRoleRights rr "
			+ "LEFT JOIN FETCH rr.secRight "
			+ "WHERE "
			+ "user.rid = :userRid")
	Set<SecUserRole> getBySecUser(@Param("userRid") Long userRid);
}
