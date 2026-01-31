package com.idd.sql.entiry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SQLParam {

	private Integer paramIndex;
	
    private String name;
    private int sqlType;
    private SQLParamMode mode;
    private Object value;

    private String inputMapping;
    private String outputMapping;

    private boolean required;
    private Object defaultValue;

    @JsonCreator
    public SQLParam(
    	@JsonProperty("paramIndex") Integer paramIndex,
        @JsonProperty("name") String name,
        @JsonProperty("sqlType") int sqlType,
        @JsonProperty("mode") SQLParamMode mode,
        @JsonProperty("value") Object value,
        @JsonProperty("inputMapping") String inputMapping,
        @JsonProperty("outputMapping") String outputMapping,
        @JsonProperty("required") boolean required,
        @JsonProperty("defaultValue") Object defaultValue
    ) {
    	if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("SqlParam.name is required");
        }
        if (mode == null) {
            throw new IllegalArgumentException("SqlParam.mode is required");
        }
        
        this.paramIndex = paramIndex;
        this.name = name;
        this.sqlType = sqlType;
        this.mode = mode;
        this.value = value;
        this.inputMapping = inputMapping;
        this.outputMapping = outputMapping;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    public Integer getParamIndex() { return paramIndex; }
    public String getName() { return name; }
    public int getSqlType() { return sqlType; }
    public SQLParamMode getMode() { return mode; }
    public Object getValue() { return value; }
    public String getInputMapping() { return inputMapping; }
    public String getOutputMapping() { return outputMapping; }
    public boolean isRequired() { return required; }
    public Object getDefaultValue() { return defaultValue; }

	public void setValue(Object value) {
		this.value = value;
	}
	
	@JsonIgnore
    public boolean isIn() {
        return mode == SQLParamMode.IN || mode == SQLParamMode.INOUT;
    }
    
    @JsonIgnore
    public boolean isOut() {
        return mode == SQLParamMode.OUT || mode == SQLParamMode.INOUT;
    }
}
