package com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPro;

public class RocheCobasProResultData {
	public String sampleNo;
	public String originalInput;
	public String result;
	public String testCode;

	public RocheCobasProResultData(String strSampleNo , String strTestCode,String strResult ,String data) 
	{
		sampleNo = strSampleNo;
		result = strResult;
		testCode = strTestCode;
		originalInput = data;
	}
	
	@Override
	public String toString() {
		return "RocheCobasProResultData{" +
				"sampleNo=" + this.sampleNo +
				", result=" + this.result +
				", testCode=" + this.testCode +
				", originalInput=" + this.originalInput +
				'}';
	}

}
