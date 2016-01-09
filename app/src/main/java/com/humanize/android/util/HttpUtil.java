package com.humanize.android.util;

import com.humanize.android.service.SharedPreferencesService;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by Kamal on 7/1/15.
 */
public class HttpUtil {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static HttpUtil httpUtil = null;

    private HttpUtil() {
    }

    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            httpUtil = new HttpUtil();
        }

        return httpUtil;
    }

    public void getShortUrl(String url, String json, Callback callback) {
        System.out.println(url);
        post(url, json, callback);
    }

    public void getImage(String url, Callback callback) {
        get(url, callback);
    }

    public void forgotPassword(String url, String emailId, Callback callback) {
        url += "?emailId=" + emailId;
        get(url, callback);
    }

    public void resetPassword(String url, String emailId, String tempPassword, String newPassword, Callback callback) {
        url += "?emailId=" + emailId + "&tempPassword=" + tempPassword + "&newPassword=" + newPassword;
        get(url, callback);
    }

    public void resetPassword(String url, String json, Callback callback) {
        post(url, json, callback);
    }
    public void recommendContentForUser(String url, String userId, String contentId, boolean flag, Callback callback) {
        url += "?userId=" + userId + "&contentId=" + contentId + "&flag=" + flag;
        get(url, callback);
    }

    public void bookmarkContentForUser(String url, String userId, String contentId, boolean flag, Callback callback) {
        url += "?userId=" + userId + "&contentId=" + contentId + "&flag=" + flag;
        get(url, callback);
    }

    public void incrViewCount(String url, String contentId, Callback callback) {
        url += "?contentId=" + contentId;
        get(url, callback);
    }

    public void updateRecommendationCount(String url, String contentId, boolean flag, Callback callback) {
        url += "?contentId=" + contentId + "&flag=" + flag;
        get(url, callback);
    }

    public void incrSharedCount(String url, String contentId, Callback callback) {
        url += "?contentId=" + contentId;
        get(url, callback);
    }

    public void submit(String url, String json, Callback callback) {
        System.out.println(url);
        System.out.println(json);
        post(url, json, callback);
    }

    public void recommendArticle(String url, String contentUrl, Callback callback) {
        url += "?contentUrl=" + contentUrl;
        get(url, callback);
    }

    public void inviteFriend(String url, String emailId, String invitedBy, Callback callback) {
        url += "?emailId=" + emailId;
        url += "&invitedBy=" + invitedBy;
        get(url, callback);
    }

    public void getBookmarkedContents(String url, List<String> boomkarkIds, Callback callback) {
        url += "?bookmarkIds=";

        for (String bookmarkId: boomkarkIds) {
            url += bookmarkId + ",";
        }

        get(url, callback);
    }

    public void getRecommendedContents(String url, List<String> recommendationsIds, Callback callback) {
        url += "?recommendationIds=";

        for (String bookmarkId: recommendationsIds) {
            url += bookmarkId + ",";
        }

        get(url, callback);
    }

    public void getContents(Callback callback) {
        String url = Config.CONTENT_FIND_URL;
        get(url, callback);
    }

    public void getContents(String category, Callback callback) {
        String url = Config.CONTENT_FIND_URL + "?categories=" + category;

        get(url, callback);
    }

    public void refreshContents(String createdDate, String category, Callback callback) {
        String url = Config.CONTENT_FIND_URL + "?categories=" + category;

        if (createdDate != null) {
            url += "&createdDate=" + createdDate + "&refresh=" + true;
        }

        System.out.println(url);
        get(url, callback);
    }

    public void getMoreContents(String createdDate, String category, Callback callback) {
        String url = Config.CONTENT_FIND_URL + "?categories=" + category;

        if (createdDate != null) {
            url += "&createdDate=" + createdDate;
        }

        get(url, callback);
    }

    public void refreshContents(String createdDate, Callback callback) {
        String url = Config.CONTENT_FIND_URL;


        if (createdDate != null) {
            url += "?createdDate=" + createdDate + "&refresh=" + true;
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

        if (createdDate != null) {
            url += "?createdDate=" + createdDate;
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
        okHttpClient.setReadTimeout(Config.READ_TIMEOUT, TimeUnit.MILLISECONDS);
        Request request;
        if (SharedPreferencesService.getInstance().getString(Config.TOKEN) != null) {
            request = new Request.Builder().url(url).addHeader(Config.TOKEN, SharedPreferencesService.getInstance().getString(Config.TOKEN)).build();
        } else {
            request = new Request.Builder().url(url).addHeader(Config.TOKEN, "").build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    private void post(String url, String json, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        //RequestBody requestBody = getParams(params);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request;

        if (SharedPreferencesService.getInstance().getString(Config.TOKEN) != null) {
            request = new Request.Builder().url(url).addHeader(Config.TOKEN, SharedPreferencesService.getInstance().getString(Config.TOKEN)).post(requestBody).build();
        } else {
            request = new Request.Builder().url(url).addHeader(Config.TOKEN, "").post(requestBody).build();
        }

        client.newCall(request).enqueue(callback);
    }
}
