package com.sunbird.machine.web.driver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.lis.interfaces.entities.Driver;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.OutboundInformation;
import com.sunbird.lis.interfaces.entities.ResultInformation;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.LabMachineMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.LabQueryMsg;
import com.sunbird.lis.interfaces.service.DriverService;
import com.sunbird.lis.interfaces.service.MachineService;
import com.sunbird.lis.interfaces.service.OutboundService;
import com.sunbird.lis.interfaces.service.ResultInformationService;

@RestController
@RequestMapping("/services")
public class DriverController {

	@Autowired
	private DriverService driverService;
	@Autowired
	private MachineService machineService;
	@Autowired
	private OutboundService outBoundService;
	@Autowired
	private ResultInformationService resultInformation;

	@RequestMapping(value = "/addDriver.srvc", method = RequestMethod.POST)
	public ResponseEntity<Driver> addDriver(@RequestBody Driver driver) {

		this.driverService.addDriver(driver);

		return new ResponseEntity<Driver>(driver, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateDriver.srvc", method = RequestMethod.POST)
	public ResponseEntity<Driver> updateDriver(@RequestBody Driver driver) {

		this.driverService.updateDriver(driver);

		return new ResponseEntity<Driver>(driver, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteDriver.srvc", method = RequestMethod.POST)
	public ResponseEntity<Driver> deleteDriver(@RequestBody Driver driver) {

		this.driverService.deleteDriver(driver.getRid());

		return new ResponseEntity<Driver>(driver, HttpStatus.OK);
	}

	@RequestMapping(value = "/getDriverList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<Driver>> getDriverList() {

		List<Driver> driverList = this.driverService.findAll();

		return new ResponseEntity<List<Driver>>(driverList, HttpStatus.OK);

	}

	@RequestMapping(value = "/saveMachineActorPath.srvc", method = RequestMethod.POST)
	public ResponseEntity<Machine> saveMachineActorPath(@RequestBody LabMachineMsg labMachineMsg) {

		Machine objMachine = null;
		objMachine = machineService.getMachineByName(labMachineMsg.machineName);
		objMachine.setMachineActorPath(labMachineMsg.machineActorPath);
		objMachine.setRequestNewTestOnly(false);
		objMachine.setRequestTestWithNoResultOnly(false);
		machineService.addMachine(objMachine);

		return new ResponseEntity<Machine>(objMachine, HttpStatus.OK);

	}

	@RequestMapping(value = "/getOrderOutboundInformation.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<OutboundInformation>> getOrderOutboundInformation(@RequestBody LabQueryMsg labQueryMsg) {

		List<OutboundInformation> outBoundInformation = outBoundService.getOrderInformationByMachineNameAndSampleId(
				labQueryMsg.machineName.toString(), labQueryMsg.specimenIds.toString());

		return new ResponseEntity<List<OutboundInformation>>(outBoundInformation, HttpStatus.OK);

	}

	@RequestMapping(value = "/addInboundResult.srvc", method = RequestMethod.POST)
	public void addInboundResult(@RequestBody List<ResultInformation> resultInformationList) {

		for (ResultInformation results : resultInformationList) {
			resultInformation.addResult(results);
		}
	}
}