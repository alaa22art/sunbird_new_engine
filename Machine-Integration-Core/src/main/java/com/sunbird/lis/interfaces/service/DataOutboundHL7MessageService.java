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
import com.sunbird.lis.interfaces.entities.DataOutboundHL7Message;
import com.sunbird.lis.interfaces.repo.DataOutboundHL7MessageRepo;

@Service("DataOutboundHL7MessageService")
public class DataOutboundHL7MessageService extends GenericService<DataOutboundHL7Message, DataOutboundHL7MessageRepo> {

	@Autowired
	private DataOutboundHL7MessageRepo repo;

	@Override
	protected DataOutboundHL7MessageRepo getRepository() {
		return repo;
	}

	@InterceptorFree
	public DataOutboundHL7Message addInbound(DataOutboundHL7Message out) {
		DataOutboundHL7Message dataOutboundHL7Message = repo.save(out);
		return dataOutboundHL7Message;
	}

	@InterceptorFree
	public long getDataOutboundHL7MessageControlID() {

		return repo.getNextControlID();
	}

	public void addListResult(List<DataOutboundHL7Message> dataOutboundHL7MessageList) {
		repo.saveAll(dataOutboundHL7MessageList);
	}

	public DataOutboundHL7Message updateResult(DataOutboundHL7Message result) {
		return repo.save(result);
	}

}
