/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.sunbird.core.base.entity.BaseAuditableEntity;

/**
 * ResultInformation
 * 

 */
@Entity
@Table(name = "data_result_information")
@Audited
public class ResultInformation extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "Record_type_id")
	private Long recordtypeid;

	@Column(name = "sequence_number")
	private Long sequenceNumber;

	@Column(name = "universal_test_id")
	private String universalTestId;

	@Column(name = "data_or_measurement_value")
	private String dataOrMeasurementValue;

	@Column(name = "units")
	@Size(max = 255)
	private String units;

	@Column(name = "reference_ranges_txt")
	@Size(max = 255)
	private String referenceRangesTxt;

	@Column(name = "nature_of_abnormality_testing")
	private Long natureOfAbnormalityTesting;

	@Column(name = "date_of_change_in_instrument_normative_values_or_Units")
	@Size(max = 255)
	private String dateofchangeininstrumentnormativevaluesorUnits;

	@Column(name = "operator_identification")
	@Size(max = 255)
	private String operatorIdentification;

	@Column(name = "test_started")
	@Temporal(TemporalType.TIMESTAMP)
	private Date testStarted;

	@Column(name = "test_completed")
	@Temporal(TemporalType.TIMESTAMP)
	private Date testCompleted;

	@Column(name = "instrument_identification")
	@Size(max = 255)
	private String instrumentIdentification;

	@Column(name = "notes")
	@Size(max = 255)
	private String notes;

	@Column(name = "machine_name")
	private String machineName;

	@Column(name = "sample_id")
	private String sampleId;

	public ResultInformation() {
	}

	public ResultInformation(Long rid) {
		this.rid = rid;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Long getRecordtypeid() {
		return recordtypeid;
	}

	public void setRecordtypeid(Long recordtypeid) {
		this.recordtypeid = recordtypeid;
	}

	public Long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getUniversalTestId() {
		return universalTestId;
	}

	public void setUniversalTestId(String universalTestId) {
		this.universalTestId = universalTestId;
	}

	public String getDataOrMeasurementValue() {
		return dataOrMeasurementValue;
	}

	public void setDataOrMeasurementValue(String dataOrMeasurementValue) {
		this.dataOrMeasurementValue = dataOrMeasurementValue;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getReferenceRangesTxt() {
		return referenceRangesTxt;
	}

	public void setReferenceRangesTxt(String referenceRangesTxt) {
		this.referenceRangesTxt = referenceRangesTxt;
	}

	public Long getNatureOfAbnormalityTesting() {
		return natureOfAbnormalityTesting;
	}

	public void setNatureOfAbnormalityTesting(Long natureOfAbnormalityTesting) {
		this.natureOfAbnormalityTesting = natureOfAbnormalityTesting;
	}

	public String getDateofchangeininstrumentnormativevaluesorUnits() {
		return dateofchangeininstrumentnormativevaluesorUnits;
	}

	public void setDateofchangeininstrumentnormativevaluesorUnits(String dateofchangeininstrumentnormativevaluesorUnits) {
		this.dateofchangeininstrumentnormativevaluesorUnits = dateofchangeininstrumentnormativevaluesorUnits;
	}

	public String getOperatorIdentification() {
		return operatorIdentification;
	}

	public void setOperatorIdentification(String operatorIdentification) {
		this.operatorIdentification = operatorIdentification;
	}

	public Date getTestStarted() {
		return testStarted;
	}

	public void setTestStarted(Date testStarted) {
		this.testStarted = testStarted;
	}

	public Date getTestCompleted() {
		return testCompleted;
	}

	public void setTestCompleted(Date testCompleted) {
		this.testCompleted = testCompleted;
	}

	public String getInstrumentIdentification() {
		return instrumentIdentification;
	}

	public void setInstrumentIdentification(String instrumentIdentification) {
		this.instrumentIdentification = instrumentIdentification;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
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
		ResultInformation other = (ResultInformation) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ResultInformation [rid=" + rid + "]";
	}

}
