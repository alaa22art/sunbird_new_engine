package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.ComLanguage;

/**
 * ComLanguageRepo.java
 * 
 **/

@Repository("ComLanguageRepo")
public interface ComLanguageRepo extends GenericRepository<ComLanguage> {

}
