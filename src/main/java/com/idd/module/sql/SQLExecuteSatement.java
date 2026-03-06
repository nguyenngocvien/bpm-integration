package com.idd.module.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class SQLExecuteSatement extends SQLConnector {

	private final static String SYSTEM = "DB";
	private final LogHelper logHelper;
	private final ServiceConfigCache serviceConfigCache;

	public SQLExecuteSatement(String dataSourceName) {
		super(dataSourceName);
		this.logHelper = new LogHelper(dataSourceName);
		this.serviceConfigCache = new ServiceConfigCache(dataSourceName);
	}

	public Response execute(String serviceName, String version, String input, String traceId) {
		Connection conn = null;
		PreparedStatement pstmt = null;

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

				String sql = sqlConfig.getSqlStatement();
				if (sql == null || sql.trim().isEmpty()) {
					throw new IllegalArgumentException("SQL statement is null or empty");
				}

				pstmt = conn.prepareStatement(sql);

				List<SQLParam> params = SQLHelper.parseParamValue(sqlConfig.getParams(), input);
				if (params == null) {
					params = new ArrayList<>();
				}

				log.setToInput(SQLHelper.buildInputLog(sql, params));

				applyInputParams(pstmt, params);

				Object output = executeByType(pstmt, sqlConfig.getDbExecuteType());
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
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
			}
		}

		return Response.error(ERROR_CODE.ERROR.getCode(), ERROR_CODE.ERROR.getDefaultMessage());
	}

	private void applyInputParams(PreparedStatement pstmt, List<SQLParam> params) throws SQLException {
		if (params == null || params.isEmpty()) {
			return;
		}

		for (SQLParam param : params) {

			if (param == null) {
				throw new IllegalArgumentException("SQLParam is null");
			}

			if (param.isOut()) {
				throw new IllegalArgumentException("OUT or INOUT params are not supported for SQL statement");
			}

			if (param.getValue() == null) {
				pstmt.setNull(
						param.getParamIndex(), SQLHelper.resolveJdbcType(param.getSqlType()));
			} else {
				pstmt.setObject(
						param.getParamIndex(),
						param.getValue(), SQLHelper.resolveJdbcType(param.getSqlType()));
			}
		}
	}

	private Object executeByType(PreparedStatement pstmt, SQLExecuteType executeType) throws SQLException {
		SQLExecuteType type = executeType != null ? executeType : SQLExecuteType.QUERY;

		if (type == SQLExecuteType.UPDATE) {
			Map<String, Object> result = new LinkedHashMap<>();
			result.put("affectedRows", pstmt.executeUpdate());
			return result;
		}

		try (ResultSet rs = pstmt.executeQuery()) {
			return SQLHelper.convertResultSet(rs);
		}
	}
}
