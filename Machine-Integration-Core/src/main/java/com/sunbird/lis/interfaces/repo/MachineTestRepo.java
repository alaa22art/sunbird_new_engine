package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MachineOrder;
import com.sunbird.lis.interfaces.entities.MachineTest;
import com.sunbird.lis.interfaces.entities.TestCatalog;

@Repository("MachineTestRepo")
public interface MachineTestRepo extends GenericRepository<MachineTest> {

	@Query("select mt from MachineTest mt "
			+ "LEFT JOIN FETCH  mt.testCatalog tc LEFT JOIN FETCH tc.specimenType "
			+ "where tc.requesterTestCode in :requesterTestCodeList and mt.machine= :machine ")
	public List<MachineTest> getMachineTests(
			@Param("requesterTestCodeList") List<String> requesterTestCodeList,
			@Param("machine") Machine machine);
	
	/*@Query("")
	public List<MachineTest> getMachinePanel(
			@Param("barcode") String barcode,
			@Param("panelCode") String panelCode,
			@Param("machine") Machine machine);*/
	
	

	@Query("select mt from MachineTest mt LEFT JOIN FETCH  mt.testCatalog tc "
			+ "where mt.machine.rid = :rid")
	public List<MachineTest> getMachineTestCatalogList(
			@Param("rid") Long rid);

	@Query("select mt from MachineTest mt "
			+ "LEFT JOIN FETCH  mt.testCatalog tc LEFT JOIN FETCH tc.specimenType "
			+ "where mt.hostCode =:hostCode and mt.machine= :machine and mt.isActive = 1")
	public List<MachineTest> getMachineTestsByHostCode(
			@Param("hostCode") String hostCode,
			@Param("machine") Machine machine);

	@Query("SELECT mt FROM MachineTest mt "
			+ " LEFT JOIN FETCH mt.testCatalog t "
			+ " LEFT JOIN FETCH mt.machine m "
			+ " WHERE "
			+ " t.rid = :testId "
			+ " and "
			+ " m.rid = :machineId ")
	public MachineTest findByTestAndMachine(@Param("testId") Long testId, @Param("machineId") Long machineTypeId);

	@Query("SELECT mt FROM MachineTest mt "
			+ " LEFT JOIN FETCH mt.testCatalog t "
			+ " LEFT JOIN FETCH mt.machine m "
			+ " WHERE "
			+ " t.rid = :testId "
			+ " and "
			+ " m.rid = :machineId "
			+ " and "
			+ " mt.rid != :rid ")
	public MachineTest findByTestAndMachineNotId(@Param("rid") Long rid, @Param("testId") Long testId,
			@Param("machineId") Long machineTypeId);

	public void deleteAllByTestCatalog(TestCatalog rId);

	@Modifying
	@Query("UPDATE MachineTest "
			+ "SET isActive = 1 "
			+ "WHERE "
			+ "machine.rid = :machineId ")
	public void activateAllTests(@Param("machineId") Long machineId);

	@Modifying
	@Query("UPDATE MachineTest "
			+ "SET isActive = 0 "
			+ "WHERE "
			+ "machine.rid = :machineId ")
	public void deactivateAllTests(@Param("machineId") Long machineId);

}
