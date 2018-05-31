package org.fossasia.openevent.core.about;

import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.common.events.EventLoadedEvent;
import org.fossasia.openevent.common.ui.SnackbarUtil;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.base.BaseFragment;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.core.bookmark.BookmarkStatus;
import org.fossasia.openevent.core.bookmark.OnBookmarkSelectedListener;
import org.fossasia.openevent.core.main.MainActivity;
import org.fossasia.openevent.core.search.GlobalSearchAdapter;
import org.fossasia.openevent.core.search.SearchActivity;
import org.fossasia.openevent.core.track.session.SessionSpeakerListAdapter;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.extras.Copyright;
import org.fossasia.openevent.data.extras.SocialLink;
import org.fossasia.openevent.data.extras.SpeakersCall;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AboutFragment extends BaseFragment implements OnBookmarkSelectedListener {

    @BindView(R.id.event_description)
    protected TextView eventDescription;
    @BindView(R.id.organiser_description)
    protected TextView organiserDescription;
    @BindView(R.id.list_social_links)
    protected RecyclerView socialLinksRecyclerView;
    @BindView(R.id.event_venue_details)
    protected TextView venueDetails;
    @BindView(R.id.list_bookmarks)
    protected RecyclerView bookmarksRecyclerView;
    @BindView(R.id.bookmarks_card)
    protected CardView bookmarksCard;
    @BindView(R.id.bookmark_header)
    protected TextView bookmarkHeader;
    @BindView(R.id.starts_on)
    protected TextView startsOn;
    @BindView(R.id.ends_on)
    protected TextView endsOn;
    @BindView(R.id.coordinate_layout_about)
    protected CoordinatorLayout coordinatorLayoutParent;
    @BindView(R.id.featured_speakers_header)
    protected TextView featuredSpeakersHeader;
    @BindView(R.id.list_featured_speakers)
    protected RecyclerView featuresSpeakersRecyclerView;
    @BindView(R.id.event_name)
    protected TextView eventName;
    @BindView(R.id.logo)
    protected ImageView eventLogo;

    private Context context;
    private View root;
    private GlobalSearchAdapter bookMarksListAdapter;
    private SocialLinksListAdapter socialLinksListAdapter;
    private SessionSpeakerListAdapter featuredSpeakersListAdapter;

    private final List<Object> sessions = new ArrayList<>();
    private final List<SocialLink> socialLinks = new ArrayList<>();
    private final List<Speaker> featuredSpeakers = new ArrayList<>();
    private static final String MAP_FRAGMENT_TAG = "mapFragment";

    private Event event;
    private static OnMapSelectedListener mapFragmentCallback;

    public interface OnMapSelectedListener {
        void onMapSelected(boolean value);
    }

    private AboutFragmentViewModel aboutFragmentViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        context = getContext();
        root = super.onCreateView(inflater, container, savedInstanceState);

        setUpBookmarksRecyclerView();
        setUpSocialLinksRecyclerView();
        setUpFeaturedSpeakersRecyclerView();

        venueDetails.setOnClickListener(v -> {
            if (event.isValid()) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(ConstantStrings.IS_MAP_FRAGMENT_FROM_MAIN_ACTIVITY, true);
                bundle.putString(ConstantStrings.LOCATION_NAME, event.getLocationName());

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Fragment mapFragment = StrategyRegistry.getInstance().getMapModuleStrategy()
                        .getMapModuleFactory()
                        .provideMapModule()
                        .provideMapFragment();
                mapFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.content_frame, mapFragment, MAP_FRAGMENT_TAG).commit();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(event.getLocationName());
                if (mapFragmentCallback != null)
                    mapFragmentCallback.onMapSelected(true);
            }
        });

        startsOn.setOnClickListener(v -> {
            if (event.isValid())
                startActivity(Utils.eventCalendar(event));
        });

        endsOn.setOnClickListener(v -> {
            if (event.isValid())
                startActivity(Utils.eventCalendar(event));
        });

        aboutFragmentViewModel = ViewModelProviders.of(this).get(AboutFragmentViewModel.class);

        return root;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_about;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aboutFragmentViewModel.getEvent().observe(this, eventData -> {
            event = eventData;
            loadEvent(event);
        });
    }

    public static AboutFragment newInstance(OnMapSelectedListener onMapSelectedListener) {
        AboutFragment fragment = new AboutFragment();
        mapFragmentCallback = onMapSelectedListener;
        return fragment;
    }

    @Subscribe
    public void onEventLoaded(EventLoadedEvent eventLoadedEvent) {
        loadEvent(eventLoadedEvent.getEvent());
    }

    private void setUpBookmarksRecyclerView() {
        bookmarksCard.setVisibility(View.VISIBLE);
        bookMarksListAdapter = new GlobalSearchAdapter(sessions, context);
        bookMarksListAdapter.setOnBookmarkSelectedListener(this);
        bookmarksRecyclerView.setAdapter(bookMarksListAdapter);
        bookmarksRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        bookmarksRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setUpSocialLinksRecyclerView() {
        socialLinksListAdapter = new SocialLinksListAdapter(socialLinks);
        socialLinksRecyclerView.setAdapter(socialLinksListAdapter);
        socialLinksRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        socialLinksRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setUpFeaturedSpeakersRecyclerView() {
        featuresSpeakersRecyclerView.setVisibility(View.VISIBLE);
        featuredSpeakersListAdapter = new SessionSpeakerListAdapter(featuredSpeakers);
        featuresSpeakersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuresSpeakersRecyclerView.setNestedScrollingEnabled(false);
        featuresSpeakersRecyclerView.setAdapter(featuredSpeakersListAdapter);
        featuresSpeakersRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void loadEvent(Event event) {
        if (event == null || !event.isValid())
            return;

        eventName.setText(event.getName());
        Views.setHtml(organiserDescription, event.getOrganizerDescription(), true);
        Views.setHtml(eventDescription, event.getDescription(), true);
        venueDetails.setText(event.getLocationName());
        startsOn.setText(String.valueOf(DateConverter.formatDateWithDefault(DateConverter.FORMAT_DATE_COMPLETE, event.getStartsAt())));
        endsOn.setText(String.valueOf(DateConverter.formatDateWithDefault(DateConverter.FORMAT_DATE_COMPLETE, event.getEndsAt())));

        socialLinks.clear();
        socialLinks.addAll(event.getSocialLinks());
        socialLinksListAdapter.notifyDataSetChanged();

        aboutFragmentViewModel.getEventLogo(event.getLogoUrl()).observe(this, logoBitmap -> {
            eventLogo.setImageBitmap(logoBitmap);
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search_home:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
            case R.id.action_ticket_home:
                if (!event.isValid()) {
                    Snackbar.make(root, R.string.info_not_available, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                Utils.setUpCustomTab(getContext(), event.getTicketUrl());
                break;
            case R.id.action_display_copyright_dialog:
                displayCopyrightInformation();
                break;
            case R.id.action_display_speakers_call_dialog:
                displaySpeakersCallInformation();
                break;
            case R.id.action_download_latest_data:
                ((MainActivity) getActivity()).downloadData();
                break;
            default:
                //No option selected. Do Nothing..
        }

        return true;
    }

    private void displayCopyrightInformation() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.copyright_dialog, null);
        dialogBuilder.setView(dialogView).setPositiveButton("Back", (dialog, which) -> dialog.cancel());
        TextView holder = dialogView.findViewById(R.id.holder_textview);
        TextView licence = dialogView.findViewById(R.id.licence);
        TextView licenceurl = dialogView.findViewById(R.id.licence_url);
        if (event.isValid() && event.getEventCopyright().isValid()) {
            Copyright copyright = event.getEventCopyright();
            licence.setText(getResources().getString(R.string.space_separated_strings, copyright.getLicence(), String.valueOf(copyright.getYear())));
            holder.setText(copyright.getHolder());
            String linkedurl = String.format("<a href=\"%s\">" + copyright.getLicenceUrl() + "</a> ", copyright.getLicenceUrl());
            licenceurl.setText(Html.fromHtml(linkedurl));
            licenceurl.setOnClickListener(view -> Utils.setUpCustomTab(getContext(), copyright.getLicenceUrl()));
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        } else {
            Snackbar.make(root, R.string.info_not_available, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void displaySpeakersCallInformation() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.speakers_call_dialog, null);
        TextView holder = dialogView.findViewById(R.id.holder_textview);
        TextView announcement = dialogView.findViewById(R.id.announcement);
        TextView fromDateOfEvent = dialogView.findViewById(R.id.from_date_textview);
        TextView toDateOfEvent = dialogView.findViewById(R.id.to_date_textview);

        if (event.isValid() && event.getSpeakersCall().isValid()) {
            final SpeakersCall speakersCall = event.getSpeakersCall();
            holder.setText(event.getEventCopyright().getHolder());
            String announcementString = Html.fromHtml(speakersCall.getAnnouncement()).toString();
            String announcementText = getResources().getString(R.string.about_fragment_announcement, announcementString, event.getEmail());
            announcement.setText(announcementText);
            int index = speakersCall.getStartsAt().indexOf("T");
            String toDate = getResources().getString(R.string.about_fragment_to_date_event, speakersCall.getStartsAt().substring(0, index));
            toDateOfEvent.setText(toDate);
            String fromDate = getResources().getString(R.string.about_fragment_from_date_event, speakersCall.getEndsAt().substring(0, index));
            fromDateOfEvent.setText(fromDate);
            final String email = announcementText + "\n\n" + fromDate + "\n" + toDate;
            dialogBuilder.setView(dialogView).setNegativeButton("Back", (dialog, which) -> dialog.cancel());
            dialogBuilder.setPositiveButton("Share",
                    (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/html");
                        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject, event.getName()));
                        intent.putExtra(Intent.EXTRA_TEXT, email);
                        startActivity(Intent.createChooser(intent, "Send Mail/Share"));
                    });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        } else {
            Snackbar.make(root, R.string.info_not_available, Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mapFragmentCallback = null;
    }

    private void handleVisibility() {
        if (!sessions.isEmpty()) {
            bookmarksCard.setVisibility(View.VISIBLE);
            bookmarkHeader.setVisibility(View.VISIBLE);
        } else {
            bookmarksCard.setVisibility(View.GONE);
            bookmarkHeader.setVisibility(View.GONE);
        }

        if (!featuredSpeakers.isEmpty()) {
            featuredSpeakersHeader.setVisibility(View.VISIBLE);
            featuresSpeakersRecyclerView.setVisibility(View.VISIBLE);
        } else {
            featuredSpeakersHeader.setVisibility(View.GONE);
            featuresSpeakersRecyclerView.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        aboutFragmentViewModel.getBookmarkedSessions().observe(this, sessionsList -> {
            sessions.clear();
            sessions.addAll(sessionsList);
            bookMarksListAdapter = new GlobalSearchAdapter(sessions, getContext());
            bookMarksListAdapter.setOnBookmarkSelectedListener(this);
            bookmarksRecyclerView.setAdapter(bookMarksListAdapter);
            handleVisibility();
        });

        aboutFragmentViewModel.getFeaturedSpeakers().observe(this, featuredSpeakersList -> {
            featuredSpeakers.clear();
            featuredSpeakers.addAll(featuredSpeakersList);
            featuredSpeakersListAdapter.notifyDataSetChanged();
            handleVisibility();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().unregister(this);
        if (event != null && event.isValid())
            event.removeAllChangeListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bookMarksListAdapter.clearOnBookmarkSelectedListener();
    }

    @Override
    public void showSnackbar(BookmarkStatus bookmarkStatus) {
        Snackbar snackbar = Snackbar.make(bookmarkHeader, SnackbarUtil.getMessageResource(bookmarkStatus), Snackbar.LENGTH_LONG);
        SnackbarUtil.setSnackbarAction(getContext(), snackbar, bookmarkStatus)
                .show();
    }
}
