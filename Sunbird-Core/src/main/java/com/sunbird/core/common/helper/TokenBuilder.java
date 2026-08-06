package com.sunbird.core.common.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sunbird.core.base.entity.UserAccount;
import com.sunbird.core.base.entity.UserAccountImpl;
import com.sunbird.core.common.util.CollectionUtil;
import com.sunbird.core.common.util.TokenUtil;

public class TokenBuilder {

	private final Map<String, Object> claims = new HashMap<String, Object>();
	private UserAccount user = new UserAccountImpl();
	private final Collection<String> authorities = new ArrayList<>();
	private Long tenantId;
	private Long branchId;
	private Boolean includeUser = Boolean.FALSE;
	private Set<String> scopes = new HashSet<>();

	public TokenBuilder() {
		user.setPrivileges(new ArrayList<>());
	}

	public String build() {
		if (includeUser) {
			if (tenantId != null) {
				user.setTenantId(tenantId);
			}
			if (branchId != null) {
				user.setBranchId(branchId);
			}
			if (!CollectionUtil.isCollectionEmpty(authorities)) {
				if (user.getPrivileges() == null) {
					user.setPrivileges(new ArrayList<>());
				}
				user.getPrivileges().addAll(authorities);
				putClaim("authorities", authorities);
			}
			if (!CollectionUtil.isCollectionEmpty(scopes)) {
				putClaim("scope", scopes);
			}
			putClaim("user", user);
			putClaim("user_name", user.getUsername());
		}
		return TokenUtil.encodeToken(claims);
	}

	public TokenBuilder setUser(UserAccount user) {
		includeUser = Boolean.TRUE;
		this.user = user;
		return this;
	}

	public TokenBuilder setTenantId(Long tenantId) {
		includeUser = Boolean.TRUE;
		this.tenantId = tenantId;
		return this;
	}

	public TokenBuilder setBranchId(Long branchId) {
		includeUser = Boolean.TRUE;
		this.branchId = branchId;
		return this;
	}

	public TokenBuilder setVersion(String version) {
		putClaim("version", version);
		return this;
	}

	public TokenBuilder setIgnoreVersion(Boolean ignoreVersion) {
		putClaim("ignoreVersion", ignoreVersion);
		return this;
	}

	/**
	 * Send the duration of the token validity in seconds
	 * 
	 * @param expiration (in seconds)
	 */
	public TokenBuilder setExpiration(Long expiration) {
		putClaim("exp", System.currentTimeMillis() / 1000 + expiration);
		return this;
	}

	/**
	 * Send the duration of the token validity in seconds
	 * 
	 * @param expiration (in seconds)
	 */
	public TokenBuilder setExpiration(String expiration) {
		Long expirationTime = Long.parseLong(expiration);
		setExpiration(expirationTime);
		return this;
	}

	public TokenBuilder addAuthority(String authority) {
		includeUser = Boolean.TRUE;
		authorities.add(authority);
		return this;
	}

	public TokenBuilder addAllAuthorities(Collection<String> authorities) {
		includeUser = Boolean.TRUE;
		authorities.addAll(authorities);
		return this;
	}

	public TokenBuilder addScope(String scope) {
		scopes.add(scope);
		return this;
	}

	public TokenBuilder addAllScopes(Collection<String> scopes) {
		scopes.addAll(scopes);
		return this;
	}

	public TokenBuilder setClientId(String clientId) {
		putClaim("client_id", clientId);
		return this;
	}

	public TokenBuilder putClaim(String key, Object value) {
		claims.put(key, value);
		return this;
	}

	public TokenBuilder putAllClaims(Map<String, Object> claims) {
		claims.putAll(claims);
		return this;
	}

}
