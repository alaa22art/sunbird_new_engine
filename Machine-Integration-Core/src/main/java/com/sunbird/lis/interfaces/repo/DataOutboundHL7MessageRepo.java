package com.sunbird.lis.interfaces.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.DataOutboundHL7Message;

/**
 * 
 */
@Repository("DataOutboundHL7MessageRepo")
public interface DataOutboundHL7MessageRepo extends GenericRepository<DataOutboundHL7Message> {

	@Query(value = "SELECT nextval('public.outbound_control_id_sequance')", nativeQuery = true)
	public long getNextControlID();

}
