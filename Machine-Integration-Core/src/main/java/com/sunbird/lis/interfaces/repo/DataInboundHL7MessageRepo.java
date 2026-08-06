package com.certacure.lis.interfaces.repo;

import java.util.List;


import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;

@Repository("DataInboundHL7MessageRepo")
public interface DataInboundHL7MessageRepo extends GenericRepository<DataInboundHL7Message> {

   
    
    

}
