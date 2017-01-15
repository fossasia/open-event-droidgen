package org.fossasia.openevent.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Manan Wason on 01/08/16.
 */
public class ReadLocalJson {
    public final static String readJsonAsset(final String name, Context context) {
        String json = null;

        try {
            InputStream inputStream = context.getAssets().open(name + ".json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");


        } catch (IOException e) {
            e.printStackTrace();


        }

        return json;
    }

}
