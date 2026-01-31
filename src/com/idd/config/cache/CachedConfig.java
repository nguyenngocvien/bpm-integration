package com.idd.config.cache;

import com.idd.shared.entity.ServiceConfig;

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
