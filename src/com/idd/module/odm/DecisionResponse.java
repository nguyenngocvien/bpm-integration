package com.idd.module.odm;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DecisionResponse {
    @JsonProperty("__DecisionID__")
    private String decisionId;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Output")
    private String output;

	public String getDecisionId() {
		return decisionId;
	}

	public void setDecisionId(String decisionId) {
		this.decisionId = decisionId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
    
    
}
