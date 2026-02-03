package com.idd.util;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonHelper() {}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseToMap(String json) {
        try {
            return MAPPER.readValue(json, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON input", e);
        }
    }
    
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Cannot parse JSON to " + clazz.getSimpleName(), e
            );
        }
    }

    @SuppressWarnings("unchecked")
    public static Object getByPath(Map<String, Object> root, String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        String[] parts = path.split("\\.");
        Object current = root;

        for (String p : parts) {
            if (!(current instanceof Map)) {
                return null;
            }
            current = ((Map<String, Object>) current).get(p);
        }
        return current;
    }
    
    /** Convert object → JSON string */
    public static String stringify(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot stringify object to JSON", e);
        }
    }
}
