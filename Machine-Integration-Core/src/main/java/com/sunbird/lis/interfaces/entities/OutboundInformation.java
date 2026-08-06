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
import javax.persistence.Convert;
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
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;

/**
 * OrderPriority
 * 
 */
@Entity
@Table(name = "data_outbound_information")
@Audited
public class OutboundInformation extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "sequence_number")
	private Long sequenceNumber;

	@Column(name = "specimen_id")
	private String specimenId;

	@Column(name = "instrument_specimen_id")
	private Long instrumentSpecimenId;
	@Column(name = "universal_test_id")
	private Long universalTestId;

	@Column(name = "specimen_collection_date_and_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date specimenCollectionDateAndTime;

	@Column(name = "collection_end_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date collectionEndTime;

	@Column(name = "collection_volume")
	@Size(max = 255)
	private String collectionVolume;

	@Column(name = "collector_id")
	private Long collectorId;

	@Column(name = "danger_code")
	@Size(max = 255)
	private String dangerCode;

	@Column(name = "relevant_clinical_information")
	@Size(max = 255)
	private String relevantClinicalInformation;

	@Column(name = "specimen_received")
	@Temporal(TemporalType.TIMESTAMP)
	private Date specimenReceived;

	@Column(name = "Specimen_Descriptor")
	@Size(max = 255)
	private String specimenDescriptor;

	@Column(name = "ordering_physician")
	@Size(max = 255)
	private String orderingPhysician;

	@Column(name = "physician_telephone_number")
	@Size(max = 255)
	private String physicianTelephoneNumber;

	@Column(name = "user_field_number_1")
	private Long userFieldNumber1;

	@Column(name = "user_field_number_2")
	private Long userFieldNumber2;

	@Column(name = "laboratory_field_number_1")
	@Size(max = 255)
	private String laboratoryFieldNumber1;

	@Column(name = "laboratory_field_number_2")
	private Long laboratoryFieldNumber2;

	@Column(name = "results_reported_or_last_modified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date resultsReportedOrLastModified;

	@Column(name = "instrument_charge_to_information_system")
	@Size(max = 255)
	private String instrumentChargeToInformationSystem;

	@Column(name = "instrument_section_id")
	private Long instrumentSectionId;

	@Column(name = "reserved_field")
	@Size(max = 255)
	private String reservedField;

	@Column(name = "location_of_specimen_collection")
	@Size(max = 255)
	private String locationOfSpecimenCollection;

	@Column(name = "nosocomial_infection_flag")
	@Convert(converter = BooleanIntegerConverter.class)
	private boolean nosocomialInfectionFlag;

	@Column(name = "specimen_service")
	@Size(max = 255)
	private String specimenService;

	@Column(name = "specimen_institution")
	@Size(max = 255)
	private String specimenInstitution;

	@Column(name = "machine_name")
	@Size(max = 255)
	private String machineName;

	@Column(name = "sender_name")
	@Size(max = 255)
	private String senderName;

	@Column(name = "patient_first_name")
	@Size(max = 255)
	private String patientFirstName;

	@Column(name = "patient_last_name")
	@Size(max = 255)
	private String patientLastName;

	@Column(name = "patient_id")
	private String patientId;

	@Column(name = "date_of_birth")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateOfBirth;

	@Column(name = "gender")
	private String gender;

	@Column(name = "attending_physician")
	private String attendingPhysician;

	public OutboundInformation() {
	}

	public OutboundInformation(Long rid) {
		this.rid = rid;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getSpecimenId() {
		return specimenId;
	}

	public void setSpecimenId(String specimenId) {
		this.specimenId = specimenId;
	}

	public Long getInstrumentSpecimenId() {
		return instrumentSpecimenId;
	}

	public void setInstrumentSpecimenId(Long instrumentSpecimenId) {
		this.instrumentSpecimenId = instrumentSpecimenId;
	}

	public Long getUniversalTestId() {
		return universalTestId;
	}

	public void setUniversalTestId(Long universalTestId) {
		this.universalTestId = universalTestId;
	}

	public Date getSpecimenCollectionDateAndTime() {
		return specimenCollectionDateAndTime;
	}

	public void setSpecimenCollectionDateAndTime(Date specimenCollectionDateAndTime) {
		this.specimenCollectionDateAndTime = specimenCollectionDateAndTime;
	}

	public Date getCollectionEndTime() {
		return collectionEndTime;
	}

	public void setCollectionEndTime(Date collectionEndTime) {
		this.collectionEndTime = collectionEndTime;
	}

	public String getCollectionVolume() {
		return collectionVolume;
	}

	public void setCollectionVolume(String collectionVolume) {
		this.collectionVolume = collectionVolume;
	}

	public Long getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(Long collectorId) {
		this.collectorId = collectorId;
	}

	public String getDangerCode() {
		return dangerCode;
	}

	public void setDangerCode(String dangerCode) {
		this.dangerCode = dangerCode;
	}

	public String getRelevantClinicalInformation() {
		return relevantClinicalInformation;
	}

	public void setRelevantClinicalInformation(String relevantClinicalInformation) {
		this.relevantClinicalInformation = relevantClinicalInformation;
	}

	public Date getSpecimenReceived() {
		return specimenReceived;
	}

	public void setSpecimenReceived(Date specimenReceived) {
		this.specimenReceived = specimenReceived;
	}

	public String getSpecimenDescriptor() {
		return specimenDescriptor;
	}

	public void setSpecimenDescriptor(String specimenDescriptor) {
		this.specimenDescriptor = specimenDescriptor;
	}

	public String getOrderingPhysician() {
		return orderingPhysician;
	}

	public void setOrderingPhysician(String orderingPhysician) {
		this.orderingPhysician = orderingPhysician;
	}

	public String getPhysicianTelephoneNumber() {
		return physicianTelephoneNumber;
	}

	public void setPhysicianTelephoneNumber(String physicianTelephoneNumber) {
		this.physicianTelephoneNumber = physicianTelephoneNumber;
	}

	public Long getUserFieldNumber1() {
		return userFieldNumber1;
	}

	public void setUserFieldNumber1(Long userFieldNumber1) {
		this.userFieldNumber1 = userFieldNumber1;
	}

	public Long getUserFieldNumber2() {
		return userFieldNumber2;
	}

	public void setUserFieldNumber2(Long userFieldNumber2) {
		this.userFieldNumber2 = userFieldNumber2;
	}

	public String getLaboratoryFieldNumber1() {
		return laboratoryFieldNumber1;
	}

	public void setLaboratoryFieldNumber1(String laboratoryFieldNumber1) {
		this.laboratoryFieldNumber1 = laboratoryFieldNumber1;
	}

	public long getLaboratoryFieldNumber2() {
		return laboratoryFieldNumber2;
	}

	public void setLaboratoryFieldNumber2(long laboratoryFieldNumber2) {
		this.laboratoryFieldNumber2 = laboratoryFieldNumber2;
	}

	public Date getResultsReportedOrLastModified() {
		return resultsReportedOrLastModified;
	}

	public void setResultsReportedOrLastModified(Date resultsReportedOrLastModified) {
		this.resultsReportedOrLastModified = resultsReportedOrLastModified;
	}

	public String getInstrumentChargeToInformationSystem() {
		return instrumentChargeToInformationSystem;
	}

	public void setInstrumentChargeToInformationSystem(String instrumentChargeToInformationSystem) {
		this.instrumentChargeToInformationSystem = instrumentChargeToInformationSystem;
	}

	public Long getInstrumentSectionId() {
		return instrumentSectionId;
	}

	public void setInstrumentSectionId(Long instrumentSectionId) {
		this.instrumentSectionId = instrumentSectionId;
	}

	public String getReservedField() {
		return reservedField;
	}

	public void setReservedField(String reservedField) {
		this.reservedField = reservedField;
	}

	public String getLocationOfSpecimenCollection() {
		return locationOfSpecimenCollection;
	}

	public void setLocationOfSpecimenCollection(String locationOfSpecimenCollection) {
		this.locationOfSpecimenCollection = locationOfSpecimenCollection;
	}

	public boolean getNosocomialInfectionFlag() {
		return nosocomialInfectionFlag;
	}

	public void setNosocomialInfectionFlag(boolean nosocomialInfectionFlag) {
		this.nosocomialInfectionFlag = nosocomialInfectionFlag;
	}

	public String getSpecimenService() {
		return specimenService;
	}

	public void setSpecimenService(String specimenService) {
		this.specimenService = specimenService;
	}

	public String getSpecimenInstitution() {
		return specimenInstitution;
	}

	public void setSpecimenInstitution(String specimenInstitution) {
		this.specimenInstitution = specimenInstitution;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getPatientFirstName() {
		return patientFirstName;
	}

	public void setPatientFirstName(String patientFirstName) {
		this.patientFirstName = patientFirstName;
	}

	public String getPatientLastName() {
		return patientLastName;
	}

	public void setPatientLastName(String patientLastName) {
		this.patientLastName = patientLastName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAttendingPhysician() {
		return attendingPhysician;
	}

	public void setAttendingPhysician(String attendingPhysician) {
		this.attendingPhysician = attendingPhysician;
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
		OutboundInformation other = (OutboundInformation) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OutboundInformation [rid=" + rid + "]";
	}

}
