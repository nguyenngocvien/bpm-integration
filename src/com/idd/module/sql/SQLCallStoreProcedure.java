package com.idd.module.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.idd.entity.ERROR_CODE;
import com.idd.entity.LogRecord;
import com.idd.entity.Response;
import com.idd.entity.ServiceConfig;
import com.idd.util.BpmLogger;
import com.idd.util.JsonHelper;
import com.idd.util.LogHelper;
import com.idd.util.SQLHelper;
import com.idd.util.ServiceConfigCache;

public class SQLCallStoreProcedure extends SQLConnector {
	
	private final static String SYSTEM = "DB";
	private final LogHelper logHelper;
	private final ServiceConfigCache serviceConfigCache;

	public SQLCallStoreProcedure(
			String dataSourceName) {
		super(dataSourceName);
		this.logHelper = new LogHelper(dataSourceName);
		this.serviceConfigCache = new ServiceConfigCache(dataSourceName);
	}
	
	public Response execute(String serviceName, String version, String input, String traceId) {
		
		Connection conn = null;
        CallableStatement cstmt = null;
        
        LogRecord log = LogRecord.init(
			traceId,
			serviceName,
            input,
            SYSTEM
        );
        
        try {
			conn = getConnection();
			
			try {
				ServiceConfig serviceConfig = serviceConfigCache.get(serviceName, version);
				if (serviceConfig == null) {
					throw new IllegalStateException(
							String.format(
					                "Service config not found for serviceName=%s, version=%s",
					                serviceName,
					                version
					            )
							);
				}
				SQLConfig sqlConfig = JsonHelper.parseObject(serviceConfig.getDetailConfig(), SQLConfig.class);
				if (sqlConfig == null) {
					throw new IllegalStateException(
							String.format(
					                "Detail of service config is invalid for serviceName=%s, version=%s",
					                serviceName,
					                version
					            )
							);
				}
		    	log.setService(sqlConfig.getPackageName() + "." + sqlConfig.getProcedureName());
		    	
		    	String sql = SQLHelper.buildCallSql(sqlConfig);
		    	cstmt = conn.prepareCall(sql);
		    	
		    	List<SQLParam> params = SQLHelper.parseParamValue(sqlConfig.getParams(), input);
		    	log.setToInput(SQLHelper.buildInputLog(sql, params));
		    	
		    	applyInputParams(cstmt, params);
		    	
		    	cstmt.execute();
		    	
		    	Map<String, Object> output = extractOutParams(cstmt, sqlConfig.getParams());
		    	log.setFromOutput(JsonHelper.stringify(output));
		    	
		    	Long logId = logHelper.saveSuccess(
	                log,
	                serviceConfig.isLogEnabled(),
	                JsonHelper.stringify(output)
	            );
		    	
		    	return Response.success(output, logId);
			} catch (IllegalArgumentException e) {

			    BpmLogger.warn("Invalid SQL configuration" + e);

			    Long logId = logHelper.saveError(log, e, ERROR_CODE.ERROR);
			    
			    String message = e.getMessage() != null
			            ? e.getMessage()
			            : e.getClass().getSimpleName();

			    return Response.error("EX-01", message, logId);

			} catch (SQLException e) {
				e.printStackTrace();
				Long logId = logHelper.saveError(
	                log,
	                e,
	                ERROR_CODE.ERROR
	            );
				
				return Response.error(ERROR_CODE.ERROR.getCode(), "Database execution error", logId);
			} catch (Exception e) {
				e.printStackTrace();
				Long logId = logHelper.saveError(
	                log,
	                e,
	                ERROR_CODE.ERROR
	            );
				
				String message = e.getMessage() != null
			            ? e.getMessage()
			            : e.getClass().getSimpleName();
				
				return Response.error(ERROR_CODE.ERROR.getCode(), message, logId);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            try { if (cstmt != null) cstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
		}
        
        return Response.error(ERROR_CODE.ERROR.getCode(), ERROR_CODE.ERROR.getDefaultMessage());
	}
	
	private void applyInputParams(
	        CallableStatement cstmt,
	        List<SQLParam> params  
	) throws SQLException {
		
		if (params == null) {
	        throw new IllegalArgumentException("SQL params is null");
	    }

	    for (SQLParam param : params) {
	    	
	    	if (param == null) {
	            throw new IllegalArgumentException("SQLParam is null");
	        }
	    	
	    	if (param.isIn()) {
	    		
		        if (param.getValue() == null) {
		            cstmt.setNull(param.getParamIndex(), param.getSqlType());
		        } else {
		            cstmt.setObject(param.getParamIndex(), param.getValue(), param.getSqlType());
		        }
			}
	    	
	    	if (param.isOut()) {
				cstmt.registerOutParameter(param.getParamIndex(), param.getSqlType());
			}
	    }
	}


	private Map<String, Object> extractOutParams(CallableStatement cstmt, List<SQLParam> params) throws SQLException {
		
		Map<String, Object> outResult = new LinkedHashMap<>();
		
	    for (SQLParam param : params) {
	    	if (param.isOut()) {
	    		Object value = cstmt.getObject(param.getParamIndex());
	    		if (value instanceof ResultSet) {
	    		    ResultSet rs = (ResultSet) value;
	    		    value = convertResultSet(rs);
	    		}
		        outResult.put(param.getOutputMapping(), value);
	    	};
	    }

	    return outResult;
	}
	
	private List<Map<String, Object>> convertResultSet(ResultSet rs)
	        throws SQLException {

	    List<Map<String, Object>> rows = new ArrayList<>();
	    ResultSetMetaData meta = rs.getMetaData();
	    int columnCount = meta.getColumnCount();

	    while (rs.next()) {
	        Map<String, Object> row = new LinkedHashMap<>();
	        for (int i = 1; i <= columnCount; i++) {
	            String colName = meta.getColumnLabel(i);
	            Object value = rs.getObject(i);
	            row.put(colName, value);
	        }
	        rows.add(row);
	    }

	    rs.close();
	    return rows;
	}
}
