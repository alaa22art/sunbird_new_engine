package com.certacure.lis.interfaces.middleware.flow_component.kx21n;

public class Kx21nResultData {

	public String[] ResultLines;
	public String sampleNo;
	public String originalInput;

	public String[] arrResultCode = { "WBC",
			"RBC",
			"HGB",
			"HCT",
			"MCV",
			"MCH",
			"MCHC",
			"PLT",
			"LYM%(W-SCR)",
			"MXD%(W-MCR)",
			"NEUT%(W-LCR)",
			"LYM#(W-SCC)",
			"MXD#(W-MCC)",
			"NEUT#(W-LCC)",
			"RDW-SD",
			"RDW-CV",
			"PDW",
			"MPV",
			"P-LCR"
	};

	public Kx21nResultData() {
		ResultLines = new String[19];
	}

	public Kx21nResultData(String[] arrFinalFormatedResult, String strSampleNumber) {
		ResultLines = arrFinalFormatedResult;
		sampleNo = strSampleNumber;

	}

	public Kx21nResultData(String[] arrFinalFormatedResult, String strSampleNumber, String originalString) {
		ResultLines = arrFinalFormatedResult;
		sampleNo = strSampleNumber;
		originalInput = originalString;

	}

}
