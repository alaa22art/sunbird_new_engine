package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;
import com.sunbird.core.base.entity.BaseAuditableTenantedEntity;
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;

/**
 * LkpGender.java
 *
 *
 */
@Entity
@Table(name = "mw_order_coverage")
public class OrderCoverageEntity extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "vista_order_id")
	private String vistaOrderId;

	@Column(name = "mrn")
	private String MRN;

	@Column(name = "has_coverage")
	private boolean hasCoverage;

	@Column(name = "certa_order_id")
	private Long certaOrderId;

	@Column(name = "certa_action_id")
	private Long certaActionId;

	@Column(name = "item_code ")
	private Long itemCode;

	@Column(name = "json_body")
	private String JSONBody;
	
	@Basic(optional = false)
	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "is_sent")
	@JsonProperty("IsSent")
	private Boolean IsSent;
	
	@Basic(optional = false)
	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "is_success")
	@JsonProperty("IsSuccess")
	private Boolean IsSuccess;

	public OrderCoverageEntity() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getVistaOrderId() {
		return vistaOrderId;
	}

	public void setVistaOrderId(String vistaOrderId) {
		this.vistaOrderId = vistaOrderId;
	}

	public String getMRN() {
		return MRN;
	}

	public void setMRN(String mRN) {
		MRN = mRN;
	}

	public boolean getHasCoverage() {
		return hasCoverage;
	}

	public void setHasCoverage(boolean hasCoverage) {
		this.hasCoverage = hasCoverage;
	}

	public Long getCertaOrderId() {
		return certaOrderId;
	}

	public void setCertaOrderId(Long certaOrderId) {
		this.certaOrderId = certaOrderId;
	}

	public Long getCertaActionId() {
		return certaActionId;
	}

	public void setCertaActionId(Long certaActionId) {
		this.certaActionId = certaActionId;
	}

	public Long getItemCode() {
		return itemCode;
	}

	public void setItemCode(Long itemCode) {
		this.itemCode = itemCode;
	}

	public String getJSONBody() {
		return JSONBody;
	}

	public void setJSONBody(String JSONBody) {
		this.JSONBody = JSONBody;
	}

	public Boolean getIsSent() {
		return IsSent;
	}

	public void setIsSent(Boolean isSent) {
		IsSent = isSent;
	}

	public Boolean getIsSuccess() {
		return IsSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		IsSuccess = isSuccess;
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
		return "OrderCoverageEntity [rid=" + rid + "]";
	}

}
