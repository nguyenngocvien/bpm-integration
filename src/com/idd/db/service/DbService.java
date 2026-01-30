package com.idd.db.service;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.idd.config.cache.ServiceConfigCache;
import com.idd.config.entiry.ERROR_CODE;
import com.idd.config.entiry.Response;
import com.idd.config.entiry.ServiceConfig;
import com.idd.config.entiry.SiLog;
import com.idd.db.entiry.SqlConfig;
import com.idd.db.executor.ProcedureExecutor;
import com.idd.db.provider.DataSourceProvider;
import com.idd.db.test.DataSourceTest;
import com.idd.shared.util.DbLogHelper;
import com.idd.shared.util.JsonHelper;

public class DbService {
	public Object callStoreProcedure(String dataSource, String procedureName, String inputData, String traceId) throws SQLException {
		
		DataSource ds = DataSourceProvider.get(dataSource);
		
		return execute(ds, procedureName, inputData, traceId);
	}
	
	private static Response execute(DataSource dataSource, String procedureName, String inputData, String traceId) throws SQLException {
		
		SiLog log = SiLog.init(
			traceId,
            procedureName,
            inputData,
            "DB"
        );
		
		ServiceConfig serviceConfig = ServiceConfigCache.get(dataSource.getConnection(), procedureName);
		System.out.println(serviceConfig.getDetailConfig());
    	SqlConfig sqlConfig = JsonHelper.parseObject(serviceConfig.getDetailConfig(), SqlConfig.class);
    	log.setService(sqlConfig.getPackageName() + "." + sqlConfig.getProcedureName());
    	
    	try {
    		
    		return new ProcedureExecutor().execute(dataSource.getConnection(), sqlConfig, inputData, log, serviceConfig.isLogEnabled());
			
		} catch (SQLException e) {
			e.printStackTrace();
			Long logId = DbLogHelper.saveError(
				dataSource.getConnection(),
                log,
                e,
                ERROR_CODE.ERROR
            );
			
			return Response.error(ERROR_CODE.ERROR.getCode(), e.getMessage(), logId);
		}
	}
	
	public static void main(String[] args) throws SQLException {

		DataSource dataSource = new DataSourceTest().getDataSource();
		
		Response result;
		
		try {
			result = execute(dataSource, "LOAD_TEAM_FILTER", "{\"function\":\"INPUT\",\"role\":\"RM_R\"}", "sss");
			System.out.println(JsonHelper.stringify(result));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
