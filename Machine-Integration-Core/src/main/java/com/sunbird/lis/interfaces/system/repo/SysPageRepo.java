package com.certacure.lis.interfaces.system.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.system.model.SysPage;

/**
 * SysPageRepo.java
 * 
 **/

@Repository("SysPageRepo")
public interface SysPageRepo extends GenericRepository<SysPage> {

}
