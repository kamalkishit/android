package com.humanize.android.service;

import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.humanize.android.config.Constants;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.Content;
import com.humanize.android.data.ContentUpdateOperations;
import com.humanize.android.data.ContentUpdateParams;
import com.humanize.android.fragment.ContactUsSuccessFragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by kamal on 1/1/16.
 */
public class ContentServiceImpl implements ContentService {

    private ApiService apiService;

    private static final LogService logService = new LogServiceImpl();
    private static final String TAG = ContentServiceImpl.class.getSimpleName();

    public ContentServiceImpl() {
        apiService = new ApiServiceImpl();
    }

    public void incrSharedCount(Content content) {
        content.setViewedCount(content.getViewedCount() + 1);
        content.setSharedCount(content.getSharedCount() + 1);
        incrSharedCount(content.getId());
    }

    public void incrViewedCount(Content content) {
        content.setViewedCount(content.getViewedCount() + 1);
        incrViewedCount(content.getId());
    }

    private void incrViewedCount(String contentId) {
        apiService.updateContent(createContentUpdateParams(contentId, ContentUpdateOperations.VIEW), new ContentUpdateCallback());
    }

    private void incrSharedCount(String contentId) {
        apiService.updateContent(createContentUpdateParams(contentId, ContentUpdateOperations.SHARE), new ContentUpdateCallback());
    }

    private ContentUpdateParams createContentUpdateParams(String contentId, ContentUpdateOperations contentUpdateOperations) {
        ContentUpdateParams contentUpdateParams = new ContentUpdateParams();
        contentUpdateParams.setContentId(contentId);
        contentUpdateParams.setContentUpdateOperations(contentUpdateOperations);

        return contentUpdateParams;
    }

    private class ContentUpdateCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            logService.e(TAG, exception.getMessage());
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (!response.isSuccessful()) {

            } else {

            }
        }
    }
}