package com.sunbird.core.common.audit;

import java.util.Date;

import org.hibernate.envers.RevisionListener;

import com.sunbird.core.common.util.SecurityUtil;

public class CustomRevisionListener implements RevisionListener {

	@Override
	public void newRevision(Object revisionEntity) {
		CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) revisionEntity;
		customRevisionEntity.setCreationDate(new Date());
		customRevisionEntity.setCreatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());
	}

}