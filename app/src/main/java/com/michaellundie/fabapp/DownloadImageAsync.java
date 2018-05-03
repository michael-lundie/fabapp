package com.michaellundie.fabapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Image Async Downloader code primarily from:
// https://android.jlelse.eu/async-loading-images-on-android-like-a-big-baws-fd97d1a91374

public class DownloadImageAsync extends AsyncTask<String, Void, Bitmap> {

    private Listener listener;

    public DownloadImageAsync(final Listener listener) {
        this.listener = listener;
    }

    public static interface Listener {
        void onImageDownloaded(final Bitmap bitmap);
        void onImageDownloadError();
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        final String url = urls[0];
        Bitmap bitmap = null;

        try {
            final InputStream inputStream = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (final MalformedURLException malformedUrlException) {
            // Handle error
        } catch (final IOException ioException) {
            // Handle error
        }
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap downloadedBitmap) {
        if (null != downloadedBitmap) {
            listener.onImageDownloaded(downloadedBitmap);
        } else {
            listener.onImageDownloadError();
        }
    }

}
