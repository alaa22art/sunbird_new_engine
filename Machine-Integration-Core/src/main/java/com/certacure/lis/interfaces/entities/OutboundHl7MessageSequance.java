package com.certacure.lis.interfaces.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.certacure.core.base.entity.BaseAuditableBranchedEntity;
import com.certacure.core.base.entity.BaseAuditableTenantedEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LkpGender.java
 *
 *
 */
@Entity
public class OutboundHl7MessageSequance extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hl7_outbound_message_sequance")
	@Basic(optional = false)
	@SequenceGenerator(name = "hl7_outbound_message_sequance", sequenceName = "hl7_outbound_message_sequance", allocationSize = 1)
	private Long rid;
	
	@Override
	public Long getRid() {
		return this.rid;
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
	public String toString() {
		return "OutboundHl7MessageSequance [rid=" + rid + "]";
	}

	

}
