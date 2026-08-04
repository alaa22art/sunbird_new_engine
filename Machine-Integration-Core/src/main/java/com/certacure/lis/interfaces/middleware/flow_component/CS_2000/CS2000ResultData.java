package com.certacure.lis.interfaces.middleware.flow_component.CS_2000;

public class CS2000ResultData {

	public String[] ResultLines;
	public String sampleNo;
	public String originalInput;

	public String[] arrResultCode = { 
			"041",
			"044",
			"051"
	};

	public CS2000ResultData() {
		ResultLines = new String[19];
	}

	public CS2000ResultData(String[] arrFinalFormatedResult, String strSampleNumber) {
		ResultLines = arrFinalFormatedResult;
		sampleNo = strSampleNumber;

	}

	public CS2000ResultData(String[] arrFinalFormatedResult, String strSampleNumber, String originalString) {
		ResultLines = arrFinalFormatedResult;
		sampleNo = strSampleNumber;
		originalInput = originalString;

	}

}
