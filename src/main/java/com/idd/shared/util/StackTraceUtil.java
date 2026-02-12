package com.idd.shared.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil {
	public static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
