package com.certacure.lis.interfaces.repo;




import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;

import com.certacure.lis.interfaces.entities.DataInboundJSONMessage;
import com.certacure.lis.interfaces.entities.DataInboundTempTable;
import com.certacure.lis.interfaces.entities.Machine;




@Repository("DataInboundTempTableRepo")
public interface DataInboundTempTableRepo extends GenericRepository<DataInboundTempTable> {

  
    
    @Query("select di from DataInboundTempTable di where di.isSent = 0")
    public List<DataInboundTempTable> getAllReadyToSendMessages();

}
