package com.humanize.android.util;

import android.os.Handler;
import android.os.Looper;

import com.humanize.android.HttpResponseCallback;
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
        String url = Config.PAPER_URL;
        get(url, httpResponseCallback);
    }

    public void getLikes(String url, HttpResponseCallback httpResponseCallback) {
        ArrayList<String> likes = new ArrayList<String>(ApplicationState.getUser().getLikes());

        if (likes != null) {
            url = url + "?ids=";

            for (String like: likes) {
                url += like + ",";
            }
        }

        System.out.println(url);
        get(url, httpResponseCallback);
    }

    public void getBookmarks(String url, HttpResponseCallback httpResponseCallback) {
        ArrayList<String> bookmarks = new ArrayList<String>(ApplicationState.getUser().getBookmarks());

        if (bookmarks != null) {
            url = url + "?ids=";

            for (String bookmark: bookmarks) {
                url += bookmark + ",";
            }
        }

        System.out.println(url);
        get(url, httpResponseCallback);
    }

    public void submit(String url, Map<String, String> params, final HttpResponseCallback httpResponseCallback) {
        post(url, params, httpResponseCallback);
    }

    public void submit(String url, String json, final HttpResponseCallback httpResponseCallback) {
        post(url, json, httpResponseCallback);
    }

    public void getContents(final HttpResponseCallback httpResponseCallback) {
        String url = Config.GET_CONTENTS_URL;
        get(url, httpResponseCallback);
    }

    public void refreshContents(String endDate, final HttpResponseCallback httpResponseCallback) {
        String url = Config.REFRESH_CONTENTS_URL;

        if (endDate != null) {
            url += "?enddate=" + endDate;
        }

        System.out.println(url);
        get(url, httpResponseCallback);
    }

    public void getMoreContents(String startDate, final HttpResponseCallback httpResponseCallback) {
        String url = Config.GET_MORE_CONTENTS_URL;
        if (startDate != null) {
            url += "?startdate=" + startDate;
        }

        get(url, httpResponseCallback);
    }

    public void getUserdata(String url, final HttpResponseCallback httpResponseCallback) {
        get(url, httpResponseCallback);
    }

    public void login(String url, Map<String, String> params, final HttpResponseCallback httpResponseCallback) {
        post(url, params, httpResponseCallback);
    }

    public void login(String url, String json, final HttpResponseCallback httpResponseCallback) {
        System.out.println(json);
        post(url, json, httpResponseCallback);
    }

    public void signup(String url, Map<String, String> params, final HttpResponseCallback httpResponseCallback) {
        post(url, params, httpResponseCallback);
    }

    public void signup(String url, String json, final HttpResponseCallback httpResponseCallback) {
        post(url, json, httpResponseCallback);
    }

    public void updateUser(String url, String json, final HttpResponseCallback httpResponseCallback) {
        post(url, json, httpResponseCallback);
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

    private void post(String url, String json, final HttpResponseCallback httpResponseCallback) {
        OkHttpClient client = new OkHttpClient();
        //RequestBody requestBody = getParams(params);
        RequestBody requestBody = RequestBody.create(JSON, json);
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
