package com.certacure.machine.web.security.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.certacure.core.common.util.JSONUtil;
import com.certacure.lis.interfaces.admin.model.SecUser;
import com.certacure.lis.interfaces.security.ClientType;

public class CustomTokenEnhancer implements TokenEnhancer {

	@Value("${system.version}")
	private String SYSTEM_VERSION;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		Map<String, Object> additionalInfo = new HashMap<String, Object>();
		Authentication userAuthentication = authentication.getUserAuthentication();
		String clientId = authentication.getOAuth2Request().getClientId();
		ClientType clientType = ClientType.valueOf(clientId);
		switch (clientType) {
			case ACCULAB:
				additionalInfo.put("ignoreVersion", Boolean.TRUE);
				break;
			case ACCULINK:
				additionalInfo.put("version", SYSTEM_VERSION);
				break;
			case CERTACURE:
                additionalInfo.put("ignoreVersion", Boolean.TRUE);
                break;
			
		}
		SecUser user = (SecUser) userAuthentication.getPrincipal();
		additionalInfo.put("user", JSONUtil.convertJSONToObject(JSONUtil.convertObjectToJSON(user), SecUser.class));
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		return accessToken;
	}

}
