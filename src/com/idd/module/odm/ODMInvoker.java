package com.idd.module.odm;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.idd.entity.ERROR_CODE;
import com.idd.entity.LogRecord;
import com.idd.entity.RESTConfig;
import com.idd.entity.Response;
import com.idd.entity.ServiceConfig;
import com.idd.module.sql.SQLConnector;
import com.idd.util.BasicAuthRestClient;
import com.idd.util.CryptoService;
import com.idd.util.DateTimeUtil;
import com.idd.util.JsonHelper;
import com.idd.util.LogHelper;
import com.idd.util.ServiceConfigCache;

public class ODMInvoker extends SQLConnector{
	
	private final static String SYSTEM = "ODM";
	private final LogHelper logHelper;
	private final ServiceConfigCache serviceConfigCache;
	private final CryptoService cryptoService;
	
	public ODMInvoker(String dataSourceName, String secretKey) {
		super(dataSourceName);
		this.logHelper = new LogHelper(dataSourceName);
		this.serviceConfigCache = new ServiceConfigCache(dataSourceName);
		this.cryptoService = new CryptoService(Base64.getDecoder().decode(secretKey));
	}

	public Response execute(String serviceName, String version, String caseId, String input) {
		Map<String, Object> fromInput = new HashMap<>();
		fromInput.put("serviceName", serviceName);
		fromInput.put("version", version);
		fromInput.put("caseId", caseId);
		fromInput.put("input", input);
		
		LogRecord log = LogRecord.init(
			caseId,
			serviceName,
            JsonHelper.stringify(fromInput),
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
			
			DecisionRequest decisionRequest = new DecisionRequest();
	        decisionRequest.setDecisionId(DateTimeUtil.generateUUID());
	        decisionRequest.setInput(input);
	        
			String body = JsonHelper.stringify(decisionRequest);
			log.setToInput(body);
			
			BasicAuthRestClient client = new BasicAuthRestClient(apiConfig.getUsername(), apiConfig.getPassword());
			String result = client.execute(apiConfig.getUrl(), apiConfig.getMethod(), apiConfig.getTimeout(), body);
			log.setToOutput(result);
			
			DecisionResponse decisionResponse = JsonHelper.parseObject(result, DecisionResponse.class);
			log.setFromOutput(decisionResponse.getOutput());
			
			Long logId = logHelper.saveSuccess(
                log,
                serviceConfig.isLogEnabled(),
                decisionResponse.getOutput()
            );

			response = Response.success(decisionResponse.getOutput(), logId);
			
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