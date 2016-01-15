package com.humanize.android.util;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;

import com.humanize.android.common.StringConstants;;
import com.squareup.picasso.Downloader;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by kamal on 1/4/16.
 */
public class PicassoDownloader implements Downloader{

    private InputStream inputStream;

    @Override
    public Response load(Uri uri, int networkPolicy) throws IOException {
        Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        OkHttpClient client = new OkHttpClient();

        /*com.squareup.okhtt.Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        inputStream = response.body().byteStream();*/

        return new Response(inputStream, false, -1);
    }

    @Override
    public void shutdown() {

    }
}
