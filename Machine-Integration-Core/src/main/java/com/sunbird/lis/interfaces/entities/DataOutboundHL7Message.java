/**
 * 
 */
package com.sunbird.lis.interfaces.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;

/**
 * @author AHimour
 *
 */

@Entity
@Table(name = "data_outbound_hl7_message")
@Audited
public class DataOutboundHL7Message extends BaseAuditableBranchedEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long rid;

	@Column(name = "message_body", length = 4000)
	private String messageBody;

	@Column(name = "message_controller_id")
	private Long messageControllerId;

	@Column(length = 255)
	private String priority;

	@Column(length = 255)
	private String source;

	//bi-directional many-to-one association to DataResultVsOutboundHl7Message
	@OneToMany(mappedBy = "dataOutboundHl7Message")
	private List<DataResultOutboundHL7Message> dataResultOutboundHL7MessagesList;

	public DataOutboundHL7Message() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getMessageBody() {
		return this.messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public Long getMessageControllerId() {
		return this.messageControllerId;
	}

	public void setMessageControllerId(Long messageControllerId) {
		this.messageControllerId = messageControllerId;
	}

	public String getPriority() {
		return this.priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<DataResultOutboundHL7Message> getDataResultOutboundHl7MessagesList() {
		return this.dataResultOutboundHL7MessagesList;
	}

	public void setDataResultOutboundHl7MessagesList(List<DataResultOutboundHL7Message> dataResultOutboundHl7Messages) {
		this.dataResultOutboundHL7MessagesList = dataResultOutboundHl7Messages;
	}

	/*
	 * public DataResultOutboundHL7Message addDataResultVsOutboundHl7Message(DataResultOutboundHL7Message dataResultVsOutboundHl7Message) {
	 * getDataResultOutboundHl7MessagesList().add(dataResultVsOutboundHl7Message);
	 * dataResultVsOutboundHl7Message.setDataOutboundHL7Message(this);
	 * 
	 * return dataResultVsOutboundHl7Message;
	 * }
	 */

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
		DataOutboundHL7Message other = (DataOutboundHL7Message) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataOutboundHl7Message [rid=" + rid + "]";
	}

}
