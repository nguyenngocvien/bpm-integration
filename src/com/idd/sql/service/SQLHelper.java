package com.idd.sql.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.idd.shared.util.JsonHelper;
import com.idd.sql.entiry.SQLConfig;
import com.idd.sql.entiry.SQLParam;

public class SQLHelper {
	public static String buildCallSql(SQLConfig config) {
	    if (config == null) {
	        throw new IllegalArgumentException("SQLConfig is null");
	    }
	    if (config.getParams() == null) {
	        throw new IllegalArgumentException("SQLConfig.params is null");
	    }

	    String fullName = String.join(".",
	            config.getSchema(),
	            config.getPackageName(),
	            config.getProcedureName()
	    );

	    String placeholders = String.join(
	            ",", Collections.nCopies(config.getParams().size(), "?")
	    );

	    return "{ call " + fullName + "(" + placeholders + ") }";
	}
	
	public static List<SQLParam> parseParamValue(List<SQLParam> params, String input) {
	    if (params == null) {
	        throw new IllegalArgumentException("SQL params is null");
	    }
	    if (input == null || input.isEmpty()) {
	        throw new IllegalArgumentException("Input data is null or empty");
	    }

	    Map<String, Object> map;
	    try {
	        map = JsonHelper.parseToMap(input);
	    } catch (Exception e) {
	        throw new IllegalArgumentException("Invalid input JSON", e);
	    }

	    for (int i = 0; i < params.size(); i++) {
	        SQLParam param = params.get(i);
	        if (param == null) {
	            throw new IllegalArgumentException("SQLParam at index " + i + " is null");
	        }

	        if (param.isIn()) {
	            if (param.getInputMapping() == null) {
	                throw new IllegalArgumentException(
	                    "Input mapping is null for param: " + param.getName()
	                );
	            }

	            Object value;
	            try {
	                value = JsonHelper.getByPath(map, param.getInputMapping());
	            } catch (Exception e) {
	                throw new IllegalArgumentException(
	                    "Failed to map input for param: " + param.getName(), e
	                );
	            }

	            param.setValue(value != null ? value : param.getDefaultValue());
	        }
	    }

	    return params;
	}
	
	public static String buildInputLog(String sql, List<SQLParam> params) {
		Map<String, Object> input = new HashMap<>();
		input.put("sql", sql);
		input.put("params", params);
		return JsonHelper.stringify(input);
	}
}
