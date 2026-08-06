package com.sunbird.core.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;
import com.sunbird.core.common.helper.Email;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * EmailUtil.java
 *

 **/
@Component("EmailUtil")
public class EmailUtil {

	@Autowired
	private JavaMailSenderImpl defaultMailSender;
	@Autowired
	private Configuration freemarkerConfig;

	public static JavaMailSenderImpl DEFAULT_MAIL_SENDER;
	public static Configuration FREE_MARKER_CONFIG;

	@PostConstruct
	public void init() {
		DEFAULT_MAIL_SENDER = defaultMailSender;
		FREE_MARKER_CONFIG = freemarkerConfig;
	}

	@Async
	public static void sendMail(Email email) {
		JavaMailSenderImpl sender = email.getSender();
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(sender.getUsername());
		message.setTo(email.getTo());
		message.setSubject(email.getSubject());
		message.setText(email.getMessage());

		try 
		{
			
			sender.send(message);
		} catch (MailAuthenticationException e) {
			throw new BusinessException(e.getMessage(), "emailAuthFail", ErrorSeverity.ERROR);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), "emailSendFail", ErrorSeverity.ERROR, Arrays.asList(e.getMessage()));
		}
	}

	@Async
	public static void sendMailTemplate(Email email) {
		try {
			JavaMailSenderImpl sender = email.getSender();
			MimeMessage message = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());

			if (!CollectionUtil.isMapEmpty(email.getAttachmentBytes())) 
			{
				for (Map.Entry<String, byte[]> entry : email.getAttachmentBytes().entrySet()) {
					helper.addAttachment(entry.getKey(), new ByteArrayResource(entry.getValue()));
				}
			}
			Template template = FREE_MARKER_CONFIG.getTemplate(email.getTemplateUri());
			String renderedHtml = FreeMarkerTemplateUtils.processTemplateIntoString(template, email.getTemplateValueMap());
			helper.setFrom(sender.getUsername());
			helper.setTo(email.getTo());
			helper.setSubject(email.getSubject());
			helper.setText(renderedHtml, true);

			sender.send(message);

		} catch (MailAuthenticationException | AddressException e) {
			throw new BusinessException(e.getMessage(), "emailAuthFail", ErrorSeverity.ERROR);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), "emailSendFail", ErrorSeverity.ERROR, Arrays.asList(e.getMessage()));
		}
	}

	public static JavaMailSenderImpl generateMailSender(String username, String password, String host, Integer port) {
		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		javaMailSenderImpl.setUsername(username);
		javaMailSenderImpl.setPassword(password);
		javaMailSenderImpl.setHost(host);
		javaMailSenderImpl.setPort(port);
		//set some default properties
		javaMailSenderImpl.setDefaultEncoding(DEFAULT_MAIL_SENDER.getDefaultEncoding());
		javaMailSenderImpl.setJavaMailProperties(DEFAULT_MAIL_SENDER.getJavaMailProperties());
		javaMailSenderImpl.setProtocol(DEFAULT_MAIL_SENDER.getProtocol());
		return javaMailSenderImpl;
	}

}
