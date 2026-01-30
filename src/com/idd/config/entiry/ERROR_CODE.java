package com.idd.config.entiry;

public enum ERROR_CODE {
	SUCCESS("00", "Success"),
    ERROR("EX-99", "Service Error");
    
    private final String code;
    private final String defaultMessage;

    ERROR_CODE(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

	public String getCode() {
		return code;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}
}
