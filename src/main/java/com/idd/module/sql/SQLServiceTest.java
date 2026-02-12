package com.idd.module.sql;

import java.sql.SQLException;

public class SQLServiceTest {
	public static void main(String[] args) throws SQLException {

		try {
//			Object result = loadTeamFilter();
			Object result = searchUser();
			
			
			System.out.println(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Object loadTeamFilter() throws SQLException {
		
		return new SQLService()
					.callStoreProcedure(
						"dataSourceTest", 
						"LOAD_TEAM_FILTERs",
						"POSv1.0",
//						"{\"laneCode\":\"INPUT\",\"roleCode\":\"RM_R,RM_C,BOM_DVKD\",\"moduleCode\":\"DVT\"}",
						"{\"laneCode\":\"INPUT\",\"roleCode\":\"RM_R,RM_C,BOM_DVKD\"}",
						"ssss"
					);
	}
	
	private static Object searchUser() throws SQLException {
		
		return new SQLService()
					.callStoreProcedure(
						"SEARCH_USER",
						"POSv1.0",
						"{\"userName\":\"linhrm\"}",
						"ssss",
						"dataSourceTest"
					);
	}
	
	
}
