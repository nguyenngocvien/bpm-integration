package com.idd.shared.entity;

import java.util.Map;

public class Response {
	private String code;
    private String message;
    private Map<String, Object> data;
    private String logId;
    
    public Response() {
		// TODO Auto-generated constructor stub
	}
    
    public static Response success(Map<String, Object> data, Long logId) {
    	Response r = new Response();
    	r.code = ERROR_CODE.SUCCESS.getCode();
    	r.message = ERROR_CODE.SUCCESS.getDefaultMessage();
    	r.data = data;
    	r.logId = logId.toString();
    	
    	return r;
	}
    
    public static Response error(String code, String message) {
    	Response r = new Response();
    	r.code = code;
    	r.message = message;
    	
    	return r;
	}
    
    public static Response error(String code, String message, Long logID) {
    	Response r = new Response();
    	r.code = code;
    	r.message = message;
    	r.logId = logID.toString();
    	
    	return r;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public String getLogId() {
		return logId;
	}
}