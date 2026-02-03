package com.idd.module.gendoc;

import com.idd.entity.RESTConfig;
import com.idd.entity.ERROR_CODE;
import com.idd.entity.LogRecord;
import com.idd.entity.Response;
import com.idd.entity.ServiceConfig;
import com.idd.module.sql.SQLConnector;
import com.idd.util.CryptoService;
import com.idd.util.JsonHelper;
import com.idd.util.LogHelper;
import com.idd.util.RestClient;
import com.idd.util.ServiceConfigCache;

public class GendocInvoker extends SQLConnector {

	private final static String SYSTEM = "GENDOC";
	private final LogHelper logHelper;
	private final ServiceConfigCache serviceConfigCache;
	private final RestClient restClient;
	private final CryptoService cryptoService;
	
	public GendocInvoker(String dataSourceName, String secretKey) {
		super(dataSourceName);
		this.logHelper = new LogHelper(dataSourceName);
		this.serviceConfigCache = new ServiceConfigCache(dataSourceName);
		this.restClient = new RestClient();
		this.cryptoService = new CryptoService(secretKey);
	}

	public Response execute(String serviceCode, String caseId, GenDocRequest request) {
		LogRecord log = LogRecord.init(
			caseId,
            request.getTemplateFileName(),
            JsonHelper.stringify(request),
            SYSTEM
        );
		
		Response response = null;
		
		try {
			ServiceConfig serviceConfig = serviceConfigCache.get(serviceCode);
			RESTConfig apiConfig = JsonHelper.parseObject(serviceConfig.getDetailConfig(), RESTConfig.class);
			
			apiConfig.setPassword(cryptoService.decrypt(apiConfig.getPassword()));
			
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
