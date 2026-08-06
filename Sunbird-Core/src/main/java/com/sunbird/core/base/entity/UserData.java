package com.sunbird.core.base.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserData implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Optional<UserAccount> user;
	private Boolean canLogin;
	private Set<String> rights;
	private String sessionId;
	private String pcName;
	private String clientIP;
	private String serverIP;

	public UserData(Optional<UserAccount> user, Set<String> rights, Boolean canLogin) {
		this.user = user;
		this.canLogin = canLogin;
		this.rights = rights;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<>();
		for (String s : rights) {
			authorities.add(new SimpleGrantedAuthority(s));
		}
		return authorities;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getPcName() {
		return pcName;
	}

	public void setPcName(String pcName) {
		this.pcName = pcName;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	@Override
	public String getPassword() {
		return getUser().getPassword();
	}

	@Override
	public String getUsername() {
		return getUser().getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return canLogin;
	}

	public Long getUserRid() {
		return getUser().getRid();
	}

	@JsonIgnore
	public UserAccount getUser() {
		return user.get();
	}

}
