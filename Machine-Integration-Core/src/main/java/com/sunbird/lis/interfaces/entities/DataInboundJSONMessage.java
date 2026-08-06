/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.springframework.beans.factory.annotation.Value;

import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;
import com.sunbird.core.base.entity.BaseAuditableEntity;
import com.sunbird.core.base.entity.BaseAuditableTenantedEntity;
import com.sunbird.core.base.entity.BaseEntity;

/**
 * 
 * 
 */
@Entity
@Table(name = "data_inbound_json_message")
@Audited
public class DataInboundJSONMessage extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long rid;

	@Column(name = "message_body", length = 255)
	private String messageBody;

	@Column(name = "message_controller_id")
	private String messageControllerId;

	@Column(length = 255)
	private String priority;

	@Column(length = 255)
	private String source;
	
	@Column(length = 255)
    private Long messageType;
	
	
    @Column( name = "is_sent")
    private int isSent;
    
    
 
	
	public int getIsSent() {
        return isSent;
    }

    public int getIsSuccess() {
        return isSuccess;
    }

    public void setIsSent(int isSent) {
        this.isSent= isSent;
    }

    public void setIsSuccess(int isSuccess) {
        this.isSuccess= isSuccess;
    }

    @Column(name = "is_success")
    private int isSuccess;


	public DataInboundJSONMessage() {
	}
	
	public Long getMessageType() {
        return messageType;
    }

    public void setMessageType(Long messageType) {
        this.messageType= messageType;
    }


	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getMessageBody() {
		return this.messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getMessageControllerId() {
		return this.messageControllerId;
	}

	public void setMessageControllerId(String messageControllerId) {
		this.messageControllerId = messageControllerId;
	}

	public String getPriority() {
		return this.priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
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
		DataInboundJSONMessage other = (DataInboundJSONMessage) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataInboundJSONmessage [rid=" + rid + "]";
	}

}
