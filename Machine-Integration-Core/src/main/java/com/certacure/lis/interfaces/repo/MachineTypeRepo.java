/**
 * 
 */
package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.MachineType;

/**

 */
@Repository("MachineTypeRepo")
public interface MachineTypeRepo extends GenericRepository<MachineType> {

}
