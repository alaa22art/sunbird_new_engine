/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
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
import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;

/**
 * MachineOrder
 * 
 * @author Alaa Himour <ahimour@certacuresolutions.com>
 * @since DES/10/2017
 */
@Entity
@Table(name = "mw_machine_order")
@Audited
public class MachineOrder extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "barcode")
	private String barcode;

	@Basic(optional = false)
	@Column(name = "is_sent_to_machine")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isSentToMachine = Boolean.FALSE;

	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "Specimen_Descriptor")
	@Size(max = 255)
	private String specimenDescriptor;

	@Column(name = "ordering_physician")
	@Size(max = 255)
	private String orderingPhysician;

	@Column(name = "machine_name")
	@Size(max = 255)
	private String machineName;

	@Column(name = "test_code")
	@Size(max = 255)
	private String testCode;

	@Column(name = "panel_code")
	@Size(max = 255)
	private String panelCode;

	@Column(name = "body_site")
	@Size(max = 255)
	private String bodySite;

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

	@Column(name = "sender_name")
	@Size(max = 255)
	private String senderName;

	@Column(name = "specimen_collection_date_and_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date specimenCollectionDateAndTime;

	@Basic(optional = false)
	@Column(name = "result_received")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean resultReceived = Boolean.FALSE;

	@Column(name = "priority")
	@Size(max = 255)
	private String priority;

	@OneToMany(mappedBy = "machineOrderId", fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "machineOrderId" })
	private Set<MachineOrderQueryResponse> machineOrderQueryResponseList;

	//bi-directional many-to-one association to DataOrderVsInboundHl7Message
	@OneToMany(mappedBy = "MachineOrder")
	@JsonIgnoreProperties({ "MachineOrder" })
	private List<DataOrderInboundHL7Message> dataOrderInboundHL7MessageList;

	@OneToMany(mappedBy = "machineOrder", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "machineOrder" }, allowSetters = true)
	private Set<MachineResult> machineResults;

	@JoinColumn(name = "source_type_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	//@NotNull
	private LkpMessageSourceType sourceType;

	@Column(name = "action_code")
	@Size(max = 255)
	private String actionCode;

	public List<DataOrderInboundHL7Message> getDataOrderInboundHL7MessageList() {
		return dataOrderInboundHL7MessageList;
	}

	public void setDataOrderInboundHL7MessageList(List<DataOrderInboundHL7Message> dataOrderInboundHL7MessageList) {
		this.dataOrderInboundHL7MessageList = dataOrderInboundHL7MessageList;
	}

	public Set<MachineResult> getMachineResults() {
		return machineResults;
	}

	public void setMachineResults(Set<MachineResult> machineResults) {
		this.machineResults = machineResults;
	}

	public String getActionCode() {
		return actionCode;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public LkpMessageSourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(LkpMessageSourceType sourceType) {
		this.sourceType = sourceType;
	}

	public Set<MachineOrderQueryResponse> getMachineOrderQueryResponseList() {
		return machineOrderQueryResponseList;
	}

	public void setMachineOrderQueryResponseList(Set<MachineOrderQueryResponse> machineOrderQueryResponseList) {
		this.machineOrderQueryResponseList = machineOrderQueryResponseList;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public MachineOrder() {
	}

	public MachineOrder(Long rid) {
		this.rid = rid;
	}

	public MachineOrder(String barcode, String patientFirstName, String patientSecondName, String patientId, Date dOB,
			String gender, String panelCode) {

		this.barcode = barcode;
		this.patientFirstName = patientFirstName;
		this.patientLastName = patientSecondName;
		this.patientId = patientId;
		this.dateOfBirth = dOB;
		this.gender = gender;
		this.panelCode = panelCode;

	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Boolean getIsSentToMachine() {
		return isSentToMachine;
	}

	public void setIsSentToMachine(Boolean isSentToMachine) {
		this.isSentToMachine = isSentToMachine;
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

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getTestCode() {
		return testCode;
	}

	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}

	public void setPanelCode(String panelCode) {
		this.panelCode = panelCode;
	}

	public String getPanelCode() {
		return panelCode;
	}

	public void setBodySite(String bodySite) {
		this.bodySite = bodySite;
	}

	public String getBodySite() {
		return bodySite;
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

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Date getSpecimenCollectionDateAndTime() {
		return specimenCollectionDateAndTime;
	}

	public void setSpecimenCollectionDateAndTime(Date specimenCollectionDateAndTime) {
		this.specimenCollectionDateAndTime = specimenCollectionDateAndTime;
	}

	public Boolean getResultReceived() {
		return resultReceived;
	}

	public void setResultReceived(Boolean resultReceived) {
		this.resultReceived = resultReceived;
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
		MachineOrder other = (MachineOrder) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MachineOrder [rid=" + rid + "]";
	}

	/*
	 * public void setSpacimenType(String spacimentType) {
	 * this.spacimentType = spacimentType;
	 * }
	 */

	/*
	 * public String getSpacimentType() {
	 * return this.spacimentType;
	 * }
	 */

	/*
	 * public void setTestDilution(String testDilution) {
	 * this.testDilution = testDilution;
	 * }
	 */

	/*
	 * public String getTestDilution() {
	 * return this.testDilution;
	 * }
	 */

	//	@Override
	//	protected void populateAudit() {
	//
	//		if (getCreationDate() == null) {
	//			setCreationDate(new Date());
	//
	//			if (getCreatedBy() == null) {
	//				setCreatedBy(SecurityUtil.getSystemUser().getRid());
	//
	//			}
	//		} else {
	//			setUpdateDate(new Date());
	//			if (getUpdatedBy() == null) {
	//				setUpdatedBy(SecurityUtil.getSystemUser().getRid());
	//			}
	//		}
	//	}

}
