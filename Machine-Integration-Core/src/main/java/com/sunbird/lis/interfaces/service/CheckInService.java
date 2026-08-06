package com.sunbird.lis.interfaces.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.entities.CheckInEntity;
import com.sunbird.lis.interfaces.repo.CheckInRepo;

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
