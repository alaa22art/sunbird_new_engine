/**
 * 
 */
package com.certacure.lis.interfaces.entities;

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

import com.certacure.core.base.entity.BaseAuditableBranchedEntity;

/**
 * 
 * 
 */

@Entity
@Table(name = "data_order_vs_inbound_hl7_messages")
@Audited
public class DataOrderInboundHL7Message extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long rid;

	//bi-directional many-to-one association to DataInboundHl7Message
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inbound_message_id")
	private DataInboundHL7Message InboundHl7Message;

	//bi-directional many-to-one association to MachineOrder
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private MachineOrder MachineOrder;

	public MachineOrder getMachineOrder() {
		return MachineOrder;
	}

	public void setMachineOrder(MachineOrder machineOrder) {
		MachineOrder = machineOrder;
	}

	public DataOrderInboundHL7Message() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public DataInboundHL7Message getInboundHl7Message() {
		return this.InboundHl7Message;
	}

	public void setInboundHl7Message(DataInboundHL7Message InboundHl7Message) {
		this.InboundHl7Message = InboundHl7Message;
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
		DataOrderInboundHL7Message other = (DataOrderInboundHL7Message) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataOrderInboundHL7Message [rid=" + rid + "]";
	}

}
