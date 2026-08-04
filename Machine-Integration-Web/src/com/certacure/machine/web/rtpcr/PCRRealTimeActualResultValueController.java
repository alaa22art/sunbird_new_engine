package com.certacure.machine.web.rtpcr;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.certacure.lis.interfaces.entities.PCRRealTimeActualResultValue;
import com.certacure.lis.interfaces.entities.PCRRealTimeWorkListOrder;
import com.certacure.lis.interfaces.service.PCRRealTimeActualResultValueService;
import com.certacure.lis.interfaces.wrapper.PCRActualResultValueWrapper;
import com.certacure.lis.interfaces.wrapper.PCRRealTimeActualResultWrapper;

@RestController
@RequestMapping("/services")
public class PCRRealTimeActualResultValueController {

	@Autowired
	private PCRRealTimeActualResultValueService pcrRealTimeActualResultValueService;

	@RequestMapping(value = "/addPcrRealTimeActualResultValue.srvc", method = RequestMethod.POST)
	public ResponseEntity<PCRRealTimeActualResultValue> addPcrRealTimeActualResultValue(
			@RequestBody PCRActualResultValueWrapper pcrActualResultValueWrapper) {
		return new ResponseEntity<PCRRealTimeActualResultValue>(
				pcrRealTimeActualResultValueService.addPcrRealTimeActualResultValue(pcrActualResultValueWrapper),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/addPcrRealTimeActualResultValues.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<PCRRealTimeActualResultValue>> addPcrRealTimeActualResultValues(
			@RequestBody PCRRealTimeActualResultWrapper pcrRealTimeActualResultWrapper) {
		return new ResponseEntity<List<PCRRealTimeActualResultValue>>(
				pcrRealTimeActualResultValueService.addPcrRealTimeActualResultValues(pcrRealTimeActualResultWrapper),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/updatePcrActualResultValues.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<PCRRealTimeActualResultValue>> updateActualResultValues(
			@RequestBody PCRRealTimeActualResultWrapper pcrRealTimeActualResultWrapper) {
		return new ResponseEntity<List<PCRRealTimeActualResultValue>>(
				pcrRealTimeActualResultValueService.updateActualResultValues(pcrRealTimeActualResultWrapper),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/addPcrActualResultValues.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<PCRRealTimeActualResultValue>> addActualResultValues(
			@RequestBody PCRRealTimeActualResultWrapper pcrRealTimeActualResultWrapper) {
		return new ResponseEntity<List<PCRRealTimeActualResultValue>>(
				pcrRealTimeActualResultValueService.addActualResultValues(pcrRealTimeActualResultWrapper),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/savePcrActualResultValues.srvc", method = RequestMethod.POST)
	public ResponseEntity<HttpStatus> savePcrActualResultValues(
			@RequestBody PCRRealTimeActualResultWrapper pcrRealTimeActualResultWrapper) {
		pcrRealTimeActualResultValueService.saveActualResultValues(pcrRealTimeActualResultWrapper);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/sendPcrRealTimeActualValues.srvc", method = RequestMethod.POST)
	public ResponseEntity<Boolean> sendPcrRealTimeActualValues(
			@RequestBody List<PCRRealTimeWorkListOrder> lstWorklistOrder) throws JsonParseException, JsonMappingException, IOException {
		return new ResponseEntity<>(
				pcrRealTimeActualResultValueService.submitActualResultValues(lstWorklistOrder),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getPcrRealTimeActualResultValues.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<PCRRealTimeActualResultValue>> getPcrRealTimeActualResultValues(@RequestBody Long rid) {
		return new ResponseEntity<List<PCRRealTimeActualResultValue>>(
				pcrRealTimeActualResultValueService.getPcrRealTimeActualResultValues(rid),
				HttpStatus.OK);
	}

}
