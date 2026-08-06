package com.sunbird.core.common.util;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheUtil {

	@Autowired
	private CacheManager cm;

	private static Logger logger;
	private static CacheManager cacheManager;

	@PostConstruct
	public void initializing() {
		cacheManager = cm;
		logger = LoggerFactory.getLogger(getClass());
	}

	public static void removeByKey(String cacheName, Object key) {
		Cache cache = getCache(cacheName);
		if (cache.get(key) == null) {
			log("Key " + key + " in " + cacheName + " cache does not exist");
			return;
		}
		cache.evict(key);
	}

	public static void removeAll(String cacheName) {
		getCache(cacheName).clear();
	}

	public static void insertIfAbsent(String cacheName, Object key, Object value) {
		getCache(cacheName).putIfAbsent(key, value);
	}

	public static void insert(String cacheName, Object key, Object value) {
		getCache(cacheName).put(key, value);
	}

	public static Object get(String cacheName, Object key) {
		return getCache(cacheName).get(key) != null ? getCache(cacheName).get(key).get() : null;
	}

	private static Cache getCache(String cacheName) {
		return cacheManager.getCache(cacheName);
	}

	private static void log(String msg) {
		logger.info(msg);
	}

}
