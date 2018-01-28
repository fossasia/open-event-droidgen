package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.SessionType;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.extras.SocialLink;
import org.fossasia.openevent.dbutils.LiveRealmDataObject;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.EventDownloadEvent;
import org.fossasia.openevent.events.JsonReadEvent;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;
import org.fossasia.openevent.events.RetrofitError;
import org.fossasia.openevent.events.SessionDownloadEvent;
import org.fossasia.openevent.events.SessionTypesDownloadEvent;
import org.fossasia.openevent.events.SpeakerDownloadEvent;
import org.fossasia.openevent.events.SponsorDownloadEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.DateConverter;
import org.fossasia.openevent.utils.Utils;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import timber.log.Timber;

public class MainActivityViewModel extends ViewModel {
    private RealmDataRepository realmRepo;
    private LiveData<Event> eventLiveData;
    private MutableLiveData<Event> mutableEventLiveData;
    private Event event;
    private MutableLiveData<String> facebookPageNameLiveData;
    private MutableLiveData<String> facebookPageIdLiveData;
    private CompositeDisposable compositeDisposable;

    private final Observer<Event> eventObserver = event -> {
        if (!isEventLoaded())
            return;

        this.event = event;
        mutableEventLiveData.setValue(event);
    };

    public MainActivityViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<Event> getEvent() {
        if (eventLiveData == null) {
            // Setup asynchronous listener for event to handle changes
            mutableEventLiveData = new MutableLiveData<>();
            eventLiveData = RealmDataRepository.asLiveDataForObject(realmRepo.getEvent());
            eventLiveData.observeForever(eventObserver);
        }

        // Since the event is saved from JSON in background thread, asynchronous methods
        // are not aware of this change, so for each call of getEvent, we also check the event
        // synchronously. This is done mainly to accomodate behaviour on first download of app
        // when the data is loaded from JSON. Once the event is saved, the MainActivity calls this
        // method again to load the data, so it should be present at this time. This feature needs
        // refactoring and some another mechanism to deal with this situation
        this.event = realmRepo.getEventSync();
        if (isEventLoaded())
            mutableEventLiveData.setValue(event);

        return mutableEventLiveData;
    }

    private boolean isEventLoaded() {
        return event != null && event.isValid();
    }

    public MutableLiveData<String> downloadPageId(String fbPageName) {
        if (facebookPageNameLiveData == null)
            facebookPageNameLiveData = new MutableLiveData<>();

        //Store the facebook page name in the shared preference from the database
        if (isEventLoaded() && fbPageName == null) {
            RealmList<SocialLink> socialLinks = event.getSocialLinks();
            if (socialLinks != null) {
                RealmResults<SocialLink> facebookPage = socialLinks.where().equalTo("name", "Facebook").findAll();
                if (facebookPage.size() == 0)
                    return facebookPageNameLiveData;

                SocialLink facebookLink = facebookPage.get(0);
                String link = facebookLink.getLink();
                String tempString = ".com";
                String pageName = link.substring(link.indexOf(tempString) + tempString.length()).replace("/", "");

                if (Utils.isEmpty(pageName))
                    return facebookPageNameLiveData;

                facebookPageNameLiveData.setValue(pageName);
            }
        }

        return facebookPageNameLiveData;
    }

