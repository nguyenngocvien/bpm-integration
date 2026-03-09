package com.idd.module.mail;

import com.idd.module.sql.SQLConnector;
import com.idd.shared.util.BpmLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmailTemplateRepository extends SQLConnector {

    private static final String SQL = "SELECT * FROM ADM_TEMPLATE_OTT_MAIL WHERE PROCESS_CODE = ? AND TEMPLATE_TYPE = 'EMAIL' AND TEMPLATE_CODE = ? AND STATUS = 1";

    public EmailTemplateRepository(String dataSourceName) {
        super(dataSourceName);
    }

    public EmailTemplate findActiveTemplate(
            String processCode,
            String templateCode) {

        Connection conn = null;

        try {
            conn = getConnection();

            try (PreparedStatement ps = conn.prepareStatement(SQL)) {

                ps.setString(1, processCode);
                ps.setString(2, templateCode);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        return null;
                    }

                    EmailTemplate t = new EmailTemplate();
                    t.setId(rs.getLong("ID"));
                    t.setProcessCode(rs.getString("PROCESS_CODE"));
                    t.setTemplateType(rs.getString("TEMPLATE_TYPE"));
                    t.setTemplateCode(rs.getString("TEMPLATE_CODE"));
                    t.setTitle(rs.getString("TEMP_TITLE"));
                    t.setContent(rs.getString("TEMP_CONTENT"));
                    t.setActive(rs.getInt("STATUS") == 1);
                    return t;
                }
            }

        } catch (Exception e) {
            BpmLogger.error(
                    "Load email template failed, templateCode=" + templateCode, e);
            return null;
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
            }
        }
    }
}
