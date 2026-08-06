package com.sunbird.core.base.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunbird.core.common.util.SecurityUtil;

/**
 * BaseAuditableTenantedEntity.java Super class for Audited entities, used to add auditing columns to child entity and set attribute values when needed, in addition this class will create an auditing
 * table
 * automatically
 *
 **/

@MappedSuperclass
@Audited
@FilterDef(name = BaseAuditableTenantedEntity.TENANT_FILTER, parameters = { @ParamDef(name = "tenantId", type = "long") })
@Filter(name = BaseAuditableTenantedEntity.TENANT_FILTER, condition = "tenant_id = :tenantId")
public abstract class BaseAuditableTenantedEntity extends BaseAuditableEntity {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	public static final String TENANT_FILTER = "tenantFilter";

	@Column(name = "TENANT_ID")
	@NotNull
	private Long tenantId;

	@Override
	@PrePersist
	public void onPrePersist() {
		populateAudit();
	}

	@Override
	@PreUpdate
	public void onPreUpdate() {
		populateAudit();
	}

	/**
	 * populate timestamp on presist and update
	 */
	@Override
	protected void populateAudit() {
		super.populateAudit();
		// update tenant id on creation || on update
		// we used this if in case we are adding a custom tenant id in some entities.
		if (getTenantId() == null && getRid() == null) {
			setTenantId(SecurityUtil.getCurrentUser().getTenantId());
		}
	}

	/**
	 * @return The tenant ID of the entity
	 */
	public Long getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId Sets the tenant ID
	 */
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

}
