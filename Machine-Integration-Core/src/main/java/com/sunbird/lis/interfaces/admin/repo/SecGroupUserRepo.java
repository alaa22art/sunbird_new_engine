package com.sunbird.lis.interfaces.admin.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.admin.model.SecGroup;
import com.sunbird.lis.interfaces.admin.model.SecGroupUser;
import com.sunbird.lis.interfaces.admin.model.SecUser;

/**
 * SecGroupUserRepo.java
 * 
 **/

@Repository("SecGroupUserRepo")
public interface SecGroupUserRepo extends GenericRepository<SecGroupUser> {

	void deleteAllBySecUser(SecUser secUser);

	void deleteAllBySecGroup(SecGroup secGroup);

	@Query("SELECT DISTINCT sgu FROM SecGroupUser sgu "
			+ "LEFT JOIN sgu.secUser user "
			+ "LEFT JOIN FETCH sgu.secGroup g "
			+ "LEFT JOIN FETCH g.secGroupRoles gr "
			+ "LEFT JOIN FETCH gr.secRole sr "
			+ "LEFT JOIN FETCH sr.secRoleRights srr "
			+ "LEFT JOIN FETCH srr.secRight "
			+ "WHERE "
			+ "user.rid = :userRid")
	Set<SecGroupUser> getBySecUser(@Param("userRid") Long userRid);

}
