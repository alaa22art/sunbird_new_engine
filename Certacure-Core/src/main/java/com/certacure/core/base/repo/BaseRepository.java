package com.certacure.core.base.repo;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.certacure.core.base.entity.BaseEntity;
import com.certacure.core.base.helper.SearchCriterion;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID extends Serializable> extends JpaRepository<T, ID> {

	public boolean isRelationLoaded(BaseEntity entity, String attributeName);

	public boolean isEntityPersisted(BaseEntity entity);

	public Page<T> find(List<SearchCriterion> filters, Pageable pageable, Class<T> entityClass, String... join);

	public T findOne(List<SearchCriterion> filters, Class<T> entityClass, String... join);

	public List<T> find(List<SearchCriterion> filters, Class<T> entityClass, String... join);

	public List<T> find(List<SearchCriterion> filters, Class<T> entityClass, Sort sort, String... join);

	public List<T> find(List<SearchCriterion> filters, Class<T> entityClass, Sort sort, Boolean distinct, String... join);

	public Page<T> find(List<SearchCriterion> filters, Pageable pageable, Class<T> entityClass, Boolean distinct, String... join);

	public long count(List<SearchCriterion> filters, Class<T> entityClass, Boolean distinct);

	public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities);
}
