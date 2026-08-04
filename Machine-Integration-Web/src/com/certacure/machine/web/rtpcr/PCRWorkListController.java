package com.certacure.machine.web.rtpcr;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.certacure.core.common.helper.FilterablePageRequest;
import com.certacure.lis.interfaces.entities.PCRRealTimeWorkList;
import com.certacure.lis.interfaces.entities.PCRRealTimeWorkListOrder;
import com.certacure.lis.interfaces.service.PCRRealTimeWorkListOrderService;
import com.certacure.lis.interfaces.service.PCRRealTimeWorkListService;
import com.certacure.lis.interfaces.wrapper.WorkListStatusWrapper;

@RestController
@RequestMapping("/services")
public class PCRWorkListController {

	@Autowired
	private PCRRealTimeWorkListService pcrRealTimeWorkListService;

	@Autowired
	private PCRRealTimeWorkListOrderService pcrRealTimeWorkListOrderService;

	@RequestMapping(value = "/addPcrWorkList.srvc", method = RequestMethod.POST)
	public ResponseEntity<PCRRealTimeWorkList> addPcrWorkList(@RequestBody PCRRealTimeWorkList pcrRealTimeWorkList) {
		return new ResponseEntity<PCRRealTimeWorkList>(pcrRealTimeWorkListService.addPcrRealTimeWorkList(pcrRealTimeWorkList),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/updatePcrWorkList.srvc", method = RequestMethod.POST)
	public ResponseEntity<PCRRealTimeWorkList> updatePcrWorkList(@RequestBody PCRRealTimeWorkList pcrRealTimeWorkList) {
		PCRRealTimeWorkList updatedPcrRealTimeWorkList = pcrRealTimeWorkListService.addPcrRealTimeWorkList(pcrRealTimeWorkList);
		return new ResponseEntity<PCRRealTimeWorkList>(updatedPcrRealTimeWorkList, HttpStatus.OK);
	}

	@RequestMapping(value = "/getAllPcrWorkLists.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<PCRRealTimeWorkList>> getPcrWorkLists() {
		return new ResponseEntity<List<PCRRealTimeWorkList>>(pcrRealTimeWorkListService.getPcrRealTimeWorkLists(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getPcrWorkListPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<PCRRealTimeWorkList>> getPcrWorkListPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<PCRRealTimeWorkList>>(
				
				
				pcrRealTimeWorkListService.getPcrWorkListPage(filterablePageRequest),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getPcrRealTimeWorkListById.srvc", method = RequestMethod.POST)
	public ResponseEntity<PCRRealTimeWorkList> getPcrWorkList(@RequestBody Long rid) {
		return new ResponseEntity<PCRRealTimeWorkList>(pcrRealTimeWorkListService.getPcrRealTimeWorkListById(rid), HttpStatus.OK);
	}

	@RequestMapping(value = "/getPcrRealTimeWorkListOrdersById.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<PCRRealTimeWorkListOrder>> getPcrRealTimeWorkListOrders(@RequestBody Long rid) {
		Set<PCRRealTimeWorkListOrder> pcrRealTimeWorkListOrders = pcrRealTimeWorkListOrderService.getPcrRealTimeWorkListOrders(rid);
		return new ResponseEntity<Set<PCRRealTimeWorkListOrder>>(pcrRealTimeWorkListOrders, HttpStatus.OK);
	}

	@RequestMapping(value = "/deletePcrRealTimeWorkListById.srvc", method = RequestMethod.POST)
	public ResponseEntity<HttpStatus> deletePcrRealTimeWorkListById(@RequestBody Long rid) {
		pcrRealTimeWorkListService.deletePcrRealTimeWorkList(rid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/changeWorkListStatus", method = RequestMethod.POST)
	public ResponseEntity<PCRRealTimeWorkList> changeWorkListStatus(@RequestBody WorkListStatusWrapper workListStatusWrapper) {
		return new ResponseEntity<PCRRealTimeWorkList>(pcrRealTimeWorkListService.changeStatus(workListStatusWrapper), HttpStatus.OK);
	}

}
