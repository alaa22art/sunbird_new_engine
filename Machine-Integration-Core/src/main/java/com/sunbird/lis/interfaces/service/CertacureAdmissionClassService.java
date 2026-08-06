package com.sunbird.lis.interfaces.service;

import java.util.Arrays;
import java.util.List;

/**
*
* @author Alaa Himour <ahimour@certacuresolutions.com>
* @since Dec/21 2018
* 
*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.entities.CertacureAdmissionClass;
import com.sunbird.lis.interfaces.entities.LkpProtocol;
import com.sunbird.lis.interfaces.entities.MachineType;
import com.sunbird.lis.interfaces.repo.CertacureAdmissionClassRepo;
import com.sunbird.lis.interfaces.repo.LkpProtocolRepo;

@Service("CertacureAdmissionClassService")
public class CertacureAdmissionClassService extends GenericService<CertacureAdmissionClass, CertacureAdmissionClassRepo> {

	@Autowired
	private CertacureAdmissionClassRepo repo;

	@Override
	protected CertacureAdmissionClassRepo getRepository() {
		return repo;
	}

	public void deleteProtocol(Long rId) {
		repo.deleteById(rId);
	}

	public List<CertacureAdmissionClass> getCertacureAdmissionCode(String pV1_21_ChargePriceIndicator) {
		
		return repo.getCertacureAdmissionCode(pV1_21_ChargePriceIndicator);
		
	}
}
