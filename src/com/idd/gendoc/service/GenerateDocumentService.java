package com.idd.gendoc.service;

import java.sql.SQLException;

import com.idd.gendoc.entiry.GenDocRequest;
import com.idd.shared.entity.Response;
import com.idd.shared.util.JsonHelper;

public class GenerateDocumentService {
	public String generate(
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
		
    	Response r = new GendocInvoker(dataSourceName)
                .execute(serviceCode, caseId, request);
    	
    	return JsonHelper.stringify(r);
    }
}
