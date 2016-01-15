package com.humanize.android;

import com.google.gson.Gson;

/**
 * Created by kamal on 11/26/15.
 */
public interface JsonParser {

    <T> T fromJson(String json, Class<T> classOfT) throws Exception;
    String toJson(Object src) throws Exception;
}
