package com.certacure.lis.interfaces.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.entities.ComLanguage;
import com.certacure.lis.interfaces.repo.ComLanguageRepo;

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
