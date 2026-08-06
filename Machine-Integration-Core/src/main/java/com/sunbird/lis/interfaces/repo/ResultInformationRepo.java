package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.ResultInformation;

/**

 */
@Repository("ResultInformationRepo")
public interface ResultInformationRepo extends GenericRepository<ResultInformation> {

}
