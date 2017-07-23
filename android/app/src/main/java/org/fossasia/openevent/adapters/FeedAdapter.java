package org.fossasia.openevent.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.facebook.CommentItem;
import org.fossasia.openevent.data.facebook.FeedItem;
import org.fossasia.openevent.utils.DateConverter;
import org.fossasia.openevent.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by rohanagarwal94 on 08/6/17.
 */
public class FeedAdapter extends BaseRVAdapter<FeedItem, FeedAdapter.RecyclerViewHolder> {

    private List<FeedItem> feedItems;
    private Context context;
    private AdapterCallback mAdapterCallback;
    private List<CommentItem> commentItems;

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.post_timestamp)
        TextView timeStamp;
        @BindView(R.id.txt_status_msg)
        TextView statusMsg;
        @BindView(R.id.txt_url)
        TextView url;
        @BindView(R.id.feed_image)
        ImageView feedImageView;
        @BindView(R.id.comment_button)
        Button getComments;

        RecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.bringToFront();

            getComments.setOnClickListener(v -> {
                FeedItem clickedFeedItem = feedItems.get(getPosition());
                commentItems = new ArrayList<>();
                if(clickedFeedItem.getComments() != null) {
                    commentItems.addAll(clickedFeedItem.getComments().getData());
                }
                if(commentItems.size()!=0)
                    mAdapterCallback.onMethodCallback(commentItems);
                else
                    Snackbar.make(v, context.getResources().getString(R.string.no_comments), Snackbar.LENGTH_SHORT).show();
            });
        }
    }

    public FeedAdapter(Context context, AdapterCallback adapterCallback, List<FeedItem> feedItems) {
        super(feedItems);
        this.feedItems = feedItems;
        this.context = context;
        try {
            this.mAdapterCallback = adapterCallback;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed, parent, false);

        return new RecyclerViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        final FeedItem feedItem = feedItems.get(position);

        String createdTime = feedItem.getCreatedTime();
        try {
            holder.timeStamp.setText(DateConverter.getRelativeTimeFromTimestamp(createdTime));
        } catch (ParseException e) {
            Timber.e(e);
        }

        if (!TextUtils.isEmpty(feedItem.getMessage())) {
            holder.statusMsg.setText(feedItem.getMessage());
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
            holder.url.setMovementMethod(LinkMovementMethod.getInstance());
            holder.url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            holder.url.setVisibility(View.GONE);
        }

        String feedImageUri = Utils.parseImageUri(feedItem.getFullPicture());
        Drawable placeholder = VectorDrawableCompat.create(context.getResources(),
                R.drawable.ic_placeholder_24dp, null);

        if(feedImageUri != null) {
            holder.feedImageView.setVisibility(View.VISIBLE);
            Picasso.with(holder.feedImageView.getContext())
                    .load(Uri.parse(feedImageUri))
                    .placeholder(placeholder)
                    .into(holder.feedImageView);
        } else {
            holder.feedImageView.setVisibility(View.GONE);
        }

    }

    public interface AdapterCallback {
        void onMethodCallback(List<CommentItem> commentItems);
    }
}
