package com.idd.main.service;

import java.sql.SQLException;

import com.idd.config.entity.Response;
import com.idd.module.gendoc.GenDocRequest;
import com.idd.module.gendoc.GendocInvoker;
import com.idd.shared.util.JsonHelper;

public class GenerateDocumentService {
	public String generate(
            String dataSourceName,
            String serviceCode,
            String version,
            String templateFileName,
            String outputFileName,
            String inputData,
            String aesKey,
            String caseId
            
    ) throws SQLException {
		GenDocRequest request = new GenDocRequest();
		request.setTemplateFileName(templateFileName);
		request.setOutputFileName(outputFileName);
		request.setInputData(inputData);
		
    	request.setInputType("json");
    	request.setEcmRepo("OBJ");
    	request.setEcmProps("");
    	request.setEcmDocClass("LOS");
    	request.setEcmFolderPath("/ExportFile");
		
    	Response r = new GendocInvoker(dataSourceName, aesKey)
                .execute(serviceCode, version, caseId, request);
    	
    	return JsonHelper.stringify(r);
    }
}
