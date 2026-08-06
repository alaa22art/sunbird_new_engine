package com.sunbird.lis.interfaces.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.entities.ComLanguage;
import com.sunbird.lis.interfaces.repo.ComLanguageRepo;

/**
 * ComLanguageService.java
 * 
 **/

@Service("ComLanguageService")
public class ComLanguageService extends GenericService<ComLanguage, ComLanguageRepo> {

	@Autowired
	private ComLanguageRepo comLanguageRepo;

	@Override
	protected ComLanguageRepo getRepository() {
		return this.comLanguageRepo;
	}

}
