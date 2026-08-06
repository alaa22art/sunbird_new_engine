package com.certacure.lis.interfaces.wrapper;

import java.io.Serializable;
import java.util.List;

import com.certacure.lis.interfaces.entities.PCRRealTimeActualResultValue;
import com.certacure.lis.interfaces.entities.PCRRealTimeWorkListOrder;

public class PCRRealTimeActualResultWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder;
	private List<PCRRealTimeActualResultValue> pcrRealTimeActualValues;

	public PCRRealTimeActualResultWrapper() {
	}

	public PCRRealTimeActualResultWrapper(PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder,
			List<PCRRealTimeActualResultValue> pcrRealTimeActualValues) {
		super();
		this.pcrRealTimeWorkListOrder = pcrRealTimeWorkListOrder;
		this.pcrRealTimeActualValues = pcrRealTimeActualValues;
	}

	public PCRRealTimeWorkListOrder getPcrRealTimeWorkListOrder() {
		return pcrRealTimeWorkListOrder;
	}

	public void setPcrRealTimeWorkListOrder(PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder) {
		this.pcrRealTimeWorkListOrder = pcrRealTimeWorkListOrder;
	}

	public List<PCRRealTimeActualResultValue> getPcrRealTimeActualValues() {
		return pcrRealTimeActualValues;
	}

	public void setPcrRealTimeActualValues(List<PCRRealTimeActualResultValue> pcrRealTimeActualValues) {
		this.pcrRealTimeActualValues = pcrRealTimeActualValues;
	}

}
