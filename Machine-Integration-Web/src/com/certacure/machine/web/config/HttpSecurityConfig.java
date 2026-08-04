package com.certacure.machine.web.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

public class HttpSecurityConfig {

	public static void configure(HttpSecurity http) throws Exception {
		http
			.cors().and()
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			.regexMatchers(HttpMethod.POST, ".+[.]pub[.]srvc").permitAll()
			.anyRequest().authenticated()
			.and()
			.exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
	}
}
