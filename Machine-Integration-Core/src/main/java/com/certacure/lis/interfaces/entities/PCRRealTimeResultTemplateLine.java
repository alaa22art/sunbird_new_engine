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
import com.certacure.core.base.entity.BaseAuditableEntity;

@Entity
@Table(name = "mw_pcr_realtime_result_template_line")
public class PCRRealTimeResultTemplateLine extends BaseAuditableEntity implements Serializable {

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

	@Column(name = "line_default_value")
	@Size(max = 500)
	private String lineDefaultValue;

	@JoinColumn(name = "result_template_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	private PCRRealTimeResultTemplate pcrRealTimeResultTemplate;

	@OneToMany(mappedBy = "pcrRealTimeResultTemplateLine")
	@JsonIgnoreProperties(value = { "pcrRealTimeResultTemplateLine" }, allowSetters = true)
	private List<PCRRealTimeActualResultValue> pcrRealTimeActualResultValues;

	public PCRRealTimeResultTemplateLine() {
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getLineDefaultValue() {
		return lineDefaultValue;
	}

	public PCRRealTimeResultTemplate getPcrRealTimeResultTemplate() {
		return pcrRealTimeResultTemplate;
	}

	public List<PCRRealTimeActualResultValue> getPcrRealTimeActualResultValues() {
		return pcrRealTimeActualResultValues;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLineDefaultValue(String lineDefaultValue) {
		this.lineDefaultValue = lineDefaultValue;
	}

	public void setPcrRealTimeResultTemplate(PCRRealTimeResultTemplate pcrRealTimeResultTemplate) {
		this.pcrRealTimeResultTemplate = pcrRealTimeResultTemplate;
	}

	public void setPcrRealTimeActualResultValues(List<PCRRealTimeActualResultValue> pcrRealTimeActualResultValues) {
		this.pcrRealTimeActualResultValues = pcrRealTimeActualResultValues;
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
		PCRRealTimeResultTemplateLine other = (PCRRealTimeResultTemplateLine) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PCRRealTimeResultTemplateLine [rid=" + rid + "]";
	}

}
