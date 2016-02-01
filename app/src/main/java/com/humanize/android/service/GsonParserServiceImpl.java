package com.humanize.android.service;

import com.google.gson.Gson;

/**
 * Created by kamal on 1/1/16.
 */
public class GsonParserServiceImpl implements JsonParserService {

    private static final LogService logService = new LogServiceImpl();
    private static final String TAG = GsonParserServiceImpl.class.getSimpleName();

    public <T> T fromJson(String json, Class<T> classOfT) throws Exception {
        try {
            return new Gson().fromJson(json, classOfT);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
            throw exception;
        }
    }

    public String toJson(Object src) throws Exception {
        try {
            return new Gson().toJson(src);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
            throw exception;
        }
    }
}
