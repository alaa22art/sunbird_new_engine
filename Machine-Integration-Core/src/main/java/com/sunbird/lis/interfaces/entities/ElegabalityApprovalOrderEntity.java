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
 * @since DES/10/2017 */


@Entity
@Table(name= "mw_elegabality_approval_order")
public class ElegabalityApprovalOrderEntity extends BaseAuditableBranchedEntity implements Serializable {
   
	private static final long serialVersionUID = 1L;


    @Id
    @Basic(optional= false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "rid")
    private Long rid;
    
    //1.OrderId 
    @Column(name= "certa_order_id")
    @JsonProperty("OrderId")
    private Long OrderId;
    //2.OrderActionId    
    @Column(name= "certa_action_id")
    @JsonProperty("OrderActionId")
    private Long OrderActionId;
    //3.OrderSource 
    @Column(name= "order_source")
    @JsonProperty("OrderSource")
    private Long OrderSource;
    //4.ItemCode 
    @Column(name= "certa_item_code")
    @JsonProperty("ItemCode")
    @Size(max= 255)
    private String ItemCode;
    //5.ItemCategoryId 
    @Column(name= "item_category_id")
    @JsonProperty("ItemCategoryId")
    private Long ItemCategoryId;
    //6.IsEligible 
    @Basic(optional = false)
    @Convert(converter = BooleanIntegerConverter.class)
    @Column(name= "is_eligible")
    @JsonProperty("IsEligible")
    private Boolean IsEligible;   
    //7.Quantity  
    @Column(name= "quantity")
    private Long Quantity;
    //8.OrderSectionCode  
   
    @Column(name= "order_section_code")
    @Size(max= 255)
    @JsonProperty("OrderSectionCode")
    private String OrderSectionCode;

    //9.Description 
    @Column(name= "description")
    @Size(max= 255)
    @JsonProperty("Description")
    private String Description;
    //10.PatientInfoId 
    @Column(name= "patient_info_Id")
    @JsonProperty("PatientInfoId")
    private Long PatientInfoId;
    //11.PatientInfoCode 
    @Column(name= "patient_info_code")
    @Size(max= 255)
    @JsonProperty("PatientInfoCode")
    private String PatientInfoCode;
    //12.PatientInfoNationalCode 
    @Column(name= "patient_info_national_code")
    @Size(max= 255)
    @JsonProperty("PatientInfoNationalCode")
    private String PatientInfoNationalCode;
    //13.VisitInfoId 
    @Column(name= "visit_info_id")
    @JsonProperty("VisitInfoId")
    private String VisitInfoId;
    //14.VisitInfoPatientType 
    @Column(name= "visit_info_patient_type")
    @JsonProperty("VisitInfoPatientType")
    private Integer VisitInfoPatientType;
    //15.VisitInfoSectionCode 
    @Column(name= "visit_info_section_code")
    @Size(max= 255)
    @JsonProperty("VisitInfoSectionCode")
    private String VisitInfoSectionCode;
    //16.VisitInfoDoctorCode 
    @Column(name= "visit_info_doctor_code")
    @Size(max= 255)
    @JsonProperty("VisitInfoDoctorCode")
    private String VisitInfoDoctorCode;
    //17.visitInfoDoctorName 
    @Column(name= "visit_info_doctor_name")
    @Size(max= 255)
    @JsonProperty("VisitInfoDoctorName")
    private String VisitInfoDoctorName;
    //18.VisitInfoDoctorCode 
    @Column(name= "visit_info_admission_reason_code")
    @Size(max= 255)
    @JsonProperty("VisitInfoAdmissionReasonCode")
    private String VisitInfoAdmissionReasonCode;
    //19.VisitInfoDealingType
    @Column(name= "visit_info_dealing_type")
    @JsonProperty("VisitInfoDealingType")
    private Integer VisitInfoDealingType;
    
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
    
    @Column(name= "json_body")
    @JsonProperty("json_body")
    private String jsonBody;
    
    @Column(name= "json_response")
    @Size(max= 255)
    @JsonProperty("json_response")
    private String jsonResponse;
    

    
    @Basic(optional = false)
    @Convert(converter = BooleanIntegerConverter.class)
    @Column(name= "is_appointment")
    @JsonProperty("isAppointment")
    private Boolean isAppointment;
    
