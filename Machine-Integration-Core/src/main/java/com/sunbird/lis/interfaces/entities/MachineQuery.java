/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sunbird.core.base.entity.BaseAuditableEntity;
import com.sunbird.core.common.util.SecurityUtil;

/**
 * MachineQuery
 * 

 */
@Entity
@Table(name = "mw_machine_query")
@Audited
public class MachineQuery extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "sample_no")
	private String sampleNo;

	@Column(name = "status")
	private Integer status;

	@JoinColumn(name = "machine_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Machine machine;
	
	@JoinColumn(name = "message_transaction_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	private MessageTransaction messageTransaction;

	@Column(name = "machine_name")
	@Size(max = 255)
	private String machineName;

	
	

	@Column(name = "specimen_position")
	private String specimenPosition;

	@OneToMany(mappedBy = "machineQueryId", fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "machineQueryId" })
	private List<MachineResult> machineResultList;

	//	@OneToMany(mappedBy = "machineQueryId", fetch = FetchType.LAZY)
	//	@JsonIgnoreProperties({ "machineQueryId" })
	//	private List<MachineOrderQueryResponse> machineOrderQueryResponseList;
	//
	//	public List<MachineOrderQueryResponse> getMachineOrderQueryResponseList() {
	//		return machineOrderQueryResponseList;
	//	}
	//
	//	public void setMachineOrderQueryResponseList(List<MachineOrderQueryResponse> machineOrderQueryResponseList) {
	//		this.machineOrderQueryResponseList = machineOrderQueryResponseList;
	//	}
	
	public MessageTransaction getMessageTransaction() {
		return messageTransaction;
	}

	
	public void setMessageTransaction(MessageTransaction messageTransaction) {
		this.messageTransaction = messageTransaction;
	}

	public MachineQuery() {
	}

	public MachineQuery(Long rid) {
		this.rid = rid;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getSampleNo() {
		return sampleNo;
	}

	public void setSampleNo(String sampleNo) {
		this.sampleNo = sampleNo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getSpecimenPosition() {
		return specimenPosition;
	}

	public void setSpecimenPosition(String specimenPosition) {
		this.specimenPosition = specimenPosition;
	}

	public List<MachineResult> getMachineResultList() {
		return machineResultList;
	}

	public void setMachineResultList(List<MachineResult> machineResultList) {
		this.machineResultList = machineResultList;
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
		MachineQuery other = (MachineQuery) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MachineQuery [rid=" + rid + "]";
	}

	@Override
	protected void populateAudit() {

		if (getCreationDate() == null) {
			setCreationDate(new Date());

			if (getCreatedBy() == null) {
				setCreatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());

			}
		} else {
			setUpdateDate(new Date());
			if (getUpdatedBy() == null) {
				setUpdatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());
			}
		}
	}

}
