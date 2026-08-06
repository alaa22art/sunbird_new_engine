package com.sunbird.machine.web.helper;

import java.util.Iterator;

public class WebSocketClient {

	private String branchKey;
	private String clientName;
	private Integer count;

	public WebSocketClient(String branchKey, String clientName) {
		this.branchKey = branchKey;
		this.clientName = clientName;
		this.count = 1;
	}

	public String getBranchKey() {
		return branchKey;
	}

	public void setBranchKey(String branchKey) {
		this.branchKey = branchKey;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public void increaseCount() {
		count++;
	}

	public void decreaseCount(Iterator<WebSocketClient> iterator) {
		count--;
		if (count.equals(0)) {
			iterator.remove(); //removes this WebSocketClient object from the list
		}
	}

}
