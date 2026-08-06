package com.sunbird.lis.interfaces.service;

/**
*
* @author Alaa Himour <ahimour@certacuresolutions.com>
* @since Dec/21 2018
* 
*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.entities.Driver;
import com.sunbird.lis.interfaces.repo.LkpDriverRepo;

@Service("DriverService")
public class DriverService extends GenericService<Driver, LkpDriverRepo> {

	@Autowired
	private LkpDriverRepo repo;

	@Override
	protected LkpDriverRepo getRepository() {
		return repo;
	}

	public Driver addDriver(Driver driver) {
		return repo.save(driver);
	}

	public Driver updateDriver(Driver driver) {
		return repo.save(driver);
	}

	public void deleteDriver(Long id) {
		repo.deleteById(id);
	}
}
