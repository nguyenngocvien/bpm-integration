package com.idd.shared.util;

import com.idd.shared.entity.ERROR_CODE;
import com.idd.shared.entity.SiLog;
import com.idd.shared.repository.LogRepository;

public class LogHelper {
	
	private final LogRepository logRepository;
	
	public LogHelper(String dataSourceName) {
		this.logRepository = new LogRepository(dataSourceName);
	}
	
    public Long saveSuccess(
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

        return logRepository.writeLog(log);
    }
    
    public Long saveError(
            SiLog log,
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
