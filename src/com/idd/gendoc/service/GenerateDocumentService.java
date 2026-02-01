package com.idd.gendoc.service;

import java.sql.SQLException;

import com.idd.gendoc.entiry.GenDocRequest;

public class GenerateDocumentService {
	public Object generate(
            String dataSourceName,
            String serviceCode,
            String caseId, 
            String templateFileName,
            String outputFileName,
            String inputData
            
    ) throws SQLException {
		GenDocRequest request = new GenDocRequest();
		request.setTemplateFileName(templateFileName);
		request.setOutputFileName(outputFileName);
		request.setInputData(inputData);
		
    	request.setInputType("json");
    	request.setECMRepository("OBJ");
    	request.setECMProperties("");
    	request.setECMDocumentClass("LOS");
    	request.setECMFolderPath("/ExportFile");
		
        return new GendocInvoker(dataSourceName)
                .execute(serviceCode, caseId, request);
    }
}
