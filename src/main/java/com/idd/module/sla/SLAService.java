package com.idd.module.sla;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.idd.config.entity.ERROR_CODE;
import com.idd.config.entity.LogRecord;
import com.idd.config.entity.Response;
import com.idd.shared.util.JsonHelper;
import com.idd.shared.util.LogHelper;

public class SLAService {
    private final static String SYSTEM = "GENDOC";
    private final static String SERVICE_NAME = "CALCULATE_SLA";

    public static String calculateSLA(int minutes, Calendar start, String datasourceName, String caseId) {

        LogHelper logHelper = new LogHelper(datasourceName);

        Response response = null;

        LogRecord log = LogRecord.init(
                caseId,
                SERVICE_NAME,
                formatInput(minutes, start),
                SYSTEM);

        try {
            Date result = new SLACalculator(datasourceName).calculateSLA(minutes, start);
            log.setFromOutput(result.toString());

            Long logId = logHelper.saveSuccess(log, true, result.toString());

            response = Response.success(result, logId);

        } catch (Exception e) {
            e.printStackTrace();

            Long logId = logHelper.saveError(
                    log,
                    e,
                    ERROR_CODE.ERROR);

            String message = e.getMessage() != null
                    ? e.getMessage()
                    : e.getClass().getSimpleName();

            response = Response.error(ERROR_CODE.ERROR.getCode(), message, logId);
        }

        return JsonHelper.stringify(response);
    }

    private static String formatInput(int minutes, Calendar start) {

        Map<String, Object> input = new HashMap<>();
        input.put("minutes", minutes);
        input.put("start", start);

        return JsonHelper.stringify(input);
    }
}
