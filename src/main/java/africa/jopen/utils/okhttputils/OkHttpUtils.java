package africa.jopen.utils.okhttputils;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
public class OkHttpUtils {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final MediaType XML = MediaType.get("application/xml; charset=utf-8");
    private static final int TIMEOUT = 30;

    private OkHttpClient client;

    public OkHttpUtils() {
        client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public void get(String url, Map<String, String> headers, Callback callback) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();

        if (headers != null) {
            Headers.Builder headerBuilder = new Headers.Builder();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
            builder.headers(headerBuilder.build());
        }

        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
    public void postJson(String url, String json, Map<String, String> headers, Callback callback) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);

        if (headers != null) {
            Headers.Builder headerBuilder = new Headers.Builder();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
            builder.headers(headerBuilder.build());
        }

        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
    public void postXml(String url, String xml, Map<String, String> headers, Callback callback) {
        RequestBody requestBody = RequestBody.create(XML, xml);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);

        if (headers != null) {
            Headers.Builder headerBuilder = new Headers.Builder();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
            builder.headers(headerBuilder.build());
        }

        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
    public void postForm(String url, Map<String, String> formData, Map<String, String> headers, Callback callback) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = formBuilder.build();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);

        if (headers != null) {
            Headers.Builder headerBuilder = new Headers.Builder();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
            builder.headers(headerBuilder.build());
        }

        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
    public void delete(String url, Map<String, String> headers, Callback callback) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .delete();

        if (headers != null) {
            Headers.Builder headerBuilder = new Headers.Builder();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
            builder.headers(headerBuilder.build());
        }

        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void getWithParams(String url, Map<String, String> params, Map<String, String> headers, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .get();

        if (headers != null) {
            Headers.Builder headerBuilder = new Headers.Builder();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
            requestBuilder.headers(headerBuilder.build());
        }

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void getWithParams(String url, Map<String, String> params, Map<String, String> headers, String username, String password, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .get();

        if (headers != null) {
            Headers.Builder headerBuilder = new Headers.Builder();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
            requestBuilder.headers(headerBuilder.build());
        }

        if (username != null && password != null) {
            String credentials = Credentials.basic(username, password);
            requestBuilder.header("Authorization", credentials);
        }

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }


}
