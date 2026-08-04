package com.certacure.lis.interfaces.admin.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.admin.model.SecUser;

/**
 * SecUserRepo.java
 * 
 **/

@Repository("SecUserRepo")
public interface SecUserRepo extends GenericRepository<SecUser> {

	@Query("SELECT su.password FROM SecUser su WHERE su.rid=?1")
	String fetchPasswordById(Long rid);

	@Query("select su from SecUser su "
			+ "left join fetch su.comLanguage "
			+ "left join fetch su.lkpGender "
			+ "left join fetch su.lkpUserStatus "
			+ "where lower(su.username) = lower(:username)")
	SecUser loadUserByUsername(@Param("username") String username);

	SecUser findByUsernameIgnoreCase(String username);

	SecUser findByEmailIgnoreCase(String email);

	@Modifying
	@Query("UPDATE SecUser su SET su.lastLoginTime=?2 WHERE su.rid=?1")
	void updateLastLoginTime(Long rid, Date date);

	@Modifying
	@Query("UPDATE SecUser su SET su.password = :newPassword WHERE su.rid = :userRid")
	void updatePassword(@Param("userRid") Long userRid, @Param("newPassword") String newPassword);

}
