package com.certacure.lis.interfaces.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.entities.CheckInEntity;
import com.certacure.lis.interfaces.repo.CheckInRepo;

@Service("CheckInService")
public class CheckInService extends GenericService<CheckInEntity, CheckInRepo> {
	
	
	@Autowired
	private CheckInRepo repo;

	@Override
	protected CheckInRepo getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}

	public CheckInEntity createCheckInRecord(CheckInEntity checkInEntity) {
		return getRepository().save(checkInEntity);
		
	}

}
