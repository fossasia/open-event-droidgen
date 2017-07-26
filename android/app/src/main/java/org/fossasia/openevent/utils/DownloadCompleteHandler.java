package org.fossasia.openevent.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.StringRes;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.events.CounterEvent;
import org.fossasia.openevent.events.DownloadEvent;
import org.fossasia.openevent.events.EventDownloadEvent;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;
import org.fossasia.openevent.events.SessionDownloadEvent;
import org.fossasia.openevent.events.SessionTypesDownloadEvent;
import org.fossasia.openevent.events.SpeakerDownloadEvent;
import org.fossasia.openevent.events.SponsorDownloadEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;

import io.reactivex.subjects.CompletableSubject;
import timber.log.Timber;

public class DownloadCompleteHandler {
    private static final String COUNTER_TAG = "DownloadCounter";

    private Context context;
    private ProgressDialog downloadProgressDialog;
    private int counter;
    private int eventsDone = 0;

    private String shownMessage = "";

    private boolean hasShownError = false;

    private EventHandler eventHandler;

    private CompletableSubject completeSubject = CompletableSubject.create();

    private DownloadCompleteHandler(Context context) {
        this.context = context;
        setupProgressBar();
    }

    public static DownloadCompleteHandler with(Context context) {
        return new DownloadCompleteHandler(context);
    }

    public DownloadCompleteHandler startListening() {
        eventHandler = new EventHandler();
        setupProgressBar();

        return this;
    }

    public DownloadCompleteHandler stopListening() {
        if(eventHandler != null)
            eventHandler.unregister();

        return this;
    }

    public DownloadCompleteHandler show() {
        showProgressBar(true);

        return this;
    }

    public DownloadCompleteHandler hide() {
        showProgressBar(false);

        return this;
    }

    public CompletableSubject withCompletionListener() {
        return completeSubject;
    }

    private void notifyComplete() {
        eventsDone = 0;
        showProgressBar(false);
        completeSubject.onComplete();
    }

    public static class DataEventError extends RuntimeException {
        private DownloadEvent downloadEvent;

        DataEventError(DownloadEvent event) {
            this.downloadEvent = event;
        }

        public DownloadEvent getDataDownloadEvent() {
            return downloadEvent;
        }

        @Override
        public String getMessage() {
            return "Error downloading " + downloadEvent.getClass().getName();
        }
    }

    private void notifyError(DownloadEvent event) {
        if (hasShownError)
            return;

        hasShownError = true;

        showProgressBar(false);

        DataEventError eventError = new DataEventError(event);
        Timber.tag(COUNTER_TAG).d(eventError.getMessage());

        completeSubject.onError(eventError);
    }

    private String getString(@StringRes int stringRes) {
        return context.getString(stringRes);
    }

    private void setupProgressBar() {
        downloadProgressDialog = new ProgressDialog(context);
        downloadProgressDialog.setIndeterminate(false);
        downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadProgressDialog.setCancelable(false);
        shownMessage = String.format(getString(R.string.downloading_format), getString(R.string.event_info));
        downloadProgressDialog.setMessage(shownMessage);
    }

    private void updateDownloadProgress(float progress, @StringRes int stringRes) {
        String message = String.format(getString(R.string.downloaded_format), getString(stringRes));
        Timber.d("Progress : %f %s", progress*100, message);
        shownMessage += "\n" + message;
        downloadProgressDialog.setMessage(shownMessage);
        downloadProgressDialog.setProgress((int) (progress*100));
    }

    private void showProgressBar(boolean show) {
        if(show)
            downloadProgressDialog.show();
        else
            downloadProgressDialog.dismiss();
    }

    private void onDownloadDone(DownloadEvent event, @StringRes int stringRes) {
        if (event.isState()) {
            eventsDone++;
            Timber.tag(COUNTER_TAG).d("%d %s %d", eventsDone, getString(stringRes), counter);
            updateDownloadProgress(eventsDone/ (float) counter, stringRes);
            if (counter == eventsDone) {
                notifyComplete();
            }
        } else {
            notifyError(event);
        }
    }

    /**
     * Encapsulating inner class for handling EventBus events
     * Created to ensure that the event based subscriptions are not visible publicly
     */
    private class EventHandler {

        EventHandler() {
            OpenEventApp.getEventBus().register(this);
        }

        private void unregister() {
            OpenEventApp.getEventBus().unregister(this);
        }

        @Subscribe
        public void onCounterReceiver(CounterEvent event) {
            counter = event.getRequestsCount();
            Timber.tag(COUNTER_TAG).d(counter + " counter");
            if (counter == 0) {
                notifyComplete();
            }
        }

        @Subscribe
        public void onTracksDownloadDone(TracksDownloadEvent event) {
            onDownloadDone(event, R.string.menu_tracks);
        }

        @Subscribe
        public void onSponsorsDownloadDone(SponsorDownloadEvent event) {
            onDownloadDone(event, R.string.menu_sponsor);
        }

        @Subscribe
        public void onSpeakersDownloadDone(SpeakerDownloadEvent event) {
            onDownloadDone(event, R.string.menu_speakers);
        }

        @Subscribe
        public void onSessionDownloadDone(SessionDownloadEvent event) {
            onDownloadDone(event, R.string.sessions);
        }

        @Subscribe
        public void onEventsDownloadDone(EventDownloadEvent event) {
            onDownloadDone(event, R.string.event_info);
        }

        @Subscribe
        public void onMicrolocationsDownloadDone(MicrolocationDownloadEvent event) {
            onDownloadDone(event, R.string.menu_locations);
        }

        @Subscribe
        public void onSessionTypesDownloadDone(SessionTypesDownloadEvent event) {
            onDownloadDone(event, R.string.session_types);
        }
    }

}
