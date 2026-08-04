package com.certacure.app;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.http.HttpProperties.Encoding;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.certacure.core.base.repo.BaseRepositoryImpl;
import com.certacure.lis.interfaces.middleware.core.ActorSysContainer;
import com.certacure.lis.interfaces.middleware.core.Master;
import com.certacure.lis.interfaces.service.MachineService;

import akka.actor.ActorSystem;
import akka.actor.Props;
import freemarker.template.TemplateModel;
import kr.pe.kwonnam.freemarker.inheritance.BlockDirective;
import kr.pe.kwonnam.freemarker.inheritance.ExtendsDirective;
import kr.pe.kwonnam.freemarker.inheritance.PutDirective;

@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication
@ComponentScan(basePackages = { "com.certacure" })
@EnableAutoConfiguration
@EntityScan("com.certacure")
@EnableJpaRepositories(basePackages = "com.certacure.**.repo", repositoryBaseClass = BaseRepositoryImpl.class)
@EnableCaching
@EnableScheduling
public class Application extends SpringBootServletInitializer {

	public static ActorSystem system;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);

	}

	@Autowired
	@Bean
	public CommandLineRunner run(MachineService machineService) {

		/*
		 * String value = "ÇáÍæÑÇäí"; String retValue = ""; String convertValue2 = "";
		 * ByteBuffer convertedBytes = null; try { CharsetEncoder encoder2 =
		 * Charset.forName("ISO-8859-1").newEncoder(); CharsetEncoder encoder3 =
		 * Charset.forName("UTF-8").newEncoder(); System.out.println("value = " +
		 * value);
		 * 
		 * assert encoder2.canEncode(value); assert encoder3.canEncode(value);
		 * 
		 * ByteBuffer conv1Bytes =
		 * encoder2.encode(CharBuffer.wrap(value.toCharArray()));
		 * 
		 * retValue = new String(conv1Bytes.array(), Charset.forName("Windows-1256"));
		 * 
		 * System.out.println("retValue = " + retValue);
		 * 
		 * convertedBytes = encoder3.encode(CharBuffer.wrap(retValue.toCharArray()));
		 * convertValue2 = new String(convertedBytes.array(), Charset.forName("UTF-8"));
		 * System.out.println("convertedValue =" + convertValue2); } catch (Exception e)
		 * { e.printStackTrace(); }
		 */

		return (args) -> {
			system = ActorSysContainer.getInstance().getSystem();
			system.actorOf(Props.create(Master.class), "Master");
		};
	}

	@Bean
	public Map<String, TemplateModel> freemarkerLayoutDirectives() {
		Map<String, TemplateModel> freemarkerLayoutDirectives = new HashMap<String, TemplateModel>();
		freemarkerLayoutDirectives.put("extends", new ExtendsDirective());
		freemarkerLayoutDirectives.put("block", new BlockDirective());
		freemarkerLayoutDirectives.put("put", new PutDirective());

		return freemarkerLayoutDirectives;
	}

	@Bean
	public FreeMarkerConfigurer freemarkerConfig() {
		FreeMarkerConfigurer freemarkerConfig = new FreeMarkerConfigurer();
		freemarkerConfig.setTemplateLoaderPath("/templates/");
		freemarkerConfig.setDefaultEncoding("UTF-8");
		Map<String, Object> freemarkerVariables = new HashMap<String, Object>();
		freemarkerVariables.put("layout", freemarkerLayoutDirectives());

		freemarkerConfig.setFreemarkerVariables(freemarkerVariables);
		return freemarkerConfig;
	}

}
