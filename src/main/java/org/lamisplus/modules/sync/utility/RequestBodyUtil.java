package org.lamisplus.modules.sync.utility;

import okhttp3.*;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.io.InputStream;


public class RequestBodyUtil {

    public static RequestBody create(final MediaType mediaType, final InputStream inputStream) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                try {
                    return inputStream.available();
                } catch (IOException e) {
                    return 0;
                }
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(inputStream);
                    sink.writeAll(source);
                } finally {
                    Util.closeQuietly(source);
                }
            }
        };
    }
}


/*

    OkHttpClient client = new OkHttpClient();

    MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    InputStream inputStream = getAssets().open("README.md");

    RequestBody requestBody = RequestBodyUtil.create(MEDIA_TYPE_MARKDOWN, inputStream);
    Request request = new Request.Builder()
            .url("https://api.github.com/markdown/raw")
            .post(requestBody)
            .build();

    Response response = client.newCall(request).execute();
if (!response.isSuccessful())
        throw new IOException("Unexpected code " + response);

        Log.d("POST", response.body().string());

 */
