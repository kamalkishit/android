package com.humanize.android.util;

import android.os.Handler;
import android.os.Looper;

import com.humanize.android.HttpResponseCallback;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by Kamal on 7/1/15.
 */
public class HttpUtil {
    private static HttpUtil httpUtil = null;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private HttpUtil() {
    }

    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            httpUtil = new HttpUtil();
        }

        return httpUtil;
    }

    public void getPaper(final HttpResponseCallback httpResponseCallback) {
        String url = Config.PAPER_FIND_URL;
        get(url, httpResponseCallback);
    }

    public void submit(String url, String json, Callback callback) {
        System.out.println(url);
        System.out.println(json);
        post(url, json, callback);
    }

    public void getContents(Callback callback) {
        String url = Config.CONTENT_FIND_URL;

        ArrayList<String> categories = new ArrayList<String>(ApplicationState.getUser().getCategories());

        if (categories != null) {
            url = url + "?categories=";

            for (String category: categories) {
                url += category + ",";
            }
        }
        get(url, callback);
    }

    public void refreshContents(String createdDate, Callback callback) {
        String url = Config.CONTENT_FIND_URL;

        ArrayList<String> categories = new ArrayList<String>(ApplicationState.getUser().getCategories());

        if (categories != null) {
            url = url + "?categories=";

            for (String category: categories) {
                url += category + ",";
            }
        }

        if (createdDate != null) {
            url += "&createdDate=" + createdDate + "&refresh=" + true;
        }

        System.out.println(url);
        get(url, callback);
    }

    public void inviteUser(String emailId, final Callback callback) {
        String url = Config.USER_INVITE_URL;
        url += "?emailId=" + emailId;

        get(url, callback);
    }

    public void getMoreContents(String createdDate, Callback callback) {
        String url = Config.CONTENT_FIND_URL;

        ArrayList<String> categories = new ArrayList<String>(ApplicationState.getUser().getCategories());

        if (categories != null) {
            url = url + "?categories=";

            for (String category: categories) {
                url += category + ",";
            }
        }
        if (createdDate != null) {
            url += "&createdDate=" + createdDate;
        }

        get(url, callback);
    }

    public void getUserdata(String url, Callback callback) {
        get(url, callback);
    }

    public void login(String url, String json, Callback callback) {
        System.out.println(json);
        post(url, json, callback);
    }

    public void login(String url, String emailId, String password, Callback callback) {
        url += "?emailId=" + emailId + "&password=" + password;
        get(url, callback);
    }

    public void signup(String url, String json, Callback callback) {
        post(url, json, callback);
    }

    public void updateUser(String url, String json, Callback callback) {
        post(url, json, callback);
    }

    private void get(String url, Callback callback) {
        System.out.println(url);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(Config.READ_TIMEOUT, TimeUnit.SECONDS.MILLISECONDS);
        Request request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    private void get(String url, final HttpResponseCallback httpResponseCallback) {
        System.out.println(url);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(Config.READ_TIMEOUT, TimeUnit.SECONDS.MILLISECONDS);
        Request request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException ioException) {
                ioException.printStackTrace();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("1");
                        httpResponseCallback.onFailure("");
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(response.code());
                            httpResponseCallback.onFailure("");
                        }
                    });
                } else {
                    final String responseStr = response.body().string().toString();
                    System.out.println(responseStr);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                httpResponseCallback.onSuccess(responseStr);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("4");
                            }
                        }
                    });
                }
            }
        });
    }

    private void post(String url, String json, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        //RequestBody requestBody = getParams(params);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(requestBody).build();

        client.newCall(request).enqueue(callback);
    }

    private void post(String url, String json, final HttpResponseCallback httpResponseCallback) {
        OkHttpClient client = new OkHttpClient();
        //RequestBody requestBody = getParams(params);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException ioException) {
                System.out.println("onFailure");
                ioException.printStackTrace();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        httpResponseCallback.onFailure("");
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.out.println("isSuccessful");
                    System.out.println(response.body().source().toString());
                    throw new IOException("Unexpected code " + response);
                }

                final String responseStr = response.body().string().toString();
                System.out.println(responseStr);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("httputil post success");
                            httpResponseCallback.onSuccess(responseStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void post(String url, Map<String, String> params, final HttpResponseCallback httpResponseCallback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = getParams(params);
        Request request = new Request.Builder().url(url).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException ioException) {
                ioException.printStackTrace();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        httpResponseCallback.onFailure("");
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            httpResponseCallback.onSuccess(new String(response.body().string().toString()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private RequestBody getParams(Map<String, String> params) {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            formEncodingBuilder = formEncodingBuilder.add(key, value);
        }

        return formEncodingBuilder.build();
    }
}
