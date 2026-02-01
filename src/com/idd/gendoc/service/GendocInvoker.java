package com.idd.gendoc.service;

import com.idd.config.cache.ServiceConfigCache;
import com.idd.gendoc.entiry.GenDocRequest;
import com.idd.shared.entity.ERROR_CODE;
import com.idd.shared.entity.Response;
import com.idd.shared.entity.ServiceConfig;
import com.idd.shared.entity.SiLog;
import com.idd.shared.restclient.ApiConfig;
import com.idd.shared.restclient.RestClient;
import com.idd.shared.sqlconnector.SQLConnector;
import com.idd.shared.util.JsonHelper;
import com.idd.shared.util.LogHelper;

public class GendocInvoker extends SQLConnector {

	private final static String SYSTEM = "GENDOC";
	private final LogHelper logHelper;
	private final ServiceConfigCache serviceConfigCache;
	private final RestClient restClient;
	
	public GendocInvoker(String dataSourceName) {
		super(dataSourceName);
		this.logHelper = new LogHelper(dataSourceName);
		this.serviceConfigCache = new ServiceConfigCache(dataSourceName);
		this.restClient = new RestClient();
	}

	public Response execute(String serviceCode, String caseId, GenDocRequest request) {
		SiLog log = SiLog.init(
			caseId,
            request.getTemplateFileName(),
            JsonHelper.stringify(request),
            SYSTEM
        );
		
		Response response = null;
		
		try {
			ServiceConfig serviceConfig = serviceConfigCache.get(serviceCode);
			ApiConfig apiConfig = JsonHelper.parseObject(serviceConfig.getDetailConfig(), ApiConfig.class);
			String body = JsonHelper.stringify(request);
			String result = restClient.execute(apiConfig, body);
			log.setFromOutput(result);
			
			Long logId = logHelper.saveSuccess(
                log,
                serviceConfig.isLogEnabled(),
                JsonHelper.stringify(result)
            );
			
			response = Response.success(result, logId);
			
		} catch (Exception e) {
			Long logId = logHelper.saveError(
                log,
                e,
                ERROR_CODE.ERROR
            );
			
			String message = e.getMessage() != null
		            ? e.getMessage()
		            : e.getClass().getSimpleName();
			
			response = Response.error(ERROR_CODE.ERROR.getCode(), message, logId);
		}
		
		return response;
	}
}
