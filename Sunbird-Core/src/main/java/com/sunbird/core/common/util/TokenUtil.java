package com.sunbird.core.common.util;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class TokenUtil {

	public static String DEFAULT_TOKEN_EXPIRATION;

	public static String FORGOT_TOKEN_EXPIRATION;

	public static String SYSTEM_VERSION;

	@Value("${system.version}")
	private String systemVersion;

	@Value("${system.token.secretKey}")
	private String SECRET_KEY;

	@Value("${system.token.defaultExpiration}")
	private String defaultTokenExpiration;

	@Value("${system.token.forgotExpiration}")
	private String forgotTokenExpiration;

	private final static AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
	private static Signer signer;

	@PostConstruct
	public void postConstructor() {
		signer = new MacSigner(SECRET_KEY);
		DEFAULT_TOKEN_EXPIRATION = defaultTokenExpiration;
		FORGOT_TOKEN_EXPIRATION = forgotTokenExpiration;
		SYSTEM_VERSION = systemVersion;
	}

	/**
	 * Get data from token.
	 * 
	 * @param token
	 * @return Map claims
	 */
	public static Map<String, Object> decodeToken(String token) {
		String claimsString = JwtHelper.decodeAndVerify(token, (SignatureVerifier) signer).getClaims();
		Map<String, Object> claims = JSONUtil.convertJSONToMap(claimsString, String.class, Object.class);
		verifyClaims(claims);
		return claims;
	}

	public static <T> T getObjectFromToken(String token, String key, Class<T> clazz) {
		Map<String, Object> claims = decodeToken(token);
		return JSONUtil.getMapper().convertValue(claims.get(key), clazz);
	}

	public static void verifyClaims(Map<String, Object> claims) {
		Boolean ignoreVersion = Boolean.FALSE;
		if (claims.get("ignoreVersion") != null) {
			ignoreVersion = (Boolean) claims.get("ignoreVersion");
		}
		if (!ignoreVersion) {
			String tokenVersion = claims.get("version") != null ? (String) claims.get("version") : null;
			if (StringUtil.isEmpty(tokenVersion) || !tokenVersion.equals(SYSTEM_VERSION)) {
				throw new InvalidTokenException("invalidVersionNumber");
			}
		}
		if (claims.containsKey("exp") && DateUtil.isBefore(new Date(Long.valueOf(claims.get("exp").toString()) * 1000L), new Date())) {
			throw new InvalidTokenException("accessTokenExpired");
		}
	}

	public static String encodeToken(Map<String, Object> claims) {
		String content;
		Object user = claims.get("user");
		if (user != null) {
			JsonNode jsonNode = JSONUtil.getMapper().convertValue(user, JsonNode.class);

			Iterator<Entry<String, JsonNode>> iterator = jsonNode.fields();
			List<String> fieldsToKeep = Arrays.asList("rid", "tenantId", "branchId", "username", "email", "role");

			while (iterator.hasNext()) {
				Entry<String, JsonNode> entry = iterator.next();
				String key = entry.getKey();
				JsonNode value = entry.getValue();
				if (!fieldsToKeep.contains(key) || value.isNull()) {
					iterator.remove();
				}
			}

			claims.put("user", jsonNode);
		}
		content = JSONUtil.convertObjectToJSON(claims);
		return JwtHelper.encode(content, signer).getEncoded();
	}

	@SuppressWarnings("unchecked")
	public static String encodeCurrentAuthenticationToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		Map<String, Object> info = (Map<String, Object>) tokenConverter.convertAccessToken(accessToken, authentication);
		return encodeToken(info);
	}

}