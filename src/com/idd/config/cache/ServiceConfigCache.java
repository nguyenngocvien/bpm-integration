package com.idd.config.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.idd.shared.entity.ServiceConfig;
import com.idd.shared.repository.ServiceConfigRepository;

public class ServiceConfigCache {
	
	class CachedConfig {
	    ServiceConfig config;
	    long cachedAt;

	    CachedConfig(ServiceConfig config) {
	        this.config = config;
	        this.cachedAt = System.currentTimeMillis();
	    }

	    boolean isExpired(long ttlMs) {
	        return System.currentTimeMillis() - cachedAt > ttlMs;
	    }
	}
	
	private final ServiceConfigRepository configRepository;

    private static final ConcurrentMap<String, CachedConfig> CACHE =
        new ConcurrentHashMap<>();

    // TTL default: 5 minutes
    private static final long TTL_MS =
        Long.getLong("si.config.cache.ttl.ms", 5 * 60 * 1000L);

    public ServiceConfigCache(String dataSourcename) {
    	this.configRepository = new ServiceConfigRepository(dataSourcename);
    }

    public ServiceConfig get(String serviceCode) {
        CachedConfig cached = CACHE.get(serviceCode);

        if (cached != null && !cached.isExpired(TTL_MS)) {
            return cached.config;
        }

        synchronized (ServiceConfigCache.class) {
            cached = CACHE.get(serviceCode);
            if (cached != null && !cached.isExpired(TTL_MS)) {
                return cached.config;
            }

            ServiceConfig fresh = configRepository.loadFromDb(serviceCode);

            if (fresh != null) {
                CACHE.put(serviceCode, new CachedConfig(fresh));
            }

            return fresh;
        }
    }

    public static void invalidate(String serviceCode) {
        CACHE.remove(serviceCode);
    }

    public static void clear() {
        CACHE.clear();
    }
}
