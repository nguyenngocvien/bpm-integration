package com.idd.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
	
	public static String generateUUID() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	    return LocalDateTime.now().format(formatter);
	}

    public static String generateMessageDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String base = LocalDateTime.now().format(formatter);
        return base + ".000";
    }
}

