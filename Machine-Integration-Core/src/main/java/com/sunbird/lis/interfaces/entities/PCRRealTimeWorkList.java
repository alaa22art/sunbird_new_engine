package com.certacure.lis.interfaces.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.certacure.core.base.entity.BaseAuditableBranchedEntity;

@Entity
@Table(name = "mw_pcr_realtime_worklist")
public class PCRRealTimeWorkList extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "name")
	@Size(max = 255)
	private String name;

	@Column(name = "max_rows")
	private Integer maxRows;

	@Column(name = "max_columns")
	private Integer maxColumns;

	@OneToMany(mappedBy = "pcrRealTimeWorkList", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "pcrRealTimeWorkList" }, allowSetters = true)
	private List<PCRRealTimeWorkListOrder> workListOrders;

	@JoinColumn(name = "status_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	private LkpPCRRealRimeWorkListStatus workListStatus;

	public PCRRealTimeWorkList() {
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public String getName() {
		return name;
	}

	public Integer getMaxRows() {
		return maxRows;
	}

	public Integer getMaxColumns() {
		return maxColumns;
	}

	public List<PCRRealTimeWorkListOrder> getWorkListOrders() {
		return workListOrders;
	}

	public LkpPCRRealRimeWorkListStatus getWorkListStatus() {
		return workListStatus;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMaxRows(Integer maxRows) {
		this.maxRows = maxRows;
	}

	public void setMaxColumns(Integer maxColumns) {
		this.maxColumns = maxColumns;
	}

	public void setWorkListOrders(List<PCRRealTimeWorkListOrder> workListOrders) {
		this.workListOrders = workListOrders;
	}

	public void setWorkListStatus(LkpPCRRealRimeWorkListStatus workListStatus) {
		this.workListStatus = workListStatus;
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
		PCRRealTimeWorkList other = (PCRRealTimeWorkList) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PCRRealTimeWorkList [rid=" + rid + "]";
	}

}
