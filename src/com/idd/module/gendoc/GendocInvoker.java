package com.idd.module.gendoc;

import java.util.Base64;

import com.idd.config.cache.ServiceConfigCache;
import com.idd.config.entity.ERROR_CODE;
import com.idd.config.entity.LogRecord;
import com.idd.config.entity.RESTConfig;
import com.idd.config.entity.Response;
import com.idd.config.entity.ServiceConfig;
import com.idd.module.sql.SQLConnector;
import com.idd.shared.crypto.CryptoService;
import com.idd.shared.restclient.BasicAuthRestClient;
import com.idd.shared.util.JsonHelper;
import com.idd.shared.util.LogHelper;

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
			
			GenDocResponse docResponse = JsonHelper.parseObject(result, GenDocResponse.class);
			Long logId = logHelper.saveSuccess(
                log,
                serviceConfig.isLogEnabled(),
                JsonHelper.stringify(result)
            );
			
			response = Response.success(docResponse, logId);
			
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
