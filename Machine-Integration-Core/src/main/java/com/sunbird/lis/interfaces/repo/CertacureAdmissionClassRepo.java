/**
 * 
 */
package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.CertacureAdmissionClass;
import com.sunbird.lis.interfaces.entities.LkpProtocol;
import com.sunbird.lis.interfaces.entities.MachineOrder;

/**

 */
@Repository("CertacureAdmissionClassRepo")
public interface CertacureAdmissionClassRepo extends GenericRepository<CertacureAdmissionClass> 
{
	@Query("select t from CertacureAdmissionClass t where t.name LIKE :className ")
	public List<CertacureAdmissionClass> getCertacureAdmissionCode(@Param("className") String className);
}

