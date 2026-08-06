package com.certacure.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.MachineTypeTest;
import com.certacure.lis.interfaces.entities.TestCatalog;

@Repository("MachineTypeTestsRepo")
public interface MachineTypeTestRepo extends GenericRepository<MachineTypeTest> {

	@Query("SELECT mtt FROM MachineTypeTest mtt "
			+ " LEFT JOIN FETCH mtt.testId t "
			+ " LEFT JOIN FETCH mtt.machineTypeId mt "
			+ " WHERE t.rid = :testId "
			+ " and "
			+ " mt.rid = :machineTypeId ")
	public MachineTypeTest findByTestAndMachineType(@Param("testId") Long testId, @Param("machineTypeId") Long machineTypeId);

	@Query("SELECT mtt FROM MachineTypeTest mtt "
			+ " LEFT JOIN FETCH mtt.testId t "
			+ " LEFT JOIN FETCH mtt.machineTypeId mt "
			+ " WHERE t.rid = :testId "
			+ " and "
			+ " mt.rid = :machineTypeId "
			+ " and "
			+ " mtt.rid != :rid ")
	public MachineTypeTest findByTestAndMachineTypeNotId(@Param("rid") Long rid, @Param("testId") Long testId,
			@Param("machineTypeId") Long machineTypeId);

	public void deleteByTestId(TestCatalog test);

	@Query("SELECT mtt FROM MachineTypeTest mtt "
			+ " LEFT JOIN FETCH mtt.machineTypeId mt "
			+ " WHERE mt.rid = :mahcineTypeId"
			+ " AND "
			+ " mtt.isActive =1 ")
	public List<MachineTypeTest> getAllTestsByMachineTypeId(@Param("mahcineTypeId") Long mahcineTypeId);
}
