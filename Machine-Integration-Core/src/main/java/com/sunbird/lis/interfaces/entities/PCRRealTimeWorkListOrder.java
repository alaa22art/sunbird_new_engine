package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;

@Entity
@Table(name = "mw_pcr_realtime_worklist_order")
public class PCRRealTimeWorkListOrder extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "realtime_order_row_index")
	private String orderRowIndex;

	@Column(name = "realtime_order_column_index")
	private String orderColumnIndex;

	@Column(name = "is_confirmed")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isConfirmed;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "realtime_order_id")
	private PCRRealTimeOrder pcrRealTimeOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "realtime_worklist_id")
	private PCRRealTimeWorkList pcrRealTimeWorkList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "realtime_order_status_id")
	private LkpPCRRealRimeOrderStatus orderStatus;

	@OneToMany(mappedBy = "pcrRealTimeWorkListOrder")
	@JsonIgnoreProperties(value = { "pcrRealTimeWorkListOrder" }, allowSetters = true)
	private Set<PCRRealTimeOrderResult> pcrRealTimeOrderResults;

	public PCRRealTimeWorkListOrder() {
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public String getOrderRowIndex() {
		return orderRowIndex;
	}

	public String getOrderColumnIndex() {
		return orderColumnIndex;
	}

	public Boolean getIsConfirmed() {
		return isConfirmed;
	}

	public PCRRealTimeOrder getPcrRealTimeOrder() {
		return pcrRealTimeOrder;
	}

	public PCRRealTimeWorkList getPcrRealTimeWorkList() {
		return pcrRealTimeWorkList;
	}

	public LkpPCRRealRimeOrderStatus getOrderStatus() {
		return orderStatus;
	}

	public Set<PCRRealTimeOrderResult> getPcrRealTimeOrderResults() {
		return pcrRealTimeOrderResults;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public void setOrderRowIndex(String orderRowIndex) {
		this.orderRowIndex = orderRowIndex;
	}

	public void setOrderColumnIndex(String orderColumnIndex) {
		this.orderColumnIndex = orderColumnIndex;
	}

	public void setIsConfirmed(Boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
	}

	public void setPcrRealTimeOrder(PCRRealTimeOrder pcrRealTimeOrder) {
		this.pcrRealTimeOrder = pcrRealTimeOrder;
	}

	public void setPcrRealTimeWorkList(PCRRealTimeWorkList pcrRealTimeWorkList) {
		this.pcrRealTimeWorkList = pcrRealTimeWorkList;
	}

	public void setOrderStatus(LkpPCRRealRimeOrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public void setPcrRealTimeOrderResults(Set<PCRRealTimeOrderResult> pcrRealTimeOrderResults) {
		this.pcrRealTimeOrderResults = pcrRealTimeOrderResults;
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
		PCRRealTimeWorkListOrder other = (PCRRealTimeWorkListOrder) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PCRRealTimeWorkListOrder [rid=" + rid + "]";
	}

}
