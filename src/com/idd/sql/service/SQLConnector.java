package com.idd.sql.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.idd.shared.util.BpmLogger;

import oracle.jdbc.pool.OracleDataSource;

public abstract class SQLConnector {
	private static final ConcurrentMap<String, DataSource> CACHE =
            new ConcurrentHashMap<>();
	
	private String dataSourceName;
	
	public SQLConnector(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	
	protected Connection getConnection() throws SQLException, NamingException {
		DataSource ds = CACHE.computeIfAbsent(dataSourceName, SQLConnector::lookup);
		return ds.getConnection();
    }

    private static DataSource lookup(String jndiName) {
    	if (jndiName == "dataSourceTest") {
			try {
				return getDataSource();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        BpmLogger.info("Lookup datasource JNDI: " + jndiName);
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(jndiName);
            BpmLogger.info("Datasource cached: " + jndiName);
            return ds;
        } catch (Exception e) {
            BpmLogger.error("Datasource lookup failed: " + jndiName, e);
            throw new RuntimeException(
            	"Cannot lookup datasource: " + jndiName, e
            );
        }
    }
    
    private static DataSource getDataSource() throws SQLException {
    	OracleDataSource ods = new OracleDataSource();
        ods.setURL("jdbc:oracle:thin:@//172.29.255.125:1521/ORCL");
        ods.setUser("SAALEM");
        ods.setPassword("Full3car");
		return ods;
	}
}
