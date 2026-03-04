package com.idd.module.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.idd.shared.util.JsonHelper;

import oracle.jdbc.OracleTypes;

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
				config.getProcedureName());

		String placeholders = String.join(
				",", Collections.nCopies(config.getParams().size(), "?"));

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
							"Input mapping is null for param: " + param.getName());
				}

				Object value;
				try {
					value = JsonHelper.getByPath(map, param.getInputMapping());
				} catch (Exception e) {
					throw new IllegalArgumentException(
							"Failed to map input for param: " + param.getName(), e);
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

	public static int resolveJdbcType(String sqlType) {

		if (sqlType == null) {
			throw new IllegalArgumentException("sqlType is null");
		}

		switch (sqlType.toUpperCase()) {

			case "VARCHAR":
			case "VARCHAR2":
				return Types.VARCHAR;

			case "NUMBER":
			case "NUMERIC":
				return Types.NUMERIC;

			case "DATE":
				return Types.DATE;

			case "TIMESTAMP":
				return Types.TIMESTAMP;

			case "CLOB":
				return Types.CLOB;

			case "REF_CURSOR":
				return OracleTypes.CURSOR;

			default:
				throw new IllegalArgumentException("Unsupported SQL type: " + sqlType);
		}
	}

	public static List<Map<String, Object>> convertResultSet(ResultSet rs)
			throws SQLException {

		List<Map<String, Object>> rows = new ArrayList<>();

		if (rs == null)
			return rows;

		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();

		while (rs.next()) {

			Map<String, Object> row = new LinkedHashMap<>();

			for (int i = 1; i <= columnCount; i++) {

				String columnName = meta.getColumnLabel(i);
				int columnType = meta.getColumnType(i);
				Object value;

				switch (columnType) {

					case Types.CLOB:
						java.sql.Clob clob = rs.getClob(i);
						if (clob != null) {
							value = clob.getSubString(1, (int) clob.length());
						} else {
							value = null;
						}
						break;

					case Types.DATE:
						java.sql.Date date = rs.getDate(i);
						value = date != null ? date.toInstant().toString() : null;
						break;

					case Types.TIMESTAMP:
						java.sql.Timestamp ts = rs.getTimestamp(i);
						value = ts != null ? ts.toInstant().toString() : null;
						break;

					case Types.NUMERIC:
					case Types.DECIMAL:
						java.math.BigDecimal bd = rs.getBigDecimal(i);
						value = bd;
						break;

					default:
						value = rs.getObject(i);
						break;
				}

				row.put(columnName, value);
			}

			rows.add(row);
		}

		rs.close();
		return rows;
	}
}
