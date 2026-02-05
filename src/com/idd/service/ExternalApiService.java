package com.idd.service;

import java.sql.SQLException;

import com.idd.config.entity.Response;
import com.idd.module.external.ExternalApiInvoker;
import com.idd.shared.util.JsonHelper;

public class ExternalApiService {
	
	public String execute(
            String dataSourceName,
            String serviceName,
            String version,
            String inputData,
            String aesKey,
            String caseId
            
    ) throws SQLException {
		
    	Response r = new ExternalApiInvoker(dataSourceName, aesKey)
                .execute(serviceName, version, caseId, inputData);
    	
    	return JsonHelper.stringify(r);
    }
}
