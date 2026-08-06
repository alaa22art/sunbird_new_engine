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
import com.sunbird.lis.interfaces.repo.DataInboundHL7MessageRepo;

@Service("DataInboundHL7MessageService")
public class DataInboundHL7MessageService extends GenericService<DataInboundHL7Message, DataInboundHL7MessageRepo> {

	@Autowired
	private DataInboundHL7MessageRepo repo;

	@Override
	protected DataInboundHL7MessageRepo getRepository() {
		return repo;
	}

	@InterceptorFree
	public DataInboundHL7Message addInbound(DataInboundHL7Message dataInboundHL7Message) {
		DataInboundHL7Message saveDataInboundHL7Message = repo.save(dataInboundHL7Message);
		return saveDataInboundHL7Message;
	}

	public void addListOrder(List<DataInboundHL7Message> dataInboundHL7Message) {
		repo.saveAll(dataInboundHL7Message);
	}

	public DataInboundHL7Message updateOrder(DataInboundHL7Message dataInboundHL7Message) {
		return repo.save(dataInboundHL7Message);
	}
	
	@InterceptorFree
	public List<DataInboundHL7Message> getAllPendingHL7Message(DataInboundHL7Message dataInboundHL7Message) {
        return repo.findAll();
    }
	
}
