package org.fossasia.openevent.cache;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

import static com.facebook.FacebookSdk.getCacheDir;

public class PicassoCache {

    /**
     * Picasso Instance
     */
    private Picasso picassoInstance = null;

    /**
     * PicassoCache Constructor
     *
     * @param context application Context
     */
    public PicassoCache(Context context) {

        //Initialize Cache
        File httpCacheDirectory = new File(getCacheDir(), "picasso-cache");
        Cache cache = new Cache(httpCacheDirectory, 15 * 1024 * 1024);

        //Initialize Stetho Interceptor into OkHttp client
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).cache(cache);

        //Initialize Picasso
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(okHttpClientBuilder.build()));

        picassoInstance = builder.build();
    }

    /**
     * Get Picasso Instance
     *
     * @param context application Context
     * @return Picasso instance
     */
    public Picasso getPicassoInstance(Context context) {

        if (picassoInstance == null) {
            synchronized (Picasso.class) {
                if (picassoInstance == null) {
                    new PicassoCache(context);
                }
            }
        }
        return picassoInstance;
    }

}
