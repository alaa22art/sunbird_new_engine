package com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPure;

public class PureResultData {
	public String sampleNo;
	public String originalInput;
	public String result;
	public String testCode;

	public PureResultData(String strSampleNo , String strTestCode,String strResult ,String data) 
	{
		sampleNo = strSampleNo;
		result = strResult;
		testCode = strTestCode;
		originalInput = data;
	}

}
