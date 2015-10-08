package com.humanize.android;

/**
 * Created by Kamal on 8/8/15.
 */
public interface HttpResponseCallback {
    public void onSuccess(String response);
    public void onFailure(String errorMsg);
}
