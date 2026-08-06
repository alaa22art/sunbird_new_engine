package com.sunbird.machine.web.machine.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.service.MachineService;

@RestController
@RequestMapping("/services")
public class MachineController {

	@Autowired
	private MachineService machineService;

	private Logger log = LoggerFactory.getLogger(getClass());

	@RequestMapping(value = "/getMachinePage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getMachinePage(@RequestBody FilterablePageRequest filterablePageRequest) {

		//		log.info("##############  Application  ############## 11111 in machine controller");
		//		ActorSystem system = ActorSystem.create("Main");
		//		system.actorOf(Props.create(Master.class), "Master");
		//		log.info("##############  Application  ############## 22222");

		Map<String, Object> result = new HashMap<>();
		Page<Machine> pageable = machineService.getMachinePage(filterablePageRequest);
		result.put("data", pageable.getContent());
		result.put("total", pageable.getTotalElements());

		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/addMachine.srvc", method = RequestMethod.POST)
	public ResponseEntity<Machine> addMachine(@RequestBody Machine machine) {
		machine.setRequestNewTestOnly(false);
		machine.setRequestTestWithNoResultOnly(false);
		this.machineService.addMachine(machine);

		return new ResponseEntity<Machine>(machine, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateMachine.srvc", method = RequestMethod.POST)
	public ResponseEntity<Machine> updateMachine(@RequestBody Machine machine) {
		return new ResponseEntity<Machine>(machineService.updateMachine(machine), HttpStatus.OK);
	}

	@RequestMapping(value = "/getMachineById.srvc", method = RequestMethod.POST)
	public ResponseEntity<Machine> getMachineById(@RequestBody Long rId) {

		Machine machine = this.machineService.getMachineById(rId);

		return new ResponseEntity<Machine>(machine, HttpStatus.OK);

	}

	@RequestMapping(value = "/getMachineList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<Machine>> getMachineById() {

		List<Machine> machineList = this.machineService.findAll();

		return new ResponseEntity<List<Machine>>(machineList, HttpStatus.OK);

	}

	//	@RequestMapping(value = "/getMachineQueryPage.srvc", method = RequestMethod.POST)
	//	public ResponseEntity<Map<String, Object>> getMachineQueryPage(@RequestBody FilterablePageRequest filterablePageRequest) {
	//		Map<String, Object> result = new HashMap<>();
	//		Page<MachineQuery> pageable = machineService.getMachineQueryPage(filterablePageRequest);
	//		result.put("data", pageable.getContent());
	//		result.put("total", pageable.getTotalElements());
	//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	//	}

	//	@RequestMapping(value = "/getMachineResultPage.srvc", method = RequestMethod.POST)
	//	public ResponseEntity<Map<String, Object>> getMachineResultPage(@RequestBody FilterablePageRequest filterablePageRequest) {
	//		Map<String, Object> result = new HashMap<>();
	//		Page<MachineResult> pageable = machineService.getMachineResultPage(filterablePageRequest);
	//		result.put("data", pageable.getContent());
	//		result.put("total", pageable.getTotalElements());
	//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	//	}

	@RequestMapping(value = "/closeConnection.srvc", method = RequestMethod.POST)
	public ResponseEntity<Machine> closeConnection(@RequestBody Machine machine) throws Exception {
		machineService.closeConnection(machine);
		return new ResponseEntity<Machine>(machine, HttpStatus.OK);
	}

	@RequestMapping(value = "/openConnection.srvc", method = RequestMethod.POST)
	public ResponseEntity<Machine> openConnection(@RequestBody Machine machine) throws Exception {
		machineService.openConnection(machine);
		return new ResponseEntity<Machine>(machine, HttpStatus.OK);
	}

}
