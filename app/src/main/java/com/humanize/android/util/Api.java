package com.humanize.android.util;

import com.humanize.android.data.ContactUs;
import com.humanize.android.data.ContentSearchParams;
import com.humanize.android.data.InviteFriend;
import com.humanize.android.data.SuggestArticle;

import java.util.List;

import okhttp3.Callback;

/**
 * Created by kamal on 1/14/16.
 */
public interface Api {

    void inviteFriend(InviteFriend inviteFriend, Callback callback);
    void suggestArticle(SuggestArticle suggestArticle, Callback callback);
    void contactUs(ContactUs contactUs, Callback callback);
    void getContents(ContentSearchParams contentSearchParams, Callback callback);
    void refreshContents(ContentSearchParams contentSearchParams, Callback callback);
    void getMoreContents(ContentSearchParams contentSearchParams, Callback callback);
}
