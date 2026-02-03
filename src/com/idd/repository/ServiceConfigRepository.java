package com.idd.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.idd.entity.ServiceConfig;
import com.idd.module.sql.SQLConnector;
import com.idd.util.BpmLogger;

public class ServiceConfigRepository extends SQLConnector {

	private static final String SQL = "SELECT * FROM SI_VERSION WHERE SERVICE_NAME = ? AND STATUS = '1' ORDER BY SERVICE_VERSION DESC FETCH FIRST 1 ROWS ONLY";

    public ServiceConfigRepository(String dataSourceName) {
		super(dataSourceName);
	}

	public ServiceConfig loadFromDb(String serviceCode) {
		
		Connection conn = null;
		
		try {
			conn = getConnection();
			
            try (PreparedStatement ps = conn.prepareStatement(SQL)) {

                ps.setString(1, serviceCode);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        return null;
                    }

                    ServiceConfig cfg = new ServiceConfig(
                    		rs.getString("APP_NAME"),
                    		rs.getString("SERVICE_NAME"),
                    		rs.getString("SYSTEM"),
                    		rs.getString("LOG_ON"),
                    		rs.getString("SERVICE_CONFIG")
                    );

                    return cfg;
                }
            }

        } catch (Exception e) {
            BpmLogger.error(
                "Load ServiceConfig failed, serviceCode=" + serviceCode, e
            );
            return null;
        } finally {
        	try { if (conn != null) conn.close(); } catch (Exception e) {}
		}
	}
}
