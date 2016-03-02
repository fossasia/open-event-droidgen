package org.fossasia.openevent.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by user on 3/1/2016.
 */
public class SaveFilters {

    public boolean saveArray(Boolean[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("Filters", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putBoolean(arrayName + "_" + i, array[i]);

        return editor.commit();
    }

    public Boolean[] loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("Filters", 0);
        Boolean array[] = new Boolean[100];
        for(int i = 0; i < array.length; i++) {
            array[i] = false;
        }
        if(prefs.contains(arrayName +"_size")) {
            int size = prefs.getInt(arrayName + "_size", 0);
        //if(size > 0) {
            for (int i = 0; i < size; i++)
                array[i] = prefs.getBoolean(arrayName + "_" + i, false);
        }
        else {
            //Log.d("load", "no file");

        }

        return array;
    }
}
