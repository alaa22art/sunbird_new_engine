package com.certacure.lis.interfaces.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.MachineResult;

/**

 */
@Repository("MachineResultRepo")
public interface MachineResultRepo extends GenericRepository<MachineResult> {

	public List<MachineResult> findTop1BySampleNoAndMachineOrderByRidDesc(String sampleNo, Machine machine);

	public List<MachineResult> findByMachineOrderByRidDesc(Machine machine);

}
