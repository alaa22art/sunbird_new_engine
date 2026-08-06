/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.sunbird.core.base.entity.BaseAuditableTenantedEntity;
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;

/**
 *
 */
@Entity
@Table(name = "mw_machine_type_tests")
@Audited
public class MachineTypeTest extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Size(max = 255)
	@Column(name = "defult_host_code")
	private String defultHostCode;

	@Size(max = 255)
	@Column(name = "machine_test_name")
	private String name;

	@Size(max = 255)
	@Column(name = "description")
	private String description;

	@JoinColumn(name = "machine_type_id", referencedColumnName = "rid")
	@NotNull
	@ManyToOne

	private MachineType machineTypeId;

	@JoinColumn(name = "test_id", referencedColumnName = "rid")
	@ManyToOne

	@NotNull
	private TestCatalog testId;

	@Basic(optional = true)
	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "is_active")
	@NotNull
	private Boolean isActive;

	public MachineTypeTest() {
	}

	public MachineTypeTest(Long rid) {
		this.rid = rid;
	}

	public MachineTypeTest(Long rid, long createdBy, Date creationDate) {
		this.rid = rid;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getDefultHostCode() {
		return defultHostCode;
	}

	public void setDefultHostCode(String defultHostCode) {
		this.defultHostCode = defultHostCode;
	}

	public MachineType getMachineTypeId() {
		return machineTypeId;
	}

	public void setMachineTypeId(MachineType machineTypeId) {
		this.machineTypeId = machineTypeId;
	}

	public TestCatalog getTestId() {
		return testId;
	}

	public void setTestId(TestCatalog testId) {
		this.testId = testId;
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
		MachineTypeTest other = (MachineTypeTest) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MachineTypeTest [rid=" + rid + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
