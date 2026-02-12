package com.idd.module.mail;

import java.sql.SQLException;

import com.idd.config.entity.Response;
import com.idd.shared.util.JsonHelper;

public class SendEmailService {
	
	public String send(
            String processCode,
            String templateCode,
            String version,
            String toEmails,
            String ccEmails,
            String bccEmails,
            String inputData,
            String caseId,
            String dataSourceName,
            String aesKey
            
    ) throws SQLException {
    	Response r = new EmailSender(dataSourceName, aesKey)
                .send(processCode, templateCode, version, toEmails, ccEmails, bccEmails, inputData, caseId);
    	
    	return JsonHelper.stringify(r);
    }
}
