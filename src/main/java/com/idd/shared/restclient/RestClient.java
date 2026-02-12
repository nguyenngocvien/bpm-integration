package com.idd.shared.restclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class RestClient {

    protected static final int DEFAULT_TIMEOUT = 10000;

    public String execute(String endpoint, String method, int timeout, String body) throws Exception {
        HttpURLConnection conn = openConnection(endpoint, method, timeout);
        applyAuth(conn);
        applyHeaders(conn);

        if (hasRequestBody(method)) {
            writeBody(conn, body);
        }

        return getResponse(conn);
    }

    protected HttpURLConnection openConnection(String endpoint, String method, int timeout) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        int effectiveTimeout = timeout > 0 ? timeout : DEFAULT_TIMEOUT;
        conn.setConnectTimeout(effectiveTimeout);
        conn.setReadTimeout(effectiveTimeout);
        conn.setRequestMethod(method);

        return conn;
    }

    protected boolean hasRequestBody(String method) {
        return "POST".equalsIgnoreCase(method)
            || "PUT".equalsIgnoreCase(method)
            || "PATCH".equalsIgnoreCase(method);
    }

    protected void writeBody(HttpURLConnection conn, String body) throws Exception {
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        byte[] input = body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
        conn.setRequestProperty("Content-Length", String.valueOf(input.length));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(input);
        }
    }

    protected void applyHeaders(HttpURLConnection conn) {
        conn.setRequestProperty("Accept", "application/json");
    }

    /**
     * - none
     * - basic
     * - bearer
     * - custom
     */
    protected abstract void applyAuth(HttpURLConnection conn) throws Exception;

    protected String getResponse(HttpURLConnection conn) throws Exception {
        int responseCode = conn.getResponseCode();

        BufferedReader br = responseCode >= 200 && responseCode <= 299
                ? new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))
                : new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        if (responseCode >= 200 && responseCode <= 299) {
            return response.toString();
        }

        throw new RuntimeException("HTTP Error " + responseCode + ": " + response);
    }
}
