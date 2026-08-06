package com.sunbird.lis.interfaces.admin.model;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.sunbird.core.base.entity.BaseAuditableEntity;
import com.sunbird.lis.interfaces.entities.LabBranch;
import com.sunbird.lis.interfaces.helper.SystemSettingType;


/**
 * SystemSetting.java
 * 
 * @since Mar/13/2022
 */
@Entity
@Table(name = "system_setting")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class SystemSetting extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_id")
	private SecTenant tenant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_id")
	private LabBranch branch;

	@NotNull
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private SystemSettingType type;

	@NotEmpty
	@Column(name = "value")
	@Size(min = 1, max = 1000)
	private String value;

	public SystemSetting() {

	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public SecTenant getTenant() {
		return tenant;
	}

	public void setTenant(SecTenant tenant) {
		this.tenant = tenant;
	}

	public LabBranch getBranch() {
		return branch;
	}

	public void setBranch(LabBranch branch) {
		this.branch = branch;
	}

	public SystemSettingType getType() {
		return type;
	}

	public void setType(SystemSettingType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}