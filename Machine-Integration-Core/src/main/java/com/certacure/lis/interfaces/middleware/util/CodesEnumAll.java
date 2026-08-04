package com.certacure.lis.interfaces.middleware.util;

public enum CodesEnumAll {

	Day("^D"),
	Month("^M"),
	Year("^Y"),
	M("M"),
	F("F"),
	U("U"),
	STAT("S"),
	ROUTINE("R"),
	BESTAT("ST"),
	BEROUTINE("RO"),
	NEW("N"),
	NULL(""),
	ADD("A"),
	CANCEL("C"),
	QULITY("Q"),
	Serum("SERUM"),
	WB("WB"),
	Plasma("Plasma"),
	Urine("Urine"),
	CSF("CSF"),
	Amniotic("Amniotic"),
	TimedUrine("Timed"),
	Blood("Blood"),
	Cervical("Cervical"),
	Saliva("Saliva"),
	Synovial("Synovial"),
	Urethral("Urethral"),
	Others("Others"),
	ProccessingID("P"),
	ReportTypeO("O"),
	ReportTypeQ("Q"),
	ReportTypeZ("Z"),
	ReportTypeF("F"),
	ReportTypeX("X"),
	ReportTypeOQ("O\\Q"),
	VersionNumber("1"),
	VersionNumberSysmexSuit("A.2"),
	DilutionBech("^1"),
	Suprnt("4");

	private CodesEnumAll(String value) {
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