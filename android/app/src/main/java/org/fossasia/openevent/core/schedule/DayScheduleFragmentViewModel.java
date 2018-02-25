package org.fossasia.openevent.core.schedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.arch.FilterableRealmLiveData;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.common.utils.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.functions.Predicate;
import io.realm.RealmResults;
import io.realm.Sort;

public class DayScheduleFragmentViewModel extends ViewModel {

    private RealmDataRepository realmRepo;
    private RealmResults<Session> sessions;
    private List<String> tracks = new ArrayList<>();
    private FilterableRealmLiveData<Session> filterableSessions;
    private String searchText = "";

    public DayScheduleFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public String getSearchText() { return searchText; }

    public LiveData<List<Session>> getSessionsByDate(String date, String searchText) {
        sessions = loadSessions(date);
        return getSortedSessionsByTracks(searchText, new ArrayList<>());
    }

    public LiveData<List<Session>> getSortedSessionsByTracks(String searchText, List<String> selectedTracks) {
        Sort sortOrder;
        if (SortOrder.sortOrderSchedule() == SortOrder.SORT_ORDER_DESCENDING) {
            sortOrder = Sort.DESCENDING;
        } else {
            sortOrder = Sort.ASCENDING;
        }
        sessions = sessions.sort(SortOrder.sortTypeSchedule(), sortOrder);
        filterableSessions = RealmDataRepository.asFilterableLiveData(sessions);
        filterableSessions.filter(getPredicateWithSearchAndTracks(searchText, selectedTracks));
        return filterableSessions;
    }

    public LiveData<List<Session>> getSessionsBySearchText(String searchText) {
        filterableSessions.filter(getPredicateWithSearchAndTracks(searchText, tracks));
        return filterableSessions;
    }

    private Predicate<Session> getPredicateWithSearchAndTracks(String searchText, List<String> selectedTracks) {
        this.searchText = searchText;
        this.tracks = selectedTracks;
        final String query = this.searchText.toLowerCase(Locale.getDefault());

        Predicate<Session> predicate = session -> {
                boolean hasQuery = session.getTitle()
                        .toLowerCase(Locale.getDefault())
                        .contains(query);

                boolean isInTracks = true;
                if (!this.tracks.isEmpty()) {
                    isInTracks = this.tracks.contains(session.getTrack().getName());
                }
                return hasQuery && isInTracks;
            };
        return predicate;
    }

    private RealmResults<Session> loadSessions(String date) {
        return realmRepo.getSessionsByDate(date);
    }
}
