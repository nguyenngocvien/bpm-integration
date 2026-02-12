package com.idd.module.external;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.idd.shared.util.JsonHelper;

public class TemplateRequestUtil {
	private static final Pattern PLACEHOLDER =
            Pattern.compile("\\{\\{(.+?)}}");

    private TemplateRequestUtil() {
    }
    
    public static String render(String template, Map<String, Object> values) {
        if (template == null || template.isEmpty()) {
            return JsonHelper.stringify(values);
        }

        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            Object value = values.get(key);

            String replacement = value != null
                    ? escapeXml(value.toString())
                    : "";

            matcher.appendReplacement(
                    result,
                    Matcher.quoteReplacement(replacement)
            );
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private static String escapeXml(String input) {
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
