package com.certacure.lis.interfaces.service;

/**
*
* @author Alaa Himour <ahimour@certacuresolutions.com>
* @since Dec/21 2018
* 
*/
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.OutboundInformation;
import com.certacure.lis.interfaces.repo.OutboundRepo;

@Service("OutboundService")
public class OutboundService extends GenericService<OutboundInformation, OutboundRepo> {

	@Autowired
	private OutboundRepo repo;

	@Override
	protected OutboundRepo getRepository() {
		return repo;
	}

	public OutboundInformation addOutbound(OutboundInformation outBoundInformation) {
		return repo.save(outBoundInformation);
	}

	public OutboundInformation updateOutbound(OutboundInformation outBoundInformation) {
		return repo.save(outBoundInformation);
	}

	public void deleteOutbound(Long rId) {
		repo.deleteById(rId);
	}

	public void deleteOutbound(OutboundInformation outBoundInformation) {
		repo.deleteById(outBoundInformation.getRid());
	}

	public Machine getMachineByActorPath(String machineActorPath) {
		return repo.getMachineByActorPath(machineActorPath);
	}

	public List<OutboundInformation> getOrderInformationByMachineNameAndSampleId(String machineName, String specimenId) {
		List<OutboundInformation> outBoundInformation = repo.getBySampleAndMachine(specimenId, machineName);
		return outBoundInformation;
	}
}
