package org.fossasia.openevent.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by manan on 24-05-2015.
 */
public class BitmapLruCache
        extends LruCache<String, Bitmap>
        implements ImageLoader.ImageCache {

    public BitmapLruCache(Context context) {
        this(getDefaultLruCacheSize(context), context);

    }

    public BitmapLruCache(int sizeinKB, Context context) {
        super(sizeinKB);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    public static int getDefaultLruCacheSize(Context context) {
        final int maxMemory =
                (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }
}