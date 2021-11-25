package org.lamisplus.modules.sync.utility;

import okhttp3.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HttpConnectionManager {
    private final OkHttpClient httpClient = new OkHttpClient();

    public String get(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("custom-key", "lamisplus")  // add request headers
                .addHeader("User-Agent", "OkHttp Bot")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            return Objects.requireNonNull(response.body()).string();
        }

    }

    public String post(String json, String url) throws IOException {
/*
        // form parameters
        RequestBody formBody = new FormBody.Builder()
                .add("username", "abc")
                .add("password", "123")
                .add("custom", "secret")
                .build();
*/

        // json request body
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "OkHttp Bot")
                .post(body)
                .build();

        try (Response response = httpClient
                .newBuilder()
                .connectTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.MINUTES)
                .build()
                .newCall(request).execute()
        ) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            return Objects.requireNonNull(response.body()).string();
        }

    }


}
