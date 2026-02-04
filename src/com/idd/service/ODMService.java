package com.idd.service;

import java.sql.SQLException;

import com.idd.entity.Response;
import com.idd.module.odm.ODMInvoker;
import com.idd.util.JsonHelper;

public class ODMService {
	
	public String execute(
            String dataSourceName,
            String ruleAppName,
            String ruleSetName,
            String version,
            String inputData,
            String aesKey,
            String caseId
            
    ) throws SQLException {
		
		String serviceName = ruleAppName + "." + ruleSetName;
		
    	Response r = new ODMInvoker(dataSourceName, aesKey)
                .execute(serviceName, version, caseId, inputData);
    	
    	return JsonHelper.stringify(r);
    }
}
