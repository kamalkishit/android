package com.humanize.android.service;

import com.humanize.android.data.ContactUs;
import com.humanize.android.data.ContentParams;
import com.humanize.android.data.ContentSearchParams;
import com.humanize.android.data.InviteFriend;
import com.humanize.android.data.PaperParams;
import com.humanize.android.data.SubmitArticle;

import okhttp3.Callback;

/**
 * Created by kamal on 1/14/16.
 */
public interface ApiService {

    void inviteFriend(InviteFriend inviteFriend, Callback callback);
    void suggestArticle(SubmitArticle submitArticle, Callback callback);
    void contactUs(ContactUs contactUs, Callback callback);
    void getContents(ContentSearchParams contentSearchParams, Callback callback);
    void getContent(String urlId, Callback callback);
    void getPaper(PaperParams paperParams, Callback callback);
}
