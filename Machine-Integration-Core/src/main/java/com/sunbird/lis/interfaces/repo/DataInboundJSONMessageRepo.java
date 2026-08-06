package com.sunbird.lis.interfaces.repo;




import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.DataInboundJSONMessage;
import com.sunbird.lis.interfaces.entities.Machine;




@Repository("DataInboundJSONMessageRepo")
public interface DataInboundJSONMessageRepo extends GenericRepository<DataInboundJSONMessage> {

  
    
    @Query("select dijm from DataInboundJSONMessage dijm where dijm.isSent = 0")
    public List<DataInboundJSONMessage> getAllReadyToSendMessages();

   
    
    

}
