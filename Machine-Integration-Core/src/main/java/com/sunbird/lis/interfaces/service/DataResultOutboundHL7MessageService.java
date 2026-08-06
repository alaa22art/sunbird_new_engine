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
import com.sunbird.lis.interfaces.entities.DataResultOutboundHL7Message;
import com.sunbird.lis.interfaces.repo.DataResultOutboundHL7MessageRepo;

@Service("DataResultOutboundHL7MessageService")
public class DataResultOutboundHL7MessageService extends GenericService<DataResultOutboundHL7Message, DataResultOutboundHL7MessageRepo> {

	@Autowired
	private DataResultOutboundHL7MessageRepo repo;

	@Override
	protected DataResultOutboundHL7MessageRepo getRepository() {
		return repo;
	}

	@InterceptorFree
	public DataResultOutboundHL7Message addResult(DataResultOutboundHL7Message result) {
		DataResultOutboundHL7Message saveResultInfo = repo.save(result);
		return saveResultInfo;
	}

	public void addListResult(List<DataResultOutboundHL7Message> dataResultOutboundHL7MessageList) {
		repo.saveAll(dataResultOutboundHL7MessageList);
	}

	public DataResultOutboundHL7Message updateResult(DataResultOutboundHL7Message result) {
		return repo.save(result);
	}

}
