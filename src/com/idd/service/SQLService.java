package com.idd.service;

import java.sql.SQLException;

import com.idd.entity.Response;
import com.idd.module.sql.SQLCallStoreProcedure;
import com.idd.util.JsonHelper;

public class SQLService {
    public String callStoreProcedure(
            String dataSourceName,
            String procedureName,
            String input,
            String caseId
    ) throws SQLException {
    	Response result = new SQLCallStoreProcedure(dataSourceName)
                .execute(procedureName, input, caseId);
    	
    	return JsonHelper.stringify(result);
    }
}
