package org.fossasia.openevent.utils;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Speaker;

import java.net.URLDecoder;

/**
 * Created by MananWason on 02-07-2015.
 */
public class SpeakerIntent {
    public String url = "dummy", reurl = "dummy", error = "none";
    Speaker speaker;

    public SpeakerIntent(Speaker speaker) {
        this.speaker = speaker;
    }

    public void clickedImage(final ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = "";
                if (imageView.getTag().toString().equals(view.getContext().getString(R.string.linkedin))) {
                    url = speaker.getLinkedin();
                    url = URLDecoder.decode(url);
                    String[] parts = url.split("&");
                    for (String s : parts) {
                        if (s.startsWith("url=")) {
                            url = s.substring(4);
                        }
                    }

                } else if (imageView.getTag().toString().equals(view.getContext().getString(R.string.twitter))) {
                    url = speaker.getTwitter();

                } else if (imageView.getTag().toString().equals(view.getContext().getString(R.string.fb))) {
                    url = speaker.getFacebook();

                } else if (imageView.getTag().toString().equals(view.getContext().getString(R.string.github))) {
                    url = speaker.getGithub();
                } else if (imageView.getTag().toString().equals(view.getContext().getString(R.string.website))) {
                    url = speaker.getWebsite();
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