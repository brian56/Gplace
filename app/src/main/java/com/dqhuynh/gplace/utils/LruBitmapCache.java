package com.dqhuynh.gplace.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;

import com.android.volley.toolbox.ImageLoader.ImageCache;
/**
 * Created by Administrator on 7/15/2015.
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements
        ImageCache {
//    public static int getDefaultLruCacheSize() {
//        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//        final int cacheSize = maxMemory / 8;
//
//        return cacheSize;
//    }
//
//    public LruBitmapCache() {
//        this(getDefaultLruCacheSize());
//    }
//
//    public LruBitmapCache(int sizeInKiloBytes) {
//        super(sizeInKiloBytes);
//    }
//
//    @Override
//    protected int sizeOf(String key, Bitmap value) {
//        return value.getRowBytes() * value.getHeight() / 1024;
//    }
//
//    @Override
//    public Bitmap getBitmap(String url) {
//        return get(url);
//    }
//
//    @Override
//    public void putBitmap(String url, Bitmap bitmap) {
//        put(url, bitmap);
//    }

    public LruBitmapCache(int maxSize) {
        super(maxSize);
    }

    public LruBitmapCache(Context ctx) {
        this(getCacheSize(ctx));
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    // Returns a cache size equal to approximately three screens worth of images.
    public static int getCacheSize(Context ctx) {
        final DisplayMetrics displayMetrics = ctx.getResources().
                getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;
        // 4 bytes per pixel
        final int screenBytes = screenWidth * screenHeight * 4;

        return screenBytes * 3;
    }

}
