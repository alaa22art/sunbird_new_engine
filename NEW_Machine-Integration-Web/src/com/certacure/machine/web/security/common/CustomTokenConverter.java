package com.certacure.machine.web.security.common;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.certacure.core.common.util.JSONUtil;
import com.certacure.core.common.util.TokenUtil;
import com.certacure.lis.interfaces.admin.model.SecUser;

public class CustomTokenConverter extends JwtAccessTokenConverter {

	@Autowired
	private CustomClaimsVerifier customClaimsVerifier;

	@PostConstruct
	public void postConstructor() {
		setJwtClaimsSetVerifier(customClaimsVerifier);
	}

	@Override
	public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
		OAuth2Authentication authentication = super.extractAuthentication(map);
		Authentication userAuthentication = authentication.getUserAuthentication();

		if (userAuthentication != null) {
			SecUser user = JSONUtil.getMapper().convertValue(map.get("user"), SecUser.class);
			if (user != null) {
				userAuthentication = new UsernamePasswordAuthenticationToken(user, userAuthentication.getCredentials(),
						userAuthentication.getAuthorities());
			}
		}

		return new OAuth2Authentication(authentication.getOAuth2Request(), userAuthentication);
	}

	@Override
	protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		return TokenUtil.encodeCurrentAuthenticationToken(accessToken, authentication);
	}

	@Override
	protected Map<String, Object> decode(String token) {
		try {
			return super.decode(token);
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause != null) {
				throw new InvalidTokenException(cause.getMessage(), cause);
			}
			throw new InvalidTokenException("Cannot convert access token to JSON", e);
		}
	}

}
