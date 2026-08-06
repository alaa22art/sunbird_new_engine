package com.certacure.lis.interfaces.service;

/**
*

* 
*/
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.DataInboundJSONMessage;
import com.certacure.lis.interfaces.repo.DataInboundHL7MessageRepo;
import com.certacure.lis.interfaces.repo.DataInboundJSONMessageRepo;

@Service("DataInboundJSONMessageService")
public class DataInboundJSONMessageService extends GenericService<DataInboundJSONMessage, DataInboundJSONMessageRepo> {

	@Autowired
	private DataInboundJSONMessageRepo repo;

	@Override
	protected DataInboundJSONMessageRepo getRepository() {
		return repo;
	}

	public DataInboundJSONMessage addInbound(DataInboundJSONMessage dataInboundJSONMessage) {
		DataInboundJSONMessage saveDataInboundJSONMessage = repo.save(dataInboundJSONMessage);
		return saveDataInboundJSONMessage;
	}

	public void addListOrder(List<DataInboundJSONMessage> dataInboundJSONMessage) {
		repo.saveAll(dataInboundJSONMessage);
	}

	public DataInboundJSONMessage updateOrder(DataInboundJSONMessage dataInboundJSONMessage) {
		return repo.save(dataInboundJSONMessage);
	}
	
	@InterceptorFree
	public List<DataInboundJSONMessage> getAllPendingJSONMessage() {
        return repo.getAllReadyToSendMessages();
            
    }
	
}
