package com.idd.db.provider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.idd.shared.util.BpmLogger;

public class DataSourceProvider {

    private static final ConcurrentMap<String, DataSource> CACHE =
            new ConcurrentHashMap<>();

    private DataSourceProvider() {}

    public static DataSource get(String jndiName) {
        return CACHE.computeIfAbsent(jndiName, DataSourceProvider::lookup);
    }

    private static DataSource lookup(String jndiName) {
        BpmLogger.info("Lookup datasource JNDI: " + jndiName);
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(jndiName);
            BpmLogger.info("Datasource cached: " + jndiName);
            return ds;
        } catch (Exception e) {
            BpmLogger.error("Datasource lookup failed: " + jndiName, e);
            throw new RuntimeException(e);
        }
    }
}
