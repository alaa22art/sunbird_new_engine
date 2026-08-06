package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.Machine;

/**

 */
@Repository("MachineRepo")
public interface MachineRepo extends GenericRepository<Machine> {

	@Query("select t from Machine t where t.name = :name")
	public Machine getMachineByName(@Param("name") String name);

	@Query("select m from Machine m LEFT JOIN FETCH m.machineType mt LEFT JOIN FETCH mt.driver d where m.machineActorPath = :machineActorPath")
	public Machine getMachineByActorPath(@Param("machineActorPath") String machineActorPath);

	@Query("select m from Machine m LEFT JOIN FETCH m.machineType mt LEFT JOIN FETCH mt.driver d where m.serverPort = :serverPort")
	public Machine getMachineByPort(@Param("serverPort") Integer serverPort);

	@Query("select p from Machine p " +
			"left outer join fetch p.machineType e left outer join fetch e.driver")
	public List<Machine> getMachines();

	@Query("select p from Machine p " +
			"left outer join fetch p.machineType e left outer join fetch e.driver where p.isActive=1")
	public List<Machine> getActiveMachines();

	@Query("SELECT m.serverPort FROM Machine m WHERE m.serverPort=:serverPort")
	public String fetchServerPortExist(@Param("serverPort") Integer serverPort);

	@Query("SELECT m.serverPort FROM Machine m WHERE m.serverPort=:serverPort AND m.rid !=:rid")
	public String fetchServerPortExist(@Param("serverPort") Integer serverPort, @Param("rid") Long rid);

	@Query("SELECT m.name FROM Machine m WHERE m.name =:name")
	public String fetchMachineNameExist(@Param("name") String name);

	@Query("SELECT m.name FROM Machine m WHERE m.name =:name AND m.rid !=:rid")
	public String fetchMachineNameExist(@Param("name") String name, @Param("rid") Long rid);

	@Query("SELECT m FROM Machine m "
			+ "LEFT JOIN FETCH m.machineType mt "
			+ "WHERE m.rid= :rid")
	public Machine getMachineById(@Param("rid") Long rId);

}
