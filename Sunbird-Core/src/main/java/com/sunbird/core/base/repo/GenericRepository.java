package com.sunbird.core.base.repo;

import org.springframework.data.repository.NoRepositoryBean;

import com.sunbird.core.base.entity.BaseEntity;


@NoRepositoryBean
public interface GenericRepository<T extends BaseEntity> extends BaseRepository<T, Long> {

}
