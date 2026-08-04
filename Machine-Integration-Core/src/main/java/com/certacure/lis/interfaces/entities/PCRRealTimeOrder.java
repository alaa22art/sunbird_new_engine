package com.certacure.lis.interfaces.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.certacure.core.base.entity.BaseAuditableEntity;

@Entity
@Table(name = "mw_pcr_realtime_order")
public class PCRRealTimeOrder extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	//NOTE:Change the relation to OneToMany later 
	@OneToOne
	@JoinColumn(name = "machine_order_id", referencedColumnName = "rid")
	private MachineOrder machineOrder;

	@OneToMany(mappedBy = "pcrRealTimeOrder", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "pcrRealTimeOrder" }, allowSetters = true)
	private Set<PCRRealTimeWorkListOrder> workListOrders;

	public PCRRealTimeOrder() {

	}

	@Override
	public Long getRid() {
		return rid;
	}

	public MachineOrder getMachineOrder() {
		return machineOrder;
	}

	public Set<PCRRealTimeWorkListOrder> getWorkListOrders() {
		return workListOrders;
	}

	public void setWorkListOrders(Set<PCRRealTimeWorkListOrder> workListOrders) {
		this.workListOrders = workListOrders;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public void setMachineOrder(MachineOrder machineOrder) {
		this.machineOrder = machineOrder;
	}

	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer result = 1;
		result = prime * result + ((rid == null) ? 0 : rid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PCRRealTimeOrder other = (PCRRealTimeOrder) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PCRRealTimeOrder [rid=" + rid + "]";
	}

}
