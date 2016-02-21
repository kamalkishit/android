package com.humanize.android.config;

import com.humanize.android.BuildConfig;

/**
 * Created by kamal on 1/14/16.
 */
public class ApiUrls {

    public final static String URL_SERVER = BuildConfig.URL_SERVER;
    public final static String URL_IMAGES = URL_SERVER + "/images/";

    public final static String URL_INVITE_FRIEND = URL_SERVER + "/api/user/invite";
    public final static String URL_CONTACT_US = URL_SERVER + "/api/user/contactUs";
    public final static String URL_SUGGEST_ARTICLE = URL_SERVER + "/api/user/suggest";
    public final static String URL_SUBMIT_ARTICLE = URL_SERVER + "/api/user/submit";
    public final static String URL_CREATE_ARTICLE = URL_SERVER + "/api/content/create";
    public final static String URL_CONTENT = URL_SERVER + "/api/content/find";
    public final static String URL_TRENDS = URL_SERVER + "/api/content/trends";
    public final static String URL_SINGLE_CONTENT = URL_SERVER + "/api/content?urlId=";
    public final static String URL_PAPER = URL_SERVER + "/api/paper";
    public final static String URL_UPDATE_CONTENT = URL_SERVER + "/api/content/update";

    public final static String URL_GCM_REGISTER = URL_SERVER + "/api/gcm/register";
}
