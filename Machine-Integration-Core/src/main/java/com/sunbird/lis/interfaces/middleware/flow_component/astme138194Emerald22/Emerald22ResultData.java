package com.certacure.lis.interfaces.middleware.flow_component.astme138194Emerald22;

public class Emerald22ResultData {

	public String[] ResultLines;
	public String sampleNo;
	public String originalInput;

	public String[] arrResultCode = {
			"WBC",
			"RBC",
			"HGB",
			"HCT",
			"PLT",
			"LYM",
			"MON",
			"NEU",
			"LYM%",
			"MON%",
			"NEU%",
			"MCV",
			"MCH",
			"MCHC",
			"RDW",
			"MPV",
			"EOS",
			"BAS",
			"EOS%",
			"BAS%"
	};

	public Emerald22ResultData() {
		ResultLines = new String[20];
	}

	public Emerald22ResultData(String[] arrFinalFormatedResult, String strSampleNumber) {
		ResultLines = arrFinalFormatedResult;
		sampleNo = strSampleNumber;

	}

	public Emerald22ResultData(String[] arrFinalFormatedResult, String strSampleNumber, String originalString) {
		ResultLines = arrFinalFormatedResult;
		sampleNo = strSampleNumber;
		originalInput = originalString;

	}

}
