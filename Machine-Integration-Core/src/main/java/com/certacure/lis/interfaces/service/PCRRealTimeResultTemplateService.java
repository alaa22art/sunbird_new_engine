package com.certacure.lis.interfaces.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.entities.PCRRealTimeResultTemplate;
import com.certacure.lis.interfaces.repo.PCRRealTimeResultTemplateRepo;

@Service("PCRRealTimeResultTemplateService")
public class PCRRealTimeResultTemplateService extends GenericService<PCRRealTimeResultTemplate, PCRRealTimeResultTemplateRepo> {

	@Autowired
	private PCRRealTimeResultTemplateRepo repo;

	@Override
	protected PCRRealTimeResultTemplateRepo getRepository() {
		return repo;
	}

	public PCRRealTimeResultTemplate getTemplate() {
		return getRepository().findOne(new ArrayList<>(), PCRRealTimeResultTemplate.class, "pcrRealTimeResultTemplateLines");

	}

}
