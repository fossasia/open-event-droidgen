package org.fossasia.openevent.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by manan on 24-05-2015.
 */
public class VolleyHelper {
    private static VolleyHelper INSTANCE;
    private static RequestQueue requestQueue;
    private static Context context;
    private static ImageLoader imageLoader;

    private VolleyHelper(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleyHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new VolleyHelper(context);
        }
        return INSTANCE;
    }


    public static ImageLoader getImageLoader(Context context) {

        if (imageLoader == null) {
            imageLoader = new ImageLoader(VolleyHelper.getRequestQueue(), new BitmapLruCache(context));
        }
        return imageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

}
