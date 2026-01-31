package com.idd.shared.entity;

public class ServiceConfig {
    private String appName;
    private String serviceName;
    private String systemName;
    private String logOn;
    private String detailConfig;
    
    public ServiceConfig(
    		String appName,
    		String serviceName,
    		String system,
    		String logOn,
    		String detail) {
    	
    	this.appName = appName;
    	this.serviceName = serviceName;
    	this.systemName = system;
    	this.logOn = logOn;
    	this.detailConfig = detail;
	}

    public boolean isLogEnabled() {
        return "1".equalsIgnoreCase(logOn);
    }

	public String getAppName() {
		return appName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getSystemName() {
		return systemName;
	}

	public String getLogOn() {
		return logOn;
	}

	public String getDetailConfig() {
		return detailConfig;
	}
    
}
