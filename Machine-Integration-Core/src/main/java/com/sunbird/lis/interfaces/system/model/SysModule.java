package com.sunbird.lis.interfaces.system.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sunbird.core.base.entity.BaseAuditableEntity;
import com.sunbird.core.common.annotation.MapNotNull;
import com.sunbird.core.common.data.model.TransField;
import com.sunbird.core.common.data.model.converter.TransFieldAttConverter;

/**
 * SysModule.java
 * 
 **/
@Entity
@Table(name = "sys_module")
@Audited
public class SysModule extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "description")
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField description;

	@Column(name = "name")
	@MapNotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField name;

	//bi-directional many-to-one association to SysPage
	@OneToMany(mappedBy = "sysModule", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("sysModule")
	private List<SysPage> sysPages;

	public SysModule() {
	}

	@Override
	public Long getRid() {
		return rid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		SysModule other = (SysModule) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SysModule [rid=" + rid + "]";
	}

	public TransField getDescription() {
		return description;
	}

	public void setDescription(TransField description) {
		this.description = description;
	}

	public TransField getName() {
		return name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public List<SysPage> getSysPages() {
		return sysPages;
	}

	public void setSysPages(List<SysPage> sysPages) {
		this.sysPages = sysPages;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

}