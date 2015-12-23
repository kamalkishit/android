package com.humanize.android.util;

/**
 * Created by Kamal on 6/29/15.
 */
public class Config {

    public final static String CONTENTS = "contents";

    // sharedPreferences data
    public final static String SHARED_PREFERENCES = "sharedPreferences";
    public final static String FIRST_TIME = "firstTime";
    public final static String JSON_CONTENTS = "jsonContents";
    public final static String JSON_BOOKMARKS = "jsonBookmarks";
    public final static String JSON_LIKES = "jsonLikes";
    public final static String JSON_PAPER = "jsonPaper";
    public final static String USER_DATA_JSON = "user_data_json";

    public final static String CONTENT_URL = "contentURL";

    public final static int READ_TIMEOUT = 5000;

    // URLS
    //public final static String SERVER_URL = "http://10.0.2.2:8080";
    public final static String SERVER_URL = "http://188.166.213.251:8080/server-0.1.0";
    //public final static String SERVER_URL = "http://192.168.1.3:8080";

    public final static String USER_SIGNUP_URL = SERVER_URL + "/users/signup";
    public final static String USER_LOGIN_URL = SERVER_URL + "/users/login";
    public final static String USER_UPDATE_URL = SERVER_URL + "/users/update";
    public final static String USER_RESET_PASSWORD_URL = SERVER_URL + "/users/reset";
    public final static String USER_FORGOT_PASSWORD_URL = SERVER_URL + "/users/forgot";
    public final static String USER_LOGOUT_URL = SERVER_URL + "/users/logout";
    public final static String USER_VERIFY_URL = SERVER_URL + "/users/verify";
    public final static String USER_INVITE_URL = SERVER_URL + "/users/invite";
    public final static String USER_DATA_URL = SERVER_URL + "/users/data";
    public final static String USER_CONTENT_BOOKMARK_URL = SERVER_URL + "/users/bookmark";
    public final static String USER_CONTENT_RECOMMEND_URL = SERVER_URL + "/users/recommend";

    public final static String CONTENT_CREATE_URL = SERVER_URL + "/content/create";
    public final static String CONTENT_UPDATE_URL = SERVER_URL + "/content/update";
    public final static String CONTENT_FIND_URL = SERVER_URL + "/content/find";
    public final static String IMAGES_URL = SERVER_URL + "/images/";
    public final static String RECOMMEND_ARTICLE_URL = SERVER_URL + "/content/recommendArticle";
    public final static String UPDATE_RECOMMENDATION_COUNT = SERVER_URL + "/content/recommend";
    public final static String INCR_VIEW_COUNT = SERVER_URL + "/content/viewed";
    public final static String INCR_SHARED_COUNT = SERVER_URL + "/content/shared";

    public final static String PAPER_CREATE_URL = SERVER_URL + "/paper/create";
    public final static String PAPER_UPDATE_URL = SERVER_URL + "/paper/update";
    public final static String PAPER_FIND_URL = SERVER_URL + "/paper/find";

    public final static String NULL_STR = "";
    public final static String IS_LOGGED_IN = "isLoggedIn";
    public final static int PASSWORD_MIN_LENGTH = 4;
    public final static int PASSWORD_MAX_LENGTH = 10;
    public static final String DEVELOPER_KEY = "AIzaSyBKBormJcLWMV0S6XznJH5F6J1V9gZJ0Jo";
    public static final String YOUTUBE_VIDEO_CODE = "_oEA18Y8gM0";
    public static int ASPECT_RATIO_WIDTH = 3;
    public static int ASPECT_RATIO_HEIGHT = 5;
    public static int IMAGE_WIDTH = 720;
    public static int IMAGE_HEIGHT = 405;
    public static boolean isCallDone = false;
}
