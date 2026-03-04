package com.idd.module.sql;

import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.idd.config.cache.ServiceConfigCache;
import com.idd.config.entity.ERROR_CODE;
import com.idd.config.entity.LogRecord;
import com.idd.config.entity.Response;
import com.idd.config.entity.ServiceConfig;
import com.idd.shared.util.BpmLogger;
import com.idd.shared.util.JsonHelper;
import com.idd.shared.util.LogHelper;

import oracle.jdbc.OracleTypes;

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
				SYSTEM);

		try {
			conn = getConnection();

			try {
				ServiceConfig serviceConfig = serviceConfigCache.get(serviceName, version);
				if (serviceConfig == null) {
					throw new IllegalStateException(
							String.format(
									"Service config not found for serviceName=%s, version=%s",
									serviceName,
									version));
				}
				SQLConfig sqlConfig = JsonHelper.parseObject(serviceConfig.getDetailConfig(), SQLConfig.class);
				if (sqlConfig == null) {
					throw new IllegalStateException(
							String.format(
									"Detail of service config is invalid for serviceName=%s, version=%s",
									serviceName,
									version));
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
						JsonHelper.stringify(output));

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
						ERROR_CODE.ERROR);

				return Response.error(ERROR_CODE.ERROR.getCode(), "Database execution error", logId);
			} catch (Exception e) {
				e.printStackTrace();
				Long logId = logHelper.saveError(
						log,
						e,
						ERROR_CODE.ERROR);

				String message = e.getMessage() != null
						? e.getMessage()
						: e.getClass().getSimpleName();

				return Response.error(ERROR_CODE.ERROR.getCode(), message, logId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (cstmt != null)
					cstmt.close();
			} catch (Exception e) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}

		return Response.error(ERROR_CODE.ERROR.getCode(), ERROR_CODE.ERROR.getDefaultMessage());
	}

	private void applyInputParams(
			CallableStatement cstmt,
			List<SQLParam> params) throws SQLException {

		if (params == null) {
			throw new IllegalArgumentException("SQL params is null");
		}

		for (SQLParam param : params) {

			if (param == null) {
				throw new IllegalArgumentException("SQLParam is null");
			}

			int index = param.getParamIndex();
			String sqlType = param.getSqlType() != null
					? param.getSqlType().toUpperCase()
					: null;

			/* ================= IN ================= */
			if (param.isIn()) {

				Object value = param.getValue();

				if (value == null) {
					cstmt.setNull(index, SQLHelper.resolveJdbcType(sqlType));
					continue;
				}

				switch (sqlType) {

					case "CLOB":
						// Oracle safest way
						if (value instanceof String) {
							String str = (String) value;
							cstmt.setCharacterStream(index,
									new StringReader(str),
									str.length());
						} else {
							throw new IllegalArgumentException("CLOB must be String");
						}
						break;

					case "VARCHAR":
					case "VARCHAR2":
						cstmt.setString(index, value.toString());
						break;

					case "NUMBER":
					case "NUMERIC":
						if (value instanceof Number) {
							cstmt.setObject(index, value);
						} else {
							cstmt.setObject(index, new java.math.BigDecimal(value.toString()));
						}
						break;

					default:
						// Do NOT force jdbcType here
						cstmt.setObject(index, value);
						break;
				}
			}

			/* ================= OUT ================= */
			if (param.isOut()) {

				if ("REF_CURSOR".equals(sqlType)) {

					cstmt.registerOutParameter(
							index,
							OracleTypes.CURSOR);

				} else {

					cstmt.registerOutParameter(
							index,
							SQLHelper.resolveJdbcType(sqlType));
				}
			}
		}
	}

	private Map<String, Object> extractOutParams(
			CallableStatement cstmt,
			List<SQLParam> params) throws SQLException {

		Map<String, Object> outResult = new LinkedHashMap<>();

		for (SQLParam param : params) {

			if (!param.isOut())
				continue;

			Object value;

			if ("REF_CURSOR".equalsIgnoreCase(param.getSqlType())) {

				ResultSet rs = null;

				try {
					rs = (ResultSet) cstmt.getObject(param.getParamIndex());
					value = SQLHelper.convertResultSet(rs);
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (Exception ignore) {
						}
					}
				}

			} else if ("CLOB".equalsIgnoreCase(param.getSqlType())) {

				java.sql.Clob clob = cstmt.getClob(param.getParamIndex());
				if (clob != null) {
					value = clob.getSubString(1, (int) clob.length());
				} else {
					value = null;
				}

			} else {

				value = cstmt.getObject(param.getParamIndex());
			}

			outResult.put(param.getOutputMapping(), value);
		}

		return outResult;
	}
}
