package com.certacure.machine.web.lkp.controller;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.certacure.core.base.entity.BaseEntity;
import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.common.util.ReflectionUtil;
import com.certacure.lis.interfaces.entities.LkpMaster;
import com.certacure.lis.interfaces.service.LkpMasterService;
import com.certacure.lis.interfaces.service.LkpService;
import com.certacure.machine.web.lkp.wrapper.LkpWrapper;

@RestController
@RequestMapping("/services")
@EnableWebMvc
public class LkpsController {

	@Autowired
	private LkpMasterService lkpMasterService;

	@Autowired
	private LkpService lkpService;

	@Autowired
	private EntityManager entityManager;

	@RequestMapping(value = "/getLkpMasterList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LkpMaster>> getLkpMasterList() {
		return new ResponseEntity<List<LkpMaster>>(lkpMasterService.findLkpMasters(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getLkpByClass.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<BaseEntity>> getLkpByClass(@RequestBody LkpWrapper lkpWrapper) {

		Class<BaseEntity> entityClass = ReflectionUtil.getEntityClassByName(lkpWrapper.getClassName(), entityManager);
		List<SearchCriterion> filters = lkpWrapper.getFilterableFilters();
		String[] joins = lkpWrapper.getJoins();
		Sort sort = lkpWrapper.getFilterableSort();

		if (lkpService.isLkpTenanted(entityClass)) {
			return new ResponseEntity<List<BaseEntity>>(
					lkpService.findTenantedLkp(filters, entityClass, sort, joins),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<List<BaseEntity>>(
					lkpService.findNonTenantedLkp(filters, entityClass, sort, joins),
					HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/getAnyLkpByClass.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<? extends BaseEntity>> getAnyLkpByClass(@RequestBody LkpWrapper lkpWrapper) {
		Class<?> entityClass = ReflectionUtil.getEntityClassByName(lkpWrapper.getClassName(), entityManager);
		List<SearchCriterion> filters = lkpWrapper.getFilterableFilters();
		String[] joins = lkpWrapper.getJoins();
		Sort sort = lkpWrapper.getFilterableSort();
		return new ResponseEntity<List<? extends BaseEntity>>(lkpService.findAnyLkp(filters, entityClass, sort, joins), HttpStatus.OK);
	}

	@RequestMapping(value = "/getOneLkpByClass.srvc", method = RequestMethod.POST)
	public ResponseEntity<BaseEntity> getOneLkpByClass(@RequestBody LkpWrapper lkpWrapper) {
		Class<BaseEntity> entityClass = ReflectionUtil.getEntityClassByName(lkpWrapper.getClassName(), entityManager);
		List<SearchCriterion> filters = lkpWrapper.getFilterableFilters();
		String[] joins = lkpWrapper.getJoins();
		BaseEntity be = lkpService.findOneAnyLkp(filters, entityClass, joins);
		return new ResponseEntity<BaseEntity>(be, HttpStatus.OK);
	}

	@RequestMapping(value = "/createLkp.srvc", method = RequestMethod.POST)
	public ResponseEntity<BaseEntity> createLkp(@RequestBody Map<String, String> map) {

		Class<? extends BaseEntity> entityClass = ReflectionUtil.getEntityClassByName(map.get("className"), entityManager);
		BaseEntity entity = ReflectionUtil.getObjectFromUnknownType(map.get("className"), map.get("object"), entityManager);

		if (lkpService.isLkpTenanted(entityClass)) {
			return new ResponseEntity<BaseEntity>(lkpService.createTenantedLkp(entityClass, entity), HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseEntity>(lkpService.createNonTenantedLkp(entity), HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/updateLkp.srvc", method = RequestMethod.POST)
	public ResponseEntity<BaseEntity> updateLkp(@RequestBody Map<String, String> map) {
		Class<? extends BaseEntity> entityClass = ReflectionUtil.getEntityClassByName(map.get("className"), entityManager);
		BaseEntity entity = ReflectionUtil.getObjectFromUnknownType(map.get("className"), map.get("object"), entityManager);

		if (lkpService.isLkpTenanted(entityClass)) {
			return new ResponseEntity<BaseEntity>(lkpService.updateTenantedLkp(entityClass, entity), HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseEntity>(lkpService.updateNonTenantedLkp(entity), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/deleteLkp.srvc", method = RequestMethod.POST)
	public ResponseEntity<Void> deleteLkp(@RequestBody Map<String, String> map) {
		Class<? extends BaseEntity> entityClass = ReflectionUtil.getEntityClassByName(map.get("className"), entityManager);
		BaseEntity entity = ReflectionUtil.getObjectFromUnknownType(map.get("className"), map.get("object"), entityManager);

		if (lkpService.isLkpTenanted(entityClass)) {
			lkpService.deleteTenantedLkp(entityClass, entity);
		} else {
			lkpService.deleteNonTenantedLkp(entity);

		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
