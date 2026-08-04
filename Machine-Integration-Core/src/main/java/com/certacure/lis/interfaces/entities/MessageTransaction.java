/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.certacure.lis.interfaces.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
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

import org.hibernate.envers.Audited;
import org.springframework.boot.context.properties.bind.DefaultValue;

import com.certacure.core.base.entity.BaseAuditableBranchedEntity;
import com.certacure.core.common.data.model.converter.BooleanIntegerConverter;
import com.certacure.core.common.util.SecurityUtil;

/** MachineQuery */
@Entity
@Table(name= "mw_message_transaction")
@Audited

public class MessageTransaction extends BaseAuditableBranchedEntity implements Serializable {

    private static final long serialVersionUID= 1L;

    @Column(name= "ack_message_text")
    private String ackMessageText;

    @Column(name= "adt_operation_type")
    private String adtOperationType;

    @Column(name= "barcode")
    private String barcode;

    @Column(name= "is_processed")
    @Convert(converter= BooleanIntegerConverter.class)
    private Boolean isProcessed;

    @Column(name= "is_sent")
    @Convert(converter= BooleanIntegerConverter.class)
    private Boolean isSent;

    @Column(name= "is_success")
    @Convert(converter= BooleanIntegerConverter.class)
    private Boolean isSuccess;

    @Column(name= "is_validated")
    @Convert(converter= BooleanIntegerConverter.class)
    private Boolean isValidated;

    @JoinColumn(name= "machine_id", referencedColumnName= "rid")
    @ManyToOne(fetch= FetchType.LAZY)
    private Machine machine;

    @Column(name= "message_body")
    private String messageBody;

    @Column(name= "message_control_id")
    private String messageControlID;

    @JoinColumn(name= "message_direction_ID", referencedColumnName= "rid")
    @ManyToOne(fetch= FetchType.LAZY)
    private LkpMessageTransactionDirection messageDirection;
    
    @JoinColumn(name= "message_type_id")
    @ManyToOne(fetch= FetchType.LAZY)
    private LkpMessageTransactionType messageType;
    
    @Column(name= "nationall_id")
    private String nationalID;
	@Column(name= "notes")
    private String notes;

	@Column(name= "patient_id")
    private String patientID;


    @Column(name= "response")
    private String response;
    @Id
    @Basic(optional= false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name= "rid")

    private Long rid;
    public MessageTransaction() {}
    public MessageTransaction(Long rid) {
        this.rid= rid;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessageTransaction other= (MessageTransaction) obj;
        if (rid == null) {
            if (other.getRid() != null)
                return false;
        } else if (!rid.equals(other.getRid()))
            return false;
        return true;
    }
    public String getAckmessageText() {
        return ackMessageText;
    }
    public String getAdtOperationType() {
            return adtOperationType;
    }
    public String getBarcode() {
        return barcode;
    }
    public Boolean getIsProcessed() {
		return isProcessed;
	}
    public Boolean getIsSent() {
        return isSent;
    }
    public Boolean getIsSuccess() {
        return isSuccess;
    }
    public Boolean getIsValidated() {
        return isValidated;
    }

    public Machine getMachine() {
        return machine;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getMessageControlID() {
        return messageControlID;
    }

    public LkpMessageTransactionDirection getMessageDirection() {
        return messageDirection;
    }

    public LkpMessageTransactionType getMessageType() {
        return messageType;
    }

    public String getNationalID() {
            return nationalID;
    }

    public String getNotes() {
        return notes;
    }

    public String getPatientID() {
            return patientID;
    }

    public String getResponse() {
            return response;
    }

    @Override
    public Long getRid() {
        return rid;
    }

    @Override
    public int hashCode() {
        final Integer prime= 31;
        Integer result= 1;
        result= prime * result + ((rid == null) ? 0 : rid.hashCode());
        return result;
    }

    @Override
    protected void populateAudit() {

        if (getCreationDate() == null) {
            setCreationDate(new Date());

            if (getCreatedBy() == null) {
                setCreatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());

            }
        } else {
            setUpdateDate(new Date());
            if (getUpdatedBy() == null) {
                setUpdatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());
            }
        }
    }

    public void setAckMessageText(String ack_message_text) {
        this.ackMessageText= ack_message_text;
    }

    public void setAdtOperationType(String typeName) {
        this.adtOperationType= typeName;
    }

    public void setBarcode(String barcode) {
        this.barcode= barcode;
    }

    public void setIsProcessed(Boolean isprocessed) {
		this.isProcessed = isprocessed;
	}

    public void setIsSent(Boolean isSent) {
        this.isSent= isSent;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess= isSuccess;
    }

    public void setIsValidated(Boolean isValidated) {
        this.isValidated= isValidated;
    }

    public void setMachine(Machine machine) {
        this.machine= machine;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody= messageBody;
    }

    public void setMessageControlID(String message_control_id) {
        this.messageControlID= message_control_id;
    }

    public void setMessageDirection(LkpMessageTransactionDirection messageDirection) {
        this.messageDirection= messageDirection;
    }

    public void setMessageType(LkpMessageTransactionType messageType) {
        this.messageType= messageType;
    }

    public void setNationalID(String nationalID) {
        this.nationalID= nationalID;
    }

    public void setNotes(String notes) {
        this.notes= notes;
    }

    public void setPatientID(String patientID) {
        this.patientID= patientID;
    }

    public void setResponse(String response) {
        this.response= response;
    }

    public void setRid(Long rid) {
        this.rid= rid;
    }

    @Override
    public String toString() {
        return "MachineQuery [rid=" + rid + "]";
    }

}
