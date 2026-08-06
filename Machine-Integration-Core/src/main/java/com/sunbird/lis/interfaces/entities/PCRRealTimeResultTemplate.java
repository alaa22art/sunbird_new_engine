package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;

@Entity
@Table(name = "mw_pcr_realtime_result_template")
public class PCRRealTimeResultTemplate extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "name")
	@Size(max = 255)
	private String name;

	@Column(name = "code")
	@Size(max = 255)
	private String code;

	@OneToMany(mappedBy = "pcrRealTimeResultTemplate", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "pcrRealTimeResultTemplate" }, allowSetters = true)
	private Set<PCRRealTimeResultTemplateLine> pcrRealTimeResultTemplateLines;

	@OneToMany(mappedBy = "pcrRealTimeResultTemplate", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "pcrRealTimeResultTemplate" }, allowSetters = true)
	private Set<PCRRealTimeResult> pcrRealTimeResults;

	public PCRRealTimeResultTemplate() {
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public Set<PCRRealTimeResultTemplateLine> getPcrRealTimeResultTemplateLines() {
		return pcrRealTimeResultTemplateLines;
	}

	public Set<PCRRealTimeResult> getPcrRealTimeResults() {
		return pcrRealTimeResults;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setPcrRealTimeResultTemplateLines(Set<PCRRealTimeResultTemplateLine> pcrRealTimeResultTemplateLines) {
		this.pcrRealTimeResultTemplateLines = pcrRealTimeResultTemplateLines;
	}

	public void setPcrRealTimeResults(Set<PCRRealTimeResult> pcrRealTimeResults) {
		this.pcrRealTimeResults = pcrRealTimeResults;
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
		PCRRealTimeResultTemplate other = (PCRRealTimeResultTemplate) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PCRRealTimeResultTemplate [rid=" + rid + "]";
	}

}
