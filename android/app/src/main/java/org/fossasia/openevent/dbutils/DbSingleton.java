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

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * User: championswimmer
 * Date: 17/5/15
 */
public class DbSingleton {

    private static DbSingleton mInstance;

    private DbHelper mDbHelper;

    private SQLiteDatabase mDb;

    private DatabaseOperations databaseOperations = new DatabaseOperations();

    // visible for testing
    public DbSingleton(Context context) {
        mDbHelper = new DbHelper(context);
    }

    /**
     * Only Exposed for testing purposes, either way Singletons suck.
     *
     * @param mDb     the readable/writable database
     * @param helper  A DB Helper
     */
    public DbSingleton(SQLiteDatabase mDb, DbHelper helper) {
        this.mDb = mDb;
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
        return upstream -> upstream.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
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
        return Observable.fromCallable(this::getEventDetails)
                .compose(applySchedulers());
    }

    public Session getSessionById(int id) {
        getReadOnlyDatabase();
        return databaseOperations.getSessionById(id, mDb);
    }

    public Observable<Session> getSessionByIdObservable(final int id) {
        return Observable.fromCallable(() -> getSessionById(id))
                .compose(applySchedulers());
    }

    public List<Speaker> getSpeakerList(String sortBy) {
        getReadOnlyDatabase();
        return databaseOperations.getSpeakerList(mDb, sortBy);
    }

    public Observable<List<Speaker>> getSpeakerListObservable(final String sortBy) {
        return Observable.fromCallable(() -> getSpeakerList(sortBy))
                .compose(applySchedulers());
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
        return Completable.fromAction(() -> addBookmarks(bookmarkId))
                .subscribeOn(Schedulers.computation());
    }

    public void deleteBookmarks(int bookmarkId) {
        databaseOperations.deleteBookmarks(bookmarkId, mDb);
    }

    public Completable deleteBookmarksObservable(final int bookmarkId) {
        return Completable.fromAction(() -> deleteBookmarks(bookmarkId));
    }

    public List<Track> getTrackList() {
        getReadOnlyDatabase();
        return databaseOperations.getTrackList(mDb);
    }

    public Observable<List<Track>> getTrackListObservable() {
        return Observable.fromCallable(this::getTrackList)
                .compose(applySchedulers());
    }

    public ArrayList<Sponsor> getSponsorList() {
        getReadOnlyDatabase();
        return databaseOperations.getSponsorList(mDb);
    }

    public Observable<ArrayList<Sponsor>> getSponsorListObservable() {
        return Observable.fromCallable(this::getSponsorList)
                .compose(applySchedulers());
    }

    public ArrayList<Microlocation> getMicrolocationList() {
        getReadOnlyDatabase();
        return databaseOperations.getMicrolocationList(mDb);
    }

    public Observable<ArrayList<Microlocation>> getMicrolocationListObservable() {
        return Observable.fromCallable(this::getMicrolocationList)
                .compose(applySchedulers());
    }

    public Microlocation getMicrolocationById(int id) {
        getReadOnlyDatabase();
        return databaseOperations.getMicrolocationById(id, mDb);
    }

    public Observable<Microlocation> getMicrolocationByIdObservable(final int id) {
        return Observable.fromCallable(() -> getMicrolocationById(id))
                .compose(applySchedulers());
    }

    public ArrayList<Session> getSessionsByTrackName(String trackName) {
        return databaseOperations.getSessionsByTrackName(trackName, mDb);
    }

    public Observable<ArrayList<Session>> getSessionsByTrackNameObservable(final String trackName) {
        return Observable.fromCallable(() -> getSessionsByTrackName(trackName))
                .compose(applySchedulers());
    }

    public List<String> getDateList() {
        getReadOnlyDatabase();
        return databaseOperations.getDateList(mDb);
    }

    public Observable<List<String>> getDateListObservable() {
        return Observable.fromCallable(this::getDateList)
                .compose(applySchedulers());
    }

    public ArrayList<Session> getSessionsByDate(String date, String sortOrder) {
        return databaseOperations.getSessionsByDate(date, sortOrder, mDb);
    }

    public Observable<ArrayList<Session>> getSessionsByDateObservable(final String date, final String sortOrder) {
        return Observable.fromCallable(() -> getSessionsByDate(date, sortOrder))
                .compose(applySchedulers());
    }

    public ArrayList<Session> getSessionsBySpeakerName(String speakerName) {
        return databaseOperations.getSessionsBySpeakerName(speakerName, mDb);
    }

