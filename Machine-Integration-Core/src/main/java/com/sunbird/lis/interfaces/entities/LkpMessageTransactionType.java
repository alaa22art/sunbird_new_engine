package com.sunbird.lis.interfaces.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.sunbird.core.base.entity.BaseAuditableEntity;

/**
 * .java
 * 
 * @author Alaa Himour <Ahimour@optimizasolutions.com>
 * @since FEB/11/2020
 **/
@Entity
@Audited
@Table(name = "lkp_message_transaction_type")
public class LkpMessageTransactionType extends BaseAuditableEntity {

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

	@NotNull
	@Size(min = 1, max = 4000)
	@Column(name = "name")
	private String name;

	@Size(max = 4000)
	@Column(name = "description")
	private String description;

	public LkpMessageTransactionType() {
	}

	public LkpMessageTransactionType(String strName) {

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
		LkpMessageTransactionType other = (LkpMessageTransactionType) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LkpMessageTransactionDirection [rid=" + rid + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
