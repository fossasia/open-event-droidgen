package org.fossasia.openevent.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Speaker;

/**
 * Created by MananWason on 02-07-2015.
 */
public class SpeakerIntent {
    Speaker speaker;
    //    Intent intent;
    Context context;

    public SpeakerIntent(Speaker speaker, Context context) {
        this.speaker = speaker;
        this.context = context;
    }

    public void mIntent(final ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url;
                if (imageView.getTag().toString().equals(context.getString(R.string.linkedin))) {
                    url = speaker.getLinkedin();

                } else if (imageView.getTag().toString().equals(context.getString(R.string.twitter))) {
                    url = speaker.getTwitter();

                } else if (imageView.getTag().toString().equals(context.getString(R.string.fb))) {
                    url = speaker.getFacebook();

                } else if (imageView.getTag().toString().equals(context.getString(R.string.github))) {
                    url = speaker.getGithub();
                } else {
                    url = "";
                    Toast.makeText(context, "What did you click?? :P", Toast.LENGTH_SHORT).show();
                }

                if (url.isEmpty()) {
                    Toast.makeText(context, "Sorry this speaker doesn't have a profile", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ((!url.startsWith("https://") && !url.startsWith("http://")) && (!url.isEmpty())) {
                    url = "http://" + url;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);


            }
        });
        return;
    }
}
