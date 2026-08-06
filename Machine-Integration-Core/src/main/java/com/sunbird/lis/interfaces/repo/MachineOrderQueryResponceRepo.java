package com.sunbird.lis.interfaces.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.MachineOrderQueryResponse;

@Repository("MachineOrderQueryResponceRepo")
public interface MachineOrderQueryResponceRepo extends GenericRepository<MachineOrderQueryResponse> {

	@Query("SELECT moq FROM MachineOrderQueryResponse moq"
			+ " LEFT JOIN FETCH moq.machineQueryId mq "
			+ " LEFT JOIN FETCH mq.machineResultList mr "
			+ " WHERE "
			+ " moq.machineOrderId.rid = :machineOrderId")
	Set<MachineOrderQueryResponse> findQueryResponseByOrderId(@Param("machineOrderId") Long rid);

}
