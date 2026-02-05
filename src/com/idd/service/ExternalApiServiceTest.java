package com.idd.service;

import java.sql.SQLException;

import com.idd.config.entity.Response;
import com.idd.module.external.ExternalApiInvoker;
import com.idd.shared.util.JsonHelper;

public class ExternalApiServiceTest {
	
	public static void main(String[] args) throws SQLException {

		Response result = new ExternalApiInvoker("dataSourceTest", "tYtPIDwQBTuevzK8NhOXQw==")
				.execute(
						"GET_LOS_CIF", 
						"POSv1.0", 
						"hihihi", 
						"{\"cif\":\"102216705\"}"
					);
		System.out.println(JsonHelper.stringify(result));
	}
}
