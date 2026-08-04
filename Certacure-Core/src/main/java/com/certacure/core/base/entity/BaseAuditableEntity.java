package com.certacure.core.base.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.certacure.core.common.util.SecurityUtil;

/**
 * BaseAuditableTenantedEntity.java Super class for Audited entities,
 * used to add auditing columns to child entity and set attribute values when needed,
 * in addition this class will create an auditing table automatically
 *
 **/

@MappedSuperclass
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public abstract class BaseAuditableEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Column(name = "VERSION")
	@Version
	private Long version;

	@NotNull
	@Column(name = "CREATED_BY", updatable = false)
	private Long createdBy;

	@NotNull
	@Column(name = "CREATION_DATE", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Column(name = "UPDATE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@PrePersist
	public void onPrePersist() {
		populateAudit();
	}

	@PreUpdate
	public void onPreUpdate() {
		populateAudit();
	}

	/**
	 * populate timestamp on presist and update
	 */
	protected void populateAudit() {
		if (getCreationDate() == null) {
			setCreationDate(new Date());

			if (getCreatedBy() == null) {
				setCreatedBy(SecurityUtil.getCurrentUser().getRid());
			}
		} else {
			setUpdateDate(new Date());
			setUpdatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());
		}
	}

	/**
	 * @return The current version number
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * @return ID of the user who created the record
	 */
	public Long getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param Set the ID of the user who created the record
	 */
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return Date the record was created
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param Set the date the record was created
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return Get ID of user who last update the record
	 */
	public Long getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy Get ID of user who last update the record
	 */
	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return Get last update date
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updatedDate Set last update date
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
