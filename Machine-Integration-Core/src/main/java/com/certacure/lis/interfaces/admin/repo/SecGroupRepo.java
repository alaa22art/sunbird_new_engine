package com.certacure.lis.interfaces.admin.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.admin.model.SecGroup;

/**
 * SecGroupRepo.java
 * 
 * @since Sep/27/2017
 **/

@Repository("SecGroupRepo")
public interface SecGroupRepo extends GenericRepository<SecGroup> {

}
