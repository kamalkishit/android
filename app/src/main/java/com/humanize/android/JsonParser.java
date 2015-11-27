package com.humanize.android;

import com.google.gson.Gson;

/**
 * Created by kamal on 11/26/15.
 */
public class JsonParser {

    public <T> T fromJson(String json, Class<T> classOfT) throws Exception {
        try {
            return new Gson().fromJson(json, classOfT);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    public String toJson(Object src) throws Exception {
        try {
            return new Gson().toJson(src);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }
}
