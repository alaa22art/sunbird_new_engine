package com.certacure.lis.interfaces.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.certacure.lis.interfaces.entities.LkpMessageTransactionType;
import com.certacure.lis.interfaces.entities.LkpProtocol;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageTransactionType;
import com.certacure.lis.interfaces.repo.LkpMessageTransactionDirectionRepo;
import com.certacure.lis.interfaces.repo.LkpProtocolRepo;

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
