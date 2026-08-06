package com.sunbird.core.base.entity;

import java.util.List;

public interface UserAccount {

	public Long getRid();

	public void setRid(Long rid);

	public Long getTenantId();

	public void setTenantId(Long tenantId);

	public Long getBranchId();

	public void setBranchId(Long branchId);

	public String getUsername();

	public void setUsername(String username);

	public String getPassword();

	public void setPassword(String password);

	public List<String> getPrivileges();

	public void setPrivileges(List<String> privileges);

}
