package com.sunbird.machine.web.helper;

public class WebSocketWrapper {

	private WebSocketType type;
	private Object data;

	public WebSocketWrapper() {
	}

	public WebSocketWrapper(WebSocketType type, Object data) {
		super();
		this.type = type;
		this.data = data;
	}

	public WebSocketType getType() {
		return type;
	}

	public void setType(WebSocketType type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
