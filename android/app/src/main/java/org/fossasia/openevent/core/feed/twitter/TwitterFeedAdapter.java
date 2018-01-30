package org.fossasia.openevent.core.feed.twitter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.core.feed.twitter.api.TwitterFeedItem;
import org.fossasia.openevent.common.ui.image.OnImageZoomListener;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.common.utils.Utils;
import org.threeten.bp.format.DateTimeParseException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class TwitterFeedAdapter extends BaseRVAdapter<TwitterFeedItem, TwitterFeedAdapter.RecyclerViewHolder> {

    private List<TwitterFeedItem> twitterFeedItems;
    private Context context;
    private OnImageZoomListener onImageZoomListener;

    class RecyclerViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_base)
        View baseView;
        TextView timeStamp;
        TextView statusMsg;
        TextView url;
        ImageView feedImageView;

        RecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            bindViews();
            view.bringToFront();

            if (onImageZoomListener != null) {
                feedImageView.setOnClickListener(v -> {
                    if (twitterFeedItems.get(getPosition()).getImages().get(0) != null) {
                        zoomImage(Utils.parseImageUri(twitterFeedItems.get(getPosition()).getImages().get(0)));
                    }
                });
            }
        }

        private void bindViews() {
            timeStamp = ButterKnife.findById(baseView, R.id.post_timestamp);
            statusMsg = ButterKnife.findById(baseView, R.id.txt_status_msg);
            url = ButterKnife.findById(baseView, R.id.txt_url);
            feedImageView = ButterKnife.findById(baseView, R.id.feed_image);
        }
    }

    public TwitterFeedAdapter(Context context, List<TwitterFeedItem> twitterFeedItems) {
        super(twitterFeedItems);
        this.context = context;
        this.twitterFeedItems = twitterFeedItems;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_twitter_feed, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        final TwitterFeedItem feedItem = twitterFeedItems.get(position);

        String createdTime = feedItem.getCreatedAt();
        try {
            holder.timeStamp.setText(DateConverter.getRelativeTimeFromUTCTimeStamp(createdTime));
        } catch (DateTimeParseException e) {
            Timber.e(e);
        }

        if (!TextUtils.isEmpty(feedItem.getText())) {
            holder.statusMsg.setText(feedItem.getText());
            holder.statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (feedItem.getLink() != null) {
            holder.url.setText(Html.fromHtml("<a href=\"" + feedItem.getLink() + "\">"
                    + feedItem.getLink() + "</a> "));

            // Making url clickable
            holder.url.setOnClickListener(view -> Utils.setUpCustomTab(context, feedItem.getLink()));
            holder.url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            holder.url.setVisibility(View.GONE);
        }

        if (feedItem.getImages().size() > 0) {
            //In case of more than one image show the first.
            String feedImageUri = Utils.parseImageUri(feedItem.getImages().get(0));
            Drawable placeholder = VectorDrawableCompat.create(context.getResources(),
                    R.drawable.ic_placeholder_24dp, null);

            holder.feedImageView.setVisibility(View.VISIBLE);
            Picasso.with(holder.feedImageView.getContext())
                    .load(Uri.parse(feedImageUri))
                    .placeholder(placeholder)
                    .into(holder.feedImageView);
        } else {
            holder.feedImageView.setVisibility(View.GONE);
        }
    }

    public void setOnImageZoomListener(OnImageZoomListener onImageZoomListener) {
        this.onImageZoomListener = onImageZoomListener;
    }

    public void removeOnImageZoomListener() {
        onImageZoomListener = null;
    }

    private void zoomImage(String imageUri) {
        if (onImageZoomListener != null) {
            onImageZoomListener.onZoom(imageUri);
        }
    }
}
