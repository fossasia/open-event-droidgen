package org.fossasia.openevent.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.fossasia.openevent.R;

/**
 * Created by MananWason on 14-06-2015.
 */
public class TracksActivity extends AppCompatActivity {
    private String track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
        track = getIntent().getStringExtra("TRACK");
        TextView textView = (TextView) findViewById(R.id.track_title);
        textView.setText(track);
    }
}
