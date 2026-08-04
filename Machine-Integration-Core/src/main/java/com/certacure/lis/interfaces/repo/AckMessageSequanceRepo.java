package com.certacure.lis.interfaces.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.AckMessageSequance;
import com.certacure.lis.interfaces.entities.LkpGender;

@Repository("AckMessageSequanceRepo")
public interface AckMessageSequanceRepo extends GenericRepository<AckMessageSequance> {

	
	@Query("select a from AckMessageSequance a where a.rid = :messageControlId")
	public AckMessageSequance getElegTransByMessageId(Long messageControlId);

}

