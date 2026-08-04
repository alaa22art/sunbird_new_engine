package com.certacure.lis.interfaces.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.MachineOrderQueryResponse;

@Repository("MachineOrderQueryResponceRepo")
public interface MachineOrderQueryResponceRepo extends GenericRepository<MachineOrderQueryResponse> {

	@Query("SELECT moq FROM MachineOrderQueryResponse moq"
			+ " LEFT JOIN FETCH moq.machineQueryId mq "
			+ " LEFT JOIN FETCH mq.machineResultList mr "
			+ " WHERE "
			+ " moq.machineOrderId.rid = :machineOrderId")
	Set<MachineOrderQueryResponse> findQueryResponseByOrderId(@Param("machineOrderId") Long rid);

}
