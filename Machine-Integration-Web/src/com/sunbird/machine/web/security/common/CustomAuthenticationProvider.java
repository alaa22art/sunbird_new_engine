package com.sunbird.machine.web.security.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.sunbird.lis.interfaces.admin.model.SecUser;
import com.sunbird.lis.interfaces.security.ClientType;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Value("${system.certacure.rcm.username}")
	private String CERTACURE_RCM_USERNAME;
	@Value("${system.certacure.rcm.password}")
	private String CERTACURE_RCM_PASSWORD;

	@SuppressWarnings("unchecked")
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		LinkedHashMap<String, String> details = (LinkedHashMap<String, String>) auth.getDetails();
		ClientType clientType = ClientType.valueOf(details.get("client_id"));
		String username = auth.getName();
		String password = auth.getCredentials().toString();

		switch (clientType) {
			case ACCULAB:
			case CERTACURE:
				Collection<GrantedAuthority> authorities = new ArrayList<>();
				if (CERTACURE_RCM_USERNAME.equals(username) && CERTACURE_RCM_PASSWORD.equals(password)) {
					SecUser user = new SecUser();
					user.setUsername(username);
					user.setRid(-1L);
					user.setTenantId(Long.valueOf(details.get("tenant_id")));
					user.setBranchId(Long.valueOf(details.get("branch_id")));
					return new UsernamePasswordAuthenticationToken(user, password, authorities);
				}
				break;
			case ACCULINK:
				//Since this CustomAuthenticationProvider is part of a chain, failure here causes the next
				//element in the chain to pick up, AKA SpringLoginService
				//So normal ACCULINK users are evaluated there
				throw new BadCredentialsException("External system authentication failed");
		}
		throw new BadCredentialsException("External system authentication failed");
	}

	@Override
	public boolean supports(Class<?> auth) {
		return auth.equals(UsernamePasswordAuthenticationToken.class);
	}

}
