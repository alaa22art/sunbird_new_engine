package com.sunbird.lis.interfaces.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.AckMessageSequance;
import com.sunbird.lis.interfaces.entities.LkpGender;

@Repository("AckMessageSequanceRepo")
public interface AckMessageSequanceRepo extends GenericRepository<AckMessageSequance> {

	
	@Query("select a from AckMessageSequance a where a.rid = :messageControlId")
	public AckMessageSequance getElegTransByMessageId(Long messageControlId);

}

