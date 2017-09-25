package org.fossasia.openevent.fragments;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SearchActivity;
import org.fossasia.openevent.adapters.GlobalSearchAdapter;
import org.fossasia.openevent.adapters.SocialLinksListAdapter;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.extras.EventDates;
import org.fossasia.openevent.data.extras.SocialLink;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.BookmarkChangedEvent;
import org.fossasia.openevent.events.EventLoadedEvent;
import org.fossasia.openevent.utils.DateConverter;
import org.fossasia.openevent.utils.Views;
import org.threeten.bp.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by harshita30 on 9/3/17.
 */

public class AboutFragment extends BaseFragment {

    @BindView(R.id.welcomeMessage)
    protected TextView welcomeMessage;
    @BindView(R.id.event_description)
    protected TextView eventDescription;
    @BindView(R.id.organiser_description)
    protected TextView organiserDescription;
    @BindView(R.id.event_timing_details)
    protected TextView eventTiming;
    @BindView(R.id.item_description_img)
    protected ImageView descriptionImg;
    @BindView(R.id.readmore)
    protected TextView readMore;
    @BindView(R.id.readless)
    protected TextView readLess;
    @BindView(R.id.list_social_links)
    protected RecyclerView socialLinksRecyclerView;
    @BindView(R.id.event_venue_details)
    protected TextView venueDetails;
    @BindView(R.id.list_bookmarks)
    protected RecyclerView bookmarksRecyclerView;
    @BindView(R.id.bookmark_header)
    protected TextView bookmarkHeader;
    @BindView(R.id.event_details_header)
    protected TextView eventDetailsHeader;

    private ArrayList<String> dateList = new ArrayList<>();

    private GlobalSearchAdapter bookMarksListAdapter;
    private SocialLinksListAdapter socialLinksListAdapter;
    private RealmResults<Session> bookmarksResult;
    private List<Object> sessions = new ArrayList<>();
    private List<SocialLink> socialLinks = new ArrayList<>();

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setUpBookmarksRecyclerView();
        setUpSocialLinksRecyclerView();

        return view;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_about;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        event = realmRepo.getEvent();
        event.addChangeListener(realmModel -> loadEvent(event));
    }

    @Subscribe
    public void onEventLoaded(EventLoadedEvent eventLoadedEvent) {
        loadEvent(eventLoadedEvent.getEvent());
    }

    private void setUpBookmarksRecyclerView() {
        bookmarksRecyclerView.setVisibility(View.VISIBLE);
        bookMarksListAdapter = new GlobalSearchAdapter(sessions, getContext());
        bookmarksRecyclerView.setAdapter(bookMarksListAdapter);
        bookmarksRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookmarksRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setUpSocialLinksRecyclerView() {
        socialLinksListAdapter = new SocialLinksListAdapter(socialLinks);
        socialLinksRecyclerView.setAdapter(socialLinksListAdapter);
        socialLinksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        socialLinksRecyclerView.setNestedScrollingEnabled(false);
    }

    private void loadEvent(Event event) {
        if (event == null || !event.isValid())
            return;

        String date = String.format("%s\n%s",
                DateConverter.formatDateWithDefault(DateConverter.FORMAT_DATE_COMPLETE, event.getStartsAt()),
                DateConverter.formatDateWithDefault(DateConverter.FORMAT_DATE_COMPLETE, event.getEndsAt()));

        welcomeMessage.setText(String.format(getResources().getString(R.string.welcome_message), event.getName()));
        Views.setHtml(organiserDescription, event.getOrganizerDescription(), true);
        Views.setHtml(eventDescription, event.getDescription(), true);
        venueDetails.setText(event.getLocationName());
        eventTiming.setText(date);
        descriptionImg.setOnClickListener(v -> collapseExpandTextView());
        // Listener to trigger when the TextView is ready to be drawn
        organiserDescription.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (organiserDescription.getLineCount() > 4)
                    readMore.setVisibility(View.VISIBLE);
                // Removing Listener after it has invoked once
                organiserDescription.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        readMore.setOnClickListener(v -> {
            organiserDescription.setMaxLines(Integer.MAX_VALUE);
            readMore.setVisibility(View.GONE);
            readLess.setVisibility(View.VISIBLE);
        });
        readLess.setOnClickListener(v -> {
            organiserDescription.setMaxLines(4);
            readLess.setVisibility(View.GONE);
            readMore.setVisibility(View.VISIBLE);
        });

        socialLinks.clear();
        socialLinks.addAll(event.getSocialLinks());
        socialLinksListAdapter.notifyDataSetChanged();
    }

    @TargetApi(16)
    void collapseExpandTextView() {
        if (eventDescription.getVisibility() == View.GONE) {
            // it's collapsed - expand it
            eventDescription.setVisibility(View.VISIBLE);
            descriptionImg.setImageResource(R.drawable.ic_expand_less_black_24dp);
        } else {
            // it's expanded - collapse it
            eventDescription.setVisibility(View.GONE);
            descriptionImg.setImageResource(R.drawable.ic_expand_more_black_24dp);
        }

        ObjectAnimator animation = ObjectAnimator.ofInt(eventDescription, "maxLines", eventDescription.getMaxLines());
        animation.setDuration(200).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Start Search activity if search icon is clicked
        if (item.getItemId() == R.id.action_search_home)
            startActivity(new Intent(getContext(), SearchActivity.class));

        return true;
    }

    @Subscribe
    public void onBookmarksChanged(BookmarkChangedEvent bookmarkChangedEvent) {
        Timber.d("Bookmarks changed");
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void handleVisibility() {
        if (!sessions.isEmpty()) {
            bookmarksRecyclerView.setVisibility(View.VISIBLE);
            bookmarkHeader.setVisibility(View.VISIBLE);
        } else {
            bookmarksRecyclerView.setVisibility(View.GONE);
            bookmarkHeader.setVisibility(View.GONE);
        }
    }

    private void loadEventDates() {
        dateList.clear();
        RealmResults<EventDates> eventDates = realmRepo.getEventDatesSync();
        for (EventDates eventDate : eventDates) {
            dateList.add(eventDate.getDate());
        }
    }

    private void loadData() {
        loadEventDates();

        bookmarksResult = realmRepo.getBookMarkedSessions();
        bookmarksResult.removeAllChangeListeners();
        bookmarksResult.addChangeListener((bookmarked, orderedCollectionInnerChangeSet) -> {

            sessions.clear();
            for (String eventDate : dateList) {
                boolean headerCheck = false;
                for (Session bookmarkedSession : bookmarked) {
                    if (bookmarkedSession.getStartDate().equals(eventDate)) {
                        if (!headerCheck) {
                            String headerDate = "Invalid";
                            try {
                                headerDate = DateConverter.formatDay(eventDate);
                            } catch (DateTimeParseException e) {
                                e.printStackTrace();
                            }
                            sessions.add(headerDate);
                            headerCheck = true;
                        }
                        sessions.add(bookmarkedSession);
                    }
                }
                bookMarksListAdapter.notifyDataSetChanged();
                handleVisibility();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        OpenEventApp.getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        OpenEventApp.getEventBus().unregister(this);
        if (bookmarksResult != null)
            bookmarksResult.removeAllChangeListeners();
        if (event != null && event.isValid())
            event.removeAllChangeListeners();
    }
}