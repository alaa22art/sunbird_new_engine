/**
 * 
 */
package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.LkpProtocol;

/**

 */
@Repository("LkpProtocolRepo")
public interface LkpProtocolRepo extends GenericRepository<LkpProtocol> {

}
