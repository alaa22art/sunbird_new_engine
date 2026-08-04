package com.certacure.lis.interfaces.wrapper;

import java.io.Serializable;

import com.certacure.lis.interfaces.entities.PCRRealTimeResultTemplateLine;
import com.certacure.lis.interfaces.entities.PCRRealTimeWorkListOrder;

public class PCRActualResultValueWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String actualValue;
	private String lineCode;
	private PCRRealTimeResultTemplateLine pcrRealTimeResultTemplateLine;
	private PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder;

	public PCRActualResultValueWrapper() {
	}

	public PCRActualResultValueWrapper(String actualValue, String lineCode, PCRRealTimeResultTemplateLine pcrRealTimeResultTemplateLine,
			PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder) {
		this.actualValue = actualValue;
		this.lineCode = lineCode;
		this.pcrRealTimeResultTemplateLine = pcrRealTimeResultTemplateLine;
		this.pcrRealTimeWorkListOrder = pcrRealTimeWorkListOrder;
	}

	public String getActualValue() {
		return actualValue;
	}

	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}

	public PCRRealTimeResultTemplateLine getPcrRealTimeResultTemplateLine() {
		return pcrRealTimeResultTemplateLine;
	}

	public void setPcrRealTimeResultTemplateLine(PCRRealTimeResultTemplateLine pcrRealTimeResultTemplateLine) {
		this.pcrRealTimeResultTemplateLine = pcrRealTimeResultTemplateLine;
	}

	public PCRRealTimeWorkListOrder getPcrRealTimeWorkListOrder() {
		return pcrRealTimeWorkListOrder;
	}

	public void setPcrRealTimeWorkListOrder(PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder) {
		this.pcrRealTimeWorkListOrder = pcrRealTimeWorkListOrder;
	}

	public String getLineCode() {
		return lineCode;
	}

	public void setLineCode(String lineCode) {
		this.lineCode = lineCode;
	}

}
