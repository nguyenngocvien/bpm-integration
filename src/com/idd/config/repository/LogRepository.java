package com.idd.config.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.idd.config.entiry.SiLog;
import com.idd.shared.util.BpmLogger;

public class LogRepository {

    private static final String SEQ_SQL =
        "SELECT SI_LOG_ID_SEQ.NEXTVAL FROM dual";

    private static final String INSERT_SQL =
        "INSERT INTO SI_LOG (" +
        "LOG_ID, SERVICE, FROM_INPUT, FROM_OUTPUT, TO_INPUT, TO_OUTPUT, " +
        "ERROR_CODE, ERROR_MESSAGE, STACKTRACE, LOG_CODE, CASE_ID, TIMING, SYSTEM, CREATED_DATE" +
        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

    public static Long writeLog(Connection conn, SiLog log) {

    	Long logId;
		try {
			logId = nextId(conn);
			
			try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

	            ps.setLong(1, logId);
	            ps.setString(2, log.getService());
	            ps.setString(3, log.getFromInput());
	            ps.setString(4, log.getFromOutput());
	            ps.setString(5, log.getToInput());
	            ps.setString(6, log.getToOutput());
	            ps.setString(7, log.getErrorCode());
	            ps.setString(8, log.getErrorMessage());
	            ps.setString(9, log.getStacktrace());
	            ps.setString(10, log.getLogCode());
	            ps.setString(11, log.getCaseId());
	            ps.setString(12, log.getTiming());
	            ps.setString(13, log.getSystem());

	            ps.executeUpdate();
	            
	            return logId;
	            
	        } catch (Exception e) {
	            BpmLogger.error("SI_LOG insert failed", e);
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
    }

    public static Long nextId(Connection conn) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SEQ_SQL);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new IllegalStateException("Cannot get SI_LOG_ID_SEQ");
        }
    }
}
