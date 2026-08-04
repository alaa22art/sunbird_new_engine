package com.certacure.lis.interfaces.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.system.model.SysModule;
import com.certacure.lis.interfaces.system.repo.SysModuleRepo;

/**
 * SysModuleService.java
 * 
 **/

@Service("SysModuleService")
public class SysModuleService extends GenericService<SysModule, SysModuleRepo> {

	@Autowired
	private SysModuleRepo repo;

	@Override
	protected SysModuleRepo getRepository() {
		return repo;
	}

	public SysModule createSysModule(SysModule sysModule) {
		return getRepository().save(sysModule);
	}

	public void deleteSysModule(SysModule sysModule) {
		getRepository().delete(sysModule);
	}

	public SysModule findSysModuleById(Long id) {
		return getRepository().findById(id).get();
	}

	public SysModule updateSysModule(SysModule sysModule) {
		return getRepository().save(sysModule);
	}

}
