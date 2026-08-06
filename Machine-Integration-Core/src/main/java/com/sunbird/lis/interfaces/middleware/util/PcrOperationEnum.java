package com.certacure.lis.interfaces.middleware.util;

public enum PcrOperationEnum {

	ADD_ORDER("ADD_ORDER"),
	UNLOAD_ORDER("UNLOAD_ORDER"),
	CHANGE_ORDER_RESULT("CHANGE_ORDER_RESULT"),
	SEND_RESULTS("SEND_RESULTS");

	private PcrOperationEnum(String value) {
		this.value = value;
	}

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
