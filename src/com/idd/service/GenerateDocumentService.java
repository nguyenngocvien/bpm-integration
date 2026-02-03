package com.idd.service;

import java.sql.SQLException;

import com.idd.entity.Response;
import com.idd.module.gendoc.GenDocRequest;
import com.idd.module.gendoc.GendocInvoker;
import com.idd.util.JsonHelper;

public class GenerateDocumentService {
	public String generate(
            String dataSourceName,
            String serviceCode,
            String caseId, 
            String templateFileName,
            String outputFileName,
            String inputData,
            String aesKey
            
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
		
    	Response r = new GendocInvoker(dataSourceName, aesKey)
                .execute(serviceCode, caseId, request);
    	
    	return JsonHelper.stringify(r);
    }
}
