package com.certacure.machine.web.security.common;

import java.util.Map;

import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.stereotype.Component;

import com.certacure.core.common.util.TokenUtil;

@Component
public class CustomClaimsVerifier implements JwtClaimsSetVerifier {

	@Override
	public void verify(Map<String, Object> claims) throws InvalidTokenException {
		TokenUtil.verifyClaims(claims);
	}
}
