package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.CheckInEntity;

@Repository("CheckInRepo")
public interface CheckInRepo extends GenericRepository<CheckInEntity> {

}
