package com.certacure.core.common.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.certacure.core.base.entity.BaseWrapper;
import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.common.util.CollectionUtil;
import com.certacure.core.common.util.DateUtil;
import com.certacure.core.common.util.StringUtil;

public class FilterablePageRequest extends BaseWrapper {

	private static final long serialVersionUID = 1L;
	private List<SearchCriterion> filters;
	private Integer page;
	private Integer size;
	private List<OrderObject> sortList;

	public FilterablePageRequest() {
		super();
		this.page = 0;
		this.size = 10;
		this.filters = new ArrayList<>();
		this.sortList = new ArrayList<>();
	}

	public FilterablePageRequest(Integer page, Integer size, List<SearchCriterion> filterList,
			List<OrderObject> sortList) {
		super();
		this.page = page;
		this.size = size;
		this.filters = filterList;
		this.sortList = sortList;
	}

	public static class OrderObject extends BaseWrapper {

		private static final long serialVersionUID = 1L;

		private Direction direction;
		private String property;

		public OrderObject() {
			super();
		}

		public OrderObject(Direction direction, String property) {
			super();
			this.direction = direction;
			this.property = property;
		}

		public Direction getDirection() {
			return direction;
		}

		public void setDirection(Direction direction) {
			this.direction = direction;
		}

		public String getProperty() {
			return property;
		}

		public void setProperty(String property) {
			this.property = property;
		}

		@Override
		public String toString() {
			return "OrderObject [direction=" + direction + ", property=" + property + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((direction == null) ? 0 : direction.hashCode());
			result = prime * result + ((property == null) ? 0 : property.hashCode());
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
			OrderObject other = (OrderObject) obj;
			if (direction != other.direction)
				return false;
			if (property == null) {
				if (other.property != null)
					return false;
			} else if (!property.equals(other.property))
				return false;
			return true;
		}

	}

	public Pageable getPageRequest() {
		return PageRequest.of(page, size, getSortObject());
	}

	public Sort getSortObject() {
		List<Order> orderList = new ArrayList<Order>();
		Sort sort = Sort.by(Direction.DESC, "rid");//default sort
		if (!CollectionUtil.isCollectionEmpty(sortList)) {
			for (OrderObject order : sortList) {
				orderList.add(new Order(order.getDirection(), order.getProperty()));
			}
			sort = Sort.by(orderList);
		}

		return sort;
	}

	public List<SearchCriterion> getFilters() {
		return filters;
	}

	public void setFilters(List<SearchCriterion> filters) {
		this.filters = filters;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public List<OrderObject> getSortList() {
		return this.sortList;
	}

	public void setSortList(List<OrderObject> sortList) {
		this.sortList = sortList;
	}

	@JsonIgnore
	public SearchCriterion getFilterByField(String field) {
		for (SearchCriterion sc : filters) {
			if (!StringUtil.isEmpty(sc.getField()) && sc.getField().equals(field)) {
				return sc;
			}
		}
		return null;
	}

	@JsonIgnore
	public String getStringFilter(String field) {
		SearchCriterion searchCriterion = getFilterByField(field);
		if (searchCriterion == null || searchCriterion.getValue() == null) {
			return null;
		}
		return (String) searchCriterion.getValue();
	}

	@JsonIgnore
	public Date getDateFilter(String field) {
		SearchCriterion searchCriterion = getFilterByField(field);
		if (searchCriterion == null || searchCriterion.getValue() == null) {
			return null;
		}
		return DateUtil.parseUTCDate((String) searchCriterion.getValue());
	}

	@JsonIgnore
	public Long getLongFilter(String field) {
		SearchCriterion searchCriterion = getFilterByField(field);
		if (searchCriterion == null || searchCriterion.getValue() == null) {
			return null;
		}
		//we are not casting here since we can get an Integer,String and Long in getValue(), so get their string and parse it.
		return Long.parseLong(searchCriterion.getValue().toString());
	}

	@JsonIgnore
	public Boolean getBooleanFilter(String field) {
		SearchCriterion searchCriterion = getFilterByField(field);
		if (searchCriterion == null || searchCriterion.getValue() == null) {
			return null;
		}
		return Boolean.valueOf((boolean) searchCriterion.getValue());
	}

	@JsonIgnore
	public void addToFilters(SearchCriterion searchCriterion) {
		if (CollectionUtil.isCollectionEmpty(getFilters())) {
			setFilters(new ArrayList<>());
		}
		getFilters().add(searchCriterion);
	}

	@JsonIgnore
	public void removeFromFilters(String field) {
		if (!CollectionUtil.isCollectionEmpty(getFilters()) && !StringUtil.isEmpty(field)) {
			getFilters().removeIf(f -> f.getField().equals(field));
		}
	}

	@JsonIgnore
	public void removeFromFilters(String... fields) {
		if (!CollectionUtil.isCollectionEmpty(getFilters()) && !CollectionUtil.isArrayEmpty(fields)) {
			getFilters().removeIf(f -> Arrays.asList(fields).contains(f.getField()));
		}
	}

	@JsonIgnore
	public void addToSortList(OrderObject orderObject) {
		if (CollectionUtil.isCollectionEmpty(getSortList())) {
			setSortList(new ArrayList<>());
		}
		getSortList().add(orderObject);
	}

	@JsonIgnore
	public void removeFromSortList(String property) {
		if (!CollectionUtil.isCollectionEmpty(getSortList()) && !StringUtil.isEmpty(property)) {
			getSortList().removeIf(f -> f.getProperty().equals(property));
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FilterablePageRequest [filters=");
		builder.append(filters);
		builder.append(", page=");
		builder.append(page);
		builder.append(", size=");
		builder.append(size);
		builder.append(", sortList=");
		builder.append(sortList);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filters == null) ? 0 : filters.hashCode());
		result = prime * result + ((page == null) ? 0 : page.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((sortList == null) ? 0 : sortList.hashCode());
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
		FilterablePageRequest other = (FilterablePageRequest) obj;
		if (filters == null) {
			if (other.filters != null)
				return false;
		} else if (!filters.equals(other.filters))
			return false;
		if (page == null) {
			if (other.page != null)
				return false;
		} else if (!page.equals(other.page))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (sortList == null) {
			if (other.sortList != null)
				return false;
		} else if (!sortList.equals(other.sortList))
			return false;
		return true;
	}

}
