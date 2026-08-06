package com.certacure.lis.interfaces.middleware.flow_component.au480;

import java.util.ArrayList;
import java.util.List;

public class AU480ResultData {

	private String rackNo = new String();
	private String cupPosition = new String();
	private String sequenceNo = new String();
	//Sample ID 
	private String sampleNo = new String();

	private String originalInput;

	private List<AU480Result> results = new ArrayList<AU480Result>();

	public AU480ResultData() {
	}

	public AU480ResultData(String barcode) {
		this.sampleNo = barcode;
	}

	public String getRackNo() {
		return rackNo;
	}

	public void setRackNo(String rackNo) {
		this.rackNo = rackNo;
	}

	public String getCupPosition() {
		return cupPosition;
	}

	public void setCupPosition(String cupPosition) {
		this.cupPosition = cupPosition;
	}

	public String getSequanceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(String sampleNo) {
		this.sequenceNo = sampleNo;
	}

	public String getSampleNo() {
		return sampleNo;
	}

	public void setSampleNo(String barcode) {
		this.sampleNo = barcode;
	}

	public void setResults(List<AU480Result> results) {
		this.results = results;
	}

	public List<AU480Result> getResults() {
		return results;
	}

	public String getOriginalInput() {
		return originalInput;
	}

	public void setOriginalInput(String originalInput) {
		this.originalInput = originalInput;
	}

}