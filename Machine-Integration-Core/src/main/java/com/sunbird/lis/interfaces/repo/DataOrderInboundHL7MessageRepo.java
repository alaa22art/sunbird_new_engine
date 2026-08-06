package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.DataOrderInboundHL7Message;

/**

 */
@Repository("DataOrderInboundHL7MessageRepo")
public interface DataOrderInboundHL7MessageRepo extends GenericRepository<DataOrderInboundHL7Message> {

}
