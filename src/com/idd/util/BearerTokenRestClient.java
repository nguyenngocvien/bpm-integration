package com.idd.util;

import java.net.HttpURLConnection;

public class BearerTokenRestClient extends RestClient {

    private final String token;

    public BearerTokenRestClient(String token) {
        this.token = token;
    }

    @Override
    protected void applyAuth(HttpURLConnection conn) {
        conn.setRequestProperty("Authorization", "Bearer " + token);
    }
}
