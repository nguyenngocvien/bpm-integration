package com.idd.shared.restclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class SoapClient {

    private static final int DEFAULT_TIMEOUT = 10000;

    public String execute(String endpoint, String method, int timeout, List<Map<String, String>> headers, String body) throws Exception {
        HttpURLConnection conn = openConnection(endpoint, method, timeout);


        if (headers != null && !headers.isEmpty()) {
        	for (Map<String, String> header : headers) {
                if (header == null || header.isEmpty()) {
                    continue;
                }
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    String name = entry.getKey();
                    String value = entry.getValue();

                    if (name == null || value == null) {
                        continue;
                    }

                    conn.setRequestProperty(name, value);
                }
            }	
        }

        if (hasRequestBody(method)) {
            writeBody(conn, body);
        }

        return getResponse(conn);
    }
    
    public HttpURLConnection openConnection(String endpoint, String method, int timeout) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        int effectiveTimeout = timeout > 0 ? timeout : DEFAULT_TIMEOUT;
        conn.setConnectTimeout(effectiveTimeout);
        conn.setReadTimeout(effectiveTimeout);
        conn.setRequestMethod(method);

        return conn;
    }

    private boolean hasRequestBody(String method) {
        return "POST".equalsIgnoreCase(method)
            || "PUT".equalsIgnoreCase(method)
            || "PATCH".equalsIgnoreCase(method);
    }

    private void writeBody(HttpURLConnection conn, String body) throws Exception {
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");

        byte[] input = body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(input);
        }
    }

    private String getResponse(HttpURLConnection conn) throws Exception {
        int responseCode = conn.getResponseCode();

        BufferedReader br = responseCode >= 200 && responseCode <= 299
                ? new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))
                : new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line).append("\n");
        }

        if (responseCode >= 200 && responseCode <= 299) {
            return response.toString();
        }

        throw new RuntimeException("HTTP Error " + responseCode + ": " + response);
    }
}
