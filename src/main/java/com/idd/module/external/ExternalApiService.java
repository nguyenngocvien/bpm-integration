package com.idd.module.external;

import java.sql.SQLException;

import com.idd.config.entity.Response;
import com.idd.shared.util.JsonHelper;

public class ExternalApiService {
	
	public String execute(
            String serviceName,
            String version,
            String inputData,
            String caseId,
            String dataSourceName,
            String aesKey
            
    ) throws SQLException {
		
    	Response r = new ExternalApiInvoker(dataSourceName, aesKey)
                .execute(serviceName, version, inputData, caseId);
    	
    	return JsonHelper.stringify(r);
    }
}
