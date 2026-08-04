package com.certacure.lis.interfaces.entities;

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
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.certacure.core.base.entity.BaseAuditableEntity;
import com.certacure.core.common.util.SecurityUtil;

/**
 *
 */
@Entity
@Table(name = "mw_machine_order_query_response")
@Audited

public class MachineOrderQueryResponse extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Basic(optional = false)
	@Size(min = 1, max = 4000)
	@Column(name = "description")
	private String description;

	@JoinColumn(name = "machine_order_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	private MachineOrder machineOrderId;

	@JoinColumn(name = "machine_query_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	private MachineQuery machineQueryId;

	public MachineOrderQueryResponse() {
	}

	public MachineOrderQueryResponse(Long rid) {
		this.rid = rid;
	}

	public MachineOrderQueryResponse(Long rid, String description, long version) {
		this.rid = rid;
		this.description = description;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MachineOrder getMachineOrderId() {
		return machineOrderId;
	}

	public void setMachineOrderId(MachineOrder machineOrderId) {
		this.machineOrderId = machineOrderId;
	}

	public MachineQuery getMachineQueryId() {
		return machineQueryId;
	}

	public void setMachineQueryId(MachineQuery machineQueryId) {
		this.machineQueryId = machineQueryId;
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
		if (!(object instanceof MachineOrderQueryResponse)) {
			return false;
		}
		MachineOrderQueryResponse other = (MachineOrderQueryResponse) object;
		if ((this.rid == null && other.rid != null) || (this.rid != null && !this.rid.equals(other.rid))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.lab.MwMachineOrderQueryResponce[ rid=" + rid + " ]";
	}

	@Override
	protected void populateAudit() {

		if (getCreationDate() == null) {
			setCreationDate(new Date());

			if (getCreatedBy() == null) {
				setCreatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());

			}
		} else {
			setUpdateDate(new Date());
			if (getUpdatedBy() == null) {
				setUpdatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());
			}
		}
	}

}
