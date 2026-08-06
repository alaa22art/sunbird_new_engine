package com.sunbird.core.common.audit;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunbird.core.base.entity.BaseWrapper;
import com.sunbird.core.common.helper.FilterablePageRequest;

public class AuditableQuery extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private String entityName;
	private boolean isSelectEntitiesOnly;
	private boolean isSelectDeletedEntities;
	private FilterablePageRequest filterablePageRequest;
	private List<String> entityJoins;

	@JsonIgnore
	private Class<?> entityClass;//internal uses

	public AuditableQuery() {
		super();
	}

	public AuditableQuery(String className, FilterablePageRequest filterablePageRequest) {
		this(className, false, true, filterablePageRequest);
	}

	public AuditableQuery(String entityName, boolean isSelectEntitiesOnly, boolean isSelectDeletedEntities,
			FilterablePageRequest filterablePageRequest) {
		super();
		this.entityName = entityName;
		this.isSelectEntitiesOnly = isSelectEntitiesOnly;
		this.isSelectDeletedEntities = isSelectDeletedEntities;
		this.filterablePageRequest = filterablePageRequest;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public boolean getIsSelectEntitiesOnly() {
		return isSelectEntitiesOnly;
	}

	public void setSelectEntitiesOnly(boolean isSelectEntitiesOnly) {
		this.isSelectEntitiesOnly = isSelectEntitiesOnly;
	}

	public boolean getIsSelectDeletedEntities() {
		return isSelectDeletedEntities;
	}

	public void setSelectDeletedEntities(boolean isSelectDeletedEntities) {
		this.isSelectDeletedEntities = isSelectDeletedEntities;
	}

	public FilterablePageRequest getFilterablePageRequest() {
		return filterablePageRequest;
	}

	public void setFilterablePageRequest(FilterablePageRequest filterablePageRequest) {
		this.filterablePageRequest = filterablePageRequest;
	}

	public List<String> getEntityJoins() {
		return entityJoins;
	}

	public void setEntityJoins(List<String> entityJoins) {
		this.entityJoins = entityJoins;
	}

}
