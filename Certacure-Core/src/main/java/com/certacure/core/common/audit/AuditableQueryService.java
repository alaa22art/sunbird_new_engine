package com.certacure.core.common.audit;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditProperty;
import org.hibernate.envers.query.criteria.MatchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.common.helper.FilterablePageRequest.OrderObject;
import com.certacure.core.common.util.CollectionUtil;
import com.certacure.core.common.util.ReflectionUtil;

/**
 * AuditableQueryService.java
 * 
 * @author Abdullah Imran <aImran@certacuresolutions.com>
 * @since Dec/09/2019
 */
@Service("AuditableQueryService")
@Transactional(readOnly = false)
public class AuditableQueryService {

	@Autowired
	private EntityManager entityManager;

	/**
	 * Get the results.
	 * 
	 * @param auditableQuery
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> get(AuditableQuery auditableQuery) {
		Class<?> entityClass = ReflectionUtil.getEntityClassByName(auditableQuery.getEntityName(), entityManager);
		auditableQuery.setEntityClass(entityClass);
		AuditQuery query = AuditReaderFactory	.get(entityManager)
												.createQuery()
												.forRevisionsOfEntity(entityClass, auditableQuery.getIsSelectEntitiesOnly(),
														auditableQuery.getIsSelectDeletedEntities());

		addOrders(query, auditableQuery);
		addFilters(query, auditableQuery);
		addPagination(query, auditableQuery);
		addRevisionFilters(query, auditableQuery);
		//add projections?query.addProjection(projection)
		return query.getResultList();
	}

	/**
	 * Revision filters
	 * 
	 * @param query
	 * @param auditableQuery
	 */
	private void addRevisionFilters(AuditQuery query, AuditableQuery auditableQuery) {
		if (auditableQuery.getFilterablePageRequest() == null
				|| CollectionUtil.isCollectionEmpty(auditableQuery.getFilterablePageRequest().getFilters())) {
			return;
		}

		for (SearchCriterion filter : auditableQuery.getFilterablePageRequest().getFilters()) {
			String field = filter.getField();
			//only use filters that filter on revision fields
			if (!field.startsWith("revision")) {
				continue;
			}
			Object value = filter.getValue();
			filterField(query, field, filter.getOperator(), value);
		}
	}

	/**
	 * Filtering
	 * 
	 * @param query
	 * @param auditableQuery
	 */
	private void addFilters(AuditQuery query, AuditableQuery auditableQuery) {
		if (auditableQuery.getFilterablePageRequest() == null
				|| CollectionUtil.isCollectionEmpty(auditableQuery.getFilterablePageRequest().getFilters())) {
			return;
		}

		for (SearchCriterion filter : auditableQuery.getFilterablePageRequest().getFilters()) {
			Object value = filter.getValue();
			String field = filter.getField();
			//Casting Numbers to their appropriate types.
			//Because jackson treats any number as an Integer, we need to cast it to Long for rid as an example.
			Class<?> clazz = ReflectionUtil.getAllFieldTypes(auditableQuery.getEntityClass()).get(field).getType();

			if (clazz.getSuperclass().equals(Number.class)) {
				if (clazz.equals(Long.class)) {
					value = Long.valueOf(value.toString());
				} else if (clazz.equals(Integer.class)) {
					value = Integer.valueOf(value.toString());
				}
			}

			filterField(query, field, filter.getOperator(), value);
		}
	}

	/**
	 * Add the actual filter to the query.
	 * 
	 * @param query
	 * @param auditProperty
	 * @param operator
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private <T> void filterField(AuditQuery query, String field, FilterOperator operator, T value) {
		AuditProperty<T> auditProperty = null;
		if (field.equals("revisionNumber")) {
			auditProperty = (AuditProperty<T>) AuditEntity.revisionNumber();
		} else if (field.equals("revisionType")) {
			auditProperty = (AuditProperty<T>) AuditEntity.revisionType();
		} else {
			auditProperty = (AuditProperty<T>) AuditEntity.property(field);
		}

		switch (operator) {
			case contains:
				query.add(auditProperty.like(value.toString(), MatchMode.ANYWHERE));
				break;
			case startswith:
				query.add(auditProperty.like(value.toString(), MatchMode.START));
				break;
			case endswith:
				query.add(auditProperty.like(value.toString(), MatchMode.END));
				break;
			case eq:
				query.add(auditProperty.eq(value));
				break;
			case neq:
				query.add(auditProperty.ne(value));
				break;
			case gt:
				query.add(auditProperty.gt(value));
				break;
			case gte:
				query.add(auditProperty.ge(value));
				break;
			case lt:
				query.add(auditProperty.lt(value));
				break;
			case lte:
				query.add(auditProperty.le(value));
				break;
			case in:
				query.add(auditProperty.in((Collection<?>) value));
				break;
			case isempty:
				query.add(auditProperty.eq((T) ""));
				break;
			case isnotempty:
				query.add(auditProperty.ne((T) ""));
				break;
			case isnotnull:
				query.add(auditProperty.isNotNull());
				break;
			case isnull:
				query.add(auditProperty.isNull());
				break;
			case notin://cant find a way to do it
			case doesnotcontain://cant find a way to do it
			default:
				break;
		}
	}

	/**
	 * Sorting
	 * 
	 * @param query
	 * @param auditableQuery
	 */
	private void addOrders(AuditQuery query, AuditableQuery auditableQuery) {
		if (auditableQuery.getFilterablePageRequest() == null
				|| CollectionUtil.isCollectionEmpty(auditableQuery.getFilterablePageRequest().getSortList())) {
			return;
		}
		for (OrderObject order : auditableQuery.getFilterablePageRequest().getSortList()) {
			AuditProperty<?> auditProperty = AuditEntity.property(order.getProperty());
			if (order.getDirection() == Direction.ASC) {
				query.addOrder(auditProperty.asc());
			} else {
				query.addOrder(auditProperty.desc());
			}
		}
	}

	/**
	 * Pagination, page number starts from 0
	 * 
	 * @param query
	 * @param auditableQuery
	 */
	private void addPagination(AuditQuery query, AuditableQuery auditableQuery) {
		int pageSize = auditableQuery.getFilterablePageRequest().getSize();
		int pageNumber = auditableQuery.getFilterablePageRequest().getPage();
		query.setFirstResult(pageNumber * pageSize);
		query.setMaxResults(pageSize);
	}

	//	/**
	//	 * Joins, add joins to a query
	//	 * 
	//	 * @param query
	//	 * @param auditableQuery
	//	 */
	//	private void addJoins(AuditQuery query, AuditableQuery auditableQuery) {
	////TODO implement addJoins
	//	}

	public Page<?> getAuditPage(AuditableQuery auditableQuery) {
		Class<?> entityClass = ReflectionUtil.getEntityClassByName(auditableQuery.getEntityName(), entityManager);
		auditableQuery.setEntityClass(entityClass);

		AuditQuery query = AuditReaderFactory	.get(entityManager)
												.createQuery()
												.forRevisionsOfEntity(entityClass,
														auditableQuery.getIsSelectEntitiesOnly(),
														auditableQuery.getIsSelectDeletedEntities())
												.addProjection(AuditEntity.revisionNumber().count());

		addFilters(query, auditableQuery);

		Integer totalSize = Integer.valueOf(query.getSingleResult().toString());

		List<Object[]> auditList = get(auditableQuery);

		Page<?> auditPage = new PageImpl<>(
				auditList,
				auditableQuery.getFilterablePageRequest().getPageRequest(),
				totalSize);
		return auditPage;
	}

}
