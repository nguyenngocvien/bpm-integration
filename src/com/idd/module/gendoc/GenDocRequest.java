package com.idd.module.gendoc;

public class GenDocRequest {
    private String TemplateFileName;
    private String OutputFileName;
    private String QRCodeEncodesString;
    private String InputType;
    private String InputData;
    private String ECMRepository;
    private String ECMProperties;
    private String ECMDocumentClass;
    private String ECMFolderPath;
	public String getTemplateFileName() {
		return TemplateFileName;
	}
	public void setTemplateFileName(String templateFileName) {
		TemplateFileName = templateFileName;
	}
	public String getOutputFileName() {
		return OutputFileName;
	}
	public void setOutputFileName(String outputFileName) {
		OutputFileName = outputFileName;
	}
	public String getQRCodeEncodesString() {
		return QRCodeEncodesString;
	}
	public void setQRCodeEncodesString(String qRCodeEncodesString) {
		QRCodeEncodesString = qRCodeEncodesString;
	}
	public String getInputType() {
		return InputType;
	}
	public void setInputType(String inputType) {
		InputType = inputType;
	}
	public String getInputData() {
		return InputData;
	}
	public void setInputData(String inputData) {
		InputData = inputData;
	}
	public String getECMRepository() {
		return ECMRepository;
	}
	public void setECMRepository(String eCMRepository) {
		ECMRepository = eCMRepository;
	}
	public String getECMProperties() {
		return ECMProperties;
	}
	public void setECMProperties(String eCMProperties) {
		ECMProperties = eCMProperties;
	}
	public String getECMDocumentClass() {
		return ECMDocumentClass;
	}
	public void setECMDocumentClass(String eCMDocumentClass) {
		ECMDocumentClass = eCMDocumentClass;
	}
	public String getECMFolderPath() {
		return ECMFolderPath;
	}
	public void setECMFolderPath(String eCMFolderPath) {
		ECMFolderPath = eCMFolderPath;
	}
}