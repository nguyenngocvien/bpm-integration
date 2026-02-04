package com.idd.service;

import java.sql.SQLException;

import com.idd.entity.Response;
import com.idd.module.sql.SQLCallStoreProcedure;
import com.idd.util.JsonHelper;

public class SQLService {
    public String callStoreProcedure(
            String dataSourceName,
            String serviceName,
            String version,
            String input,
            String caseId
    ) throws SQLException {
    	Response result = new SQLCallStoreProcedure(dataSourceName)
                .execute(serviceName, version, input, caseId);
    	
    	return JsonHelper.stringify(result);
    }
}
