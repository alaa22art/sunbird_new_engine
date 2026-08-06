package com.sunbird.machine.web.lkp.wrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.common.helper.FilterablePageRequest;

public class LkpWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	private String className;
	private FilterablePageRequest filterablePageRequest;
	private String[] joins;

	public LkpWrapper() {
	}

	public List<SearchCriterion> getFilterableFilters() {
		return Optional	.ofNullable(this.filterablePageRequest)
						.map(FilterablePageRequest::getFilters).orElse(new ArrayList<>());
	}

	public Sort getFilterableSort() {
		Sort sort = null;
		//apply default sorting by code only if the class is a Lkp, backward compatibility issue, we can remove it after everyone start using the "data" option in the directive
		if (this.className.startsWith("Lkp")) {
			sort = Sort.by(new Order(Direction.ASC, "code"));
		}
		return Optional	.ofNullable(this.filterablePageRequest)
						.map(FilterablePageRequest::getSortObject).orElse(sort);
	}

	public String[] getJoins() {
		return Optional.ofNullable(joins).orElse(new String[] {});
	}

	public void setJoins(String[] joins) {
		this.joins = joins;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public FilterablePageRequest getFilterablePageRequest() {
		return filterablePageRequest;
	}

	public void setFilterablePageRequest(FilterablePageRequest filterablePageRequest) {
		this.filterablePageRequest = filterablePageRequest;
	}

}
