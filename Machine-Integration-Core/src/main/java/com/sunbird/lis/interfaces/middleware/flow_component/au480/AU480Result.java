package com.sunbird.lis.interfaces.middleware.flow_component.au480;

public class AU480Result {

	private String hostCode;
	private String testValue;

	public AU480Result(String testNo, String testValue) {
		this.hostCode = testNo;
		this.testValue = testValue;
	}

	public String getHostCode() {
		return hostCode;
	}

	public void setTestNo(String testNo) {
		this.hostCode = testNo;
	}

	public String getTestValue() {
		return testValue;
	}

	public void setTestValue(String testValue) {
		this.testValue = testValue;
	}

}
