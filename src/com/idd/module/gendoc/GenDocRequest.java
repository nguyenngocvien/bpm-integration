package com.idd.module.gendoc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenDocRequest {
	
	@JsonProperty("TemplateFileName")
    private String templateFileName;
	
    @JsonProperty("OutputFileName")
    private String outputFileName;
    
    @JsonProperty("QRCodeEncodesString")
    private String QRCodeEncodesString;
    
    @JsonProperty("InputType")
    private String inputType;
    
    @JsonProperty("InputData")
    private String inputData;
    
    @JsonProperty("ECMRepository")
    private String ecmRepo;
    
    @JsonProperty("ECMProperties")
    private String ecmProps;
    
    @JsonProperty("ECMDocumentClass")
    private String ecmDocClass;
    
    @JsonProperty("ECMFolderPath")
    private String ecmFolderPath;

	public String getTemplateFileName() {
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getQRCodeEncodesString() {
		return QRCodeEncodesString;
	}

	public void setQRCodeEncodesString(String qRCodeEncodesString) {
		QRCodeEncodesString = qRCodeEncodesString;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getInputData() {
		return inputData;
	}

	public void setInputData(String inputData) {
		this.inputData = inputData;
	}

	public String getEcmRepo() {
		return ecmRepo;
	}

	public void setEcmRepo(String ecmRepo) {
		this.ecmRepo = ecmRepo;
	}

	public String getEcmProps() {
		return ecmProps;
	}

	public void setEcmProps(String ecmProps) {
		this.ecmProps = ecmProps;
	}

	public String getEcmDocClass() {
		return ecmDocClass;
	}

	public void setEcmDocClass(String ecmDocClass) {
		this.ecmDocClass = ecmDocClass;
	}

	public String getEcmFolderPath() {
		return ecmFolderPath;
	}

	public void setEcmFolderPath(String ecmFolderPath) {
		this.ecmFolderPath = ecmFolderPath;
	}
    
}