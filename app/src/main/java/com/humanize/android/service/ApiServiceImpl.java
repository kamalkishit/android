package com.humanize.android.service;

import com.humanize.android.config.ApiUrls;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.Article;
import com.humanize.android.data.ContactUs;
import com.humanize.android.data.ContentSearchParams;
import com.humanize.android.data.ContentUpdateParams;
import com.humanize.android.data.InviteFriend;
import com.humanize.android.data.LoginUser;
import com.humanize.android.data.PaperParams;
import com.humanize.android.data.ResetPasswordUser;
import com.humanize.android.data.SignupUser;
import com.humanize.android.data.SubmitArticle;
import com.humanize.android.data.UserDevice;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by kamal on 1/14/16.
 */
public class ApiServiceImpl implements ApiService {

    private static MediaType JSON = MediaType.parse(StringConstants.MEDIA_TYPE_JSON);

    private static final LogService logService = new LogServiceImpl();
    private static final String TAG = ApiServiceImpl.class.getName();

    public void inviteFriend(InviteFriend inviteFriend, Callback callback) {
        try {
            post(ApiUrls.URL_INVITE_FRIEND, toJson(inviteFriend), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void submitArticle(SubmitArticle submitArticle, Callback callback) {
        try {
            post(ApiUrls.URL_SUGGEST_ARTICLE, toJson(submitArticle), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void contactUs(ContactUs contactUs, Callback callback) {
        try {
            post(ApiUrls.URL_CONTACT_US, toJson(contactUs), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void getContents(ContentSearchParams contentSearchParams, Callback callback) {
        try {
            post(ApiUrls.URL_CONTENT, toJson(contentSearchParams), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void getContent(String urlId, Callback callback) {
        try {
            get(ApiUrls.URL_SINGLE_CONTENT + urlId, callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void getTrends(Callback callback) {
        try {
            get(ApiUrls.URL_TRENDS, callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void getPaper(PaperParams paperParams, Callback callback) {
        try {
            post(ApiUrls.URL_PAPER, toJson(paperParams), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void updateContent(ContentUpdateParams contentUpdateParams, Callback callback) {
        try {
            post(ApiUrls.URL_UPDATE_CONTENT, toJson(contentUpdateParams), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void registerDevice(UserDevice userDevice, Callback callback) {
        try {
            post(ApiUrls.URL_GCM_REGISTER, toJson(userDevice), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void createArticle(Article article, Callback callback) {
        try {
            post(ApiUrls.URL_CREATE_ARTICLE, toJson(article), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }


    public void signup(SignupUser signupUser, Callback callback) {
        try {
            post(ApiUrls.URL_SIGNUP, toJson(signupUser), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void login(LoginUser loginUser, Callback callback) {
        try {
            post(ApiUrls.URL_LOGIN, toJson(loginUser), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void forgotPassword(String emailId, Callback callback) {
        try {
            post(ApiUrls.URL_LOGIN, toJson(emailId), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void resetPassword(ResetPasswordUser resetPasswordUser, Callback callback) {
        try {
            post(ApiUrls.URL_RESET_PASSWORD, toJson(resetPasswordUser), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    public void getUserData(String token, Callback callback) {
        try {
            post(ApiUrls.URL_LOGIN, toJson(token), callback);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    private String toJson(Object object) throws Exception {
        return new GsonParserServiceImpl().toJson(object);
    }

    private void get(String url, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    private void post(String url, String json, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
}
