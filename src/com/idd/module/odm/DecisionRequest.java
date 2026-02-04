package com.idd.module.odm;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DecisionRequest {
	@JsonProperty("__DecisionID__")
    private String decisionId;
    
    @JsonProperty("Input")
    private String input;

	public String getDecisionId() {
		return decisionId;
	}

	public void setDecisionId(String decisionId) {
		this.decisionId = decisionId;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
    
    
}
