package com.certacure.core.base.entity;

import java.util.List;

public class UserAccountImpl implements UserAccount {

	private Long rid;
	private Long tenantId;
	private Long branchId;
	private String username;
	private String password;
	private List<String> privileges;

	@Override
	public String toString() {//used inside jwt generated token -> user_name
		return username;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	@Override
	public void setRid(Long rid) {
		this.rid = rid;
	}

	@Override
	public Long getTenantId() {
		return tenantId;
	}

	@Override
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public Long getBranchId() {
		return branchId;
	}

	@Override
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public List<String> getPrivileges() {
		return privileges;
	}

	@Override
	public void setPrivileges(List<String> privileges) {
		this.privileges = privileges;
	}

}
