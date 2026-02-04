package com.idd.module.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import java.util.Properties;

public class SmtpSessionFactory {

    public Session createSession(SmtpConfig cfg) {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", cfg.getHost());
        props.put("mail.smtp.port", String.valueOf(cfg.getPort()));
        props.put("mail.smtp.auth", String.valueOf(cfg.isAuth()));
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        props.put("mail.debug", String.valueOf(cfg.isDebug()));

        if (cfg.isSsl()) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }

        if (cfg.isAuth()) {
            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            cfg.getUsername(),
                            cfg.getPassword()
                    );
                }
            });
        }

        return Session.getInstance(props);
    }
}
