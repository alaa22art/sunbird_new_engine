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
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.entities.Driver;
import com.sunbird.lis.interfaces.entities.LkpProtocol;
import com.sunbird.lis.interfaces.entities.MachineType;
import com.sunbird.lis.interfaces.helper.MachineIntegrationRights;
import com.sunbird.lis.interfaces.repo.LkpDriverRepo;
import com.sunbird.lis.interfaces.repo.LkpProtocolRepo;
import com.sunbird.lis.interfaces.repo.MachineTypeRepo;

@Service("MachineTypeService")
public class MachineTypeService extends GenericService<MachineType, MachineTypeRepo> {

	@Autowired
	private MachineTypeRepo repo;

	@Autowired
	private LkpProtocolRepo protocalRepo;

	@Autowired
	private LkpDriverRepo driverRepo;

	@Override
	protected MachineTypeRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.ADD_MACHINE_TYPE_SETUP + "')")
	public MachineType addMachineType(MachineType machineType) {
		MachineType mType = getRepository().save(machineType);
		return mType;
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.UPD_MACHINE_TYPE_SETUP + "')")
	public MachineType updateMachineType(MachineType machineType) {
		MachineType mType = repo.save(machineType);
		return mType;
	}

	public void deleteMachineType(Long rId) {
		repo.deleteById(rId);
	}

	/*
	 * public MachineType getMachineTypeById(Long rID) {
	 * return repo.getMachineTypeById(rID);
	 * }
	 */

	public MachineType getMachineTypeById(Long rID) {

		return repo.findOne(Arrays.asList(new SearchCriterion("rid", rID, FilterOperator.eq)), MachineType.class, "driver",
				"lowLevelProtocolId", "highLevelProtocolId");
	}

	public List<LkpProtocol> getProtocalList() {
		return protocalRepo.findAll();
	}

	public List<Driver> getDriverList() {
		return driverRepo.findAll();
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_MACHINE_TYPE_SETUP + "')")
	public Page<MachineType> getMachineTypePage(FilterablePageRequest filterablePageRequest) {

		String[] joins = new String[] { "driver", "lowLevelProtocolId", "highLevelProtocolId" };

		Page<MachineType> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				MachineType.class, joins);

		return page;
	}

}
