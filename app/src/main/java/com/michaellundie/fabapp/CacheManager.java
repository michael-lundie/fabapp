package com.michaellundie.fabapp;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

public class CacheManager {

    public static final String LOG_TAG = CacheManager.class.getSimpleName();

    private LruCache<Integer, RecyclingBitmapDrawable> mMemoryCache;

    private static CacheManager instance;

    public static CacheManager getInstance() {
        if(instance == null) {
            instance = new CacheManager();
            instance.init();
        }

        return instance;
    }

    private void init() {

        // We are declaring a cache of 6Mb for our use.
        // You need to calculate this on the basis of your need
        mMemoryCache = new LruCache<Integer, RecyclingBitmapDrawable>(6 * 1024 * 1024) {
            @Override
            protected int sizeOf(Integer key, RecyclingBitmapDrawable bitmapDrawable) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    return bitmapDrawable.getBitmap().getByteCount() ;
                } else {
                    return bitmapDrawable.getBitmap().getRowBytes() * bitmapDrawable.getBitmap().getHeight();
                }
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, RecyclingBitmapDrawable oldValue, RecyclingBitmapDrawable newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                oldValue.setIsCached(false);
            }
        };

    }

    public void addBitmapToMemoryCache(Integer key, RecyclingBitmapDrawable bitmapDrawable) {
        if (getBitmapFromMemCache(key) == null) {
            // The removed entry is a recycling drawable, so notify it
            // that it has been added into the memory cache
            bitmapDrawable.setIsCached(true);
            mMemoryCache.put(key, bitmapDrawable);
        }
    }

    public RecyclingBitmapDrawable getBitmapFromMemCache(Integer key) {
                if(key ==null) {
                    Log.i(LOG_TAG, "TEST: Key is null, null will be returned.");
                }
                Log.i(LOG_TAG, "TEST: getBitmapFromMemCache called.");
                return mMemoryCache.get(key);

        }

    public void clear() {
        mMemoryCache.evictAll();
    }
}
