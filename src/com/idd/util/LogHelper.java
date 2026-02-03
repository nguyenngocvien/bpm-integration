package com.idd.util;

import com.idd.entity.ERROR_CODE;
import com.idd.entity.LogRecord;
import com.idd.repository.LogRepository;

public class LogHelper {
	
	private final LogRepository logRepository;
	
	public LogHelper(String dataSourceName) {
		this.logRepository = new LogRepository(dataSourceName);
	}
	
    public Long saveSuccess(
    		LogRecord log,
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

        return logRepository.writeLog(log);
    }
    
    public Long saveError(
            LogRecord log,
            Exception ex,
            ERROR_CODE errorCode
    ) {
        log.setTiming(elapsed(log.getStart()));
        log.setErrorCode(errorCode.getCode());
        log.setErrorMessage(ex.getMessage());
        log.setStacktrace(StackTraceUtil.getStackTraceAsString(ex));

        return logRepository.writeLog(log);
    }

    private static String elapsed(long start) {
        return (System.currentTimeMillis() - start) + "ms";
    }
}
