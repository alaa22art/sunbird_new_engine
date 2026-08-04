/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.certacure.lis.interfaces.entities;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.certacure.core.base.entity.BaseAuditableTenantedEntity;

/**
 *

 */
@Entity
@Table(name = "mw_test_catalog")
@Audited
public class TestCatalog extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "name")
	private String name;

	@Size(max = 255)
	@Column(name = "requester_test_code")
	private String requesterTestCode;

	@OneToMany(mappedBy = "testCatalog", fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "TestCatalog" })
	private List<MachineTest> machineTestsList;

	@JoinColumn(name = "specimen_type", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "specimenList" })
	@NotNull
	private LkpSpecimenType specimenType;

	public LkpSpecimenType getSpecimenType() {
		return specimenType;
	}

	public void setSpecimenType(LkpSpecimenType specimenType) {
		this.specimenType = specimenType;
	}

	public TestCatalog() {
	}

	public TestCatalog(Long rid) {
		this.rid = rid;
	}

	public TestCatalog(Long rid, long createdBy, Date creationDate) {
		this.rid = rid;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getRequesterTestCode() {
		return requesterTestCode;
	}

	public void setRequesterTestCode(String requesterTestCode) {
		this.requesterTestCode = requesterTestCode;
	}

	public List<MachineTest> getMachineTestsList() {
		return machineTestsList;
	}

	public void setMachineTestsList(List<MachineTest> machineTestsList) {
		this.machineTestsList = machineTestsList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer result = 1;
		result = prime * result + ((rid == null) ? 0 : rid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof TestCatalog)) {
			return false;
		}
		TestCatalog other = (TestCatalog) object;
		if ((this.rid == null && other.rid != null) || (this.rid != null && !this.rid.equals(other.rid))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.lab.TestCatalog[ rid=" + rid + " ]";
	}

}
