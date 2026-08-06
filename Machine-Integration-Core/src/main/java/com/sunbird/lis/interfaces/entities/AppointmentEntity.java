package com.sunbird.lis.interfaces.entities;











import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;

/*
 * 
 * Appointment JSON will receive from Certacure Engine Should Re send this JSON to CL   : 
{

"AppointmentId": "21212121", 

"MRN": "1212121" ,

"PatientCode":  "21212",

"ResourceId":  "22",

"AppointmentDate":  "2023-12-31 16:20:00",

"StartTime":  "03:00",

"EndTime": "04:00",

"SpecialityId":  "21451", 

"AppointmnetStatus": "1", //OPEN = 1, OutPatient = CLOSE

"HasCoverage": "1",  // AA =1 , AR =2

"PatientType":"1"  , //InPatient = 1, OutPatient = 2
}
 */
@Entity
@Table(name= "mw_appointment")
public class AppointmentEntity extends BaseAuditableBranchedEntity implements Serializable
{        
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
    @Id
    @Basic(optional= false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name= "rid")
    private Long rid;

    @Column(name= "mrn")
    @JsonProperty("MRN")
	private String MRN;
        
    @Column(name= "appointment_id")
    @JsonProperty("AppointmentId")
    private String AppointmentId;
    
    @Column(name= "national_id")
    @JsonProperty("NationalId")
    private String NationalId;
    
    @Column(name= "patient_code")
    @JsonProperty("PatientCode")
    private String PatientCode;
    
    @Column(name= "resource_id")
    @JsonProperty("ResourceId")
    private String ResourceId;
    
    @Column(name= "appointment_date")
    @JsonProperty("AppointmentDate")
    private String AppointmentDate;
    
    @Column(name= "start_time")
    @JsonProperty("StartTime")
    private String StartTime;
    
    @Column(name= "end_time")
    @JsonProperty("EndTime")
    private String EndTime;
    
    @Column(name= "speciality_id")
    @JsonProperty("SpecialityId")
    private String SpecialityId;
    
    @Column(name= "appointmnet_status")
    @JsonProperty("AppointmnetStatus")
    private String AppointmnetStatus; //=> Fixed CR
    
    @Column(name= "has_coverage")
    @JsonProperty("HasCoverage")
    private boolean HasCoverage; //=> AA = 1, AR = 2
    
    @Column(name= "operation_type")
    @JsonProperty("OperationType")
    private String OperationType; //I insert , U update 
    
    @Basic(optional = false)
    @Convert(converter = BooleanIntegerConverter.class)
    @Column(name= "is_sent")
    @JsonProperty("IsSent")
    private Boolean IsSent;
    
    @Basic(optional = false)
    @Convert(converter = BooleanIntegerConverter.class)
    @Column(name= "is_success")
    @JsonProperty("IsSuccess")
    private Boolean IsSuccess;
    
    
    public  AppointmentEntity() {
	}

        
	@Override
	public Long getRid() {
		// TODO Auto-generated method stub
		return rid;
	}


	public String getMRN() {
		return MRN;
	}


	public void setMRN(String mRN) {
		MRN = mRN;
	}


	public String getAppointmentId() {
		return AppointmentId;
	}


	public void setAppointmentId(String appointmentId) {
		AppointmentId = appointmentId;
	}


	public String getNationalId() {
		return NationalId;
	}


	public void setNationalId(String nationalId) {
		NationalId = nationalId;
	}


	public String getPatientCode() {
		return PatientCode;
	}


	public void setPatientCode(String patientCode) {
		PatientCode = patientCode;
	}


	public String getResourceId() {
		return ResourceId;
	}


	public void setResourceId(String resourceId) {
		ResourceId = resourceId;
	}


	public String getAppointmentDate() {
		return AppointmentDate;
	}


	public void setAppointmentDate(String appointmentDate) {
		AppointmentDate = appointmentDate;
	}


	public String getStartTime() {
		return StartTime;
	}


	public void setStartTime(String startTime) {
		StartTime = startTime;
	}


	public String getEndTime() {
		return EndTime;
	}


	public void setEndTime(String endTime) {
		EndTime = endTime;
	}


	public String getSpecialityId() {
		return SpecialityId;
	}


	public void setSpecialityId(String specialityId) {
		SpecialityId = specialityId;
	}


	public String getAppointmnetStatus() {
		return AppointmnetStatus;
	}


	public void setAppointmnetStatus(String appointmnetStatus) {
		AppointmnetStatus = appointmnetStatus;
	}


	public Boolean getHasCoverage() {
		return HasCoverage;
	}


	public void setHasCoverage(boolean hasCoverage) {
		HasCoverage = hasCoverage;
	}


	public String getOperationType() {
		return OperationType;
	}


	
	public void setRid(Long rid) {
		this.rid = rid;
	}


	public Boolean getIsSent() {
		return IsSent;
	}


	public void setIsSent(Boolean isSent) {
		IsSent = isSent;
	}


	public Boolean getIsSuccess() {
		return IsSuccess;
	}


	public void setIsSuccess(Boolean isSuccess) {
		IsSuccess = isSuccess;
	}


	public void setOperationType(String operationType) {
		OperationType = operationType;
	}
	
	public String setOperationType() {
		return OperationType;
	}
	
	
    

}