    public MutableLiveData<String> downloadPageName(String fbPageId, String fbPageName, String token) {
        if (facebookPageIdLiveData == null)
            facebookPageIdLiveData = new MutableLiveData<>();

        if (fbPageId == null && fbPageName != null) {
            Disposable facebookDisposable = APIClient.getFacebookGraphAPI().getPageId(fbPageName,
                    token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(facebookPageId -> {
                                String id = facebookPageId.getId();
                                facebookPageIdLiveData.setValue(id);
                            },
                            throwable -> Timber.d("Facebook page id download failed: " + throwable.toString()));
            compositeDisposable.add(facebookDisposable);
        }
        return facebookPageIdLiveData;
    }

    public void handleEvent(final JsonReadEvent jsonReadEvent) {
        final String name = jsonReadEvent.getName();
        final String json = jsonReadEvent.getJson();

        Disposable jsonDisposable = Completable.fromAction(() -> {
            ObjectMapper objectMapper = OpenEventApp.getObjectMapper();

            // Need separate instance for background thread
            Realm realm = Realm.getDefaultInstance();

            RealmDataRepository realmDataRepository = RealmDataRepository
                    .getInstance(realm);

            switch (name) {
                case ConstantStrings.EVENT: {
                    Event event = objectMapper.readValue(json, Event.class);

                    saveEventDates(event);
                    realmDataRepository.saveEvent(event).subscribe();

                    OpenEventApp.postEventOnUIThread(new EventDownloadEvent(true));
                    break;
                }
                case ConstantStrings.TRACKS: {
                    List<Track> tracks = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Track.class));

                    realmDataRepository.saveTracks(tracks).subscribe();

                    OpenEventApp.postEventOnUIThread(new TracksDownloadEvent(true));
                    break;
                }
                case ConstantStrings.SESSIONS: {
                    List<Session> sessions = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Session.class));

                    for (Session current : sessions) {
                        current.setStartDate(current.getStartsAt().split("T")[0]);
                    }

                    realmDataRepository.saveSessions(sessions).subscribe();

                    OpenEventApp.postEventOnUIThread(new SessionDownloadEvent(true));
                    break;
                }
                case ConstantStrings.SPEAKERS: {
                    List<Speaker> speakers = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Speaker.class));

                    realmRepo.saveSpeakers(speakers).subscribe();

                    OpenEventApp.postEventOnUIThread(new SpeakerDownloadEvent(true));
                    break;
                }
                case ConstantStrings.SPONSORS: {
                    List<Sponsor> sponsors = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Sponsor.class));

                    realmRepo.saveSponsors(sponsors).subscribe();

                    OpenEventApp.postEventOnUIThread(new SponsorDownloadEvent(true));
                    break;
                }
                case ConstantStrings.MICROLOCATIONS: {
                    List<Microlocation> microlocations = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Microlocation.class));

                    realmRepo.saveLocations(microlocations).subscribe();

                    OpenEventApp.postEventOnUIThread(new MicrolocationDownloadEvent(true));
                    break;
                }
                case ConstantStrings.SESSION_TYPES: {
                    List<SessionType> sessionTypes = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, SessionType.class));

                    realmRepo.saveSessionTypes(sessionTypes).subscribe();

                    OpenEventApp.postEventOnUIThread(new SessionTypesDownloadEvent(true));
                    break;
                }
                default:
                    //do nothing
            }
            realm.close();
        }).observeOn(Schedulers.computation()).subscribe(() -> Timber.d("Saved event from JSON"), throwable -> {
            throwable.printStackTrace();
            Timber.e(throwable);
            OpenEventApp.postEventOnUIThread(new RetrofitError(throwable));
        });
        compositeDisposable.add(jsonDisposable);
    }

    public void saveEventDates(Event event) {
        if (event != null) {
            String startTime = event.getStartsAt();
            String endTime = event.getEndsAt();

            Disposable dateDisposable = Observable.fromCallable(() ->
                    DateConverter.getDaysInBetween(startTime, endTime)
            ).subscribe(eventDates -> realmRepo.saveEventDates(eventDates).subscribe(), throwable -> {
                Timber.e(throwable);
                Timber.e("Error start parsing start date: %s and end date: %s in ISO format",
                        startTime, endTime);
                OpenEventApp.postEventOnUIThread(new RetrofitError(new Throwable("Error parsing dates")));
            });
            compositeDisposable.add(dateDisposable);
        }
    }

    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        if (eventLiveData != null)
            eventLiveData.removeObserver(eventObserver);
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        super.onCleared();
    }
}
