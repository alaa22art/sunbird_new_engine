package com.certacure.lis.interfaces.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.certacure.core.base.entity.BaseAuditableEntity;
import com.certacure.core.common.annotation.MapNotNull;
import com.certacure.core.common.data.model.TransField;
import com.certacure.core.common.data.model.converter.TransFieldAttConverter;

/**
 * LkpMessagesType.java
 * 
 **/
@Entity
@Audited
@Table(name = "lkp_messages_type")
public class LkpMessagesType extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "code")
	private String code;

	@Size(max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "description")
	private TransField description;

	@MapNotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "name")
	private TransField name;

	//bi-directional many-to-one association to ComTenantMessage
	@OneToMany(mappedBy = "lkpMessagesType")
	private List<ComTenantMessage> comTenantMessage;

	public LkpMessagesType() {
	}

	@Override
	public Long getRid() {
		return this.rid;
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
		LkpMessagesType other = (LkpMessagesType) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LkpMessagesType [rid=" + rid + "]";
	}

	public TransField getName() {
		return name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public List<ComTenantMessage> getComTenantMessage() {
		return comTenantMessage;
	}

	public void setComTenantMessage(List<ComTenantMessage> comTenantMessage) {
		this.comTenantMessage = comTenantMessage;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public TransField getDescription() {
		return description;
	}

	public void setDescription(TransField description) {
		this.description = description;
	}

}