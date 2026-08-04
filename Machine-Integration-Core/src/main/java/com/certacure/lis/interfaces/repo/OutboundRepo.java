package com.certacure.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.OutboundInformation;

/**
 */
@Repository("OutboundRepo")
public interface OutboundRepo extends GenericRepository<OutboundInformation> {

	@Query("select t from OutboundInformation t where t.specimenId = :specimenId and t.machineName = :machineName")
	public List<OutboundInformation> getBySampleAndMachine(@Param("specimenId") String specimenId,
			@Param("machineName") String machineName);

	@Query("select t from Machine t where t.machineActorPath = :machineActorPath")
	public Machine getMachineByActorPath(@Param("machineActorPath") String machineActorPath);
}
