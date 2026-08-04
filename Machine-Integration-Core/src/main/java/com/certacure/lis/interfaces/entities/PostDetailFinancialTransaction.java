package com.certacure.lis.interfaces.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Calendar;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.certacure.core.base.entity.BaseAuditableBranchedEntity;
import com.certacure.core.common.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Entity implementation class for Entity: PostDetailFinancialTransaction */
@Entity
@Table(name= "mw_post_detail_financial_transaction")
public class PostDetailFinancialTransaction extends BaseAuditableBranchedEntity
    implements Serializable {
    private static final long serialVersionUID= 1L;

    @Id
    @Basic(optional= false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name= "rid")

    private Long rid;

    @Override
    public Long getRid() {
        return rid;
    }

    @Column(name= "vista_order_id")
    private String vistaOrderID;

    @Column(name= "batch_ref_number")
    private String batchRefNumber;

    @Column(name= "posting_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date postingDate;

    @Column(name= "type")
    private String type;

    @Column(name= "code")
    private String code;

    @Column(name= "description")
    private String description;

    @Column(name= "order_by_id")
    private String orderByID;
    
    @Column(name= "transaction_type")
    private String transactionType;

    @Column(name= "serv_section")
    private String servSection;

    @Column(name= "expected_start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedStartDate;

    @Column(name= "expected_end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedEndDate;
    
    @Column(name= "rcm_order_id")
    private String RCMOrderID;

    @Column(name= "rcm_order_action_id")
    private String RCMOrderActionID;

    @Column(name= "response_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date responseDate;

    @Column(name= "rcm_item_code")
    private String RCMItemCode;
    
    @Column(name= "encounter")
    private String encounter;
    
    @Column(name= "appointment_id")
    private String appointmentID;
    
    @Column(name= "attending_doctor_id")
    private String attendingDoctorID;
    
    @Column(name= "assigned_patient_location")
    private String assignedPatientLocation;
    
    @Column(name= "order_by_doctor_id")
    private String orderByDoctorId;

	@Column(name = "transaction_date")
	private String transactionDate;
	
	@Column(name = "transaction_posting_date")
	private String transactionPostingDate;
	
	
	@Column(name = "group_reference")
	private String groupReference;

	public String getGroupReference() {
		return groupReference;
	}

	public void setGroupReference(String groupReference) {
		this.groupReference = groupReference;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionPostingDate() {
		return transactionPostingDate;
	}

	public void setTransactionPostingDate(String transactionPostingDate) {
		this.transactionPostingDate = transactionPostingDate;
	}

	public String getAssignedPatientLocation() {
		return assignedPatientLocation;
	}

	public void setAssignedPatientLocation(String assignedPatientLocation) {
		this.assignedPatientLocation = assignedPatientLocation;
	}

	public String getAttendingDoctorID() {
		return attendingDoctorID;
	}

	public void setAttendingDoctorID(String attendingDoctorID) {
		this.attendingDoctorID = attendingDoctorID;
	}

	public String getRCMOrderID() {
        return RCMOrderID;
    }

    public String getRCMOrderActionID() {
        return RCMOrderActionID;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public String getRCMItemCode() {
        return RCMItemCode;
    }

    public void setRCMOrderID(String rCMOrderID) {
        RCMOrderID= rCMOrderID;
    }

    public void setRCMOrderActionID(String rCMOrderActionID) {
        RCMOrderActionID= rCMOrderActionID;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate= responseDate;
    }

    public void setRCMItemCode(String rCMItemCode) {
        RCMItemCode= rCMItemCode;
    }

    public String getVistaOrderID() {
        return vistaOrderID;
    }

    public void setVistaOrderID(String vistaOrderID) {
        this.vistaOrderID = vistaOrderID;
    }

    public String getBatchRefNumber() {
        return batchRefNumber;
    }

    public void setBatchRefNumber(String batchRefNumber) {
        this.batchRefNumber= batchRefNumber;
    }

    public Date getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(Date date) {
        this.postingDate= date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type= type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code= code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description= description;
    }

    public String getOrderByID() {
        return orderByID;
    }

    public void setOrder_by_id(String orderByID) {
        this.orderByID= orderByID;
    }

    public String getServSection() {
        return servSection;
    }

    public void setServSection(String servSection) {
        this.servSection= servSection;
    }

    public Date getExpectedStartDate() {
        return expectedStartDate;
    }

    public void setExpectedStartDate(Date expected_start_date) {
        this.expectedStartDate= expected_start_date;
    }

    public Date getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(Date expectedEndDate) {
        this.expectedEndDate= expectedEndDate;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public void setRid(Long rid) {
        this.rid= rid;
    }
    
    public void setTransactionType(String fT1_6_TransactionType) {
    	transactionType = fT1_6_TransactionType;
		
	}
    
    public void getTransactionType(String fT1_6_TransactionType) {
		// TODO Auto-generated method stub
		
	}

    public String getEncounter() {
        return encounter;
    }

    public void setEncounter(String encounter) {
        this.encounter= encounter;
    }
    
    public String getAppointmentID() {
		return appointmentID;
	}

	public void setAppointmentID(String appointmentID) 
	{
		this.appointmentID = appointmentID;
	}
	
	public String getOrderByDoctorId() {
		return orderByDoctorId;
	}

	public void setOrderByDoctorId(String orderByDoctorId) {
		this.orderByDoctorId = orderByDoctorId;
	}

    @Override
    public int hashCode() {
        final int prime= 31;
        int result= 1;
        result= prime * result + ((rid == null) ? 0 : rid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
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
        return "PostDetailFinancialTransaction [rid=" + rid + "]";
    }

    @Override
    protected void populateAudit() {
        setCreationDate(new Date());

        if (getCreatedBy() == null) {
            setCreatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());

        } else {
            setUpdateDate(new Date());
            if (getUpdatedBy() == null) {
                setUpdatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());
            }
        }
    }

	

}
