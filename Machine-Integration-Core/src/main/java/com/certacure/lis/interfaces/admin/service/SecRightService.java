package com.certacure.lis.interfaces.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.admin.model.SecRight;
import com.certacure.lis.interfaces.admin.repo.SecRightRepo;

/**
 * SecRightService.java
 * 
 **/

@Service("SecRightService")
public class SecRightService extends GenericService<SecRight, SecRightRepo> {

	@Autowired
	private SecRightRepo rightRepo;

	@Override
	protected SecRightRepo getRepository() {
		return rightRepo;
	}

	public SecRight createRight(SecRight secRight) {
		return getRepository().save(secRight);
	}

	public List<SecRight> findRights() {
		return getRepository().find(new ArrayList<>(), SecRight.class, "sysPage");
	}

}
