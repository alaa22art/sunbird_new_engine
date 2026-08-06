package com.certacure.core.base.entity;

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
import com.certacure.core.common.util.SecurityUtil;

/**
 * BaseAuditableBranchedEntity.java Super class for Audited entities, used to add auditing columns to child entity and set attribute values when needed, in addition this class will create an auditing
 * table
 * automatically
 *
 **/

@MappedSuperclass

@FilterDef(name = BaseAuditableBranchedEntity.BRANCH_FILTER, parameters = { @ParamDef(name = "branchId", type = "long") })
@Filter(name = BaseAuditableBranchedEntity.BRANCH_FILTER, condition = "branch_id = :branchId")
public abstract class BaseAuditableBranchedEntity extends BaseAuditableTenantedEntity {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	public static final String BRANCH_FILTER = "branchFilter";

	@Column(name = "BRANCH_ID", updatable = false)
	@NotNull
	private Long branchId;

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
		if (getBranchId() == null && getRid() == null) {
			setBranchId(SecurityUtil.getCurrentUser().getBranchId());
		}

	}

	/**
	 * @return The branch ID of the entity
	 */
	public Long getBranchId() {
		return branchId;
	}

	/**
	 * @param branchId Sets the branch ID
	 */
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

}
