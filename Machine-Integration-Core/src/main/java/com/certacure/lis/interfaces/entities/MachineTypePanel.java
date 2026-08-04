/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.certacure.lis.interfaces.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.certacure.core.base.entity.BaseAuditableEntity;
import com.certacure.core.common.data.model.converter.BooleanIntegerConverter;

/**
 * MachineTypePanel
 * 

 */
@Entity
@Table(name = "mw_machine_type_panel")
@Audited
public class MachineTypePanel extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "panel_name")
	private String panelName;

	@Column(name = "panel_host_code")
	private String panelHostCode;

	@JoinColumn(name = "machine_type_id")
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "machine_type" })
	@NotNull
	private MachineType machineType;

	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "is_active")
	private Boolean isActive;

	public MachineType getMachineType() {
		return machineType;
	}

	public void setMachineType(MachineType machineType) {
		this.machineType = machineType;
	}

	public MachineTypePanel() {
	}

	public MachineTypePanel(Long rid) {
		this.rid = rid;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getPanelHostCode() {
		return panelHostCode;
	}

	public void setPanelHostCode(String code) {
		this.panelHostCode = code;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getPanelName() {
		return panelName;
	}

	public void setPanelName(String panelName) {
		this.panelName = panelName;
	}

	@OneToMany(mappedBy = "machineType", fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "machineType" })
	private List<Machine> machineList;

	public List<Machine> getMachineList() {
		return machineList;
	}

	public void setMachineList(List<Machine> machineList) {
		this.machineList = machineList;
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
		MachineTypePanel other = (MachineTypePanel) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MachineTypePanel [rid=" + rid + "]";
	}

}
