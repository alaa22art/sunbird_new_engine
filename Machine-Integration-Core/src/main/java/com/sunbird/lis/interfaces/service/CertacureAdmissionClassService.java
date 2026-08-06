package com.certacure.lis.interfaces.service;

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

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.entities.CertacureAdmissionClass;

import com.certacure.lis.interfaces.entities.LkpProtocol;
import com.certacure.lis.interfaces.entities.MachineType;
import com.certacure.lis.interfaces.repo.CertacureAdmissionClassRepo;

import com.certacure.lis.interfaces.repo.LkpProtocolRepo;

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
