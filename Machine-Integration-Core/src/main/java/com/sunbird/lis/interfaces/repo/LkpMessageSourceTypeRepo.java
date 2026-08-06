/**
 * 
 */
package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.LkpMessageSourceType;

/**

 */
@Repository("LkpMessageSourceTypeRepo")
public interface LkpMessageSourceTypeRepo extends GenericRepository<LkpMessageSourceType> {

}
