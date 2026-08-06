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
import com.sunbird.lis.interfaces.entities.LkpProtocol;
import com.sunbird.lis.interfaces.repo.LkpProtocolRepo;

@Service("ProtocolService")
public class ProtocolService extends GenericService<LkpProtocol, LkpProtocolRepo> {

	@Autowired
	private LkpProtocolRepo repo;

	@Override
	protected LkpProtocolRepo getRepository() {
		return repo;
	}

	public LkpProtocol addProtocol(LkpProtocol protocol) {
		return repo.save(protocol);
	}

	public LkpProtocol updateProtocol(LkpProtocol protocol) {
		return repo.save(protocol);
	}

	public void deleteProtocol(Long rId) {
		repo.deleteById(rId);
	}
}
