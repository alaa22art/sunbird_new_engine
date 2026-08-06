package com.sunbird.lis.interfaces.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MachineQuery;

/**
 */
@Repository("MachineQueryRepo")
public interface MachineQueryRepo extends GenericRepository<MachineQuery> {

	public MachineQuery findTop1BySampleNoAndMachineOrderByRidDesc(String sampleNo, Machine machine);

	@Query("SELECT mq FROM MachineQuery mq "
			+ "LEFT JOIN FETCH mq.machineResultList mr "
			+ "WHERE "
			+ "mr.machineQueryId.rid = :machineQueryId and mr.testCode = :testCode ")
	Set<MachineQuery> findQueryResult(@Param("machineQueryId") Long machineQueryId, @Param("testCode") String testCode);

}
