package com.idd.config.entity;

import java.util.Date;
import java.util.UUID;

public class LogRecord {

	private Long id;
	private String service;
	private String fromInput;
	private String fromOutput;
	private String toInput;
	private String toOutput;
	private String errorCode;
	private String errorMessage;
	private String stacktrace;
	private String logCode;
	private String caseId;
	private long start;
	private String timing;
	private String system;
	private Date createdDate;
	
	public static LogRecord init(
            String caseId,
            String serviceCode,
            String inputData,
            String targetSystem
    ) {
		LogRecord log = new LogRecord();
		log.setCaseId(caseId);
		log.setService(serviceCode);
		log.setLogCode(UUID.randomUUID().toString());
		log.setSystem(targetSystem);
		log.setFromInput(inputData);
		log.setStart(System.currentTimeMillis());
		
		return log;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getFromInput() {
		return fromInput;
	}

	public void setFromInput(String fromInput) {
		this.fromInput = fromInput;
	}

	public String getFromOutput() {
		return fromOutput;
	}

	public void setFromOutput(String fromOutput) {
		this.fromOutput = fromOutput;
	}

	public String getToInput() {
		return toInput;
	}

	public void setToInput(String toInput) {
		this.toInput = toInput;
	}

	public String getToOutput() {
		return toOutput;
	}

	public void setToOutput(String toOutput) {
		this.toOutput = toOutput;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStacktrace() {
		return stacktrace;
	}

	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}

	public String getLogCode() {
		return logCode;
	}

	public void setLogCode(String logCode) {
		this.logCode = logCode;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public String getTiming() {
		return timing;
	}

	public void setTiming(String timing) {
		this.timing = timing;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
