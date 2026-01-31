package com.idd.config.cache;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.idd.shared.entity.ServiceConfig;
import com.idd.shared.repository.ServiceConfigRepository;

public final class ServiceConfigCache {

    private static final ConcurrentMap<String, CachedConfig> CACHE =
        new ConcurrentHashMap<>();

    // TTL default: 5 minutes
    private static final long TTL_MS =
        Long.getLong("si.config.cache.ttl.ms", 5 * 60 * 1000L);

    private ServiceConfigCache() {}

    public static ServiceConfig get(Connection conn, String serviceCode) {
        CachedConfig cached = CACHE.get(serviceCode);

        if (cached != null && !cached.isExpired(TTL_MS)) {
            return cached.config;
        }

        synchronized (ServiceConfigCache.class) {
            cached = CACHE.get(serviceCode);
            if (cached != null && !cached.isExpired(TTL_MS)) {
                return cached.config;
            }

            ServiceConfig fresh =
                ServiceConfigRepository.loadFromDb(conn, serviceCode);

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
