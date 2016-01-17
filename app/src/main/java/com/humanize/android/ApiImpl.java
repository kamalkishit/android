package com.humanize.android;

import com.humanize.android.data.ContactUs;
import com.humanize.android.data.ContentSearchParams;
import com.humanize.android.data.InviteFriend;
import com.humanize.android.data.SuggestArticle;
import com.humanize.android.util.Api;
import com.humanize.android.util.GsonParserImpl;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by kamal on 1/14/16.
 */
public class ApiImpl implements Api {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void inviteFriend(InviteFriend inviteFriend, Callback callback) {
        try {
            post(ApiUrls.URL_INVITE_FRIEND, toJson(inviteFriend), callback);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void suggestArticle(SuggestArticle suggestArticle, Callback callback) {
        try {
            post(ApiUrls.URL_SUGGEST_ARTICLE, toJson(suggestArticle), callback);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void contactUs(ContactUs contactUs, Callback callback) {
        try {
            post(ApiUrls.URL_CONTACT_US, toJson(contactUs), callback);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void getContents(ContentSearchParams contentSearchParams, Callback callback) {
        try {
            post(ApiUrls.URL_CONTENT, toJson(contentSearchParams), callback);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void refreshContents(ContentSearchParams contentSearchParams, Callback callback) {
        try {
            post(ApiUrls.URL_CONTENT, toJson(contentSearchParams), callback);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void getMoreContents(ContentSearchParams contentSearchParams, Callback callback) {
        try {
            post(ApiUrls.URL_CONTENT, toJson(contentSearchParams), callback);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private String toJson(Object object) throws Exception {
        return new GsonParserImpl().toJson(object);
    }

    private void post(String url, String json, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
}
