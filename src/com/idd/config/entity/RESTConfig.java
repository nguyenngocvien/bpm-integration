package com.idd.config.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class RESTConfig {
    private String url;
    private String method;
    private Integer timeout;
    private String authType;
    private String username;
    private String password;
    private String token;
    private String apiKeyHeader;
    private String apiKeyValue;
    private String oauth2ClientId;
    private String oauth2ClientSecret;
    private String oauth2TokenUrl;
    
    public boolean isBasic() {
        return "BASIC".equalsIgnoreCase(authType);
    }
    
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public String getAuthType() {
		return authType;
	}
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getApiKeyHeader() {
		return apiKeyHeader;
	}
	public void setApiKeyHeader(String apiKeyHeader) {
		this.apiKeyHeader = apiKeyHeader;
	}
	public String getApiKeyValue() {
		return apiKeyValue;
	}
	public void setApiKeyValue(String apiKeyValue) {
		this.apiKeyValue = apiKeyValue;
	}
	public String getOauth2ClientId() {
		return oauth2ClientId;
	}
	public void setOauth2ClientId(String oauth2ClientId) {
		this.oauth2ClientId = oauth2ClientId;
	}
	public String getOauth2ClientSecret() {
		return oauth2ClientSecret;
	}
	public void setOauth2ClientSecret(String oauth2ClientSecret) {
		this.oauth2ClientSecret = oauth2ClientSecret;
	}
	public String getOauth2TokenUrl() {
		return oauth2TokenUrl;
	}
	public void setOauth2TokenUrl(String oauth2TokenUrl) {
		this.oauth2TokenUrl = oauth2TokenUrl;
	}
}