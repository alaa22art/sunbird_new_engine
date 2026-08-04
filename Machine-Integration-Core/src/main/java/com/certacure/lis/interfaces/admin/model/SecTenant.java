package com.certacure.lis.interfaces.admin.model;

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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.certacure.core.base.entity.BaseAuditableEntity;
import com.certacure.core.common.data.model.converter.BooleanIntegerConverter;
import com.certacure.lis.interfaces.entities.LkpCity;
import com.certacure.lis.interfaces.entities.LkpCountry;

/**
 * SecTenant.class
 * 
 * @since Aug/3/2017
 **/
@Entity
@Audited
@Table(name = "sec_tenant")
public class SecTenant extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Column(name = "phone_no")
	@Size(min = 1, max = 255)
	private String phoneNo;

	@NotNull
	@Column(name = "name")
	private String name;

	@NotNull
	@Column(name = "mobile_pattern")
	@Size(min = 1, max = 255)
	private String mobilePattern;

	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "code", unique = true)
	private String code;

	@NotNull
	@Column(name = "address")
	private String address;

	@Column(name = "website")
	@Size(max = 255)
	private String website;

	@NotNull
	@Column(name = "email", unique = true)
	@Size(max = 255)
	@Email
	private String email;

	@Column(name = "logo")
	private byte[] logo;

	@Column(name = "is_active")
	@NotNull
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id")
	private LkpCountry country;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id")
	private LkpCity city;

	public SecTenant() {

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
		SecTenant other = (SecTenant) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SecTenant [rid=" + rid + "]";
	}

	public String getMobilePattern() {
		return mobilePattern;
	}

	public void setMobilePattern(String mobilePattern) {
		this.mobilePattern = mobilePattern;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public LkpCountry getCountry() {
		return country;
	}

	public void setCountry(LkpCountry country) {
		this.country = country;
	}

	public LkpCity getCity() {
		return city;
	}

	public void setCity(LkpCity city) {
		this.city = city;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
