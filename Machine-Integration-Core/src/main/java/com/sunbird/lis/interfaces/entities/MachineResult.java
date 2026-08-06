/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import org.hibernate.envers.RelationTargetAuditMode;

import com.sunbird.core.base.entity.BaseAuditableEntity;
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;
import com.sunbird.core.common.util.SecurityUtil;

/**
 * MachineResult
 * 

 */
@Entity
@Table(name = "mw_machine_result")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class MachineResult extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "sample_no")
	private String sampleNo;

	@Column(name = "notes")
	private String notes;

	@JoinColumn(name = "machine_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Machine machine;

	//bi-directional many-to-one association to DataResultVsOutboundHl7Message
	@OneToMany(mappedBy = "machineResult")
	private List<DataResultOutboundHL7Message> dataResultVsOutboundHl7MessageList;

	@Column(name = "machine_name")
	@Size(max = 255)
	private String machineName;

	@Basic(optional = false)
	@Column(name = "is_sent_to_lis")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isSentToLIS;

	@Column(name = "data_or_measurement_value")
	private String dataOrMeasurementValue;

	@Column(name = "abnormal_flag")
	private String abnormalFlag;

	@Column(name = "unit")
	@Size(max = 255)
	private String unit;

	@Column(name = "test_code")
	private String testCode;

	@Column(name = "result_code")
	private String resultCode;

	@Column(name = "result_status")
	private String resultStatus;

	@JoinColumn(name = "machine_query_id", referencedColumnName = "rid")
	@ManyToOne
	private MachineQuery machineQueryId;

	@JoinColumn(name = "message_transaction_id", referencedColumnName = "rid")
	@ManyToOne
	private MessageTransaction messageTransaction;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "last_order_id")
	private MachineOrder machineOrder;

	@Column(name = "note")
	private String note;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "machine_test_id")
	private MachineTest machineTest;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "action_code_id")
	private LkpOrderActionCode actionCode;

	@Column(name = "test_completed_date_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date testCompletedDateTime;

	@Column(name = "referance_ranges")
	private String referanceRanges;

	public String getReferanceRanges() {
		return referanceRanges;
	}

	public MessageTransaction getMessageTransaction() {
		return messageTransaction;
	}

	public void setMessageTransaction(MessageTransaction messageTransaction) {
		this.messageTransaction = messageTransaction;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnit() {
		return unit;
	}

	public List<DataResultOutboundHL7Message> getDataResultVsOutboundHl7MessageList() {
		return dataResultVsOutboundHl7MessageList;
	}

	public void setDataResultVsOutboundHl7MessageList(List<DataResultOutboundHL7Message> dataResultVsOutboundHl7MessageList) {
		this.dataResultVsOutboundHl7MessageList = dataResultVsOutboundHl7MessageList;
	}

	public void setTestCompletedDateTime(Date testCompletedDateTime) {
		this.testCompletedDateTime = testCompletedDateTime;
	}

	public Date getTestCompletedDateTime() {
		return testCompletedDateTime;
	}

	public MachineTest getMachineTest() {
		return machineTest;
	}

	public void setMachineTest(MachineTest machineTest) {
		this.machineTest = machineTest;
	}

	public LkpOrderActionCode getActionCode() {
		return actionCode;
	}

	public void setActionCode(LkpOrderActionCode actionCode) {
		this.actionCode = actionCode;
	}

	public MachineResult() {
	}

	public MachineResult(Long rid) {
		this.rid = rid;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getSampleNo() {
		return sampleNo;
	}

	public void setSampleNo(String sampleNo) {
		this.sampleNo = sampleNo;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getDataOrMeasurementValue() {
		return dataOrMeasurementValue;
	}

	public void setDataOrMeasurementValue(String dataOrMeasurementValue) {
		this.dataOrMeasurementValue = dataOrMeasurementValue;
	}

	public String getTestCode() {
		return testCode;
	}

	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public MachineQuery getMachineQueryId() {
		return machineQueryId;
	}

	public void setMachineQueryId(MachineQuery machineQueryId) {
		this.machineQueryId = machineQueryId;
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
		MachineResult other = (MachineResult) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MachineResult [rid=" + rid + "]";
	}

	public Boolean getIsSentToLIS() {
		return isSentToLIS;
	}

	public void setIsSentToLIS(Boolean isSentToLIS) {
		this.isSentToLIS = isSentToLIS;
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

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;

	}

	public String getAbnormalFlag() {
		return abnormalFlag;
	}

	public void setAbnormalFlag(String abnormalFlag) {
		this.abnormalFlag = abnormalFlag;
	}

	public MachineOrder getMachineOrder() {
		return machineOrder;
	}

	public void setMachineOrder(MachineOrder machineOrder) {
		this.machineOrder = machineOrder;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setReferanceRanges(String referanceRanges) {
		this.referanceRanges = referanceRanges;

	}

}
