package com.sunbird.lis.interfaces.repo;

import java.util.List;


import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.DataInboundHL7Message;

@Repository("DataInboundHL7MessageRepo")
public interface DataInboundHL7MessageRepo extends GenericRepository<DataInboundHL7Message> {

   
    
    

}
