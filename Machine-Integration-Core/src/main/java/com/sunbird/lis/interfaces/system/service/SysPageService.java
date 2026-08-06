package com.sunbird.lis.interfaces.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.system.model.SysPage;
import com.sunbird.lis.interfaces.system.repo.SysPageRepo;

/**
 * SysPageService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/05/2017
 **/

@Service("SysPageService")
public class SysPageService extends GenericService<SysPage, SysPageRepo> {

	@Autowired
	private SysPageRepo repo;

	@Override
	protected SysPageRepo getRepository() {
		return repo;
	}

	public SysPage createSysPage(SysPage sysPage) {
		return getRepository().save(sysPage);
	}

	public void deleteSysPage(SysPage sysPage) {
		getRepository().delete(sysPage);
	}

	public SysPage findSysPageById(Long id) {
		return getRepository().findById(id).get();
	}

	public SysPage updateSysPage(SysPage sysPage) {
		return getRepository().save(sysPage);
	}

}
