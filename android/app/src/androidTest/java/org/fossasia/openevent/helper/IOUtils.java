package org.fossasia.openevent.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

/**
 * User: mohit
 * Date: 25/1/16
 */
public final class IOUtils {
    private IOUtils() {
    }

    public static String readRaw(@RawRes int rawResource, @NonNull Context context) {

        InputStream inputStream = context.getResources().openRawResource(rawResource);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            Timber.e("Parsing Error Occurred at IOUtils::readRaw.");
        }
        return byteArrayOutputStream.toString();
    }
}
