package com.certacure.lis.interfaces.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.OutboundHl7MessageSequance;

import com.certacure.lis.interfaces.repo.OutboundHl7MessageSequanceRepo;



@Service("OutboundHl7MessageService")
public class OutboundHl7MessageSequanceService extends GenericService<OutboundHl7MessageSequance, OutboundHl7MessageSequanceRepo> {

	@Autowired
	private OutboundHl7MessageSequanceRepo repo;

	@Override
	protected OutboundHl7MessageSequanceRepo getRepository() {
		return repo;
	}
	
	
		@InterceptorFree
	    public OutboundHl7MessageSequance add(OutboundHl7MessageSequance ackMessageSequanse) 
	    {
		
		 return getRepository().save(ackMessageSequanse);

	    }

		@InterceptorFree
		public OutboundHl7MessageSequance getElegTransByMessageId(Long messageControlId) {
			return getRepository().getElegTransByMessageId(messageControlId);
			
		}
		
		@InterceptorFree
		public Long getNextSequenceValue() {
			return getRepository().getNextSequenceValue();
			
		}
	 
	 
}
