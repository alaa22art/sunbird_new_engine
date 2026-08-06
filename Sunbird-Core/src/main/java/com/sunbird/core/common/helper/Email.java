package com.sunbird.core.common.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Email.java
 * 
 **/
public class Email {

	private String templateUri;
	private String message;
	private String to;
	private String subject;
	private Map<String, String> templateValueMap;
	private Map<String, byte[]> attachmentBytes;
	private JavaMailSenderImpl sender;

	public Email(String to, String subject, String message) {
		this.to = to;
		this.subject = subject;
		this.message = message;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public JavaMailSenderImpl getSender() {
		return sender;
	}

	public void setSender(JavaMailSenderImpl sender) {
		this.sender = sender;
	}

	public Map<String, byte[]> getAttachmentBytes() {
		return attachmentBytes;
	}

	public void setAttachmentBytes(Map<String, byte[]> attachmentBytes) {
		this.attachmentBytes = attachmentBytes;
	}

	public void addAttachmentBytes(String key, byte[] value) {
		if (getAttachmentBytes() == null) {
			setAttachmentBytes(new HashMap<>());
		}
		getAttachmentBytes().put(key, value);
	}

	public String getTemplateUri() {
		return templateUri;
	}

	public void setTemplateUri(String templateUri) {
		this.templateUri = templateUri + ".ftl";
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Map<String, String> getTemplateValueMap() {
		return templateValueMap;
	}

	public void setTemplateValueMap(Map<String, String> templateValueMap) {
		this.templateValueMap = templateValueMap;
	}

}
