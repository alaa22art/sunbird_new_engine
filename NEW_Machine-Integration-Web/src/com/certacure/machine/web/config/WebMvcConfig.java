package com.certacure.machine.web.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.certacure.core.common.util.HttpUtil;
import com.certacure.core.common.util.JSONUtil;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Value("${system.website.base}")
	private String SERVER_BASE;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		List<String> urls = HttpUtil.generateAllowedOrigins(SERVER_BASE);
		String[] origins = new String[urls.size()];
		origins = urls.toArray(origins);
		registry.addMapping("/**")
				.allowedOrigins(origins)
				.allowedMethods("POST");
	}

	/*
	 * Here we register the Hibernate5Module into an ObjectMapper, then set this custom-configured ObjectMapper
	 * to the MessageConverter and return it to be added to the HttpMessageConverters of our application
	 */
	public MappingJackson2HttpMessageConverter jacksonMessageConverter() {
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		messageConverter.setObjectMapper(JSONUtil.getMapper());
		return messageConverter;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		//Here we add our custom-configured HttpMessageConverter
		converters.removeIf(c -> c	.getClass()
									.equals(MappingJackson2HttpMessageConverter.class));
		converters.add(jacksonMessageConverter());
		converters.add(new ResourceHttpMessageConverter());//used for returning files such as pdf
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

}
