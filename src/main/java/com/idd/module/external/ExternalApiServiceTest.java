package com.idd.module.external;

import java.sql.SQLException;

public class ExternalApiServiceTest {
	
	public static void main(String[] args) throws SQLException {

		String result = new ExternalApiService()
				.execute(
						"GET_LOS_CIF",
						"POSv1.0",
						"{\"cif\":\"102216705\"}",
						"hihihi",
						"dataSourceTest",
						"tYtPIDwQBTuevzK8NhOXQw=="
				);
		System.out.println(result);
	}
}
