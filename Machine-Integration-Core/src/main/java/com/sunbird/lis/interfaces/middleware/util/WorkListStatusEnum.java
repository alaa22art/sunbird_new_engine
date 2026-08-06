package com.sunbird.lis.interfaces.middleware.util;

import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;

public enum WorkListStatusEnum {

	OPEN("OPEN", 0),
	IN_PRGRESS("IN_PROGRESS", 1),
	RESULTS_ENTRY("RESULTS_ENTRY", 2),
	FINALIZED("FINALIZED", 3),
	CLOSED("CLOSED", 4);

	private WorkListStatusEnum(String value) {
		this.value = value;
	}

	private WorkListStatusEnum(String value, Integer level) {
		this.value = value;
		this.level = level;
	}

	private String value;
	private Integer level;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public boolean isAfter(WorkListStatusEnum workListStatus) {
		return this.getLevel() > workListStatus.getLevel();
	}

	public boolean isBefore(WorkListStatusEnum workListStatus) {
		return this.getLevel() < workListStatus.getLevel();
	}

	public boolean isEqual(WorkListStatusEnum workListStatus) {
		return this.getLevel() == workListStatus.getLevel();
	}

	public boolean isAfterByOneLevel(WorkListStatusEnum workListStatus) {
		return this.getLevel() == workListStatus.getLevel() + 1;
	}

	public boolean isBeforeByOneLevel(WorkListStatusEnum workListStatus) {
		return this.getLevel() == workListStatus.getLevel() - 1;
	}

	public static WorkListStatusEnum getWorkListStatusByCode(String code) {
		for (WorkListStatusEnum workListStatus : WorkListStatusEnum.values()) {
			if (workListStatus.getValue().equals(code)) {
				return workListStatus;
			}
		}
		throw new BusinessException("This Worklist Status Is Not Defined", "worklistCodeNotExist", ErrorSeverity.ERROR);
	}

}
