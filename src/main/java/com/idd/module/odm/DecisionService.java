package com.idd.module.odm;

import java.sql.SQLException;

import com.idd.config.entity.Response;
import com.idd.shared.util.JsonHelper;

public class DecisionService {
	
	public String execute(
            String ruleAppName,
            String ruleSetName,
            String version,
            String inputData,
            String caseId,
            String dataSourceName,
            String aesKey
            
    ) throws SQLException {
		
		String serviceName = ruleAppName + "." + ruleSetName;
		
    	Response r = new DecisionInvoker(dataSourceName, aesKey)
                .execute(serviceName, version, inputData, caseId);
    	
    	return JsonHelper.stringify(r);
    }
}
