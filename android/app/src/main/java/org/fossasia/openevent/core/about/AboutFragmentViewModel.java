package org.fossasia.openevent.core.about;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.fossasia.openevent.common.arch.FilterableRealmLiveData;
import org.fossasia.openevent.common.arch.LiveRealmData;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.extras.EventDates;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.threeten.bp.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Predicate;
import io.realm.RealmResults;

public class AboutFragmentViewModel extends ViewModel {

    private final RealmDataRepository realmRepo;

    private FilterableRealmLiveData<Session> filterableRealmLiveData;
    private List<String> dateList;
    private LiveData<Event> eventLiveData;
    private LiveRealmData<Speaker> featuredSpeakersLiveData;
    private MutableLiveData<Bitmap> eventLogo;

    public AboutFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<Event> getEvent() {
        if (eventLiveData == null) {
            eventLiveData = RealmDataRepository.asLiveDataForObject(realmRepo.getEvent());
        }
        return eventLiveData;
    }

    public List<String> getDateList() {
        if (dateList == null) {
            dateList = new ArrayList<>();
            RealmResults<EventDates> eventDates = realmRepo.getEventDatesSync();
            for (EventDates eventDate : eventDates) {
                dateList.add(eventDate.getDate());
            }
        }
        return dateList;
    }

    public LiveData<List<Object>> getBookmarkedSessions() {
        if (filterableRealmLiveData == null)
            filterableRealmLiveData = RealmDataRepository.asFilterableLiveData(realmRepo.getBookMarkedSessions());
        Predicate<Session> predicate = Session::getIsBookmarked;
        filterableRealmLiveData.filter(predicate);

        return Transformations.map(filterableRealmLiveData, this::getSessionsList);
    }

    private List<Object> getSessionsList(List<Session> bookmarked) {
        List<Object> sessionsList = new ArrayList<>();
        for (String eventDate : getDateList()) {
            boolean headerCheck = false;
            for (Session bookmarkedSession : bookmarked) {
                if (bookmarkedSession.getStartDate() != null && bookmarkedSession.getStartDate().equals(eventDate)) {
                    if (!headerCheck) {
                        String headerDate = "Invalid";
                        try {
                            headerDate = DateConverter.formatDay(eventDate);
                        } catch (DateTimeParseException e) {
                            e.printStackTrace();
                        }
                        sessionsList.add(headerDate);
                        headerCheck = true;
                    }
                    sessionsList.add(bookmarkedSession);
                }
            }
        }
        return sessionsList;
    }

    public LiveRealmData<Speaker> getFeaturedSpeakers() {
        if (featuredSpeakersLiveData == null) {
            featuredSpeakersLiveData = RealmDataRepository.asLiveData(realmRepo.getFeaturedSpeakers());
        }
        return featuredSpeakersLiveData;
    }

    public LiveData<Bitmap> getEventLogo(String url) {
        if (eventLogo == null) {
            eventLogo = new MutableLiveData<>();
            RequestCreator requestCreator = StrategyRegistry.getInstance().getHttpStrategy().getPicassoWithCache().load(url);
            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    eventLogo.setValue(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    //no implementation
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    //no implementation
                }
            };
            requestCreator.into(target);
        }
        return eventLogo;
    }
}
