/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.certacure.lis.interfaces.entities;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.certacure.core.base.entity.BaseAuditableTenantedEntity;
import com.certacure.core.common.data.model.converter.BooleanIntegerConverter;

/**
 *
 */
@Entity
@Table(name = "mw_machine_tests")
@Audited

public class MachineTest extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Size(max = 255)
	@Column(name = "name")
	private String name;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "host_code")
	private String hostCode;

	@Size(max = 255)
	@Column(name = "result_code")
	private String resultCode;

	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@JoinColumn(name = "machine_id", referencedColumnName = "rid")
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Machine machine;

	@JoinColumn(name = "test_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JsonIgnoreProperties({ "catalogTestList" })
	private TestCatalog testCatalog;

	public MachineTest() {
	}

	public TestCatalog getTestCatalog() {
		return testCatalog;
	}

	public void setTestCatalog(TestCatalog testCatalog) {
		this.testCatalog = testCatalog;
	}

	public MachineTest(Long rid) {
		this.rid = rid;
	}

	public MachineTest(Long rid, String hostCode, long createdBy, Date creationDate) {
		this.rid = rid;
		this.hostCode = hostCode;
	}

	public MachineTest(Machine machine, TestCatalog test, String defultHostCode, boolean isActive) {

		this.machine = machine;
		this.testCatalog = test;
		this.hostCode = defultHostCode;
		this.isActive = isActive;
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

	public String getHostCode() {
		return hostCode;
	}

	public void setHostCode(String hostCode) {
		this.hostCode = hostCode;
	}

	public String getResultCode() {
		if (resultCode != null)
			return resultCode;
		else
			return "";
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
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
		if (!(object instanceof MachineTest)) {
			return false;
		}
		MachineTest other = (MachineTest) object;
		if ((this.rid == null && other.rid != null) || (this.rid != null && !this.rid.equals(other.rid))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.lab.MwMachineTests[ rid=" + rid + " ]";
	}

}
