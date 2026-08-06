package com.sunbird.core.base.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * BaseWrapper.java
 * 
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

}