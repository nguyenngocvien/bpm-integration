package com.idd.module.sql;

import java.util.ArrayList;
import java.util.List;

public class SQLConfig {
	private String dataSourceName;
	
	private SQLExecuteType dbExecuteType;
	
    private String sqlStatement;
    
    private String schema;
    private String packageName;
    private String procedureName;
    
    private List<SQLParam> params = new ArrayList<>();
    
    public SQLConfig procedureConfig (
    		String dataSourceName, 
    		String schema,
    		String packageName,
    		String procedureName,
    		List<SQLParam> params
    	) {
    	this.dataSourceName = dataSourceName;
    	this.dbExecuteType = SQLExecuteType.PROCEDURE;
    	this.schema = schema;
    	this.packageName = packageName;
    	this.procedureName = procedureName;
    	this.params = params;
    	
    	return this;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public SQLExecuteType getDbExecuteType() {
		return dbExecuteType;
	}

	public String getSqlStatement() {
		return sqlStatement;
	}

	public String getSchema() {
		return schema;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public List<SQLParam> getParams() {
		return params;
	}
}

