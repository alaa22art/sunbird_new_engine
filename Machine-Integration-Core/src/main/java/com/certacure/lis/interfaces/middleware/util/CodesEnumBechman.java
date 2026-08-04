package com.certacure.lis.interfaces.middleware.util;

public enum CodesEnumBechman {
	STAT("ST"),
	ROUTINE("RO"),
	Dilution("^1"),
	DilutionFactor("1^1");

	private CodesEnumBechman(String value) {
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
