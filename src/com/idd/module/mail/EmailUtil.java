package com.idd.module.mail;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailUtil {
	public static String renderContent(String template, Map<String, Object> variables) {
        if (template == null) return "";

        String result = template;

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(key, value);
        }

        return result;
    }
	
	/**
     * Normalize + validate email list
     * Invalid emails are ignored
     */
    public static List<String> filterValidEmails(List<String> emails) {
        if (emails == null || emails.isEmpty()) {
            return Collections.emptyList();
        }

        return emails.stream()
                .map(EmailUtil::normalize)
                .filter(Objects::nonNull)
                .filter(EmailUtil::isValid)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * Normalize email:
     * - trim
     * - remove spaces
     * - remove trailing dots
     */
    public static String normalize(String email) {
        if (email == null) return null;
        return email.trim()
                .replaceAll("\\s+", "")
                .replaceAll("\\.+$", "");
    }

    /**
     * Validate email format
     */
    public static boolean isValid(String email) {
        try {
            InternetAddress address = new InternetAddress(email, true);
            address.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }
    
    /**
     * Convert List<String> → InternetAddress[]
     */
    public static InternetAddress[] toInternetAddress(List<String> emails) {
        return filterValidEmails(emails)
                .stream()
                .map(email -> {
                    try {
                        return new InternetAddress(email);
                    } catch (AddressException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(InternetAddress[]::new);
    }
}
