package com.idd.module.gendoc;

import java.sql.SQLException;

import com.idd.config.entity.Response;
import com.idd.shared.util.JsonHelper;

public class GenerateDocumentService {
	public String generate(
            String serviceCode,
            String version,
            String templateFileName,
            String outputFileName,
            String inputData,
            String inputType,
            String ecmRepo,
            String ecmProps,
            String ecmDocClass,
            String ecmFolderPath,
            String caseId,
            String dataSourceName,
            String aesKey
            
    ) throws SQLException {
		GenDocRequest request = new GenDocRequest();
		request.setTemplateFileName(templateFileName);
		request.setOutputFileName(outputFileName);
		request.setInputData(inputData);
    	request.setInputType(inputType != null && !inputType.isEmpty() ? inputType : "json");
    	request.setEcmRepo(ecmRepo != null && !ecmRepo.isEmpty() ? ecmRepo : "OBJ");
    	request.setEcmProps(ecmProps != null && !ecmProps.isEmpty() ? ecmProps : "");
    	request.setEcmDocClass(ecmDocClass != null && !ecmDocClass.isEmpty() ? ecmDocClass : "LOS");
    	request.setEcmFolderPath(ecmFolderPath != null && !ecmFolderPath.isEmpty() ? ecmFolderPath : "/ExportFile");
		
    	Response r = new GendocInvoker(dataSourceName, aesKey)
                .execute(serviceCode, version, request, caseId);
    	
    	return JsonHelper.stringify(r);
    }
}
