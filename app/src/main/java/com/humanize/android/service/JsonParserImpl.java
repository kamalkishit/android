package com.humanize.android.service;

import com.google.gson.Gson;
import com.humanize.android.JsonParser;

/**
 * Created by kamal on 1/1/16.
 */
public class JsonParserImpl implements JsonParser {

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
