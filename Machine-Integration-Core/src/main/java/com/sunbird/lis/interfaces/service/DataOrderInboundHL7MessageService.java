package com.sunbird.lis.interfaces.service;

/**
*

* 
*/
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.entities.DataOrderInboundHL7Message;
import com.sunbird.lis.interfaces.repo.DataOrderInboundHL7MessageRepo;

@Service("DataOrderInboundHL7MessageService")
public class DataOrderInboundHL7MessageService extends GenericService<DataOrderInboundHL7Message, DataOrderInboundHL7MessageRepo> {

	@Autowired
	private DataOrderInboundHL7MessageRepo repo;

	@Override
	protected DataOrderInboundHL7MessageRepo getRepository() {
		return repo;
	}

	public DataOrderInboundHL7Message addOrder(DataOrderInboundHL7Message dataOrderInboundHL7Message) {
		DataOrderInboundHL7Message saveOrderInboundHL7Message = repo.save(dataOrderInboundHL7Message);
		return saveOrderInboundHL7Message;
	}

	public void addListOrder(List<DataOrderInboundHL7Message> dataOrderInboundHL7Message) {
		repo.saveAll(dataOrderInboundHL7Message);
	}

	public DataOrderInboundHL7Message updateOrder(DataOrderInboundHL7Message dataOrderInboundHL7Message) {
		return repo.save(dataOrderInboundHL7Message);
	}

	public void deleteResult(Long rId) {
		repo.deleteById(rId);
	}

}
