package com.idd.module.mail;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

public class SmtpEmailSender {

    private final SmtpSessionFactory smtpSessionFactory;
    private static final long MAX_ATTACHMENT_SIZE = 10 * 1024 * 1024; // 10MB

    public SmtpEmailSender() {
        this.smtpSessionFactory = new SmtpSessionFactory();
    }
    
    public void send(SmtpConfig smtpConfig, EmailMessage message) throws AddressException, MessagingException, UnsupportedEncodingException {

    	Session session = smtpSessionFactory.createSession(smtpConfig);
    	
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(smtpConfig.getFromAddress()));
        mimeMessage.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(message.getTo())
        );

        if (!message.getCc().isEmpty()) {
            mimeMessage.setRecipients(
                    Message.RecipientType.CC,
                    InternetAddress.parse(message.getCc())
            );
        }

        if (!message.getBcc().isEmpty()) {
            mimeMessage.setRecipients(
                    Message.RecipientType.BCC,
                    InternetAddress.parse(message.getBcc())
            );
        }

        mimeMessage.setSubject(message.getSubject(), "UTF-8");

        // ================= BODY =================
        MimeMultipart multipart = new MimeMultipart("mixed");

        MimeBodyPart bodyPart = new MimeBodyPart();
        
        String content = message.getContent() != null ? message.getContent() : "";

        if (message.isHtml()) {
            bodyPart.setContent(content, "text/html; charset=UTF-8");
        } else {
            bodyPart.setText(content, "UTF-8");
        }
        multipart.addBodyPart(bodyPart);

        // ================= ATTACHMENTS =================
        if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
            for (EmailAttachment att : message.getAttachments()) {
            	
            	if (att.getContent().length > MAX_ATTACHMENT_SIZE) {
            	    throw new IllegalArgumentException(
            	        "Attachment too large: " + att.getFileName()
            	    );
            	}
            	
                MimeBodyPart attachPart = new MimeBodyPart();

                attachPart.setFileName(
            	    MimeUtility.encodeText(att.getFileName(), "UTF-8", null)
            	);
                
                attachPart.setDataHandler(
                        new DataHandler(
                                new ByteArrayDataSource(
                                        att.getContent(),
                                        att.getContentType()
                                )
                        )
                );

                multipart.addBodyPart(attachPart);
            }
        }

        mimeMessage.setContent(multipart);
        mimeMessage.setSentDate(new Date());

        Transport.send(mimeMessage);
    }
}
