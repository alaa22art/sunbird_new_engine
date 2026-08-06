/**
 * 
 */
package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.MachineTypePanel;

/**
 */
@Repository("MachineTypePanelRepo")
public interface MachineTypePanelRepo extends GenericRepository<MachineTypePanel> {

	@Query("SELECT pnl FROM MachineTypePanel pnl "
			+ "LEFT JOIN FETCH  pnl.machineType mt "
			+ " WHERE "
			+ "mt.rid = :machineTypeId "
			+ "AND "
			+ " pnl.panelName = :panelName ")
	public MachineTypePanel findPanelByMachineTypeAndTestName(@Param("machineTypeId") long machineTypeId,
			@Param("panelName") String panelName);

	@Query("SELECT pnl FROM MachineTypePanel pnl "
			+ "LEFT JOIN FETCH  pnl.machineType mt "
			+ " WHERE "
			+ "mt.rid = :machineTypeRid ")
	public List<MachineTypePanel> findPanelByMachineType(Long machineTypeRid);

	@Query("SELECT pnl FROM MachineTypePanel pnl "
			+ "LEFT JOIN FETCH  pnl.machineType mt "
			+ " WHERE "
			+ "mt.rid = :machineTypeRid "
			+ "AND "
			+ "pnl.panelName = :panelName ")
	public List<MachineTypePanel> findPanelHostCodeByMachineTypeAndPanelName(Long machineTypeRid, String panelName);

}
