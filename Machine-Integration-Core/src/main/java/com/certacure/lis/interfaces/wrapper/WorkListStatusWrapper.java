package com.certacure.lis.interfaces.wrapper;

import com.certacure.core.base.entity.BaseWrapper;
import com.certacure.lis.interfaces.entities.PCRRealTimeWorkList;

public class WorkListStatusWrapper extends BaseWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PCRRealTimeWorkList workList;
	private String statusCode;

	public WorkListStatusWrapper() {
		super();
	}

	public PCRRealTimeWorkList getWorkList() {
		return workList;
	}

	public void setWorkList(PCRRealTimeWorkList workList) {
		this.workList = workList;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

}
