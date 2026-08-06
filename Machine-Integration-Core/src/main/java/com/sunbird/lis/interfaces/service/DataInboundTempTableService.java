package com.sunbird.lis.interfaces.service;

/**
*

* 
*/
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.entities.DataInboundHL7Message;
import com.sunbird.lis.interfaces.entities.DataInboundJSONMessage;
import com.sunbird.lis.interfaces.entities.DataInboundTempTable;
import com.sunbird.lis.interfaces.repo.DataInboundHL7MessageRepo;
import com.sunbird.lis.interfaces.repo.DataInboundJSONMessageRepo;
import com.sunbird.lis.interfaces.repo.DataInboundTempTableRepo;

@Service("DataInboundTempTable")
public class DataInboundTempTableService extends GenericService<DataInboundTempTable, DataInboundTempTableRepo> {

	@Autowired
	private DataInboundTempTableRepo repo;

	@Override
	protected DataInboundTempTableRepo getRepository() {
		return repo;
	}

	public DataInboundTempTable addInbound(DataInboundTempTable dataInbound) {
	    DataInboundTempTable saveDataInbound = repo.save(dataInbound);
		return saveDataInbound;
	}

	
	public DataInboundTempTable updateOrder(DataInboundTempTable dataInbound) {
		return repo.save(dataInbound);
	}
	
	@InterceptorFree
	public List<DataInboundTempTable> getAllPendingMessage() {
        return repo.getAllReadyToSendMessages();
            
    }
	
}
