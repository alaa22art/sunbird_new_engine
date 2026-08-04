package com.certacure.core.base.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

import com.certacure.core.base.entity.BaseEntity;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.helper.SearchCriterion.JunctionOperator;
import com.certacure.core.base.repo.GenericRepository;
import com.certacure.core.common.helper.FilterablePageRequest;
import com.certacure.core.common.helper.FilterablePageRequest.OrderObject;
import com.certacure.core.common.util.CollectionUtil;
import com.certacure.core.common.util.ReflectionUtil;

public class Finder<E extends BaseEntity> {

	private List<SearchCriterion> filters;
	private Boolean distinct;
	private List<String> joins;
	private FilterablePageRequest filterablePageRequest;

	private GenericRepository<E> repo;
	private Boolean hasCollectionJoin;

	public Finder(GenericRepository<E> repo) {
		super();
		this.repo = repo;
		reset();
	}

	public void reset() {
		filters = new ArrayList<SearchCriterion>();
		distinct = false;
		joins = new ArrayList<String>();
		filterablePageRequest = new FilterablePageRequest();
		hasCollectionJoin = Boolean.FALSE;
	}

	public List<E> findList() {
		return repo.find(filters, null, filterablePageRequest.getSortObject(), distinct, joins.stream().toArray(String[]::new));
	}

	public Set<E> findSet() {
		return new HashSet<E>(findList());
	}

	/**
	 * {@link #setPage(Integer)} and {@link #setSize(Integer)} must be called prior to calling this method.
	 * 
	 * @return A {@link Page} object containing a list of content and other information
	 */
	public Page<E> findPage() {
		if (hasCollectionJoin) {
			return ReflectionUtil.findPageWithJoins(repo, null, filterablePageRequest, joins.stream().toArray(String[]::new));
		} else {
			return repo.find(filters, filterablePageRequest.getPageRequest(), null, distinct, joins.stream().toArray(String[]::new));
		}
	}

	public E findOne() {
		return repo.findOne(filters, null, joins.stream().toArray(String[]::new));
	}

	public long count() {
		return repo.count(filters, null, distinct);
	}

	public Finder<E> addFilters(List<SearchCriterion> filters) {
		this.filters.addAll(filters);
		return this;
	}

	public Finder<E> addFilter(SearchCriterion filter) {
		this.filters.add(filter);
		return this;
	}

	public Finder<E> addFilter(String field, Object value) {
		return addFilter(new SearchCriterion(field, value, FilterOperator.eq));
	}

	public Finder<E> addFilter(String field, Object value, FilterOperator operator) {
		return addFilter(new SearchCriterion(field, value, operator));
	}

	public Finder<E> addFilter(String field, Object value, FilterOperator operator, JunctionOperator junctionOperator) {
		return addFilter(new SearchCriterion(field, value, operator, junctionOperator));
	}

	public Finder<E> addRidFilter(Long rid, FilterOperator operator) {
		return addFilter("rid", rid, operator);
	}

	public Finder<E> addRidFilter(Long rid) {
		return addRidFilter(rid, FilterOperator.eq);
	}

	public Finder<E> distinct(Boolean distinct) {
		this.distinct = distinct;
		return this;
	}

	public Finder<E> addJoins(String... joins) {
		for (String join : joins) {
			this.joins.add(join);
		}
		return this;
	}

	public Finder<E> setPage(Integer page) {
		this.filterablePageRequest.setPage(page);
		return this;
	}

	public Finder<E> setSize(Integer size) {
		this.filterablePageRequest.setSize(size);
		return this;
	}

	public Finder<E> addSortList(List<OrderObject> sortList) {
		if (CollectionUtil.isCollectionEmpty(filterablePageRequest.getSortList())) {
			filterablePageRequest.setSortList(sortList);
		} else {
			filterablePageRequest.getSortList().addAll(sortList);
		}
		return this;
	}

	public Finder<E> addSort(OrderObject sort) {
		List<OrderObject> sortList = filterablePageRequest.getSortList();
		if (CollectionUtil.isCollectionEmpty(sortList)) {
			sortList = new ArrayList<OrderObject>();
		}
		sortList.add(sort);
		filterablePageRequest.setSortList(sortList);
		return this;
	}

	public Finder<E> addSort(Direction direction, String property) {
		return addSort(new OrderObject(direction, property));
	}

	/**
	 * This function does the following:
	 * <br>
	 * 1- Sets the sortList (overwrites existing sort)
	 * <br>
	 * 2- Adds the filters inside filterablePageRequest to the filterList
	 * <br>
	 * 3- Sets the pageNumber
	 * <br>
	 * 4- Sets the pageSize
	 * <br>
	 * 
	 * @param filterablePageRequest
	 * @return Return the Finder object for chaining
	 */
	public Finder<E> setFilterablePageRequest(FilterablePageRequest filterablePageRequest) {
		addFilters(filterablePageRequest.getFilters());
		this.filterablePageRequest = filterablePageRequest;
		return this;
	}

	/**
	 * Call it when having a list field as a join.
	 * 
	 * Maybe use ReflectionUtil.getDeepFieldType(...) to improve it.
	 * 
	 * @return Return the Finder object for chaining
	 */
	public Finder<E> hasCollectionJoin() {
		hasCollectionJoin = Boolean.TRUE;
		return this;
	}

}
