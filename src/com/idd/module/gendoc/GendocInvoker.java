package com.idd.module.gendoc;

import com.idd.entity.RESTConfig;

import java.util.Base64;

import com.idd.entity.ERROR_CODE;
import com.idd.entity.LogRecord;
import com.idd.entity.Response;
import com.idd.entity.ServiceConfig;
import com.idd.module.sql.SQLConnector;
import com.idd.util.BasicAuthRestClient;
import com.idd.util.CryptoService;
import com.idd.util.JsonHelper;
import com.idd.util.LogHelper;
import com.idd.util.ServiceConfigCache;

public class GendocInvoker extends SQLConnector {

	private final static String SYSTEM = "GENDOC";
	private final LogHelper logHelper;
	private final ServiceConfigCache serviceConfigCache;
	private final CryptoService cryptoService;
	
	public GendocInvoker(String dataSourceName, String secretKey) {
		super(dataSourceName);
		this.logHelper = new LogHelper(dataSourceName);
		this.serviceConfigCache = new ServiceConfigCache(dataSourceName);
		this.cryptoService = new CryptoService(Base64.getDecoder().decode(secretKey));
	}

	public Response execute(String serviceName, String version, String caseId, GenDocRequest request) {
		LogRecord log = LogRecord.init(
			caseId,
            request.getTemplateFileName(),
            JsonHelper.stringify(request),
            SYSTEM
        );
		
		Response response = null;
		
		try {
			ServiceConfig serviceConfig = serviceConfigCache.get(serviceName, version);
			if (serviceConfig == null) {
				throw new IllegalStateException(
						String.format(
				                "Service config not found for serviceName=%s, version=%s",
				                serviceName,
				                version
				            )
						);
			}
			
			RESTConfig apiConfig = JsonHelper.parseObject(serviceConfig.getDetailConfig(), RESTConfig.class);
			if (apiConfig == null) {
				throw new IllegalStateException(
						String.format(
				                "Detail of service config is invalid for serviceName=%s, version=%s",
				                serviceName,
				                version
				            )
						);
			}
			apiConfig.setPassword(cryptoService.decrypt(apiConfig.getPassword()));
			
			String body = JsonHelper.stringify(request);
			log.setToInput(body);
			
			BasicAuthRestClient client = new BasicAuthRestClient(apiConfig.getUsername(), apiConfig.getPassword());
			String result = client.execute(apiConfig.getUrl(), apiConfig.getMethod(), apiConfig.getTimeout(), body);
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
