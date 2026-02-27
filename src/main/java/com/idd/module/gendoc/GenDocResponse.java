package com.idd.module.gendoc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenDocResponse {

	@JsonProperty("Code")
	private String code;

	@JsonProperty("Message")
	private String message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
