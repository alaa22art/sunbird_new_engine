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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.certacure.core.base.entity.BaseAuditableEntity;

/**
 * DriverDefinition
 * 
 * @author Alaa Himour <ahimour@optimizasolutions.com>
 * @since JAN/21/2017
 */
@Entity
@Table(name = "mw_driver")
@Audited
public class Driver extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "name")
	private String name;

	@Column(name = "config_txt")
	private String configTxt;

	@Column(name = "observer_config")
	private String observerConfig;

	@OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "driver" })
	private List<MachineType> machineTypeList;

	public Driver() {
	}

	public Driver(Long rid) {
		this.rid = rid;
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

	public String getConfigTxt() {
		return configTxt;
	}

	public void setConfigTxt(String configTxt) {
		this.configTxt = configTxt;
	}

	public String getObserverConfig() {
		return observerConfig;
	}

	public void setObserverConfig(String observerConfig) {
		this.observerConfig = observerConfig;
	}

	public List<MachineType> getMachineTypeList() {
		return machineTypeList;
	}

	public void setMachineTypeList(List<MachineType> machineTypeList) {
		this.machineTypeList = machineTypeList;
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
		Driver other = (Driver) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Driver [rid=" + rid + "]";
	}

}
