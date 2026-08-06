package com.sunbird.core.base.service;

import org.springframework.transaction.annotation.Transactional;

import com.sunbird.core.base.entity.BaseEntity;
import com.sunbird.core.base.repo.GenericRepository;

@Transactional(readOnly = false)
public abstract class GenericService<E extends BaseEntity, T extends GenericRepository<E>> extends BaseService<Long, E, T> {

	@Override
	protected abstract T getRepository();
}
