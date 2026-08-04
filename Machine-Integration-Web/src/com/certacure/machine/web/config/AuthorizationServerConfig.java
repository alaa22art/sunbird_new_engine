package com.certacure.machine.web.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.certacure.core.common.util.TokenUtil;
import com.certacure.lis.interfaces.security.ClientType;
import com.certacure.machine.web.security.common.CustomTokenConverter;
import com.certacure.machine.web.security.common.CustomTokenEnhancer;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${system.token.secretKey}")
	private String TOKEN_SIGNING_KEY;

	@Value("${system.token.defaultRefreshExpiration}")
	private String REFRESH_TOKEN_EXP;

	//Client details (this is for xxxx integration only)
	@Value("${system.certacure.rcm.secret}")
	private String CERTACURE_RCM_SECRET;

	//Client details (this is public for web-apps)
	@Value("${system.certacure.engine.secret}")
	private String CERTACURE_ENGINE_SECRET;

	//Grant types
	private static final String GRANT_TYPE_PASSWORD = "password";
	//	private static final String AUTHORIZATION_CODE = "authorization_code";
	private static final String REFRESH_TOKEN = "refresh_token";
	//	private static final String IMPLICIT = "implicit";

	//Scopes
	//This is needed for the oauth/token request to work
	//It is a string which can be used for authentication inside 
	//controllers/services using @PreAuthorize("#oauth2.hasScope('scope_name')")
	private static final String SCOPE = "SCOPE";

	//Token validity
	private static final int VALID_FOREVER = -1;

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new CustomTokenConverter();
		converter.setSigningKey(TOKEN_SIGNING_KEY);
		return converter;
	}

	public ClientDetailsService clientDetailsService() throws Exception {
		InMemoryClientDetailsServiceBuilder builder = new InMemoryClientDetailsServiceBuilder();
		return builder	.withClient(ClientType.ACCULINK.toString())
						.secret(new BCryptPasswordEncoder().encode(CERTACURE_ENGINE_SECRET))
						.authorizedGrantTypes(GRANT_TYPE_PASSWORD, REFRESH_TOKEN)
						.scopes(SCOPE)
						.accessTokenValiditySeconds(Integer.parseInt(TokenUtil.DEFAULT_TOKEN_EXPIRATION))
						.refreshTokenValiditySeconds(Integer.parseInt(REFRESH_TOKEN_EXP))
						.and()
						.withClient(ClientType.CERTACURE.toString())
						.secret(new BCryptPasswordEncoder().encode(CERTACURE_RCM_SECRET))
						.authorizedGrantTypes(GRANT_TYPE_PASSWORD)
						.scopes(SCOPE)
						.accessTokenValiditySeconds(VALID_FOREVER)
						.and()
						.build();
	}

	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		return defaultTokenServices;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));

		endpoints	.tokenStore(tokenStore()).tokenEnhancer(tokenEnhancerChain)
					.authenticationManager(authenticationManager);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.allowFormAuthenticationForClients();
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService());
	}
}