    @Column(name= "ip_printer_address")
    @Size(max= 255)
    @JsonProperty("ipPrinterAddress")
    private String ipPrinterAddress;
    
    @Column(name= "notes")
    @Size(max= 1000)
    @JsonProperty("notes")
    private String notes;
    
    
    public String getIpPrinterAddress() {
		return ipPrinterAddress;
	}


	public void setIpPrinterAddress(String ipPrinterAddress) {
		this.ipPrinterAddress = ipPrinterAddress;
	}

    
    
    public Boolean getIsAppointment() {
		return isAppointment;
	}


	public void setIsAppointment(Boolean isAppointment) {
		this.isAppointment = isAppointment;
	}

	

	public ElegabalityApprovalOrderEntity() {}
    
    
//    	public ElegabalityApprovalEntity(ResultSet resultSet) throws SQLException {
//            this.rid = resultSet.getLong("rid");
//            this.OrderId = resultSet.getString("order_id");
//            this.OrderActionId = resultSet.getString("order_action_id");
//            this.OrderSource = resultSet.getString("order_source");
//            this.ItemCode = resultSet.getString("item_code");
//            this.ItemCategoryId = resultSet.getString("item_category_id");
//            this.IsEligible = resultSet.getString("is_eligible");
//            this.Quantity = resultSet.getString("quantity");
//            this.OrderSectionCode = resultSet.getString("order_section_code");
//            this.Description = resultSet.getString("description");
//            this.PatientInfoId = resultSet.getString("patient_info_id");
//            this.PatientInfoCode = resultSet.getString("patient_info_code");
//            this.PatientInfoNationalCode = resultSet.getString("patient_info_national_code");
//            this.VisitInfoId = resultSet.getString("visit_info_id");
//            this.VisitInfoPatientType = resultSet.getString("visit_info_patient_type");
//            this.VisitInfoSectionCode = resultSet.getString("visit_info_section_code");
//            this.VisitInfoDoctorCode = resultSet.getString("visit_info_doctor_code");
//            this.visitInfoDoctorName = resultSet.getString("visit_info_doctor_name");
//            this.VisitInfoAdmissionReasonCode = resultSet.getString("visit_info_admission_reason_code");
//            this.VisitInfoDealingType = resultSet.getString("visit_info_dealing_type");
//            this.IsSent = resultSet.getString("is_sent");
//            this.IsSuccess = resultSet.getString("is_success");
//        }
	

