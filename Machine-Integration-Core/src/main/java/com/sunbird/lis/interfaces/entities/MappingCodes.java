/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;
import com.sunbird.core.base.entity.BaseAuditableTenantedEntity;
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;



@Entity
@Table(name= "mw_mapping_codes")
public class MappingCodes extends BaseAuditableTenantedEntity implements Serializable {
   

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;



    @Id
    @Basic(optional= false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name= "rid")
    private Long rid;
    

    @Column(name= "certa_item_code")
    @JsonProperty("certa_item_code")
    @Size(max= 255)
    private String certaItemCode;
    
    @Column(name= "vista_item_code")
    @JsonProperty("requesterTestCode")
    @Size(max= 255)
    private String vistaItemCode;
    
    
    @Column(name= "section")
    @JsonProperty("section")
    @Size(max= 255)
    private String section;
    
    @Column(name= "name")
    @JsonProperty("name")
    @Size(max= 500)
    private String name;


	public String getSection() {
		return section;
	}


	public void setSection(String section) {
		this.section = section;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}






	public MappingCodes() {}






	public String getCertaItemCode() {
		return certaItemCode;
	}






	public void setCertaItemCode(String certaItemCode) {
		this.certaItemCode = certaItemCode;
	}






	public String getVistaItemCode() {
		return vistaItemCode;
	}






	public void setVistaItemCode(String vistaItemCode) {
		this.vistaItemCode = vistaItemCode;
	}






	public Long getRid() {
		return rid;
	}
    
    

    
    

}