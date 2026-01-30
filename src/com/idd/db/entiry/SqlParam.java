package com.idd.db.entiry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SqlParam {

    private final String name;
    private final int sqlType;
    private final ParamMode mode;

    private final String inputMapping;
    private final String outputMapping;

    private final boolean required;
    private final Object defaultValue;

    @JsonCreator
    public SqlParam(
        @JsonProperty("name") String name,
        @JsonProperty("sqlType") int sqlType,
        @JsonProperty("mode") ParamMode mode,
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
        
        this.name = name;
        this.sqlType = sqlType;
        this.mode = mode;
        this.inputMapping = inputMapping;
        this.outputMapping = outputMapping;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    
    private SqlParam(Builder b) {
        this.name = b.name;
        this.sqlType = b.sqlType;
        this.mode = b.mode;
        this.inputMapping = b.inputMapping;
        this.outputMapping = b.outputMapping;
        this.required = b.required;
        this.defaultValue = b.defaultValue;
    }

    public String getName() { return name; }
    public int getSqlType() { return sqlType; }
    public ParamMode getMode() { return mode; }
    public String getInputMapping() { return inputMapping; }
    public String getOutputMapping() { return outputMapping; }
    public boolean isRequired() { return required; }
    public Object getDefaultValue() { return defaultValue; }

    public static class Builder {
        private String name;
        private int sqlType;
        private ParamMode mode;
        private String inputMapping;
        private String outputMapping;
        private boolean required;
        private Object defaultValue;

        public Builder name(String v) { this.name = v; return this; }
        public Builder sqlType(int v) { this.sqlType = v; return this; }
        public Builder mode(ParamMode v) { this.mode = v; return this; }
        public Builder inputMapping(String v) { this.inputMapping = v; return this; }
        public Builder outputMapping(String v) { this.outputMapping = v; return this; }
        public Builder required(boolean v) { this.required = v; return this; }
        public Builder defaultValue(Object v) { this.defaultValue = v; return this; }

        public SqlParam build() {
            return new SqlParam(this);
        }
    }
}
