package com.idd.module.sql;

import java.sql.SQLException;

import com.idd.config.entity.Response;
import com.idd.shared.util.JsonHelper;

public class SQLService {
    public String callStoreProcedure(
            String serviceName,
            String version,
            String input,
            String caseId,
            String dataSourceName) throws SQLException {
        Response result = new SQLCallStoreProcedure(dataSourceName)
                .execute(serviceName, version, input, caseId);

        return JsonHelper.stringify(result);
    }

    public String executeStatement(
            String serviceName,
            String version,
            String input,
            String caseId,
            String dataSourceName) throws SQLException {
        Response result = new SQLExecuteSatement(dataSourceName)
                .execute(serviceName, version, input, caseId);

        return JsonHelper.stringify(result);
    }
}
