package com.certacure.core.common.helper;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import com.certacure.core.base.entity.BaseEntity;
import com.certacure.core.common.util.SecurityUtil;

/**
 * CustomCacheKeyGenerator.java
 * 

 **/
@Component
public class CustomCacheKeyGenerator implements KeyGenerator {

	@SuppressWarnings("unchecked")
	@Override
	public Object generate(Object target, Method method, Object... params) {
		Class<? extends BaseEntity> entityClass = null;
		Long tenantId = SecurityUtil.getCurrentUser().getTenantId();
		for (Object obj : params) {
			if (obj instanceof Class<?>) {
				entityClass = (Class<? extends BaseEntity>) obj;
				break;
			}
		}
		return generateKey(tenantId, entityClass);
	}

	public static String generateKey(Long tenantId, Class<? extends BaseEntity> entityClass) {

		if (entityClass == null) {
			throw new IllegalArgumentException("Cannot Find [Class<?>] Parameter");
		} else if (tenantId == null) {
			throw new IllegalArgumentException("Tenant Id Cannot Be NULL");
		}

		StringBuilder sb = new StringBuilder();

		sb.append(entityClass.getSimpleName());
		sb.append("-");
		sb.append(tenantId);

		return sb.toString();
	}

}
