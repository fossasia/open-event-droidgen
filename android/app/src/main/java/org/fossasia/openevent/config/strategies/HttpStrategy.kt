package org.fossasia.openevent.config.strategies

import android.content.Context
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.fossasia.openevent.BuildConfig
import org.fossasia.openevent.config.ConfigStrategy
import java.io.File

/**
 * Configures Http Cache and stetho plugin and sets picasso to use the configured client
 *
 * Also provides an interface for application to use Picasso with cache
 * To be used via [org.fossasia.openevent.config.StrategyRegistry]
 */
class HttpStrategy : ConfigStrategy {

    lateinit var picassoWithCache: Picasso
        private set

    override fun configure(context: Context): Boolean {
        //Initialize Cache
        val httpCacheDirectory = File(context.cacheDir, "picasso-cache")
        val cache = Cache(httpCacheDirectory, (15 * 1024 * 1024).toLong())

        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder().cache(cache)

        if (BuildConfig.DEBUG) {
            // Create an InitializerBuilder
            Stetho.initialize(
                    Stetho.newInitializerBuilder(context)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                            .enableWebKitInspector(RealmInspectorModulesProvider.builder(context).build())
                            .build())

            //Initialize Stetho Interceptor into OkHttp client
            val httpClient = OkHttpClient.Builder().addNetworkInterceptor(StethoInterceptor()).build()
            okHttpClientBuilder.addNetworkInterceptor(StethoInterceptor())

            //Initialize Picasso
            val picasso = Picasso.Builder(context).downloader(OkHttp3Downloader(httpClient)).build()
            Picasso.setSingletonInstance(picasso)
        }

        //Initialize Picasso with cache
        picassoWithCache = Picasso.Builder(context).downloader(OkHttp3Downloader(okHttpClientBuilder.build())).build()

        return false
    }

}
