package com.certacure.machine.web.machine.type.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.certacure.core.common.helper.FilterablePageRequest;
import com.certacure.lis.interfaces.entities.MachineType;
import com.certacure.lis.interfaces.service.MachineTypeService;

@RestController
@RequestMapping("/services")
public class MachineTypeController {

	@Autowired
	private MachineTypeService machineTypeService;

	@RequestMapping(value = "/addMachineType.srvc", method = RequestMethod.POST)
	public ResponseEntity<MachineType> addMachineType(@RequestBody MachineType machineType) {
		MachineType mType = machineTypeService.addMachineType(machineType);

		return new ResponseEntity<MachineType>(mType, HttpStatus.OK);
	}

	@RequestMapping(value = "/getMachineTypeById.srvc", method = RequestMethod.POST)
	public ResponseEntity<MachineType> getMachineTypeById(@RequestBody Long rId) {

		MachineType machineType = this.machineTypeService.getMachineTypeById(rId);

		return new ResponseEntity<MachineType>(machineType, HttpStatus.OK);

	}

	//TODO which page used this api
	@RequestMapping(value = "/getMachineTypeList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<MachineType>> getMachineTypeList() {

		List<MachineType> machineTypeList = this.machineTypeService.findAll();

		return new ResponseEntity<List<MachineType>>(machineTypeList, HttpStatus.OK);

	}

	@RequestMapping(value = "/deleteMachineType.srvc", method = RequestMethod.POST)
	public ResponseEntity<MachineType> deleteMachineType(@RequestBody MachineType machineType) {

		this.machineTypeService.deleteMachineType(machineType.getRid());

		return new ResponseEntity<MachineType>(machineType, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateMachineType.srvc", method = RequestMethod.POST)
	public ResponseEntity<MachineType> updateMachineType(@RequestBody MachineType machineType) {

		MachineType mType = this.machineTypeService.updateMachineType(machineType);

		return new ResponseEntity<MachineType>(mType, HttpStatus.OK);

	}

	@RequestMapping(value = "/getMachineTypePage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getMachineTypePage(@RequestBody FilterablePageRequest filterablePageRequest) {
		Map<String, Object> result = new HashMap<>();
		Page<MachineType> pageable = machineTypeService.getMachineTypePage(filterablePageRequest);
		result.put("data", pageable.getContent());
		result.put("total", pageable.getTotalElements());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

}
