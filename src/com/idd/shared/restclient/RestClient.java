package com.idd.shared.restclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RestClient {

	public String execute(ApiConfig config, String input) throws Exception {
		if (config.isBasic()) {
			return execute(config.getUrl(), config.getMethod(), config.getUsername(), config.getPassword(), input);
		}
		
		return execute(config.getUrl(), config.getMethod(), input);
	}

	public String execute(String endpoint, String method, String body) throws Exception {
		URL url = new URL(endpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setRequestProperty("Accept", "application/json");
		
		if (method == "POST") {
			conn.setRequestProperty("Content-Type", "application/json; utf-8");
			conn.setDoOutput(true);
			
			try (OutputStream os = conn.getOutputStream()) {
	            byte[] input = body.getBytes(StandardCharsets.UTF_8);
	            os.write(input, 0, input.length);
	        }
		}
		
		return getResponse(conn);
	}
	
	public String execute(String endpoint, String method, String user, String pass, String body) throws Exception {
		URL url = new URL(endpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		
		String auth = user + ":" + pass;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
		conn.setRequestProperty("Accept", "application/json");
		
		if (method == "POST") {
			conn.setRequestProperty("Content-Type", "application/json; utf-8");
			conn.setDoOutput(true);
			
			try (OutputStream os = conn.getOutputStream()) {
	            byte[] input = body.getBytes(StandardCharsets.UTF_8);
	            os.write(input, 0, input.length);
	        }
		}
		
		return getResponse(conn);
	}
	
	public String execute(String endpoint, String method, String bearerToken, String body) throws Exception {
		URL url = new URL(endpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setRequestProperty("Authorization", "Bearer " + bearerToken);
		conn.setRequestProperty("Accept", "application/json");
		
		if (method == "POST") {
			conn.setRequestProperty("Content-Type", "application/json; utf-8");
			conn.setDoOutput(true);
			
			try (OutputStream os = conn.getOutputStream()) {
	            byte[] input = body.getBytes(StandardCharsets.UTF_8);
	            os.write(input, 0, input.length);
	        }
		}
		
		return getResponse(conn);
	}
	
	private String getResponse(HttpURLConnection conn) throws Exception {
        int responseCode = conn.getResponseCode();
        
        if (responseCode >= 200 && responseCode <= 299) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            throw new RuntimeException("HTTP Error: " + responseCode);
        }
    }
}
