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
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;

/** MachineOrder
 * 
 * @author Alaa Himour <ahimour@certacuresolutions.com>
 * @since DES/10/2024 */


/**
 * 
 */
@Entity
@Table(name= "mw_outbound_error_email")
public class OutboundErrorEmailEntity extends BaseAuditableBranchedEntity implements Serializable {
   
	private static final long serialVersionUID = 1L;


    @Id
    @Basic(optional= false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "rid")
    private Long rid;
   
    @Basic(optional = false)
    @Convert(converter = BooleanIntegerConverter.class)
    @Column(name= "is_sent")
    @JsonProperty("is_sent")
    private Boolean isSent;
    
    @Basic(optional = false)
    @Convert(converter = BooleanIntegerConverter.class)
    @Column(name= "is_success")
    @JsonProperty("is_success")
    private Boolean isSuccess;
    
    
    @Basic(optional = false)
    @Convert(converter = BooleanIntegerConverter.class)
    @Column(name= "is_failed")
    @JsonProperty("is_failed")
    private Boolean isFailed;
    
    @Column(name= "body")
    @JsonProperty("body")
    private String body;
    
    @Column(name= "sender")
    @JsonProperty("sender")
    private String sender;
    
    @Column(name= "receiver")
    @JsonProperty("receiver")
    private String receiver;
           
    @Column(name= "notes")
    @Size(max= 1000)
    @JsonProperty("notes")
    private String notes;
    
    @Column(name= "message_text")
    @JsonProperty("message_text")
    private String messageText;
    
    @Column(name= "patient_id")
    @JsonProperty("patientId")
    private String patientId;
    
    @Column(name= "message_control_id")
    @JsonProperty("message_control_id")
    private String MessageControlID;
    
    @Column(name= "adt_operation_type")
    @JsonProperty("adtOperationType")
    private String adtOperationType;
    
    @Column(name= "admission_no")
    @JsonProperty("admissionNo")
    private String admissionNo;
    
    
	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}


	@Override
	public Long getRid() {
		// TODO Auto-generated method stub
		return rid;
	}


	public Boolean getIsSent() {
		return isSent;
	}


	public void setIsSent(Boolean isSent) {
		this.isSent = isSent;
	}


	public Boolean getIsSuccess() {
		return isSuccess;
	}


	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}


	public Boolean getIsFailed() {
		return isFailed;
	}


	public void setIsFailed(Boolean isFailed) {
		this.isFailed = isFailed;
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}


	public String getSender() {
		return sender;
	}


	public void setSender(String sender) {
		this.sender = sender;
	}


	public String getReceiver() {
		return receiver;
	}


	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}


	public void setRid(Long rid) {
		this.rid = rid;
	}


	public String getMessageText() {
		return messageText;
	}


	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}


	public String getMessageControlID() {
		return MessageControlID;
	}


	public void setMessageControlID(String messageControlID) {
		MessageControlID = messageControlID;
	}


	public String getPatientId() {
		// TODO Auto-generated method stub
		return patientId;
	}
	
	public void setPatientId(String strPatientId) {
		 patientId = strPatientId;
	}


	public String getadtOperationType() {
		// TODO Auto-generated method stub
		return adtOperationType;
	}
	
	public void setadtOperationType(String stradtOperationType ) {
		// TODO Auto-generated method stub
		 adtOperationType = stradtOperationType;
	}


	public String getAdmissionNo() {
		// TODO Auto-generated method stub
		return admissionNo;
	}
	
	public void setAdmissionNo(String strAdmissionNo) {

		 admissionNo =  strAdmissionNo;
	}


	

					
   

}