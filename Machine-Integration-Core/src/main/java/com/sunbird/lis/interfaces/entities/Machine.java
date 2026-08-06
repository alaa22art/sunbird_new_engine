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
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;
import com.sunbird.core.common.data.model.converter.BooleanIntegerConverter;
import com.sunbird.core.common.util.SecurityUtil;

/**
 * Machine
 * 
 * @author Alaa Himour <ahimour@optimizasolutions.com>
 * @since Dec/10/2017
 *        update 21/12/2017 add
 *        lis_result_ws_url / lis_order_ws_url / user / password
 * 
 */
@Entity
@Table(name = "mw_machine")
@Audited
public class Machine extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "address")
	private String address;

	@Basic(optional = false)
	@Column(name = "name")
	@NotNull
	private String name;

	@Column(name = "server_port")
	private Integer serverPort;

	@Basic(optional = false)
	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "is_send_state_active")
	@NotNull
	private Boolean isSendStateActive;

	@Basic(optional = false)
	@Column(name = "is_receive_state_active")
	@Convert(converter = BooleanIntegerConverter.class)
	@NotNull
	private Boolean isReceiveStateActive;

	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	@NotNull
	private Boolean isActive;

	@Column(name = "machine_actor_path")
	private String machineActorPath;

	@JoinColumn(name = "machine_type_id", referencedColumnName = "rid")
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "machineList" })
	@NotNull
	private MachineType machineType;

	@Column(name = "user_name")
	private String UserName;

	@Column(name = "password")
	private String password;

	@NotNull
	@Column(name = "machine_ip_address")
	private String machineIpAddress;

	@NotNull
	@Column(name = "server_ip_address")
	private String serverIpAddress;

	@OneToMany(mappedBy = "machine", fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "machine" })
	private List<MachineQuery> machineQueryList;

	@OneToMany(mappedBy = "machine", fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "machine" })
	private List<MachineResult> machineResultList;

	@Column(name = "connected")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean connected;

	@Column(name = "is_port_open")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isPortOpen;

	@Basic(optional = false)
	@Column(name = "request_new_test_only")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean requestNewTestOnly;

	@Basic(optional = false)
	@Column(name = "request_test_with_no_result_only")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean requestTestWithNoResultOnly;

	@Column(name = "is_panel_order")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isPanelOrder;
	
	@Column(name = "connection_mode")
	private String connectionMode;

	public String getConnectionMode() {
		return connectionMode;
	}

	
	public Boolean getIsPanelOrder() {
		return isPanelOrder;
	}

	public void setIsPanelOrder(Boolean isPanelOrder) {
		this.isPanelOrder = isPanelOrder;
	}

	public Boolean getRequestNewTestOnly() {
		return requestNewTestOnly;
	}

	public void setRequestNewTestOnly(Boolean requestNewTestOnly) {
		this.requestNewTestOnly = requestNewTestOnly;
	}

	public Boolean getRequestTestWithNoResultOnly() {
		return requestTestWithNoResultOnly;
	}

	public void setRequestTestWithNoResultOnly(Boolean requestTestWithNoResultOnly) {
		this.requestTestWithNoResultOnly = requestTestWithNoResultOnly;
	}

	public Boolean getIsPortOpen() {
		return isPortOpen;
	}

	public void setIsPortOpen(Boolean isPortOpen) {
		this.isPortOpen = isPortOpen;
	}

	public Boolean getConnected() {
		return this.connected;
	}

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String UserName) {
		this.UserName = UserName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMachineIpAddress() {
		return machineIpAddress;
	}

	public void setMachineIpAddress(String machineIpAddress) {
		this.machineIpAddress = machineIpAddress;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Machine() {
	}

	public Machine(Long rid) {
		this.rid = rid;
	}

	public Machine(Long rid, String name, Boolean receiveState, Boolean sendState) {
		this.rid = rid;
		this.name = name;
		this.setIsReceiveStateActive(receiveState);
		this.setIsSendStateActive(sendState);
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setConnectionMode(String connectionMode) {
		this.connectionMode = connectionMode;
	}


	public String getMachineActorPath() {
		return machineActorPath;
	}

	public void setMachineActorPath(String machineActorPath) {
		this.machineActorPath = machineActorPath;
	}

	//	public List<MachineQuery> getMachineQueryList() {
	//		return machineQueryList;
	//	}
	//
	//	public void setMachineQueryList(List<MachineQuery> machineQueryList) {
	//		this.machineQueryList = machineQueryList;
	//	}
	//
	//	public void setMachineResultList(List<MachineResult> machineResultList) {
	//		this.machineResultList = machineResultList;
	//	}
	//
	//	public List<MachineResult> getMachineResultList() {
	//		return this.machineResultList;
	//	}

	//	public List<MachineOrder> getMachineOrderList() {
	//		return machineOrderList;
	//	}

	//	public void setMachineOrderList(List<MachineOrder> machineOrderList) {
	//		this.machineOrderList = machineOrderList;
	//	}

	@Override
	protected void populateAudit() {

		if (getCreationDate() == null) {
			setCreationDate(new Date());

			if (getCreatedBy() == null) {
				setCreatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());
				setTenantId(SecurityUtil.getCurrentUserElseInternal().getTenantId());
				setBranchId(SecurityUtil.getCurrentUserElseInternal().getBranchId());
			}
		} else {
			setUpdateDate(new Date());
			if (getUpdatedBy() == null) {
				setUpdatedBy(SecurityUtil.getCurrentUserElseInternal().getRid());
			}
		}
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
		Machine other = (Machine) obj;
		if (rid == null) {
			if (other.getRid() != null)
				return false;
		} else if (!rid.equals(other.getRid()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Machine [rid=" + rid + "]";
	}

	public Boolean getIsSendStateActive() {
		return isSendStateActive;
	}

	public void setIsSendStateActive(Boolean isSendStateActive) {
		this.isSendStateActive = isSendStateActive;
	}

	public Boolean getIsReceiveStateActive() {
		return isReceiveStateActive;
	}

	public void setIsReceiveStateActive(Boolean isReceiveStateActive) {
		this.isReceiveStateActive = isReceiveStateActive;
	}

	public MachineType getMachineType() {
		return machineType;
	}

	public void setMachineType(MachineType machineType) {
		this.machineType = machineType;
	}

	public String getServerIpAddress() {
		return serverIpAddress;
	}

	public void setServerIpAddress(String serverIpAddress) {
		this.serverIpAddress = serverIpAddress;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;

	}

}
