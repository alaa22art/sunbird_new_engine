package com.certacure.lis.interfaces.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.AckMessageSequance;

import com.certacure.lis.interfaces.repo.AckMessageSequanceRepo;



@Service("AckMessageSequanceService")
public class AckMessageSequanceService extends GenericService<AckMessageSequance, AckMessageSequanceRepo> {

	@Autowired
	private AckMessageSequanceRepo repo;

	@Override
	protected AckMessageSequanceRepo getRepository() {
		return repo;
	}
	
	
		@InterceptorFree
	    public AckMessageSequance add(AckMessageSequance ackMessageSequanse) 
	    {
		
		 return getRepository().save(ackMessageSequanse);

	    }

		@InterceptorFree
		public AckMessageSequance getElegTransByMessageId(Long messageControlId) {
			return getRepository().getElegTransByMessageId(messageControlId);
			
		}
	 
}
