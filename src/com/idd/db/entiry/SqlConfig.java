package com.idd.db.entiry;

import java.util.ArrayList;
import java.util.List;

public class SqlConfig {
	private String dataSourceName;
	
	private DbExecuteType dbExecuteType;
	
    private String sqlStatement;
    
    private String schema;
    private String packageName;
    private String procedureName;
    
    private List<SqlParam> params = new ArrayList<>();
    
    public SqlConfig procedureConfig (
    		String dataSourceName, 
    		String schema,
    		String packageName,
    		String procedureName,
    		List<SqlParam> params
    	) {
    	this.dataSourceName = dataSourceName;
    	this.dbExecuteType = DbExecuteType.PROCEDURE;
    	this.schema = schema;
    	this.packageName = packageName;
    	this.procedureName = procedureName;
    	this.params = params;
    	
    	return this;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public DbExecuteType getDbExecuteType() {
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

	public List<SqlParam> getParams() {
		return params;
	}
}
