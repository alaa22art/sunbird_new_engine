package com.certacure.lis.interfaces.entities;

import java.io.Serializable;

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

import com.certacure.core.base.entity.BaseAuditableEntity;

@Entity
@Table(name = "mw_pcr_realtime_order_result")
public class PCRRealTimeOrderResult extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "realtime_result_id", referencedColumnName = "rid")
	private PCRRealTimeResult pcrRealTimeResult;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "realtime_worklist_order_id", referencedColumnName = "rid")
	private PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder;

	public PCRRealTimeOrderResult() {
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public PCRRealTimeResult getPcrRealTimeResult() {
		return pcrRealTimeResult;
	}

	public PCRRealTimeWorkListOrder getPcrRealTimeWorkListOrder() {
		return pcrRealTimeWorkListOrder;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public void setPcrRealTimeResult(PCRRealTimeResult pcrRealTimeResult) {
		this.pcrRealTimeResult = pcrRealTimeResult;
	}

	public void setPcrRealTimeWorkListOrder(PCRRealTimeWorkListOrder pcrRealTimeWorkListOrder) {
		this.pcrRealTimeWorkListOrder = pcrRealTimeWorkListOrder;
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
		PCRRealTimeOrderResult other = (PCRRealTimeOrderResult) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PCRRealTimeOrderResult [rid=" + rid + "]";
	}

}
