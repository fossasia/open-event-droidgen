package org.fossasia.openevent.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Window;

import org.fossasia.openevent.R;
import org.fossasia.openevent.utils.Progress;

/**
 * Created by MananWason on 10-06-2015.
 */
public class SplashActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        Progress progress = new Progress();
        Log.d("SPLASH", Progress.progress + "");


        try {
            wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
