package com.certacure.lis.interfaces.wrapper;

import java.io.Serializable;

import com.certacure.lis.interfaces.entities.PCRRealTimeWorkList;

public class PCRWorkListOrderWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String barcode;
	private PCRRealTimeWorkList pcrRealTimeWorkList;
	private String orderRowIndex;
	private String orderColumnIndex;

	public PCRWorkListOrderWrapper() {
	}

	public PCRWorkListOrderWrapper(String barcode, PCRRealTimeWorkList pcrRealTimeWorkList, String orderRowIndex, String orderColumnIndex) {
		this.barcode = barcode;
		this.pcrRealTimeWorkList = pcrRealTimeWorkList;
		this.orderRowIndex = orderRowIndex;
		this.orderColumnIndex = orderColumnIndex;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public PCRRealTimeWorkList getPcrRealTimeWorkList() {
		return pcrRealTimeWorkList;
	}

	public void setPcrRealTimeWorkList(PCRRealTimeWorkList pcrRealTimeWorkList) {
		this.pcrRealTimeWorkList = pcrRealTimeWorkList;
	}

	public String getOrderRowIndex() {
		return orderRowIndex;
	}

	public void setOrderRowIndex(String orderRowIndex) {
		this.orderRowIndex = orderRowIndex;
	}

	public String getOrderColumnIndex() {
		return orderColumnIndex;
	}

	public void setOrderColumnIndex(String orderColumnIndex) {
		this.orderColumnIndex = orderColumnIndex;
	}

}
