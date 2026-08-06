package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.LkpCity;

/**
 * LkpCityRepo.java
 * 
 **/

@Repository("LkpCityRepo")
public interface LkpCityRepo extends GenericRepository<LkpCity> {

}
