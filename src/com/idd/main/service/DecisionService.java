package com.idd.main.service;

import java.sql.SQLException;

import com.idd.config.entity.Response;
import com.idd.module.odm.DecisionInvoker;
import com.idd.shared.util.JsonHelper;

public class DecisionService {
	
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
		
    	Response r = new DecisionInvoker(dataSourceName, aesKey)
                .execute(serviceName, version, caseId, inputData);
    	
    	return JsonHelper.stringify(r);
    }
}
