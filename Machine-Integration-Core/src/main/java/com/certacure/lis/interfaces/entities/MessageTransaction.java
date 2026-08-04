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

    @Id
    @Basic(optional= false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name= "rid")

    private Long rid;

    @Column(name= "barcode")
    private String barcode;

    @Column(name= "message_body")
    private String messageBody;

    @Column(name= "notes")
    private String notes;

    @Column(name= "message_control_id")
    private String messageControlID;

    @JoinColumn(name= "message_type_id")
    @ManyToOne(fetch= FetchType.LAZY)
    private LkpMessageTransactionType messageType;

    @JoinColumn(name= "machine_id", referencedColumnName= "rid")
    @ManyToOne(fetch= FetchType.LAZY)
    private Machine machine;

    @JoinColumn(name= "message_direction_ID", referencedColumnName= "rid")
    @ManyToOne(fetch= FetchType.LAZY)
    private LkpMessageTransactionDirection messageDirection;

    @Column(name= "is_validated")
    @Convert(converter= BooleanIntegerConverter.class)
    private Boolean isValidated;

    @Column(name= "is_succuss")
    @Convert(converter= BooleanIntegerConverter.class)
    private Boolean isSuccuss;

    @Column(name= "is_sent")
    @Convert(converter= BooleanIntegerConverter.class)
    private Boolean isSent;
    
    @Column(name= "ack_message_text")
    private String ackMessageText;


    @Column(name= "response")
    private String response;
    @Column(name= "patient_id")
    private String patientID;
    @Column(name= "nationall_id")
    private String nationalID;
    @Column(name= "adt_operation_type")
    private String adtOperationType;
    public String getAdtOperationType() {
            return adtOperationType;
    }
    public void setAdtOperationType(String typeName) {
        this.adtOperationType= typeName;
    }
    public String getResponse() {
            return response;
    }
    public void setResponse(String response) {
        this.response= response;
    }
    public String getPatientID() {
            return patientID;
    }
    public void setPatientID(String patientID) {
        this.patientID= patientID;
    }
    public String getNationalID() {
            return nationalID;
    }
    public void setNationalID(String nationalID) {
        this.nationalID= nationalID;
    }

    public String getAckmessageText() {
        return ackMessageText;
    }

    public void setAckMessageText(String ack_message_text) {
        this.ackMessageText= ack_message_text;
    }

    public Boolean getIsSent() {
        return isSent;
    }

    public void setIsSent(Boolean isSent) {
        this.isSent= isSent;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine= machine;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode= barcode;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody= messageBody;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes= notes;
    }

    public LkpMessageTransactionType getMessageType() {
        return messageType;
    }

    public void setMessageType(LkpMessageTransactionType messageType) {
        this.messageType= messageType;
    }

    public LkpMessageTransactionDirection getMessageDirection() {
        return messageDirection;
    }

    public void setMessageDirection(LkpMessageTransactionDirection messageDirection) {
        this.messageDirection= messageDirection;
    }

    public Boolean getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(Boolean isValidated) {
        this.isValidated= isValidated;
    }

    public Boolean getIsSuccuss() {
        return isSuccuss;
    }

    public void setIsSuccuss(Boolean isSuccuss) {
        this.isSuccuss= isSuccuss;
    }

    public MessageTransaction() {}

    public MessageTransaction(Long rid) {
        this.rid= rid;
    }

    @Override
    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid= rid;
    }

    public String getMessageControlID() {
        return messageControlID;
    }

    public void setMessageControlID(String message_control_id) {
        this.messageControlID= message_control_id;
    }

    @Override
    public int hashCode() {
        final Integer prime= 31;
        Integer result= 1;
        result= prime * result + ((rid == null) ? 0 : rid.hashCode());
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
        MessageTransaction other= (MessageTransaction) obj;
        if (rid == null) {
            if (other.getRid() != null)
                return false;
        } else if (!rid.equals(other.getRid()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MachineQuery [rid=" + rid + "]";
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

}
