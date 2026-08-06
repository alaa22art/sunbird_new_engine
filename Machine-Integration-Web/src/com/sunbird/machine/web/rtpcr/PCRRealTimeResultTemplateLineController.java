package com.certacure.machine.web.rtpcr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.certacure.lis.interfaces.entities.PCRRealTimeResultTemplateLine;
import com.certacure.lis.interfaces.service.PCRRealTimeResultTemplateLineService;

@RestController
@RequestMapping("/services")
public class PCRRealTimeResultTemplateLineController {

	@Autowired
	private PCRRealTimeResultTemplateLineService pcrRealTimeResultTemplateLineService;

	@RequestMapping("/getResultTemplateLines.srvc")
	public ResponseEntity<List<PCRRealTimeResultTemplateLine>> getResultTemplateLines() {
		return new ResponseEntity<List<PCRRealTimeResultTemplateLine>>(
				pcrRealTimeResultTemplateLineService.getPcrRealTimeResultTemplateLines(),
				HttpStatus.OK);
	}

}