    public Observable<ArrayList<Session>> getSessionsBySpeakersNameObservable(final String speakerName) {
        return Observable.fromCallable(() -> getSessionsBySpeakerName(speakerName))
                .compose(applySchedulers());
    }

    public ArrayList<Session> getSessionsByLocationName(String locationName) {
        return databaseOperations.getSessionsByLocationName(locationName, mDb);
    }

    public Observable<ArrayList<Session>> getSessionsByLocationNameObservable(final String locationName) {
        return Observable.fromCallable(() -> getSessionsByLocationName(locationName))
                .compose(applySchedulers());
    }

    public ArrayList<Speaker> getSpeakersBySessionName(String sessionName) {
        getReadOnlyDatabase();
        return databaseOperations.getSpeakersBySessionName(sessionName, mDb);
    }

    public Observable<ArrayList<Speaker>> getSpeakersBySessionNameObservable(final String sessionName) {
        return Observable.fromCallable(() -> getSpeakersBySessionName(sessionName))
                .compose(applySchedulers());
    }

    public Track getTrackByName(String trackName) {
        return databaseOperations.getTrackByTrackName(trackName, mDb);
    }

    public Observable<Track> getTrackByNameObservable(final String trackName) {
        return Observable.fromCallable(() -> getTrackByName(trackName))
                .compose(applySchedulers());
    }

    public Track getTrackById(int id) {
        return databaseOperations.getTrackByTrackId(id, mDb);
    }

    public Observable<Track> getTrackByIdObservable(final int id) {
        return Observable.fromCallable(() -> getTrackById(id))
                .compose(applySchedulers());
    }

    public Speaker getSpeakerBySpeakerName(String speakerName) {
        return databaseOperations.getSpeakerBySpeakerName(speakerName, mDb);
    }

    public Observable<Speaker> getSpeakerBySpeakerNameObservable(final String speakerName) {
        return Observable.fromCallable(() -> getSpeakerBySpeakerName(speakerName))
                .compose(applySchedulers());
    }

    public Session getSessionBySessionName(String sessionName) {
        return databaseOperations.getSessionBySessionName(sessionName, mDb);
    }

    public Observable<Session> getSessionBySessionNameObservable(final String sessionName) {
        return Observable.fromCallable(() -> getSessionBySessionName(sessionName))
                .compose(applySchedulers());
    }

    public boolean isBookmarked(int sessionId) {
        return databaseOperations.isBookmarked(sessionId, mDb);
    }

    public Observable<Boolean> isBookmarkedObservable(final int sessionId) {
        return Observable.fromCallable(() -> isBookmarked(sessionId))
                .compose(applySchedulers());
    }

    public boolean isBookmarksTableEmpty() {
        return databaseOperations.isBookmarksTableEmpty(mDb);
    }

    public Observable<Boolean> isBookmarksTableEmptyObservable() {
        return Observable.fromCallable(this::isBookmarksTableEmpty)
                .compose(applySchedulers());
    }

    public ArrayList<Integer> getBookmarkIds() throws ParseException {
        getReadOnlyDatabase();
        return databaseOperations.getBookmarkIds(mDb);
    }

    public Observable<ArrayList<Integer>> getBookmarkIdsObservable() throws ParseException {
        return Observable.fromCallable(this::getBookmarkIds)
                .compose(applySchedulers());
    }

    public Microlocation getLocationByLocationName(String LocationName) {
        return databaseOperations.getLocationByName(LocationName, mDb);
    }

    public void insertQueries(ArrayList<String> queries) {
        databaseOperations.insertQueries(queries, mDbHelper);
    }

    public Completable insertQueriesObservable(final ArrayList<String> queries) {
        return Completable.fromAction(() -> insertQueries(queries))
                .subscribeOn(Schedulers.computation());
    }

    public void insertQuery(String query) {
        databaseOperations.insertQuery(query, mDbHelper);
    }

    public Completable insertQueryObservable(final String query) {
        return Completable.fromAction(() -> insertQuery(query))
                .subscribeOn(Schedulers.computation());
    }

    public void clearTable(String table) {
        databaseOperations.clearDatabaseTable(table, mDbHelper);
    }

    public void clearDatabase() {
        databaseOperations.clearDatabase(mDbHelper);
    }

    public Completable clearDatabaseObservable() {
        return Completable.fromAction(this::clearDatabase)
                .subscribeOn(Schedulers.computation());
    }

}
