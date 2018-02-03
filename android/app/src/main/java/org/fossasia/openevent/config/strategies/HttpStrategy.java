package org.fossasia.openevent.config.strategies;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import org.fossasia.openevent.BuildConfig;
import org.fossasia.openevent.config.ConfigStrategy;

import java.io.File;

import lombok.Getter;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Configures Http Cache and stetho plugin and sets picasso to use the configured client
 *
 * Also provides an interface for application to use Picasso with cache
 * To be used via {@link org.fossasia.openevent.config.StrategyRegistry}
 */
@Getter
public class HttpStrategy implements ConfigStrategy {

    private Picasso picassoWithCache;

    @Override
    public boolean configure(Context context) {
        //Initialize Cache
        File httpCacheDirectory = new File(context.getCacheDir(), "picasso-cache");
        Cache cache = new Cache(httpCacheDirectory, 15 * 1024 * 1024);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().cache(cache);

        if (BuildConfig.DEBUG) {
            // Create an InitializerBuilder
            Stetho.initialize(
                    Stetho.newInitializerBuilder(context)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                            .enableWebKitInspector(RealmInspectorModulesProvider.builder(context).build())
                            .build());

            //Initialize Stetho Interceptor into OkHttp client
            OkHttpClient httpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();
            okHttpClientBuilder = okHttpClientBuilder.addNetworkInterceptor(new StethoInterceptor());

            //Initialize Picasso
            Picasso picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(httpClient)).build();
            Picasso.setSingletonInstance(picasso);
        }

        //Initialize Picasso with cache
        picassoWithCache = new Picasso.Builder(context).downloader(new OkHttp3Downloader(okHttpClientBuilder.build())).build();

        return false;
    }

}
