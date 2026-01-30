package com.idd.db.executor;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import com.idd.config.entiry.Response;
import com.idd.config.entiry.SiLog;
import com.idd.db.entiry.ParamMode;
import com.idd.db.entiry.SqlConfig;
import com.idd.db.entiry.SqlParam;
import com.idd.shared.util.DbLogHelper;
import com.idd.shared.util.JsonHelper;

public class ProcedureExecutor {
	
	class RuntimeParam {
	    final SqlParam def;
	    Object value;

	    RuntimeParam(SqlParam def, Object value) {
	        this.def = def;
	        this.value = value;
	    }
	}

    public Response execute(
            Connection conn,
            SqlConfig config,
            String jsonInput, 
            SiLog log,
            Boolean isLogEnable ) throws SQLException {
    	
        Map<String, Object> inputMap = JsonHelper.parseToMap(jsonInput);
        List<RuntimeParam> runtimeParams = buildRuntimeParams(config, inputMap);
        log.setToInput(JsonHelper.stringify(inputMap));

        String callSql = buildCallSql(config);

        try (CallableStatement cs = conn.prepareCall(callSql)) {

            bindParams(cs, runtimeParams);
            boolean hasResultSet = cs.execute();

            Map<String, Object> output = new HashMap<>();
            extractOutParams(cs, runtimeParams, output);

            if (hasResultSet) {
            	output.put("resultSet", extractResultSet(cs.getResultSet()));
            	log.setToInput(JsonHelper.stringify(output));
            }
            log.setToInput(log.getToInput());
            
            Long logId = DbLogHelper.saveSuccess(
            	conn,
                log,
                isLogEnable,
                JsonHelper.stringify(output)
            );

            return Response.success(output, logId);
            
        }
    }
    
    private List<RuntimeParam> buildRuntimeParams(
            SqlConfig config,
            Map<String, Object> input) {

        List<RuntimeParam> list = new ArrayList<>();

        for (SqlParam p : config.getParams()) {

            Object value = null;

            if (p.getMode() == ParamMode.IN || p.getMode() == ParamMode.INOUT) {
                value = JsonHelper.getByPath(input, p.getInputMapping());

                if (value == null) {
                    value = p.getDefaultValue();
                }

                if (value == null && p.isRequired()) {
                    throw new IllegalArgumentException(
                        "Missing required param: " + p.getName()
                    );
                }
            }

            list.add(new RuntimeParam(p, value));
        }
        return list;
    }
    
    private String buildCallSql(SqlConfig config) {

        String fullName =
            config.getSchema() + "." +
            config.getPackageName() + "." +
            config.getProcedureName();

        String placeholders = String.join(
            ",", Collections.nCopies(config.getParams().size(), "?")
        );

        return "{ call " + fullName + "(" + placeholders + ") }";
    }
    
    private void bindParams(
            CallableStatement cs,
            List<RuntimeParam> params) throws SQLException {

        for (int i = 0; i < params.size(); i++) {
            RuntimeParam rp = params.get(i);
            SqlParam p = rp.def;
            int idx = i + 1;

            switch (p.getMode()) {
                case IN:
                    cs.setObject(idx, rp.value, p.getSqlType());
                    break;

                case OUT:
                    cs.registerOutParameter(idx, p.getSqlType());
                    break;

                case INOUT:
                    cs.setObject(idx, rp.value, p.getSqlType());
                    cs.registerOutParameter(idx, p.getSqlType());
                    break;

                default:
                    throw new IllegalStateException(
                        "Unsupported ParamMode: " + p.getMode()
                    );
            }
        }
    }
    
    private void extractOutParams(
            CallableStatement cs,
            List<RuntimeParam> params,
            Map<String, Object> output) throws SQLException {

        for (int i = 0; i < params.size(); i++) {
            SqlParam p = params.get(i).def;

            if (p.getMode() == ParamMode.IN) {
                continue;
            }

            Object outVal = cs.getObject(i + 1);
            String key = p.getOutputMapping() != null
                    ? p.getOutputMapping()
                    : p.getName();

            if (outVal instanceof ResultSet) {

                ResultSet rs = (ResultSet) outVal;
                List<Map<String, Object>> rows = extractResultSet(rs);

                output.put(key, rows);

            } else {
                output.put(key, outVal);
            }
        }
    }
    
    private List<Map<String, Object>> extractResultSet(ResultSet rs)
            throws SQLException {

        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();

        try {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    row.put(md.getColumnLabel(i), rs.getObject(i));
                }
                rows.add(row);
            }
        } finally {
            try {
                rs.close();
            } catch (SQLException ignore) {
                // optional: log debug
            }
        }

        return rows;
    }
}
