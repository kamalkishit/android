package com.humanize.android.service;

/**
 * Created by kamal on 11/26/15.
 */
public interface JsonParserService {

    <T> T fromJson(String json, Class<T> classOfT) throws Exception;
    String toJson(Object src) throws Exception;
}
