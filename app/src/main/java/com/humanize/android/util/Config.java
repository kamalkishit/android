package com.humanize.android.util;

/**
 * Created by Kamal on 6/29/15.
 */
public class Config {

    public final static String CONTENTS = "contents";

    // sharedPreferences data
    public final static String SHARED_PREFERENCES = "sharedPreferences";
    public final static String TOKEN = "token";
    public final static String FIRST_TIME = "firstTime";
    public final static String JSON_CONTENTS = "jsonContents";
    public final static String JSON_BOOKMARKED_CONTENTS = "jsonBookmarks";
    public final static String JSON_RECOMMENDED_CONTENTS = "jsonLikes";
    public final static String JSON_USER_DATA = "jsonUserData";
    public final static String JSON_PAPER = "jsonPaper";

    public final static String CONTENT_URL = "contentURL";

    public final static int READ_TIMEOUT = 8000;

    // URLS
    //public final static String SERVER_URL = "http://10.0.2.2:8080";
    public final static String SERVER_URL = "http://humannize.com:8080/humanize-1";
    //public final static String SERVER_URL = "http://188.166.254.151:8080/humanize-1";
    //public final static String SERVER_URL = "http://192.168.1.2:8080";

    public final static String USER_SIGNUP_URL = SERVER_URL + "/user/signup";
    public final static String USER_LOGIN_URL = SERVER_URL + "/user/login";
    public final static String USER_UPDATE_URL = SERVER_URL + "/user/update";
    public final static String USER_RESET_PASSWORD_URL = SERVER_URL + "/user/reset";
    public final static String USER_FORGOT_PASSWORD_URL = SERVER_URL + "/user/forgot";
    public final static String USER_INVITE_URL = SERVER_URL + "/user/invite";
    public final static String USER_DATA_URL = SERVER_URL + "/user/data";
    public final static String USER_CONTENT_BOOKMARK_URL = SERVER_URL + "/user/bookmark";
    public final static String USER_CONTENT_RECOMMEND_URL = SERVER_URL + "/user/recommend";

    public final static String CONTENT_CREATE_URL = SERVER_URL + "/content/create";
    public final static String CONTENT_UPDATE_URL = SERVER_URL + "/content/update";
    public final static String CONTENT_FIND_URL = SERVER_URL + "/content/find";
    public final static String BOOKMARK_FIND_URL = SERVER_URL + "/content/bookmarks";
    public final static String RECOMMENDATIONS_FIND_URL = SERVER_URL + "/content/recommendations";
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
    public final static int PASSWORD_MIN_LENGTH = 8;
    public final static int PASSWORD_MAX_LENGTH = 16;
    public static final String DEVELOPER_KEY = "AIzaSyBKBormJcLWMV0S6XznJH5F6J1V9gZJ0Jo";
    public static final String YOUTUBE_VIDEO_CODE = "_oEA18Y8gM0";
    public static int ASPECT_RATIO_WIDTH = 3;
    public static int ASPECT_RATIO_HEIGHT = 5;
    public static int IMAGE_WIDTH = 720;
    public static int IMAGE_HEIGHT = 405;
    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;
    public static int NAV_DRAWER_WIDTH = 0;
    public static boolean isCallDone = false;
}
