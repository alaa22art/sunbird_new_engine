package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.DataResultOutboundHL7Message;

/**

 */
@Repository("DataResultOutboundHL7MessageRepo")
public interface DataResultOutboundHL7MessageRepo extends GenericRepository<DataResultOutboundHL7Message> {

}
