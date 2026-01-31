package com.idd.sql.service;

import java.sql.SQLException;

public class SQLService {
    public Object callStoreProcedure(
            String dataSourceName,
            String procedureName,
            String input,
            String caseId
    ) throws SQLException {
        return new SQLCallStoreProcedure(dataSourceName)
                .execute(procedureName, input, caseId);
    }
}
