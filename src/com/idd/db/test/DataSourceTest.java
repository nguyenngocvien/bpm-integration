package com.idd.db.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;

public class DataSourceTest {

    private final OracleDataSource dataSource;

    public DataSourceTest() throws SQLException {

        OracleDataSource ods = new OracleDataSource();
        ods.setURL("jdbc:oracle:thin:@//172.29.255.125:1521/ORCL");
        ods.setUser("SAALEM");
        ods.setPassword("Full3car");

        this.dataSource = ods;
    }
    

    public void testConnection() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL")) {

            if (rs.next()) {
                System.out.println("✅ Oracle connected, result = " + rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("❌ Oracle connection test failed", e);
        }
    }


	public DataSource getDataSource() {
		// TODO Auto-generated method stub
		return dataSource;
	}
}
