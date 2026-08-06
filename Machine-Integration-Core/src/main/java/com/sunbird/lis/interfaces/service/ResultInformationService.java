package com.certacure.lis.interfaces.service;

/**
*
* @author Alaa Himour <ahimour@certacuresolutions.com>
* @since Dec/21 2018
* 
*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.entities.ResultInformation;
import com.certacure.lis.interfaces.repo.ResultInformationRepo;

@Service("ResultInformationService")
public class ResultInformationService extends GenericService<ResultInformation, ResultInformationRepo> {

	@Autowired
	private ResultInformationRepo repo;

	@Override
	protected ResultInformationRepo getRepository() {
		return repo;
	}

	public ResultInformation addResult(ResultInformation resultInformation) {
		return repo.save(resultInformation);
	}

	public ResultInformation updateResult(ResultInformation resultInformation) {
		return repo.save(resultInformation);
	}

	public void deleteResult(Long rId) {
		repo.deleteById(rId);
	}
}
