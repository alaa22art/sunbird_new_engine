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

import com.certacure.core.base.entity.BaseAuditableEntity;

@Entity
@Table(name = "lkp_pcr_realtime_worklist_status")
public class LkpPCRRealRimeWorkListStatus extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Size(max = 255)
	@Column(name = "code")
	private String code;

	@Size(max = 255)
	@Column(name = "name")
	private String name;

	public LkpPCRRealRimeWorkListStatus() {
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

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

}
