package com.certacure.core.base.entity;

import java.io.Serializable;
import java.util.LinkedHashMap;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.certacure.core.common.util.StringUtil;

/**
 * BaseEntity.java Super class for entities, used to unify the get primary key attribute name and to add common attributes
 *
 * @since may 03, 2017
 */
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer", "handler" })
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	@JsonDeserialize
	private Boolean markedForDeletion = false;

	@Transient
	@JsonProperty
	private LinkedHashMap<String, Object> transients = new LinkedHashMap<>();

	/**
	 *
	 * @return The entity ID (Primary key)
	 */
	public abstract Long getRid();

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [rid=" + getRid() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getRid() == null) ? 0 : getRid().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (getRid() == null) {
			if (other.getRid() != null)
				return false;
		} else if (!getRid().equals(other.getRid()))
			return false;
		return true;
	}

	/**
	 * @return A boolean to specify if the entity is marked for deletion
	 */
	public Boolean getMarkedForDeletion() {
		return markedForDeletion;
	}

	/**
	 * @param markedForDeletion Sets whether the entity is marked for deletion
	 */
	public void setMarkedForDeletion(Boolean markedForDeletion) {
		this.markedForDeletion = markedForDeletion;
	}

	public LinkedHashMap<String, Object> getTransients() {
		return transients;
	}

	public void setTransients(LinkedHashMap<String, Object> transients) {
		this.transients = transients;
	}

	@JsonIgnore
	public BaseEntity addTransient(String key, Object value) {
		if (StringUtil.isEmpty(key) || value == null) {
			return this;
		}
		if (getTransients() == null) {
			setTransients(new LinkedHashMap<>());
		}
		getTransients().put(key, value);
		return this;
	}

}
