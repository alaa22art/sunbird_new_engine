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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.certacure.core.base.entity.BaseAuditableEntity;
import com.certacure.core.common.data.model.TransField;
import com.certacure.core.common.data.model.converter.TransFieldAttConverter;

/**
 * AstmProtocolVersion
 * 
 * @author Alaa Himour 
 * @since JAN/21/2017
 */
@Entity
@Table(name = "lkp_protocol")
@Audited
public class LkpProtocol extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "name")
	@Convert(converter = TransFieldAttConverter.class)
	private TransField name;

	@Column(name = "description")
	@Convert(converter = TransFieldAttConverter.class)
	private TransField description;

	@Column(name = "code")
	private String code;

	@OneToMany(mappedBy = "lowLevelProtocolId", fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "lowLevelProtocolId" })
	private List<MachineType> machineTypeList;

	@OneToMany(mappedBy = "highLevelProtocolId", fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "lowLevelProtocolId" })
	private List<MachineType> machineTypeList1;

	public LkpProtocol() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LkpProtocol(Long rid) {
		this.rid = rid;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public TransField getName() {
		return name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public List<MachineType> getMachineTypeList() {
		return machineTypeList;
	}

	public void setMachineTypeList(List<MachineType> machineTypeList) {
		this.machineTypeList = machineTypeList;
	}

	@XmlTransient
	public List<MachineType> getMachineTypeList1() {
		return machineTypeList1;
	}

	public void setMachineTypeList1(List<MachineType> machineTypeList1) {
		this.machineTypeList1 = machineTypeList1;
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
		LkpProtocol other = (LkpProtocol) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Protocol [rid=" + rid + "]";
	}

	public TransField getDescription() {
		return description;
	}

	public void setDescription(TransField description) {
		this.description = description;
	}

}
