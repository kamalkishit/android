package com.humanize.android.service;

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

/**
 * Created by kamal on 1/14/16.
 */
public interface ApiService {

    void inviteFriend(InviteFriend inviteFriend, Callback callback);
    void submitArticle(SubmitArticle submitArticle, Callback callback);
    void contactUs(ContactUs contactUs, Callback callback);
    void getContents(ContentSearchParams contentSearchParams, Callback callback);
    void getContent(String urlId, Callback callback);
    void getTrends(Callback callback);
    void getPaper(PaperParams paperParams, Callback callback);
    void updateContent(ContentUpdateParams contentUpdateParams, Callback callback);
    void registerDevice(UserDevice userDevice, Callback callback);
    void createArticle(Article article, Callback callback);

    void signup(SignupUser signupUser, Callback callback);
    void login(LoginUser loginUser, Callback callback);
    void forgotPassword(String emailId, Callback callback);
    void resetPassword(ResetPasswordUser resetPasswordUser, Callback callback);
    void getUserData(String token, Callback callback);
}
