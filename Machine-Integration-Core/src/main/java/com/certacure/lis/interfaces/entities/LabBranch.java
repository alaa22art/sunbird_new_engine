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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.certacure.core.base.entity.BaseAuditableTenantedEntity;
import com.certacure.core.common.annotation.MapNotNull;
import com.certacure.core.common.data.model.TransField;
import com.certacure.core.common.data.model.converter.BooleanIntegerConverter;
import com.certacure.core.common.data.model.converter.TransFieldAttConverter;

/**
 *
 */
@Entity
@Table(name = "lab_branch")
@Audited
public class LabBranch extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@MapNotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "name")
	private TransField name;

	@Column(name = "phone_no")
	@NotNull
	private String phoneNo;

	@Column(name = "mobile_pattern")
	@Size(min = 0, max = 255)
	private String mobilePattern;

	@MapNotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "address")
	private TransField address;

	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "is_active")
	@NotNull
	private Boolean isActive;

	@Size(min = 1, max = 3)
	@Column(name = "code")
	@NotNull
	private String code;

	@JoinColumn(name = "city_id")
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private LkpCity city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id")

	@NotNull
	private LkpCountry country;

	@Column(name = "integration_url")
	private String integrationUrl;

	@JsonIgnore
	@Column(name = "integration_token")
	private String integrationToken;

	public String getIntegrationUrl() {
		return integrationUrl;
	}

	public void setIntegrationUrl(String integrationUrl) {
		this.integrationUrl = integrationUrl;
	}

	public LkpCountry getCountry() {
		return country;
	}

	public void setCountry(LkpCountry country) {
		this.country = country;
	}

	public String getMobilePattern() {
		return mobilePattern;
	}

	public void setMobilePattern(String mobilePattern) {
		this.mobilePattern = mobilePattern;
	}

	public String getIntegrationToken() {
		return integrationToken;
	}

	public void setIntegrationToken(String integrationToken) {
		this.integrationToken = integrationToken;
	}

	public LabBranch() {
	}

	public LabBranch(Long rid) {
		this.rid = rid;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public TransField getName() {
		return name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public TransField getAddress() {
		return address;
	}

	public void setAddress(TransField address) {
		this.address = address;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LkpCity getCity() {
		return city;
	}

	public void setCity(LkpCity city) {
		this.city = city;
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
		LabBranch other = (LabBranch) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LabBranch [rid=" + rid + "]";
	}

}
