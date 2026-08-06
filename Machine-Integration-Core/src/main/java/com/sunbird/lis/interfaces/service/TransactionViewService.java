package com.sunbird.lis.interfaces.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.entities.TransactionView;
import com.sunbird.lis.interfaces.repo.TransactionViewRepo;

@Service("TransactionViewService")
public class TransactionViewService {

	@Autowired
	private TransactionViewRepo repo;

	public List<TransactionView> getMachineTransactionPage(FilterablePageRequest filterablePageRequest) {

		List<TransactionView> page = repo.getMachineTransaction();

		return page;
	}

}
