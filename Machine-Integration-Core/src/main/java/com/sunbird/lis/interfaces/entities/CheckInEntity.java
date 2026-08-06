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

@Entity
@Table(name= "mw_checkin")
public class CheckInEntity extends BaseAuditableBranchedEntity implements Serializable{

	/**
	 *  "National_Id": "4000056821",  // mandatory 
		"MRN": "235360", // mandatory 
		"Appt_Date": "20231121104501", // mandatory 
		"Clinic_IEN":"826" // optional 
	 */
	private static final long serialVersionUID = 1L;

	   @Id
	    @Basic(optional= false)
	    @GeneratedValue(strategy= GenerationType.IDENTITY)
	    @Column(name= "rid")
	    private Long rid;
	   
	    @Column(name = "appt_id", nullable = false)
	    @JsonProperty("Appt_ID")
	    private String ApptID;
	   
	    @Column(name = "mrn", nullable = false)
	    @JsonProperty("MRN")
	    private String MRN;

	    @Column(name = "national_id", nullable = false)
	    @JsonProperty("National_Id")
	    private String NationalId;

	    @Column(name = "appt_date", nullable = false)
//	    @JsonFormat(pattern = "yyyyMMddHHmmss")
	    @JsonProperty("Appt_Date")
	    private String ApptDate;

	    @Column(name = "clinic_ien")
	    @JsonProperty("Clinic_IEN")
	    private String ClinicIEN;
	    
	    @Column(name= "json_source_input")
	    private String jsonSourceInput;
	    @Column(name= "json_source_response")
	    private String jsonSourceresponse;
	    @Column(name= "json_destination_input")
	    private String jsonDestinationInput;
	    @Column(name= "json_destination_response")
	    private String jsonDestinationResponse;
	    
	    
	    
	    
	    public String getNationalId() {
			return NationalId;
		}


		public void setNationalId(String nationalId) {
			NationalId = nationalId;
		}

		@Column(name = "json_body")
	    @JsonProperty("jsonBody")
	    private String jsonBody;
	    
		@Basic(optional = false)
	    @Convert(converter = BooleanIntegerConverter.class)
	    @Column(name= "is_sent")
	    @JsonProperty("isSent")
	    private Boolean IsSent;
	    
	    public Boolean getIsSent() {
			return IsSent;
		}
	    
	    @Basic(optional = false)
	    @Convert(converter = BooleanIntegerConverter.class)
	    @Column(name= "is_succuss")
	    @JsonProperty("isSuccuss")
	    private Boolean IsSuccess;

	    public CheckInEntity() {
	    	
	    }
	    
	
	public String getApptID() {
			return ApptID;
		}





		public void setAppt_ID(String apptID) {
			ApptID = apptID;
		}





		public String getMRN() {
			return MRN;
		}





		public void setMRN(String mRN) {
			MRN = mRN;
		}





		public String getApptDate() {
			return ApptDate;
		}





		public void setApptDate(String appt_Date) {
			ApptDate = appt_Date;
		}





		public String getClinicIEN() {
			return ClinicIEN;
		}





		public void setClinic_IEN(String clinic_IEN) {
			ClinicIEN = clinic_IEN;
		}









		public Boolean getIsSuccess() {
			return IsSuccess;
		}





		public void setIsSuccess(Boolean isSuccess) {
			IsSuccess = isSuccess;
		}





		public void setRid(Long rid) {
			this.rid = rid;
		}





		public void setIsSent(Boolean isSent) {
			IsSent = isSent;
		}





	@Override
	public Long getRid() {
		// TODO Auto-generated method stub
		return rid;
	}


	public String getJsonSourceInput() {
		return jsonSourceInput;
	}


	public void setJsonSourceInput(String jsonSourceInput) {
		this.jsonSourceInput = jsonSourceInput;
	}


	public String getJsonSourceresponse() {
		return jsonSourceresponse;
	}


	public void setJsonSourceresponse(String jsonSourceresponse) {
		this.jsonSourceresponse = jsonSourceresponse;
	}


	public String getJsonDestinationInput() {
		return jsonDestinationInput;
	}


	public void setJsonDestinationInput(String jsonDestinationInput) {
		this.jsonDestinationInput = jsonDestinationInput;
	}


	public String getJsonDestinationResponse() {
		return jsonDestinationResponse;
	}


	public void setJsonDestinationResponse(String jsonDestinationResponse) {
		this.jsonDestinationResponse = jsonDestinationResponse;
	}
	
	
	@Override
	public String toString() 
	{
		return 
				
		   "{"
		     + "\"" + "ApptID"  + "\"" + ":"  + "\"" + ApptID+ "\"" + ","
		     + "\"" + "NationalId"  + "\"" + ":"  + "\"" + NationalId  + "\"" + ","
		     + "\"" + "MRN"  + "\"" + ":"  + "\"" + MRN + "\"" + ","
		     + "\"" + "ApptDate"  + "\"" + ":"  + "\"" +  ApptDate  + "\"" + ","
		     + "\"" + "ClinicIEN"  + "\"" + ":"  + "\"" + ClinicIEN + "\"" + 
		    "}";
				
		}
	

}
