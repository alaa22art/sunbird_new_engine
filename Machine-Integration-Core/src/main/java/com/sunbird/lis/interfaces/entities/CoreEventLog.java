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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;
import com.sunbird.core.common.util.SecurityUtil;

/**
 *
 *
 */

@Entity
@Table(name = "mw_event_log")
public class CoreEventLog extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "event")
	private String text;

	@Column(name = "details")
	@Size(max = 255)
	private String details;

	@Column(name = "sent_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sentDate;

	@Column(name = "status_Id")
	private Integer statusId;

	@Column(name = "source")
	@Size(max = 4000)
	private String source;

	@JoinColumn(name = "machine_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Machine machine;

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	public CoreEventLog() {
	}

	public CoreEventLog(Long rid) {
		this.rid = rid;
	}

	public CoreEventLog(akka.event.Logging.LogEvent event) {
		//String strMsg = event.message() == null ? null : event.message().toString();
		String strMsg = event.message() == null ? null : getMessageBody(event.message().toString());
		String strSource = event.logSource();
		Date dtSentDate = new Date();
		Integer iLevel = event.level();
		this.setText(strMsg);
		this.setStatusId(iLevel);
		this.setSentDate(dtSentDate);
		this.setSource(strSource);
	}

	private String getMessageBody(String strMessage) {

		/*
		 * if (strMessage.contains("sender") && strMessage.contains("msg")) {
		 * 
		 * } else {
		 * 
		 * }
		 */

		return null;

	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {

		this.text = text;

	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return this.source;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
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
		CoreEventLog other = (CoreEventLog) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CoreEventLog [rid=" + rid + "]";
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	@Override
	protected void populateAudit() {

		if (getCreationDate() == null) {
			setCreationDate(new Date());

			if (getCreatedBy() == null) {
				setCreatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());
				if (getTenantId() == null) {
					setTenantId(SecurityUtil.getCurrentUserElseInternal().getTenantId());
				}

				if (getBranchId() == null) {
					setBranchId(SecurityUtil.getCurrentUserElseInternal().getBranchId());
				}
			}
		} else {
			setUpdateDate(new Date());
			if (getUpdatedBy() == null) {
				setUpdatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());
			}
		}
	}

}
