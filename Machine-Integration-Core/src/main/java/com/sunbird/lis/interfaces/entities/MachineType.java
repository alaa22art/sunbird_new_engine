/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunbird.lis.interfaces.entities;

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
import com.sunbird.core.base.entity.BaseAuditableEntity;
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;

/**
 * MachineTypes
 * 
 * @author Alaa Himour <ahimour@certacuresolutions.com>
 */
@Entity
@Table(name = "mw_machine_type")
@Audited
public class MachineType extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "name")
	private String name;

	@Column(name = "code")
	private String code;

	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "is_active")
	private Boolean isActive;

	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "is_machine_Name_Required")
	private Boolean isMachineNameRequired;

	public Boolean getIsMachineNameRequired() {
		return isMachineNameRequired;
	}

	public void setIsMachineNameRequired(Boolean ismachineNameRequired) {
		this.isMachineNameRequired = ismachineNameRequired;
	}

	@JoinColumn(name = "low_level_protocol_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "machineTypeList" })
	private LkpProtocol lowLevelProtocolId;

	@JoinColumn(name = "high_level_protocol_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "machineTypeList" })
	private LkpProtocol highLevelProtocolId;

	@JoinColumn(name = "driver_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "driver" })
	@NotNull
	private Driver driver;

	public MachineType() {
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LkpProtocol getLowLevelProtocolId() {
		return lowLevelProtocolId;
	}

	public void setLowLevelProtocolId(LkpProtocol lowLevelProtocolId) {
		this.lowLevelProtocolId = lowLevelProtocolId;
	}

	public LkpProtocol getHighLevelProtocolId() {
		return highLevelProtocolId;
	}

	public void setHighLevelProtocolId(LkpProtocol highLevelProtocolId) {
		this.highLevelProtocolId = highLevelProtocolId;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	@OneToMany(mappedBy = "machineType", fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "machineType" })
	private List<Machine> machineList;

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
		MachineType other = (MachineType) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MachineType [rid=" + rid + "]";
	}

}
