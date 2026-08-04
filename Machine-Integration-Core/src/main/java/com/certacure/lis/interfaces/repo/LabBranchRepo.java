/**
 * 
 */
package com.certacure.lis.interfaces.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.LabBranch;

/**
 *
 * 
 */
@Repository("BranchRepo")
public interface LabBranchRepo extends GenericRepository<LabBranch> {

	@Query("SELECT lb.integrationToken FROM LabBranch lb WHERE lb.rid = :branchRid")
	String fetchIntegrationTokenById(@Param("branchRid") Long branchRid);
}
