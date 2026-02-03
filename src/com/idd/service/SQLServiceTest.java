package com.idd.service;

import java.sql.SQLException;

public class SQLServiceTest {
	public static void main(String[] args) throws SQLException {

		try {
			Object result = new SQLService()
					.callStoreProcedure(
						"dataSourceTest", 
						"LOAD_TEAM_FILTER", 
//						"{\"laneCode\":\"INPUT\",\"roleCode\":\"RM_R,RM_C,BOM_DVKD\",\"moduleCode\":\"DVT\"}",
						"{\"laneCode\":\"INPUT\",\"roleCode\":\"RM_R,RM_C,BOM_DVKD\"}",
						"ssss"
					);
			
			System.out.println(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
