package com.certacure.lis.interfaces.system.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.system.model.SysModule;

/**
 * SysModuleRepo.java
 * 
 **/

@Repository("SysModuleRepo")
public interface SysModuleRepo extends GenericRepository<SysModule> {

}
