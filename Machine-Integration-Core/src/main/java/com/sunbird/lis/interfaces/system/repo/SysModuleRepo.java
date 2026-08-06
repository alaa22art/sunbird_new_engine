package com.sunbird.lis.interfaces.system.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.system.model.SysModule;

/**
 * SysModuleRepo.java
 * 
 **/

@Repository("SysModuleRepo")
public interface SysModuleRepo extends GenericRepository<SysModule> {

}
