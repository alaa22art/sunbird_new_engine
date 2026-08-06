package com.sunbird.lis.interfaces.helper;

public enum ExternalURL {
	
	TEST(""),
	RCM(""),
	INFOR_ISSUE_STOCK("http://192.168.21.226:8811"),
	INFOR_RETURN_STOCK("http://192.168.21.226:8812");
	
	//INFOR_ISSUE_STOCK("http://192.168.20.236:8811"),
	//INFOR_RETURN_STOCK("http://192.168.20.236:8812");

	ExternalURL(String value) {
		this.setValue(value);
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private String value;

}
