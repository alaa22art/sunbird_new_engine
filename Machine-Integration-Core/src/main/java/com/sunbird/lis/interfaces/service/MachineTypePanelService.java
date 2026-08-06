package com.certacure.lis.interfaces.service;

import java.util.List;

/**
*
* @author Alaa Himour <ahimour@certacuresolutions.com>
* @since JAN/22 2020
* 
*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.MachineTypePanel;
import com.certacure.lis.interfaces.repo.MachineTypePanelRepo;

@Service("MachineTypePanelService")
public class MachineTypePanelService extends GenericService<MachineTypePanel, MachineTypePanelRepo> {

	@Autowired
	private MachineTypePanelRepo repo;

	@Override
	protected MachineTypePanelRepo getRepository() {
		return repo;
	}

	@InterceptorFree
	public MachineTypePanel getPanelByMachineTypeAndPanelName(long machineTypeId, String panelName) {
		return getRepository().findPanelByMachineTypeAndTestName(machineTypeId, panelName);
	}

	@InterceptorFree
	public List<MachineTypePanel> getPanelByMachineType(Long machineTypeRid) {
		return getRepository().findPanelByMachineType(machineTypeRid);
	}

	public String getPanelHostCode(Long machineTypeRid, String panelName) {

		List<MachineTypePanel> lstPanel = getRepository().findPanelHostCodeByMachineTypeAndPanelName(machineTypeRid, panelName);

		return lstPanel.get(0).getPanelHostCode();
	}

}
