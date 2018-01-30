package org.fossasia.openevent.core.search;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;

import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class SearchActivityViewModel extends ViewModel {

    private String searchText = "";
    private List<Object> objectList = new ArrayList<>();
    private Realm realm;
    private MutableLiveData<List<Object>> searchResults = new MutableLiveData<>();

    public SearchActivityViewModel() {
        realm = Realm.getDefaultInstance();
    }

    public LiveData<List<Object>> getSearchResults(String search) {
        objectList.clear();
        String wildcardQuery = String.format("*%s*", search.toLowerCase(Locale.getDefault()));
        searchText = search;
        if (!TextUtils.isEmpty(search)) {
            addResultFromSessions(wildcardQuery);
            addResultsFromLocations(wildcardQuery);
            addResultsFromSpeakers(wildcardQuery);
            addResultsFromTracks(wildcardQuery);
        }

        searchResults.setValue(objectList);
        return searchResults;
    }

    public String getSearchText() {
        return searchText;
    }

    public void addResultsFromTracks(String queryString) {
        RealmResults<Track> filteredTracks = realm.where(Track.class)
                .like("name", queryString, Case.INSENSITIVE).findAllSortedAsync("name");

        filteredTracks.addChangeListener(tracks -> {
            if (tracks.size() > 0) {
                objectList.add("Tracks");
            }
            objectList.addAll(tracks);
            Timber.d("Filtering done total results %d", tracks.size());
        });
    }

    public void addResultsFromSpeakers(String queryString) {
        RealmResults<Speaker> filteredSpeakers = realm.where(Speaker.class)
                .like("name", queryString, Case.INSENSITIVE).or()
                .like("country", queryString, Case.INSENSITIVE).or()
                .like("organisation", queryString, Case.INSENSITIVE).findAllSortedAsync("name");

        filteredSpeakers.addChangeListener(speakers -> {
            if (speakers.size() > 0) {
                objectList.add("Speakers");
            }
            objectList.addAll(speakers);
            Timber.d("Filtering done total results %d", speakers.size());
        });
    }

    public void addResultsFromLocations(String queryString) {
        RealmResults<Microlocation> filteredMicrolocations = realm.where(Microlocation.class)
                .like("name", queryString, Case.INSENSITIVE).findAllSortedAsync("name");

        filteredMicrolocations.addChangeListener(microlocations -> {
            if (microlocations.size() > 0) {
                objectList.add("Locations");
            }
            objectList.addAll(microlocations);
            Timber.d("Filtering done total results %d", microlocations.size());
        });
    }

    public void addResultFromSessions(String queryString) {
        RealmResults<Session> filteredSessions = realm.where(Session.class)
                .like("title", queryString, Case.INSENSITIVE).findAllSortedAsync("title");

        filteredSessions.addChangeListener(sessions -> {
            if (sessions.size() > 0) {
                objectList.add("Sessions");
            }
            objectList.addAll(sessions);
            Timber.d("Filtering done total results %d", sessions.size());
        });
    }

    @Override
    public void onCleared(){
        //Closing realm instance and detaching listeners to avoid memory leaks
        realm.close();
        realm.removeAllChangeListeners();
    }
}
