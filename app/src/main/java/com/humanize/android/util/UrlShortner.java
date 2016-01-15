package com.humanize.android.util;


import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;

import com.humanize.android.common.StringConstants;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;

/**
 * Created by kamal on 1/4/16.
 */
public class UrlShortner {

    public String getShortUrl(String longUrl) {
        try {
            Request request = new Request(longUrl);
            HttpUtil.getInstance().getShortUrl("https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyCIs43UuRuXjHb8-SeWOD559rjSoXNgO3Y", new JsonParserImpl().toJson(request), new BookmarkCallback());
        } catch (Exception exception) {

        }


        return null;
    }

    private class BookmarkCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            exception.printStackTrace();
        }

        @Override
        public void onResponse(Call call, final okhttp3.Response response) throws IOException {
            if (!response.isSuccessful()) {

            } else {
                final String responseStr = response.body().string().toString();
                try {
                    Response responseObj = new JsonParserImpl().fromJson(responseStr, Response.class);
                    System.out.println(responseObj.getId());
                } catch (Exception exception) {

                }
            }
        }
    }


    class Response {

        private String kind;
        private String id;
        private String longUrl;

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLongUrl() {
            return longUrl;
        }

        public void setLongUrl(String longUrl) {
            this.longUrl = longUrl;
        }
    }

    class Request {

        private String longUrl;

        Request(String longUrl) {
            this.longUrl = longUrl;
        }

        public String getLongUrl() {
            return longUrl;
        }

        public void setLongUrl(String longUrl) {
            this.longUrl = longUrl;
        }
    }
}