	@Override
    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid= rid;
    }

  

    public Long getOrderActionId() {
        return OrderActionId;
    }

    public Long getOrderSource() {
        return OrderSource;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public Long getItemCategoryId() {
        return ItemCategoryId;
    }

    public Boolean getIsEligible() {
        return IsEligible;
    }

    public Long getQuantity() {
        return Quantity;
    }

    public String getOrderSectionCode() {
        return OrderSectionCode;
    }

    public String getDescription() {
        return Description;
    }

    public Long getPatientInfoId() {
        return PatientInfoId;
    }

    public String getPatientInfoCode() {
        return PatientInfoCode;
    }

    public String getPatientInfoNationalCode() {
        return PatientInfoNationalCode;
    }

    public String getVisitInfoId() {
        return VisitInfoId;
    }

    public Integer getVisitInfoPatientType() {
        return VisitInfoPatientType;
    }

    public String getVisitInfoSectionCode() {
        return VisitInfoSectionCode;
    }

    public String getVisitInfoDoctorCode() {
        return VisitInfoDoctorCode;
    }

    public String getVisitInfoDoctorName() {
        return VisitInfoDoctorName;
    }

    public String getVisitInfoAdmissionReasonCode() {
        return VisitInfoAdmissionReasonCode;
    }

    public Integer getVisitInfoDealingType() {
        return VisitInfoDealingType;
    }

   

    public void setOrderActionId(Long OrderActionId) {
        this.OrderActionId= OrderActionId;
    }

    public void setOrderSource(Long OrderSource) {
        this.OrderSource= OrderSource;
    }

    public void setItemCode(String ItemCode) {
        this.ItemCode= ItemCode;
    }

    public void setItemCategoryId(Long ItemCategoryId) {
        this.ItemCategoryId= ItemCategoryId;
    }

    public void setIsEligible(Boolean IsEligible) {
        this.IsEligible= IsEligible;
    }

    public void setQuantity(Long Quantity) {
        this.Quantity= Quantity;
    }

    public void setOrderSectionCode(String OrderSectionCode) {
        this.OrderSectionCode= OrderSectionCode;
    }

    public void setDescription(String Description) {
    	this.Description= Description;
    }

    public void setPatientInfoId(Long PatientInfoId) {
    	this.PatientInfoId= PatientInfoId;
    }

    public void setPatientInfoCode(String PatientInfoCode) {
    	this.PatientInfoCode= PatientInfoCode;
    }

    public void setPatientInfoNationalCode(String PatientInfoNationalCode) {
    	this.PatientInfoNationalCode= PatientInfoNationalCode;
    }

    public void setVisitInfoId(String VisitInfoId) {
    	this.VisitInfoId= VisitInfoId;
    }

    public void setVisitInfoPatientType(Integer VisitInfoPatientType) {
    	this.VisitInfoPatientType= VisitInfoPatientType;
    }

    public void setVisitInfoSectionCode(String VisitInfoSectionCode) {
    	this.VisitInfoSectionCode= VisitInfoSectionCode;
    }

    public void setVisitInfoDoctorCode(String VisitInfoDoctorCode) {
    	this.VisitInfoDoctorCode= VisitInfoDoctorCode;
    }

    public void setVisitInfoDoctorName(String VisitInfoDoctorName) {
        this.VisitInfoDoctorName= VisitInfoDoctorName;
    }

    public void setVisitInfoAdmissionReasonCode(String VisitInfoAdmissionReasonCode) {
    	this.VisitInfoAdmissionReasonCode= VisitInfoAdmissionReasonCode;
    }

    public void setVisitInfoDealingType(Integer VisitInfoDealingType) {
    	this.VisitInfoDealingType= VisitInfoDealingType;
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


	public Long getOrderId() {
		return OrderId;
	}


	public void setOrderId(Long orderId) {
		OrderId = orderId;
	}


	public Boolean getIsFailed() {
		return isFailed;
	}


	public void setIsFailed(Boolean isFailed) {
		this.isFailed = isFailed;
	}


	public String getJsonBody() {
		return jsonBody;
	}


	public void setJsonBody(String jsonBody) {
		this.jsonBody = jsonBody;
	}


	public String getJsonResponse() {
		return jsonResponse;
	}


	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}
	
	
	@Override
	public String toString() {
		
		String JsonString = "{"
			    + "orderId " +":"+ OrderId+","
			    +"orderActionId "+":"+ OrderActionId+","
			    +"orderSource "+":"+ OrderSource+","
			    +"itemCode "+":"+ "\""+ ItemCode +"\"" +","
			    +"itemCategoryId "+":"+ ItemCategoryId+","
			    +"isEligible "+":"+ IsEligible +","
			    +"quantity "+":"+ Quantity +","
			    +"orderSectionCode "+":"+ "\""+ OrderSectionCode +"\""+","
			    +"description "+":"+ "\""+ Description +"\""+","
			    +"patientInfoId "+":"+ PatientInfoId+","
			    +"patientInfoCode "+":"+ "\""+ PatientInfoCode + "\""+ ","
			    +"patientInfoNationalCode "+":" + "\""+PatientInfoNationalCode + "\""+","
			    +"visitInfoId "+":"+  "\"" + VisitInfoId+ "\""+ ","
			    +"visitInfoPatientType "+":" + "\""+ VisitInfoPatientType  + "\""+ ","
			    +"visitInfoSectionCode "+":" + "\""+ VisitInfoSectionCode + "\""+ ","
			    +"visitInfoDoctorCode "+":"+ "\""+ VisitInfoDoctorCode + "\""+ ","
			    +"visitInfoDoctorName "+":"+"\""+ VisitInfoDoctorName +"\"" +","
			    +"visitInfoAdmissionReasonCode "+":"+"\""+ VisitInfoAdmissionReasonCode +"\""+","
			    +"visitInfoDealingType "+":"+  VisitInfoDealingType+","
			    +"isAppointment "+":"+ isAppointment+ ","
			    +"ipPrinterAddress "+":"+"\"" +ipPrinterAddress +"\""
			+"}"; 
		
		
		
		return JsonString;
	}


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}


	

					
   

}