package com.certacure.lis.interfaces.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MachineQuery;

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
