package com.idd.service;

import java.sql.SQLException;

public class DecisionServiceTest {
	
	public static void main(String[] args) throws SQLException {

		try {
			Object result = callRule();
			System.out.println(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String callRule() throws SQLException {
		
		return new DecisionService()
					.execute(
						"dataSourceTest",
						"SAALEM_EXIM_POS",
						"Routing_POS_Operation",
						"POSv2.0",
						"{\"WORKFLOW_TYPE\":\"POS_TSP\"}",
						"tYtPIDwQBTuevzK8NhOXQw==",
						"hehehehe"
					);
	}
}
