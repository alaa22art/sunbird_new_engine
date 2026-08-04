package com.certacure.core.base.repo;

import org.springframework.data.repository.NoRepositoryBean;

import com.certacure.core.base.entity.BaseEntity;


@NoRepositoryBean
public interface GenericRepository<T extends BaseEntity> extends BaseRepository<T, Long> {

}
