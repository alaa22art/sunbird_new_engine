package com.sunbird.lis.interfaces.admin.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.admin.model.SecTenant;

/**
 * SecTenantRepo.class
 * 

 **/
@Repository("SecTenantRepo")
public interface SecTenantRepo extends GenericRepository<SecTenant> {

	@Query("SELECT st FROM SecTenant st "
			+ "LEFT JOIN FETCH st.country c "
			+ "LEFT JOIN FETCH c.currency "
			+ "WHERE st.rid = :rid")
	SecTenant fetchTenantDataById(@Param("rid") Long rid);

	SecTenant findByEmail(String email);

	SecTenant findByCode(String code);

}
