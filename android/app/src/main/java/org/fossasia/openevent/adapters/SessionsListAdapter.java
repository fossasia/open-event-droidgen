package org.fossasia.openevent.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.DateUtils;
import org.fossasia.openevent.utils.NotificationUtil;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.utils.WidgetUpdater;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.realm.Realm;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 26-06-2015
 */
public class SessionsListAdapter extends BaseRVAdapter<Session, SessionsListAdapter.SessionViewHolder> {

    private Context context;
    private int trackId;
    public static int listPosition;
    private int type;
    private static final int locationWiseSessionList = 1;
    private static final int trackWiseSessionList = 4;
    private static final int speakerWiseSessionList = 2;

    private TextDrawable.IBuilder drawableBuilder = TextDrawable.builder().round();

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();

    private int color;

    @SuppressWarnings("all")
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            Realm realm = Realm.getDefaultInstance();

            List<Session> filteredSessions = realm.copyFromRealm(RealmDataRepository.getInstance(realm)
            .getSessionsFiltered(trackId, constraint.toString()));

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredSessions;
            filterResults.count = filteredSessions.size();
            Timber.d("Filtering done total results %d", filterResults.count);

            realm.close();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results == null || results.values == null) {
                Timber.e("No results published. There is an error in query. Check " + getClass().getName() + " filter!");

                return;
            }

            animateTo((List<Session>) results.values);
        }
    };

    public SessionsListAdapter(Context context, List<Session> sessions, int type) {
        super(sessions);
        this.context = context;
        this.color = ContextCompat.getColor(context, R.color.color_primary);
        this.type = type;
    }

    public void setColor(int color) {
        this.color = color;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tracksactvity_item, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SessionViewHolder holder, final int position) {
        Session session = getItem(position);
        //removing draft sessions
        if((!Utils.isEmpty(session.getState())) && session.getState().equals("draft")) {
            getDataList().remove(position);
            notifyItemRemoved(position);
        }

        String sessionTitle = Utils.checkStringEmpty(session.getTitle());
        String sessionSubTitle = Utils.checkStringEmpty(session.getSubtitle());

        holder.sessionTitle.setText(sessionTitle);

        if(Utils.isEmpty(sessionSubTitle)) {
            holder.sessionSubtitle.setVisibility(View.GONE);
        } else {
            holder.sessionSubtitle.setVisibility(View.VISIBLE);
            holder.sessionSubtitle.setText(sessionSubTitle);
        }

        Track track = session.getTrack();

        if (!RealmDataRepository.isNull(track)) {
            int storedColor = Color.parseColor(track.getColor());

            if(type != trackWiseSessionList) {
                color = storedColor;
            }

            TextDrawable drawable = drawableBuilder.build(String.valueOf(track.getName().charAt(0)), storedColor);
            holder.trackImageIcon.setImageDrawable(drawable);
            holder.trackImageIcon.setBackgroundColor(Color.TRANSPARENT);
            holder.sessionTrack.setText(track.getName());

            holder.itemView.setOnClickListener(v -> {
                final String sessionName = session.getTitle();

                String trackName = track.getName();
                Intent intent = new Intent(context, SessionDetailActivity.class);
                intent.putExtra(ConstantStrings.SESSION, sessionName);
                intent.putExtra(ConstantStrings.TRACK, trackName);
                intent.putExtra(ConstantStrings.ID, session.getId());
                intent.putExtra(ConstantStrings.TRACK_ID, track.getId());
                listPosition = holder.getLayoutPosition();
                context.startActivity(intent);
            });
        } else {
            holder.trackImageIcon.setVisibility(View.GONE);
            holder.sessionTrack.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(v -> {
                final String sessionName = session.getTitle();

                Intent intent = new Intent(context, SessionDetailActivity.class);
                intent.putExtra(ConstantStrings.SESSION, sessionName);
                intent.putExtra(ConstantStrings.ID, session.getId());
                listPosition = holder.getLayoutPosition();
                context.startActivity(intent);
            });

            Timber.d("This session has a null or incomplete track somehow : " + session.getTitle() + " " + track);
        }

        String date = DateUtils.formatDateWithDefault(DateUtils.FORMAT_DATE_COMPLETE, session.getStartsAt());
        holder.sessionDate.setText(date);
        holder.sessionTime.setText(String.format("%s - %s",
                DateUtils.formatDateWithDefault(DateUtils.FORMAT_12H, session.getStartsAt()),
                DateUtils.formatDateWithDefault(DateUtils.FORMAT_12H, session.getEndsAt())));
        if(session.getMicrolocation() != null) {
            String locationName = Utils.checkStringEmpty(session.getMicrolocation().getName());
            holder.sessionLocation.setText(locationName);
        }

        Observable.just(session.getSpeakers())
                .map(speakers -> {
                    ArrayList<String> speakerName = new ArrayList<>();

                    for(Speaker speaker: speakers){
                        String name = Utils.checkStringEmpty(speaker.getName());
                        speakerName.add(name);
                    }

                    if (speakers.isEmpty()) {
                        holder.sessionSpeaker.setVisibility(View.GONE);
                        holder.speakerIcon.setVisibility(View.GONE);
                    }

                    return TextUtils.join(", ", speakerName);
                }).subscribe(speakerList -> holder.sessionSpeaker.setText(speakerList));

        switch (type) {
            case trackWiseSessionList:
                holder.trackImageIcon.setVisibility(View.GONE);
                holder.sessionTrack.setVisibility(View.GONE);
                break;
            case locationWiseSessionList:
                holder.sessionLocation.setVisibility(View.GONE);
                holder.locationIcon.setVisibility(View.GONE);
                break;
            case speakerWiseSessionList:
                holder.sessionSpeaker.setVisibility(View.GONE);
                holder.speakerIcon.setVisibility(View.GONE);
                break;
            default:
        }

        if(session.getIsBookmarked()) {
            holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_white_24dp);
        } else {
            holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
        }
        DrawableCompat.setTint(holder.sessionBookmarkIcon.getDrawable(), Color.parseColor(session.getTrack().getFontColor()));

        final int sessionId = session.getId();

        holder.sessionBookmarkIcon.setOnClickListener(v -> {
            if(session.getIsBookmarked()) {

                realmRepo.setBookmark(sessionId, false).subscribe();
                holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_border_white_24dp);

                if ("MainActivity".equals(context.getClass().getSimpleName())) {
                    Snackbar.make(holder.sessionCard, R.string.removed_bookmark, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, view -> {

                                realmRepo.setBookmark(sessionId, true).subscribe();
                                holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_white_24dp);
                                WidgetUpdater.updateWidget(context);
                            }).show();
                } else {
                    Snackbar.make(holder.sessionCard, R.string.removed_bookmark, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                NotificationUtil.createNotification(session, context).subscribe(
                        () -> Snackbar.make(holder.sessionCard,
                                R.string.added_bookmark,
                                Snackbar.LENGTH_SHORT)
                                .show(),
                        throwable -> Snackbar.make(holder.sessionCard,
                                R.string.error_create_notification,
                                Snackbar.LENGTH_LONG).show());

                realmRepo.setBookmark(sessionId, true).subscribe();
                holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_white_24dp);

                Snackbar.make(holder.sessionCard, R.string.added_bookmark, Snackbar.LENGTH_SHORT).show();
            }
            WidgetUpdater.updateWidget(context);
            DrawableCompat.setTint(holder.sessionBookmarkIcon.getDrawable(), Color.parseColor(session.getTrack().getFontColor()));
        });

        // Set color generated by palette on views
        holder.sessionHeader.setBackgroundColor(color);
        holder.sessionTitle.setTextColor(Color.parseColor(track.getFontColor()));
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    class SessionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.session_title)
        TextView sessionTitle;

        @BindView(R.id.session_subtitle)
        TextView sessionSubtitle;

        @BindView(R.id.trackImageDrawable)
        ImageView trackImageIcon;

        @BindView(R.id.session_track)
        TextView sessionTrack;

        @BindView(R.id.session_date)
        TextView sessionDate;

        @BindView(R.id.session_speaker)
        TextView sessionSpeaker;

        @BindView(R.id.icon_speaker)
        ImageView speakerIcon;

        @BindView(R.id.icon_location)
        ImageView locationIcon;

        @BindView(R.id.session_time)
        TextView sessionTime;

        @BindView(R.id.session_location)
        TextView sessionLocation;

        @BindView(R.id.session_bookmark_status)
        ImageView sessionBookmarkIcon;

        @BindView(R.id.session_details)
        LinearLayout sessionDetailsHolder;

        @BindView(R.id.session_card)
        CardView sessionCard;

        @BindView(R.id.titleLinearLayout)
        LinearLayout sessionHeader;

        SessionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
