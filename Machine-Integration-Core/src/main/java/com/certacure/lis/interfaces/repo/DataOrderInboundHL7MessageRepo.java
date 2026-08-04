package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.DataOrderInboundHL7Message;

/**

 */
@Repository("DataOrderInboundHL7MessageRepo")
public interface DataOrderInboundHL7MessageRepo extends GenericRepository<DataOrderInboundHL7Message> {

}
