package com.idd.main.service;

import java.sql.SQLException;

import com.idd.config.entity.Response;
import com.idd.module.mail.EmailSender;
import com.idd.shared.util.JsonHelper;

public class EmailService {
	
	public String execute(
            String processCode,
            String templateCode,
            String version,
            String toEmails,
            String ccEmails,
            String bccEmails,
            String inputData,
            String dataSourceName,
            String aesKey,
            String caseId
            
    ) throws SQLException {
    	Response r = new EmailSender(dataSourceName, aesKey)
                .send(processCode, templateCode, version, caseId, toEmails, ccEmails, bccEmails, inputData);
    	
    	return JsonHelper.stringify(r);
    }
}
