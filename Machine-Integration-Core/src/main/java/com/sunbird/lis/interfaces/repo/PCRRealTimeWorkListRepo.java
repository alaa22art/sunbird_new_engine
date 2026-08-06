package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.PCRRealTimeWorkList;

@Repository("PCRRealTimeWorkListRepo")
public interface PCRRealTimeWorkListRepo extends GenericRepository<PCRRealTimeWorkList> {

}
