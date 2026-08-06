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
import com.sunbird.core.base.entity.BaseAuditableEntity;
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;

@Entity
@Table(name = "mw_pcr_realtime_result")
public class PCRRealTimeResult extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "is_result_sent")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isResultSent;

	@OneToMany(mappedBy = "pcrRealTimeResult", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "pcrRealTimeResult" }, allowSetters = true)
	private Set<PCRRealTimeOrderResult> pcrRealTimeOrderResults;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "realtime_result_template_id", referencedColumnName = "rid")
	private PCRRealTimeResultTemplate pcrRealTimeResultTemplate;

	@OneToMany(mappedBy = "pcrRealTimeResult", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "pcrRealTimeResult" }, allowSetters = true)
	private Set<PCRRealTimeActualResultValue> pcrRealTimeActualResultValues;

	//	@ManyToOne(fetch = FetchType.LAZY)
	//	@JoinColumn(name = "realtime_result_status_id")
	//	private LkpPcrRealTime orderStatus;

	public PCRRealTimeResult() {
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public PCRRealTimeResultTemplate getPcrRealTimeResultTemplate() {
		return pcrRealTimeResultTemplate;
	}

	public Set<PCRRealTimeActualResultValue> getPcrRealTimeActualResultValues() {
		return pcrRealTimeActualResultValues;
	}

	public Set<PCRRealTimeOrderResult> getPcrRealTimeResults() {
		return pcrRealTimeOrderResults;
	}

	public Boolean getIsResultSent() {
		return isResultSent;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public void setIsResultSent(Boolean isResultSent) {
		this.isResultSent = isResultSent;
	}

	public void setPcrRealTimeResultTemplate(PCRRealTimeResultTemplate pcrRealTimeResultTemplate) {
		this.pcrRealTimeResultTemplate = pcrRealTimeResultTemplate;
	}

	public void setPcrRealTimeActualResultValues(Set<PCRRealTimeActualResultValue> pcrRealTimeActualResultValues) {
		this.pcrRealTimeActualResultValues = pcrRealTimeActualResultValues;
	}

	public void setPcrRealTimeResults(Set<PCRRealTimeOrderResult> pcrRealTimeOrderResults) {
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
		PCRRealTimeResult other = (PCRRealTimeResult) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PCRRealTimeResult [rid=" + rid + "]";
	}

}
