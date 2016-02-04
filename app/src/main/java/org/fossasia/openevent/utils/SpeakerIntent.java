package org.fossasia.openevent.utils;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Speaker;

/**
 * Created by MananWason on 02-07-2015.
 */
public class SpeakerIntent {
    Speaker speaker;

    public SpeakerIntent(Speaker speaker) {
        this.speaker = speaker;
    }

    public void clickedImage(final ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "";
                if (imageView.getTag().toString().equals(view.getContext().getString(R.string.linkedin))) {
                    url = speaker.getLinkedin();

                } else if (imageView.getTag().toString().equals(view.getContext().getString(R.string.twitter))) {
                    url = speaker.getTwitter();

                } else if (imageView.getTag().toString().equals(view.getContext().getString(R.string.fb))) {
                    url = speaker.getFacebook();

                } else if (imageView.getTag().toString().equals(view.getContext().getString(R.string.github))) {
                    url = speaker.getGithub();
                }

                if ((!url.startsWith("https://") && !url.startsWith("http://"))) {
                    url = "http://" + url;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);


            }
        });
    }
}
