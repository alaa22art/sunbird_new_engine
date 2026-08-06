package com.certacure.lis.interfaces.helper;

public enum SystemSettingType {

	ENABLE_REALTIME_PCR_VIEW("ENABLE_REALTIME_PCR_VIEW") ; //<BOOLEAN> enabling this setting must be accommodated with the 4 following settings
	
	private SystemSettingType(String value)
	{
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private String value;

}
