package com.idd.main.service;

import java.sql.SQLException;

import com.idd.config.entity.Response;
import com.idd.module.sql.SQLCallStoreProcedure;
import com.idd.shared.util.JsonHelper;

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
