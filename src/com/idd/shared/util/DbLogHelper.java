package com.idd.shared.util;

import java.sql.Connection;

import com.idd.config.entiry.ERROR_CODE;
import com.idd.config.entiry.SiLog;
import com.idd.config.repository.LogRepository;

public class DbLogHelper {
	
    public static Long saveSuccess(
    		Connection conn,
    		SiLog log,
    		Boolean isLogEnabled,
            String result
    ) {
        if (!isLogEnabled) {
            return null;
        }

        log.setFromOutput(result);
        log.setTiming(elapsed(log.getStart()));
        log.setErrorCode(ERROR_CODE.SUCCESS.getCode());
        log.setErrorMessage(ERROR_CODE.SUCCESS.getDefaultMessage());

        return LogRepository.writeLog(conn, log);
    }
    
    public static Long saveError(
    		Connection conn,
            SiLog log,
            Exception ex,
            ERROR_CODE errorCode
    ) {
        log.setTiming(elapsed(log.getStart()));
        log.setErrorCode(errorCode.getCode());
        log.setErrorMessage(ex.getMessage());
        log.setStacktrace(StackTraceUtil.getStackTraceAsString(ex));

        return LogRepository.writeLog(conn, log);
    }

    private static String elapsed(long start) {
        return (System.currentTimeMillis() - start) + "ms";
    }
}
