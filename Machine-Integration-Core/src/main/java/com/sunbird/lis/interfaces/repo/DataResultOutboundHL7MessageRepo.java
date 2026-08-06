package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.DataResultOutboundHL7Message;

/**

 */
@Repository("DataResultOutboundHL7MessageRepo")
public interface DataResultOutboundHL7MessageRepo extends GenericRepository<DataResultOutboundHL7Message> {

}
