package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.LkpCity;

/**
 * LkpCityRepo.java
 * 
 **/

@Repository("LkpCityRepo")
public interface LkpCityRepo extends GenericRepository<LkpCity> {

}
