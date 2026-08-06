package com.sunbird.lis.interfaces.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionType;
import com.sunbird.lis.interfaces.entities.LkpProtocol;
import com.sunbird.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageTransactionType;
import com.sunbird.lis.interfaces.repo.LkpMessageTransactionDirectionRepo;
import com.sunbird.lis.interfaces.repo.LkpProtocolRepo;

@Service("MessageTransactionDirectionService")
public class MessageTrnsactionDirectionService extends GenericService<LkpMessageTransactionDirection, LkpMessageTransactionDirectionRepo> {

	@Autowired
	private LkpMessageTransactionDirectionRepo repo;

	@Override
	protected LkpMessageTransactionDirectionRepo getRepository() {
		return repo;
	}

	public LkpMessageTransactionDirection addProtocol(LkpMessageTransactionDirection direction) {
		return repo.save(direction);
	}

	public LkpMessageTransactionDirection updateProtocol(LkpMessageTransactionDirection direction) {
		return repo.save(direction);
	}

	public void deleteProtocol(Long rId) {
		repo.deleteById(rId);
	}

}
