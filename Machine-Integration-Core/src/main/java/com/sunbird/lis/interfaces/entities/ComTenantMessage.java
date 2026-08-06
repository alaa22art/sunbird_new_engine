package com.certacure.lis.interfaces.entities;

import java.io.Serializable;

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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.certacure.core.base.entity.BaseAuditableTenantedEntity;
import com.certacure.core.common.annotation.MapNotNull;
import com.certacure.core.common.data.model.TransField;
import com.certacure.core.common.data.model.converter.TransFieldAttConverter;

/**
 * ComTenantMessage.java
 * 
 **/
@Entity
@Audited
@Table(name = "com_tenant_messages")
public class ComTenantMessage extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid", unique = true)
	private Long rid;

	@NotNull
	@Column(name = "code", updatable = false, unique = true)
	@Size(min = 1, max = 4000)
	private String code;

	@MapNotNull
	@Column(name = "description")
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField description;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id")
	private LkpMessagesType lkpMessagesType;

	public ComTenantMessage() {
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
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
		ComTenantMessage other = (ComTenantMessage) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ComTenantMessage [rid=" + rid + "]";
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

	public LkpMessagesType getLkpMessagesType() {
		return lkpMessagesType;
	}

	public void setLkpMessagesType(LkpMessagesType lkpMessagesType) {
		this.lkpMessagesType = lkpMessagesType;
	}

}