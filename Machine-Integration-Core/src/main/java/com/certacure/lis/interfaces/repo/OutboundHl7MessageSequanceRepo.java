package com.certacure.lis.interfaces.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.OutboundHl7MessageSequance;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.LkpGender;

@Repository("OutboundHl7MessageSequanceRepo")
public interface OutboundHl7MessageSequanceRepo extends GenericRepository<OutboundHl7MessageSequance> {

	
	@Query("select a from OutboundHl7MessageSequance a where a.rid = :messageControlId")
	public OutboundHl7MessageSequance getElegTransByMessageId(Long messageControlId);
	
	@InterceptorFree
	@Query(value = "SELECT nextval('hl7_outbound_message_sequance')", nativeQuery = true)
    public Long getNextSequenceValue();

}

