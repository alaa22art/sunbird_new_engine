/**
 * 
 */
package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;

/**
 * @author AHimour
 *
 */

@Entity
@Table(name = "data_result_vs_outbound_hl7_message")
@Audited
public class DataResultOutboundHL7Message extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long rid;

	//bi-directional many-to-one association to DataOutboundHl7Message
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id")
	private DataOutboundHL7Message dataOutboundHl7Message;

	//bi-directional many-to-one association to MwMachineResult
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "result_id")
	private MachineResult machineResult;

	public DataResultOutboundHL7Message() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public DataOutboundHL7Message getDataOutboundHl7Message() {
		return this.dataOutboundHl7Message;
	}

	public void setDataOutboundHL7Message(DataOutboundHL7Message dataOutboundHl7Message) {
		this.dataOutboundHl7Message = dataOutboundHl7Message;
	}

	public MachineResult getMachineResult() {
		return this.machineResult;
	}

	public void setMachineResult(MachineResult machineResult) {
		this.machineResult = machineResult;
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
		DataResultOutboundHL7Message other = (DataResultOutboundHL7Message) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataResultOutboundHL7Message [rid=" + rid + "]";
	}

}
