package com.idd.shared.restclient;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthRestClient extends RestClient {

    private final String username;
    private final String password;

    public BasicAuthRestClient(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected void applyAuth(HttpURLConnection conn) {
        String auth = username + ":" + password;
        String encoded = Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        conn.setRequestProperty("Authorization", "Basic " + encoded);
    }
}