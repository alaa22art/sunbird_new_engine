package com.certacure.lis.interfaces.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_sample_transaction_3") // A view!
public class TransactionView {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "rid")
	private Long rid;

	private String machineId;
	private String sampleNo;
	//private Boolean isQueryArrive;
	//private Boolean isOrderArrive;
	//private Boolean isResultArrive;
	private String lastQueryDateTime;
	private String lastOrderDateTime;
	private String lastResultDateTime;

	public String getMachineId() {
		return machineId;
	}

	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	public String getsampleNo() {
		return sampleNo;
	}

	public void setsampleNo(String sampleNo) {
		this.sampleNo = sampleNo;
	}

	public String getLastQueryDateTime() {
		return lastQueryDateTime;
	}

	public void setLastQueryDateTime(String lastQueryDateTime) {
		this.lastQueryDateTime = lastQueryDateTime;
	}

	public String getLastOrderDateTime() {

		return lastOrderDateTime;
	}

	public void setLastOrderDateTime(String lastOrderDateTime) {

		this.lastOrderDateTime = lastOrderDateTime;
	}

	public String getLastResultDateTime() {
		return lastResultDateTime;
	}

	public void setLastResultDateTime(String lastResultDateTime) {
		this.lastResultDateTime = lastResultDateTime;
	}

}
