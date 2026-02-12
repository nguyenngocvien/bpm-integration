package com.idd.module.external;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.idd.config.cache.ServiceConfigCache;
import com.idd.config.entity.ERROR_CODE;
import com.idd.config.entity.ExternalApiConfig;
import com.idd.config.entity.LogRecord;
import com.idd.config.entity.Response;
import com.idd.config.entity.ServiceConfig;
import com.idd.module.sql.SQLConnector;
import com.idd.shared.crypto.CryptoService;
import com.idd.shared.restclient.BasicAuthRestClient;
import com.idd.shared.restclient.BearerTokenRestClient;
import com.idd.shared.restclient.SoapClient;
import com.idd.shared.util.JsonHelper;
import com.idd.shared.util.LogHelper;
import com.idd.shared.util.XmlToJsonConverter;

public class ExternalApiInvoker extends SQLConnector {

	private final static String SYSTEM = "EXTERNAL";
	private final LogHelper logHelper;
	private final ServiceConfigCache serviceConfigCache;
	private final CryptoService cryptoService;
	
	public ExternalApiInvoker(String dataSourceName, String secretKey) {
		super(dataSourceName);
		this.logHelper = new LogHelper(dataSourceName);
		this.serviceConfigCache = new ServiceConfigCache(dataSourceName);
		this.cryptoService = new CryptoService(Base64.getDecoder().decode(secretKey));
	}

	public Response execute(String serviceName, String version, String input, String caseId) {
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
			log.setSystem(serviceConfig.getSystemName());
			
			ExternalApiConfig apiConfig = JsonHelper.parseObject(serviceConfig.getDetailConfig(), ExternalApiConfig.class);
			if (apiConfig == null) {
				throw new IllegalStateException(
						String.format(
				                "Detail of service config is invalid for serviceName=%s, version=%s",
				                serviceName,
				                version
				            )
						);
			}
			if (apiConfig.getApiType() == null || apiConfig.getApiType().isEmpty()) {
				throw new IllegalStateException(
						String.format(
				                "External api type is invalid for serviceName=%s, version=%s",
				                serviceName,
				                version
				            )
						);
			}
			
			apiConfig.setPassword(cryptoService.decrypt(apiConfig.getPassword()));
			
			Map<String, Object> inputMap = JsonHelper.parseToMap(input);
			
			String body = TemplateRequestUtil.render(apiConfig.getRequestTemplate(), inputMap);
			log.setToInput(body);
			
			String result = null;
			
			if (apiConfig.isRest()) {
				if (apiConfig.isBasic()) {
					result = new BasicAuthRestClient(apiConfig.getUsername(), apiConfig.getPassword())
							.execute(apiConfig.getUrl(), apiConfig.getMethod(), apiConfig.getTimeout(), body);
				} else if (apiConfig.isToken()) {
					result = new BearerTokenRestClient(apiConfig.getToken())
							.execute(apiConfig.getUrl(), apiConfig.getMethod(), apiConfig.getTimeout(), body);
				}
			} else if (apiConfig.isSoap()) {
				result = new SoapClient().execute(apiConfig.getUrl(), apiConfig.getMethod(), apiConfig.getTimeout(), apiConfig.getHeaders(), body);
			}
			
			log.setToOutput(result);
			
			String output = XmlToJsonConverter.convert(result);
			output = JsonHelper.normalizeJson(output);
			log.setFromOutput(output);

			Long logId = logHelper.saveSuccess(
                log,
                serviceConfig.isLogEnabled(),
                output
            );
			
			response = Response.success(output, logId);
			
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