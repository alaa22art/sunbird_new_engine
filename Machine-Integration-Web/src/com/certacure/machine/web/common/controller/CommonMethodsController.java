package com.certacure.machine.web.common.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.certacure.core.common.helper.FieldMetaData;
import com.certacure.core.common.util.ReflectionUtil;

/**
 * CommonMethodsController.java
 * 
 **/
@RestController
@RequestMapping("/services")
public class CommonMethodsController {

	//To be used by CommonMethods.js

	@Autowired
	private EntityManager entityManager;

	@RequestMapping(value = "/getClassMetaData.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, FieldMetaData>> getClassMetaData(@RequestBody String className) {
		Class<?> clazz = ReflectionUtil.getEntityClassByName(className, entityManager);
		Map<String, FieldMetaData> metaData = ReflectionUtil.getEntityFieldMetaData(clazz);
		return new ResponseEntity<Map<String, FieldMetaData>>(metaData, HttpStatus.OK);
	}

	@RequestMapping(value = "/getClassMetaDataList.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Map<String, FieldMetaData>>> getClassMetaDataList(@RequestBody List<String> classNameList) {
		Map<String, Map<String, FieldMetaData>> classesMetaData = new HashMap<>();
		for (String className : classNameList) {
			Class<?> clazz = ReflectionUtil.getEntityClassByName(className, entityManager);
			classesMetaData.put(className, ReflectionUtil.getEntityFieldMetaData(clazz));
		}
		return new ResponseEntity<Map<String, Map<String, FieldMetaData>>>(classesMetaData, HttpStatus.OK);
	}

}
