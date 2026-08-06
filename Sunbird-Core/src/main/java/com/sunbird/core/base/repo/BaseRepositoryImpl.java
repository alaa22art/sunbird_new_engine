package com.certacure.core.base.repo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.type.TextType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.certacure.core.base.entity.BaseEntity;
import com.certacure.core.base.helper.JoinWrapper;
import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.JunctionOperator;
import com.certacure.core.common.data.model.TransField;
import com.certacure.core.common.util.DateUtil;

public class BaseRepositoryImpl<T extends BaseEntity, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements BaseRepository<T, ID> {

	private final EntityManager entityManager;

	public BaseRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) throws ClassNotFoundException {
		super(entityInformation, entityManager);

		this.entityManager = entityManager;
		//this.entityClass = entityInformation.getJavaType();
	}

	@Override
	public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
		List<S> entityList = super.saveAll(entities);
		super.flush();
		return entityList;
	}

	@Override
	public void delete(T entity) {
		super.delete(entity);
		super.flush();
	}

	@Override
	public boolean isRelationLoaded(BaseEntity entity, String attributeName) {
		if (entity == null)
			return true;

		PersistenceUnitUtil unitUtil = getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();
		return unitUtil.isLoaded(entity, attributeName);
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public boolean isEntityPersisted(BaseEntity entity) {
		if (entity == null || entity.getRid() == null) {
			return false;
		}

		return entityManager.contains(entity) || entityManager.find(entity.getClass(), entity.getRid()) != null;
	}

	@Override
	public Page<T> find(List<SearchCriterion> filters, Pageable pageable, Class<T> entityClass,
			String... join) {
		return findAll(getFilterSpecification(filters, entityClass, false, join), pageable);
	}

	@Override
	public List<T> find(List<SearchCriterion> filters, Class<T> entityClass, Sort sort,
			String... join) {
		sort = addDefaultSort(sort);
		return findAll(getFilterSpecification(filters, entityClass, false, join), sort);
	}

	@Override
	public List<T> find(List<SearchCriterion> filters, Class<T> entityClass, String... join) {
		return findAll(getFilterSpecification(filters, entityClass, false, join), addDefaultSort(null));
	}

	@Override
	public T findOne(List<SearchCriterion> filters, Class<T> entityClass, String... join) {
		return findOne(getFilterSpecification(filters, entityClass, false, join)).orElse(null);
	}

	private Predicate alwaysTrue(CriteriaBuilder builder) {
		return builder.isTrue(builder.literal(true));
	}

	private <X> void addFetches(JoinWrapper<X> joinWrapper, String[] keys, int level) {
		JoinWrapper<X> childWrapper = joinWrapper.get(keys[level]);
		if (childWrapper == null) {
			childWrapper = new JoinWrapper<X>();
			joinWrapper.put(keys[level], childWrapper);
			childWrapper.setFetch(joinWrapper.getFetch().fetch(keys[level], JoinType.LEFT));
		}
		if (level < keys.length - 1) {
			addFetches(childWrapper, keys, level + 1);
		}
	}

	private Specification<T> getFilterSpecification(List<SearchCriterion> filterValues, Class<T> entityClass, Boolean distinct,
			String... join) {
		return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) ->
			{
				if (distinct) {
					query.distinct(distinct);
				}
				if (query.getResultType().equals(getDomainClass())) {
					JoinWrapper<T> rootWrapper = new JoinWrapper<T>();
					for (String joinName : join) {
						String[] keys = joinName.split("[.]");

						JoinWrapper<T> joinWrapper = rootWrapper.get(keys[0]);
						if (joinWrapper == null) {
							joinWrapper = new JoinWrapper<T>();
							rootWrapper.put(keys[0], joinWrapper);
							joinWrapper.setFetch(root.fetch(keys[0], JoinType.LEFT));
						}

						if (keys.length > 1) {
							addFetches(joinWrapper, keys, 1);
						}
					}
				}

				Optional<Predicate> orPredicate = filterValues	.stream()
																.filter(entry -> predicateFilter(JunctionOperator.Or, entry))
																.map(entry -> getPredicate(root, builder, entry, entityClass, join))
																.reduce((a, b) -> builder.or(a, b));

				Optional<Predicate> andPredicate = filterValues	.stream()
																.filter(entry -> predicateFilter(JunctionOperator.And, entry))
																.map(entry -> getPredicate(root, builder, entry, entityClass, join))
																.reduce((a, b) -> builder.and(a, b));

				List<Predicate> predicates = new ArrayList<Predicate>();
				if (andPredicate.isPresent()) {
					predicates.add(andPredicate.get());
				}
				if (orPredicate.isPresent()) {
					predicates.add(orPredicate.get());
				}
				Optional<Predicate> predicate = predicates.stream().reduce((a, b) -> builder.and(a, b));
				return predicate.orElseGet(() -> alwaysTrue(builder));
			};
	}

	private Boolean predicateFilter(JunctionOperator junctionOperator, SearchCriterion entry) {
		if (entry.getJunctionOperator() != junctionOperator) {
			return false;
		}
		switch (entry.getOperator()) {
			default:
				break;
			case isnull:
			case isnotnull:
			case isnotempty:
			case isempty:
				return true;
		}
		return entry.getValue() != null;
	}

	@SuppressWarnings("unchecked")
	private Predicate getPredicate(Root<T> root, CriteriaBuilder builder, SearchCriterion entry, Class<T> entityClass, String... join) {
		Object value = entry.getValue();
		Path<?> path = root;

		String[] keys = entry.getField().split("[.]");
		for (String key : keys) {
			path = path.get(key);//keep diving until you create the full path
		}

		Class<?> deepFieldType = path.getJavaType();
		//Class<?> deepFieldType = ReflectionUtil.getDeepFieldType(entityClass, entry.getField());
		if (deepFieldType.equals(Date.class) && !(value instanceof Date)) {
			value = DateUtil.parseUTCDate((String) value);
		}

		Expression<String> strKeyExpr;
		// Cast to TEXT instead of varchar(255)[default] if the field is a TransField
		if (deepFieldType.equals(TransField.class)) {
			strKeyExpr = builder.function("lower", String.class, path.as(TextType.class));
		} else {
			strKeyExpr = builder.lower(path.as(String.class));
		}

		Predicate returnPredicate = null;

		switch (entry.getOperator()) {
			case contains:
				returnPredicate = builder.like(strKeyExpr, ("%" + value + "%").toLowerCase());
				break;
			case doesnotcontain:
				returnPredicate = builder.notLike(strKeyExpr, ("%" + value + "%").toLowerCase());
				break;
			case startswith:
				returnPredicate = builder.like(strKeyExpr, (value + "%").toLowerCase());
				break;
			case endswith:
				returnPredicate = builder.like(strKeyExpr, ("%" + value).toLowerCase());
				break;
			case isempty:
				returnPredicate = builder.equal(strKeyExpr, "");
				break;
			case isnotempty:
				returnPredicate = builder.notEqual(strKeyExpr, "");
				break;
			case eq:
				if (value instanceof String) {
					returnPredicate = builder.equal(strKeyExpr, value.toString().toLowerCase());
				} else {
					returnPredicate = builder.equal(path, value);
				}
				break;
			case neq:
				if (value instanceof String) {
					returnPredicate = builder.notEqual(strKeyExpr, value.toString().toLowerCase());
				} else {
					returnPredicate = builder.notEqual(path, value);
				}
				break;
			case gt:
				if (value instanceof Date) {
					returnPredicate = builder.greaterThan((Expression<? extends Date>) path, (Date) value);
				} else {
					returnPredicate = builder.gt((Expression<? extends Number>) path, (Number) value);
				}
				break;
			case gte:
				if (value instanceof Date) {
					returnPredicate = builder.greaterThanOrEqualTo((Expression<? extends Date>) path, (Date) value);
				} else {
					returnPredicate = builder.ge((Expression<? extends Number>) path, (Number) value);
				}
				break;
			case lt:
				if (value instanceof Date) {
					returnPredicate = builder.lessThan((Expression<? extends Date>) path, (Date) value);
				} else {
					returnPredicate = builder.lt((Expression<? extends Number>) path, (Number) value);
				}

				break;
			case lte:
				if (value instanceof Date) {
					returnPredicate = builder.lessThanOrEqualTo((Expression<? extends Date>) path, (Date) value);
				} else {
					returnPredicate = builder.le((Expression<? extends Number>) path, (Number) value);
				}
				break;
			case isnotnull:
				returnPredicate = builder.isNotNull(path);
				break;
			case isnull:
				returnPredicate = builder.isNull(path);
				break;
			case in:
				//TODO check generic types instead of only long
				returnPredicate = path.in((Collection<?>) value);
				break;
			case notin:
				returnPredicate = path.in((Collection<?>) value);
				returnPredicate = builder.not(returnPredicate);
				break;
			default:
				returnPredicate = null;
				break;
		}
		return returnPredicate;
	}

	@Override
	public Page<T> find(List<SearchCriterion> filters, Pageable pageable, Class<T> entityClass, Boolean distinct,
			String... join) {
		return findAll(getFilterSpecification(filters, entityClass, distinct, join), pageable);
	}

	@Override
	public List<T> find(List<SearchCriterion> filters, Class<T> entityClass, Sort sort, Boolean distinct,
			String... join) {
		if (sort == null) {
			sort = Sort.unsorted();
		}
		return findAll(getFilterSpecification(filters, entityClass, distinct, join), sort);
	}

	@Override
	public long count(List<SearchCriterion> filters, Class<T> entityClass, Boolean distinct) {
		return count(getFilterSpecification(filters, entityClass, distinct));
	}

	/**
	 * Add default sorting if sort is empty.
	 * 
	 * @param sort
	 * 
	 * @return Sort
	 */
	private Sort addDefaultSort(Sort sort) {

		if (sort != null) {
			//if sort is not null but has no sorting criteria
			if (sort.iterator() != null && sort.iterator().hasNext()) {
				return sort;
			} else {
				return Sort.by(Direction.DESC, "rid");
			}
		} else {
			return Sort.by(Direction.DESC, "rid");
		}

	}

}
