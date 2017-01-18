package org.fossasia.openevent.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.fossasia.openevent.OpenEventApp;

/**
 * Created by MananWason on 10-06-2015.
 */
public class SplashActivity extends Activity {

    private Runnable runnable;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int SPLASH_DISPLAY_LENGTH = 1000;
        OpenEventApp.getEventBus().register(this);

        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}

