package com.idd.sql.service;

import java.sql.SQLException;

import com.idd.shared.util.JsonHelper;

public class SQLServiceTest {
	public static void main(String[] args) throws SQLException {

		try {
			Object result = new SQLService()
					.callStoreProcedure(
						"dataSourceTest", 
						"LOAD_TEAM_FILTER", 
						"{\"function\":\"INPUT\",\"role\":\"RM_R,RM_C,BOM_DVKD\",\"moduleCode\":\"DVT\"}", 
						"sss"
					);
			
			System.out.println(JsonHelper.stringify(result));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
