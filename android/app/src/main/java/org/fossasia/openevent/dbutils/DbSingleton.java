package org.fossasia.openevent.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.SocialLink;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.Version;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * User: championswimmer
 * Date: 17/5/15
 */
public class DbSingleton {

    private static DbSingleton mInstance;

    private static Context mContext;

    private static DbHelper mDbHelper;

    private SQLiteDatabase mDb;

    private DatabaseOperations databaseOperations = new DatabaseOperations();

    // visible for testing
    public DbSingleton(Context context) {
        mContext = context;
        mDbHelper = new DbHelper(mContext);

    }

    /**
     * Only Exposed for testing purposes, either way Singletons suck.
     *
     * @param mDb     the readable/writable database
     * @param context A sane context (Application context please)
     * @param helper  A DB Helper
     */
    public DbSingleton(SQLiteDatabase mDb, Context context, DbHelper helper) {
        this.mDb = mDb;
        mContext = context;
        mDbHelper = helper;
    }

    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new DbSingleton(context);
        }
    }

    public static DbSingleton getInstance() {
        return mInstance;
    }

    private void getReadOnlyDatabase() {
        if ((mDb == null) || (!mDb.isReadOnly())) {
            mDb = mDbHelper.getReadableDatabase();
        }
    }

    private <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public ArrayList<Session> getSessionList() {
        getReadOnlyDatabase();
        return databaseOperations.getSessionList(mDb);
    }

    public Event getEventDetails() {
        getReadOnlyDatabase();
        return databaseOperations.getEventDetails(mDb);
    }

    public Observable<Event> getEventDetailsObservable() {
        return Observable.fromCallable(new Callable<Event>() {
            @Override
            public Event call() throws Exception {
                return getEventDetails();
            }
        }).compose(this.<Event>applySchedulers());
    }

    public Session getSessionById(int id) {
        getReadOnlyDatabase();
        return databaseOperations.getSessionById(id, mDb);
    }

    public Observable<Session> getSessionByIdObservable(final int id) {
        return Observable.fromCallable(new Callable<Session>() {
            @Override
            public Session call() throws Exception {
                return getSessionById(id);
            }
        }).compose(this.<Session>applySchedulers());
    }

    public List<Speaker> getSpeakerList(String sortBy) {
        getReadOnlyDatabase();
        return databaseOperations.getSpeakerList(mDb, sortBy);
    }

    public Observable<List<Speaker>> getSpeakerListObservable(final String sortBy) {
        return Observable.fromCallable(new Callable<List<Speaker>>() {
            @Override
            public List<Speaker> call() throws Exception {
                return getSpeakerList(sortBy);
            }
        }).compose(this.<List<Speaker>>applySchedulers());
    }

    public Version getVersionIds() {
        getReadOnlyDatabase();
        return databaseOperations.getVersionIds(mDb);
    }

    public List<SocialLink> getSocialLink(){
        getReadOnlyDatabase();
        return databaseOperations.getSocialLink(mDb);
    }

    public void addBookmarks(int bookmarkId) {
        databaseOperations.addBookmarksToDb(bookmarkId);
    }

    public Completable addBookmarksObservable(final int bookmarkId) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                addBookmarks(bookmarkId);
            }
        }).subscribeOn(Schedulers.computation());
    }

    public void deleteBookmarks(int bookmarkId) {
        databaseOperations.deleteBookmarks(bookmarkId, mDb);
    }

    public Completable deleteBookmarksObservable(final int bookmarkId) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                deleteBookmarks(bookmarkId);
            }
        });
    }

    public List<Track> getTrackList() {
        getReadOnlyDatabase();
        return databaseOperations.getTrackList(mDb);
    }

    public Observable<List<Track>> getTrackListObservable() {
        return Observable.fromCallable(new Callable<List<Track>>() {
            @Override
            public List<Track> call() throws Exception {
                return getTrackList();
            }
        }).compose(this.<List<Track>>applySchedulers());
    }

    public ArrayList<Sponsor> getSponsorList() {
        getReadOnlyDatabase();
        return databaseOperations.getSponsorList(mDb);
    }

    public Observable<ArrayList<Sponsor>> getSponsorListObservable() {
        return Observable.fromCallable(new Callable<ArrayList<Sponsor>>() {
            @Override
            public ArrayList<Sponsor> call() throws Exception {
                return getSponsorList();
            }
        }).compose(this.<ArrayList<Sponsor>>applySchedulers());
    }

    public ArrayList<Microlocation> getMicrolocationsList() {
        getReadOnlyDatabase();
        return databaseOperations.getMicrolocationsList(mDb);
    }

    public Observable<ArrayList<Microlocation>> getMicrolocationsListObservable() {
        return Observable.fromCallable(new Callable<ArrayList<Microlocation>>() {
            @Override
            public ArrayList<Microlocation> call() throws Exception {
                return getMicrolocationsList();
            }
        }).compose(this.<ArrayList<Microlocation>>applySchedulers());
    }

    public Microlocation getMicrolocationById(int id) {
        getReadOnlyDatabase();
        return databaseOperations.getMicroLocationById(id, mDb);
    }

    public Observable<Microlocation> getMicrolocationByIdObservable(final int id) {
        return Observable.fromCallable(new Callable<Microlocation>() {
            @Override
            public Microlocation call() throws Exception {
                return getMicrolocationById(id);
            }
        }).compose(this.<Microlocation>applySchedulers());
    }

    public ArrayList<Session> getSessionbyTracksname(String trackName) {
        return databaseOperations.getSessionbyTracksname(trackName, mDb);
    }

    public Observable<ArrayList<Session>> getSessionbyTracksnameObservable(final String trackName) {
        return Observable.fromCallable(new Callable<ArrayList<Session>>() {
            @Override
            public ArrayList<Session> call() throws Exception {
                return getSessionbyTracksname(trackName);
            }
        }).compose(this.<ArrayList<Session>>applySchedulers());
    }

    public List<String> getDateList() {
        getReadOnlyDatabase();
        return databaseOperations.getDateList(mDb);
    }

    public Observable<List<String>> getDateListObservable() {
        return Observable.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return getDateList();
            }
        }).compose(this.<List<String>>applySchedulers());
    }

    public ArrayList<Session> getSessionbyDate(String date, String sortOrder) {
        return databaseOperations.getSessionbyDate(date, sortOrder, mDb);
    }

    public Observable<ArrayList<Session>> getSessionByDateObservable(final String date, final String sortOrder) {
        return Observable.fromCallable(new Callable<ArrayList<Session>>() {
            @Override
            public ArrayList<Session> call() throws Exception {
                return getSessionbyDate(date, sortOrder);
            }
        }).compose(this.<ArrayList<Session>>applySchedulers());
    }

    public ArrayList<Session> getSessionbySpeakersName(String speakerName) {
        return databaseOperations.getSessionbySpeakersname(speakerName, mDb);
    }

    public Observable<ArrayList<Session>> getSessionbySpeakersNameObservable(final String speakerName) {
        return Observable.fromCallable(new Callable<ArrayList<Session>>() {
            @Override
            public ArrayList<Session> call() throws Exception {
                return getSessionbySpeakersName(speakerName);
            }
        }).compose(this.<ArrayList<Session>>applySchedulers());
    }

    public ArrayList<Session> getSessionbyLocationName(String locationName) {
        return databaseOperations.getSessionbyLocationname(locationName, mDb);
    }

    public Observable<ArrayList<Session>> getSessionbyLocationNameObservable(final String locationName) {
        return Observable.fromCallable(new Callable<ArrayList<Session>>() {
            @Override
            public ArrayList<Session> call() throws Exception {
                return getSessionbyLocationName(locationName);
            }
        }).compose(this.<ArrayList<Session>>applySchedulers());
    }

    public ArrayList<Speaker> getSpeakersbySessionName(String sessionName) {
        getReadOnlyDatabase();
        return databaseOperations.getSpeakersbySessionName(sessionName, mDb);
    }

    public Observable<ArrayList<Speaker>> getSpeakersbySessionNameObservable(final String sessionName) {
        return Observable.fromCallable(new Callable<ArrayList<Speaker>>() {
            @Override
            public ArrayList<Speaker> call() throws Exception {
                return getSpeakersbySessionName(sessionName);
            }
        }).compose(this.<ArrayList<Speaker>>applySchedulers());
    }

    public Track getTrackbyName(String trackName) {
        return databaseOperations.getTracksbyTracksname(trackName, mDb);
    }

    public Track getTrackbyId(int id) {
        return databaseOperations.getTracksbyTracksId(id, mDb);
    }

    public Observable<Track> getTrackbyIdObservable(final int id) {
        return Observable.fromCallable(new Callable<Track>() {
            @Override
            public Track call() throws Exception {
                return getTrackbyId(id);
            }
        }).compose(this.<Track>applySchedulers());
    }

    public Speaker getSpeakerbySpeakersname(String speakerName) {
        return databaseOperations.getSpeakerbySpeakersname(speakerName, mDb);
    }

    public Observable<Speaker> getSpeakerbySpeakersnameObservable(final String speakerName) {
        return Observable.fromCallable(new Callable<Speaker>() {
            @Override
            public Speaker call() throws Exception {
                return getSpeakerbySpeakersname(speakerName);
            }
        }).compose(this.<Speaker>applySchedulers());
    }

    public Session getSessionbySessionname(String sessionName) {
        return databaseOperations.getSessionbySessionname(sessionName, mDb);
    }

    public Observable<Session> getSessionbySessionnameObservable(final String sessionName) {
        return Observable.fromCallable(new Callable<Session>() {
            @Override
            public Session call() throws Exception {
                return getSessionbySessionname(sessionName);
            }
        }).compose(this.<Session>applySchedulers());
    }

    public boolean isBookmarked(int sessionId) {
        return databaseOperations.isBookmarked(sessionId, mDb);
    }

    public Observable<Boolean> isBookmarkedObservable(final int sessionId) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return isBookmarked(sessionId);
            }
        }).compose(this.<Boolean>applySchedulers());
    }

    public boolean isBookmarksTableEmpty() {
        return databaseOperations.isBookmarksTableEmpty(mDb);
    }

    public Observable<Boolean> isBookmarksTableEmptyObservable() {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return isBookmarksTableEmpty();
            }
        }).compose(this.<Boolean>applySchedulers());
    }

    public ArrayList<Integer> getBookmarkIds() throws ParseException {
        getReadOnlyDatabase();
        return databaseOperations.getBookmarkIds(mDb);
    }

    public Observable<ArrayList<Integer>> getBookmarkIdsObservable() throws ParseException {
        return Observable.fromCallable(new Callable<ArrayList<Integer>>() {
            @Override
            public ArrayList<Integer> call() throws Exception {
                return getBookmarkIds();
            }
        }).compose(this.<ArrayList<Integer>>applySchedulers());
    }

    public Microlocation getLocationByLocationname(String LocationName) {
        return databaseOperations.getLocationByName(LocationName, mDb);
    }

    public void insertQueries(ArrayList<String> queries) {
        databaseOperations.insertQueries(queries, mDbHelper);
    }

    public Completable insertQueriesObservable(final ArrayList<String> queries) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                insertQueries(queries);
            }
        }).subscribeOn(Schedulers.computation());
    }

    public void insertQuery(String query) {
        databaseOperations.insertQuery(query, mDbHelper);
    }

    public Completable insertQueryObservable(final String query) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                insertQuery(query);
            }
        }).subscribeOn(Schedulers.computation());
    }

    public void clearTable(String table) {
        databaseOperations.clearDatabaseTable(table, mDbHelper);
    }

    public void clearDatabase() {
        databaseOperations.clearDatabase(mDbHelper);
    }

    public Completable clearDatabaseObservable() {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                clearDatabase();
            }
        }).subscribeOn(Schedulers.computation());
    }

}
