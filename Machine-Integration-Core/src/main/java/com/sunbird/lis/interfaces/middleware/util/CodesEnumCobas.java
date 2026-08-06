package com.certacure.lis.interfaces.middleware.util;

public enum CodesEnumCobas {

	SERUM("1"),
	PLASMA("1"),
	URINE("2"),
	CSF("3"),
	BLOOD("4"),
	OTHERS("5"),
	DilutionC111("^A"),
	SpecialInstruction("TSDWN^REPLY"),
	SpecialInstructionC111("TSDWN^REAL");

	private CodesEnumCobas(String value) {
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