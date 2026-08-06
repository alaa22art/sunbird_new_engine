package com.sunbird.lis.interfaces.system.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.system.model.SysPage;

/**
 * SysPageRepo.java
 * 
 **/

@Repository("SysPageRepo")
public interface SysPageRepo extends GenericRepository<SysPage> {

}
