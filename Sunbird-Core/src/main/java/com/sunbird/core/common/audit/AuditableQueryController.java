package com.sunbird.core.common.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
public class AuditableQueryController {

	@Autowired
	private AuditableQueryService auditableQueryService;

	//@Autowired
	//private EntityManager entityManager;

	@RequestMapping(value = "/getAuditPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<?>> getSectionPage(@RequestBody AuditableQuery auditableQuery) {
		return new ResponseEntity<Page<?>>(auditableQueryService.getAuditPage(auditableQuery), HttpStatus.OK);
	}
}
