package com.idd.shared.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public final class XmlToJsonConverter {

    private static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private XmlToJsonConverter() {
    }

    public static String convert(String xml) {
        try {
            JsonNode jsonNode = XML_MAPPER.readTree(xml.getBytes());
            return JSON_MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(jsonNode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert XML to JSON", e);
        }
    }
}
