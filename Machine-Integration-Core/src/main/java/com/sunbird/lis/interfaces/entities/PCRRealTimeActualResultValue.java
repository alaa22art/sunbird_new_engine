package com.sunbird.lis.interfaces.entities;

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
import javax.validation.constraints.Size;

import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;

@Entity
@Table(name = "mw_pcr_realtime_actual_result_value")
public class PCRRealTimeActualResultValue extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "value")
	@Size(max = 500)
	private String value;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "realtime_template_result_line_id", referencedColumnName = "rid")
	private PCRRealTimeResultTemplateLine pcrRealTimeResultTemplateLine;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "realtime_result_id")
	private PCRRealTimeResult pcrRealTimeResult;

	public PCRRealTimeActualResultValue() {
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public String getValue() {
		return value;
	}

	public PCRRealTimeResult getPcrRealTimeResult() {
		return pcrRealTimeResult;
	}

	public PCRRealTimeResultTemplateLine getPcrRealTimeResultTemplateLine() {
		return pcrRealTimeResultTemplateLine;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setPcrRealTimeResult(PCRRealTimeResult pcrRealTimeResult) {
		this.pcrRealTimeResult = pcrRealTimeResult;
	}

	public void setPcrRealTimeResultTemplateLine(PCRRealTimeResultTemplateLine pcrRealTimeResultTemplateLine) {
		this.pcrRealTimeResultTemplateLine = pcrRealTimeResultTemplateLine;
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
		PCRRealTimeActualResultValue other = (PCRRealTimeActualResultValue) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PCRRealTimeActualResultValue [rid=" + rid + "]";
	}

}
