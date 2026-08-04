package com.certacure.lis.interfaces.admin.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.admin.model.SecRight;

/**
 * SecRightRepo.java
 * 
 **/

@Repository("SecRightRepo")
public interface SecRightRepo extends GenericRepository<SecRight> {

}
