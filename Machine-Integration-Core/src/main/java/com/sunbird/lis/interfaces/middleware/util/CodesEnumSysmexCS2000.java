package com.sunbird.lis.interfaces.middleware.util;

public enum CodesEnumSysmexCS2000 {
	DilutionSysCS2000("^^100.00");

	private CodesEnumSysmexCS2000(String value) {
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
