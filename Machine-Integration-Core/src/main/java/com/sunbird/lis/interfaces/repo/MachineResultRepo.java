package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MachineResult;

/**

 */
@Repository("MachineResultRepo")
public interface MachineResultRepo extends GenericRepository<MachineResult> {

	public List<MachineResult> findTop1BySampleNoAndMachineOrderByRidDesc(String sampleNo, Machine machine);

	public List<MachineResult> findByMachineOrderByRidDesc(Machine machine);

}
