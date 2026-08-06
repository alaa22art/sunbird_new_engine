package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.TestCatalog;

@Repository("TestCatalogRepo")
public interface TestCatalogRepo extends GenericRepository<TestCatalog> {

	@Query("select tc from TestCatalog tc "
			+ "LEFT JOIN  tc.machineTestsList mt where mt.hostCode  =:hostCode and mt.machine= :machine ")
	public List<TestCatalog> getMachineTestsList(@Param("hostCode") String hostCode,
			@Param("machine") Machine machine);

	@Query("select tc from TestCatalog tc "
			+ "LEFT JOIN  tc.machineTestsList mt where mt.hostCode  =:hostCode and mt.machine= :machine ")
	public TestCatalog getMachineTests(@Param("hostCode") String hostCode,
			@Param("machine") Machine machine);

	@Query("select tc from TestCatalog tc")
	public List<TestCatalog> getTestCatalogList();

}
