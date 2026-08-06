package com.certacure.lis.interfaces.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "mw_hl7_message_sequance")
public class AckMessageSequance extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Column(name = "eligibility_transaction_id")
	private Long eligibilityTransactionID;
	
	@Column(name = "transaction_type")
	@Size(max= 255)
	private String transactionType;
	
	public Long getEligibilityTransactionID() {
		return eligibilityTransactionID;
	}

	public void setEligibilityTransactionID(Long eligibilityTransactionID) {
		this.eligibilityTransactionID = eligibilityTransactionID;
		this.transactionType = "APPOINTMENT";
	}

	public AckMessageSequance() {
	}

	public AckMessageSequance(Long rid2) {
		eligibilityTransactionID = rid2;
	}

	public AckMessageSequance(Long eligibilityOrderId, String transactionType) {
		this.eligibilityTransactionID = eligibilityOrderId;
		this.transactionType = transactionType;
	}

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
		return "AckMessageSequance [rid=" + rid + "]";
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

}
