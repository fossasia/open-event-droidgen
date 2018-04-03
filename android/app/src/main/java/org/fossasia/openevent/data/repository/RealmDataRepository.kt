package org.fossasia.openevent.data.repository

import android.text.TextUtils
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import io.realm.*
import org.fossasia.openevent.common.arch.FilterableRealmLiveData
import org.fossasia.openevent.common.arch.LiveRealmData
import org.fossasia.openevent.common.arch.LiveRealmDataObject
import org.fossasia.openevent.common.events.BookmarkChangedEvent
import org.fossasia.openevent.config.StrategyRegistry
import org.fossasia.openevent.core.auth.model.User
import org.fossasia.openevent.data.*
import org.fossasia.openevent.data.extras.EventDates
import timber.log.Timber

class RealmDataRepository private constructor(val realmInstance: Realm) {

    /**
     * Returns Future style User which is null
     * To get the contents of User, add an OnRealmChangeListener
     * which notifies about the object state asynchronously
     *
     * @return User Returns User Future
     */
    val user: User
        get() {
            val realm = Realm.getDefaultInstance()
            val user = realm.where(User::class.java).findFirstAsync()
            realm.close()
            return user
        }

    /**
     * Returns User synchronously
     *
     * @return User
     */
    val userSync: User?
        get() {
            val realm = Realm.getDefaultInstance()
            val user = realm.where(User::class.java).findFirst()
            realm.close()
            return user
        }

    /**
     * Returns Future style Event which is null
     * To get the contents of Event, add an OnRealmChangeListener
     * which notifies about the object state asynchronously
     * @return Event Returns Event Future
     */
    val event: Event
        get() = realmInstance.where(Event::class.java).findFirstAsync()

    /**
     * Returns Event synchronously
     * @return Event
     */
    val eventSync: Event?
        get() = realmInstance.where(Event::class.java).findFirst()

    val tracks: RealmResults<Track>
        get() = realmInstance.where(Track::class.java)
                .sort("name")
                .findAllAsync()

    val tracksSync: RealmResults<Track>
        get() = realmInstance.where(Track::class.java)
                .sort("name")
                .findAll()

    val bookMarkedSessions: RealmResults<Session>
        get() = realmInstance.where(Session::class.java).equalTo("isBookmarked", true).findAllAsync()

    val bookMarkedSessionsSync: List<Session>
        get() {
            val realm = Realm.getDefaultInstance()
            val sessions = realm.where(Session::class.java).equalTo("isBookmarked", true).findAll()
            val list = realm.copyFromRealm(sessions)
            realm.close()
            return list
        }

    val featuredSpeakers: RealmResults<Speaker>
        get() = realmInstance.where(Speaker::class.java)
                .equalTo("isFeatured", true)
                .findAllAsync()

    val sponsors: RealmResults<Sponsor>
        get() = realmInstance.where(Sponsor::class.java)
                .sort("level", Sort.DESCENDING, "name", Sort.ASCENDING)
                .findAllAsync()

    val discountCodes: RealmResults<DiscountCode>
        get() = realmInstance.where(DiscountCode::class.java)
                .sort("code")
                .findAllAsync()

    val locations: RealmResults<Microlocation>
        get() = realmInstance.where(Microlocation::class.java)
                .sort("name")
                .findAllAsync()

    val locationsSync: RealmResults<Microlocation>
        get() = realmInstance.where(Microlocation::class.java)
                .sort("name")
                .findAll()

    val sessionTypes: RealmResults<SessionType>
        get() = realmInstance.where(SessionType::class.java)
                .sort("name")
                .findAllAsync()

    val sessionTypesSync: RealmResults<SessionType>
        get() = realmInstance.where(SessionType::class.java)
                .sort("name")
                .findAll()

    val eventDates: RealmResults<EventDates>
        get() = realmInstance.where(EventDates::class.java).findAllAsync()

    val eventDatesSync: RealmResults<EventDates>
        get() = realmInstance.where(EventDates::class.java).findAll()

    val notifications: RealmResults<Notification>
        get() = realmInstance.where(Notification::class.java)
                .sort("receivedAt")
                .findAllAsync()

    // FAQ Section
    val eventFAQs: RealmResults<FAQ>
        get() = realmInstance.where(FAQ::class.java).findAllAsync()

    //User Section
    private fun saveUserInRealm(user: User) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(user)
        realm.commitTransaction()
        realm.close()
    }

    /**
     * Saves the User object in database and returns Completable
     * object for tracking the state of operation
     *
     * @param user User which is to be stored
     * @return Completable object to be subscribed by caller
     */
    fun saveUser(user: User): Completable {
        return Completable.fromAction {
            saveUserInRealm(user)
            Timber.d("Saved User")
        }
    }

    fun clearUserData() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction({ realm1 -> realm1.delete(User::class.java) })
        realm.close()
    }


    // Events Section
    private fun saveEventInRealm(event: Event) {
        realmInstance.beginTransaction()
        realmInstance.insertOrUpdate(event)
        realmInstance.commitTransaction()
    }

    /**
     * Saves the Event object in database and returns Completable
     * object for tracking the state of operation
     * @param event Event which is to be stored
     * @return Completable object to be subscribed by caller
     */
    fun saveEvent(event: Event): Completable {
        return Completable.fromAction {
            saveEventInRealm(event)
            Timber.d("Saved Event")
        }
    }

    // Tracks Section

    /**
     * Saves tracks while merging with sessions asynchronously
     * @param tracks Tracks to be saved
     */
    private fun saveTracksInRealm(tracks: List<Track>) {
        // Since this is a threaded operation. We need our own instance of Realm
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction({ realm1 ->
            for (track in tracks) {
                val sessions = track.sessions

                if (sessions != null && !sessions.isEmpty()) {

                    val newSessions = RealmList<Session>()

                    for (session in sessions) {
                        // To prevent overwriting of previously saved values
                        val stored = realm1.where(Session::class.java).equalTo("id", session.id).findFirst()

                        if (stored != null) {
                            newSessions.add(stored)
                        } else {
                            newSessions.add(session)
                        }
                    }

                    track.sessions = newSessions
                    track.name = track.name // Trimming the response
                }

                realm1.insertOrUpdate(track)
            }
        })

        realm.close()
    }

    /**
     * Saves the list of Tracks in database and returns Completable
     * object for tracking the state of operation
     * @param tracks Tracks to be saved
     * @return Completable object to be subscribed by caller
     */
    fun saveTracks(tracks: List<Track>): Completable {
        return Completable.fromAction {
            saveTracksInRealm(tracks)
            Timber.d("Saved Tracks")
        }
    }

    /**
     * Returns filtered tracks according to query
     * @param query Query String WITHOUT wildcards
     * @return List of Tracks following constraints
     */
    fun getTracksFiltered(query: String): RealmResults<Track> {
        val wildcardQuery = String.format("*%s*", query)

        return realmInstance.where(Track::class.java)
                .like("name", wildcardQuery, Case.INSENSITIVE)
                .sort("name")
                .findAll()
    }

    fun getTrack(trackId: Int): Track {
        return realmInstance.where(Track::class.java).equalTo("id", trackId).findFirstAsync()
    }

    // Session Section

    /**
     * Saves sessions while merging with tracks and speakers asynchronously
     * @param sessions Sessions to be saved
     */
    private fun saveSessionsInRealm(sessions: List<Session>) {
        // Since this is a threaded operation. We need our own instance of Realm
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction({ transaction ->

            for (session in sessions) {
                // If session was previously bookmarked, set this one too
                val storedSession = transaction.where(Session::class.java).equalTo("id", session.id).findFirst()
                if (storedSession != null && storedSession.isBookmarked)
                    session.isBookmarked = true

                val speakers = session.speakers

                if (speakers != null && !speakers.isEmpty()) {

                    val newSpeakers = RealmList<Speaker>()

                    for (speaker in speakers) {
                        // To prevent overwriting of previously saved values
                        val stored = transaction.where(Speaker::class.java).equalTo("id", speaker.id).findFirst()

                        if (stored != null) {
                            newSpeakers.add(stored)
                        } else {
                            newSpeakers.add(speaker)
                        }
                    }

                    session.speakers = newSpeakers
                }

                val track = session.track

                if (track != null) {
                    // To prevent overwriting of previously saved values
                    val stored = transaction.where(Track::class.java).equalTo("id", track.id).findFirst()

                    if (stored != null) {
                        session.track = stored
                    } else {
                        // Set intermediate information for partial update

                        if (TextUtils.isEmpty(track.color))
                            track.color = "#bbbbbb"

                        if (track.name == null)
                            track.name = ""
                        else
                            track.name = track.name
                    }
                }

                if (session.title.contains("Create Full"))
                    Timber.d("Session $session")

                transaction.insertOrUpdate(session)
            }
        })

        realm.close()
    }

    fun saveSessions(sessions: List<Session>): Completable {
        return Completable.fromAction {
            saveSessionsInRealm(sessions)
            Timber.d("Saved Sessions")
        }
    }

    /**
     * Sets bookmark of a session asynchronously
     * @param sessionId Session ID whose bookmark is to be updated
     * @param bookmark boolean value of bookmark to be set
     * @return Completable denoting action completion
     */
    fun setBookmark(sessionId: Int, bookmark: Boolean): Completable {

        return Completable.fromAction {

            val realm1 = Realm.getDefaultInstance()

            realm1.beginTransaction()
            realm1.where(Session::class.java)
                    .equalTo("id", sessionId)
                    .findFirst()?.isBookmarked = bookmark

            StrategyRegistry.instance
                    .eventBusStrategy
                    ?.postEventOnUIThread(BookmarkChangedEvent())
            realm1.commitTransaction()

            realm1.close()
        }.subscribeOn(Schedulers.io())
    }

    fun getSession(sessionId: Int): Session {
        return realmInstance.where(Session::class.java).equalTo("id", sessionId).findFirstAsync()
    }

    fun getSessionSync(sessionId: Int): Session? {
        return realmInstance.where(Session::class.java).equalTo("id", sessionId).findFirst()
    }

    fun getSession(title: String): Session {
        return realmInstance.where(Session::class.java).equalTo("title", title).findFirstAsync()
    }

    /**
     * Returns sessions belonging to a specific track filtered by
     * a query string.
     * @param trackId ID of Track which Sessions should belong to
     * @param query Query of search WITHOUT wildcards
     * @return List of Sessions following constraints
     */
    fun getSessionsFiltered(trackId: Int, query: String): RealmResults<Session> {
        val wildcardQuery = String.format("*%s*", query)

        return realmInstance.where(Session::class.java)
                .equalTo("track.id", trackId)
                .like("title", wildcardQuery, Case.INSENSITIVE)
                .sort("startsAt")
                .findAll()
    }

    fun getSessionsByLocation(location: String): RealmResults<Session> {
        return realmInstance.where(Session::class.java)
                .equalTo("microlocation.name", location)
                .sort(Session.START_TIME)
                .findAllAsync()
    }

    fun getSessionsByDate(date: String): RealmResults<Session> {
        return realmInstance.where(Session::class.java).equalTo("startDate", date).findAllAsync()
    }

    fun getSessionsByDateFiltered(date: String, query: String, sortCriteria: String): RealmResults<Session> {
        val wildcardQuery = String.format("*%s*", query)

        return realmInstance.where(Session::class.java)
                .equalTo("startDate", date)
                .like("title", wildcardQuery, Case.INSENSITIVE)
                .sort(sortCriteria)
                .findAllAsync()
    }

    // Speakers Section

    /**
     * Saves speakers while merging with sessions asynchronously
     * @param speakers Speakers to be saved
     */
    private fun saveSpeakersInRealm(speakers: List<Speaker>) {

        // Since this is a threaded operation. We need our own instance of Realm
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction({ transaction ->
            for (speaker in speakers) {
                val sessions = speaker.sessions

                if (sessions != null && !sessions.isEmpty()) {
                    val newSessions = RealmList<Session>()

                    for (session in sessions) {
                        // To prevent overwriting of previously saved values
                        val stored = transaction.where(Session::class.java).equalTo("id", session.id).findFirst()

                        if (stored != null) {
                            newSessions.add(stored)
                        } else {
                            newSessions.add(session)
                        }
                    }

                    speaker.sessions = newSessions
                }

                transaction.insertOrUpdate(speaker)
            }
        })

        realm.close()
    }

    fun saveSpeakers(speakers: List<Speaker>): Completable {
        return Completable.fromAction {
            saveSpeakersInRealm(speakers)
            Timber.d("Saved Speakers")
        }
    }

    fun getSpeaker(speakerName: String): Speaker {
        return realmInstance.where(Speaker::class.java).equalTo("name", speakerName).findFirstAsync()
    }

    fun getSpeakersForName(speakerName: String): RealmResults<Speaker> {
        return realmInstance.where(Speaker::class.java).equalTo("name", speakerName).findAllAsync()
    }

    fun getSpeakers(sortCriteria: String): RealmResults<Speaker> {
        return realmInstance.where(Speaker::class.java)
                .sort(sortCriteria)
                .findAllAsync()
    }

    fun getSpeakersSync(sortCriteria: String): RealmResults<Speaker> {
        return realmInstance.where(Speaker::class.java)
                .sort(sortCriteria)
                .findAllAsync()
    }

    fun getSpeakersFiltered(query: String, sortCriteria: String): RealmResults<Speaker> {
        val wildcardQuery = String.format("*%s*", query)

        return realmInstance.where(Speaker::class.java)
                .like("name", wildcardQuery, Case.INSENSITIVE)
                .sort(sortCriteria)
                .findAllAsync()
    }

    // Sponsors Section

    private fun saveSponsorsInRealm(sponsors: List<Sponsor>) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction({ realm1 -> realm1.insertOrUpdate(sponsors) })
        realm.close()
    }

    fun saveSponsors(sponsors: List<Sponsor>): Completable {
        return Completable.fromAction {
            saveSponsorsInRealm(sponsors)
            Timber.d("Saved Sponsors")
        }
    }

    //DiscountCode section

    private fun saveDiscountCodesinRealm(discountCodes: List<DiscountCode>) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction({ realm1 -> realm1.insertOrUpdate(discountCodes) })
        realm.close()
    }

    fun saveDiscountCodes(discountCodes: List<DiscountCode>): Completable {
        return Completable.fromAction {
            saveDiscountCodesinRealm(discountCodes)
            Timber.d("Saved DiscountCodes")
        }
    }

    // Location Section

    private fun saveLocationsInRealm(locations: List<Microlocation>) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction({ realm1 -> realm1.insertOrUpdate(locations) })
        realm.close()
    }

    fun saveLocations(locations: List<Microlocation>): Completable {
        return Completable.fromAction {
            saveLocationsInRealm(locations)
            Timber.d("Saved Locations")
        }
    }

    //Session Types Section

    private fun saveSessionTypesInRealm(sessionTypes: List<SessionType>) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction({ realm1 -> realm1.insertOrUpdate(sessionTypes) })
        realm.close()
    }

    fun saveSessionTypes(sessionTypes: List<SessionType>): Completable {
        return Completable.fromAction {
            saveSessionTypesInRealm(sessionTypes)
            Timber.d("Saved Session Types")
        }
    }

    // Dates Section

    /**
     * Saves Event Dates Synchronously
     * @param eventDates List of dates of entire event span (inclusive)
     */
    private fun saveEventDatesInRealm(eventDates: List<EventDates>) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction({ transaction ->
            transaction.delete(EventDates::class.java)
            transaction.insertOrUpdate(eventDates)
        })
        realm.close()
    }

    fun saveEventDates(eventDates: List<EventDates>): Completable {
        return Completable.fromAction { saveEventDatesInRealm(eventDates) }
    }

    // Notifications section

    fun saveNotifications(notifications: List<Notification>): Completable {
        return Completable.fromAction {
            saveNotificationsInRealm(notifications)
            Timber.d("Saved notifications")
        }
    }

    private fun saveNotificationsInRealm(notifications: List<Notification>) {
        realmInstance.executeTransaction({ realm1 ->
            realm1.delete(Notification::class.java)
            realm1.insertOrUpdate(notifications)
        })
    }

    fun saveFAQs(faqs: List<FAQ>): Completable {
        return Completable.fromAction {
            saveFAQsInRealm(faqs)
            Timber.d("Saved FAQs")
        }
    }

    private fun saveFAQsInRealm(faqs: List<FAQ>) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction({ transaction ->
            // Using a threaded instance now to handle relationship with FAQ types in the future.
            transaction.delete(FAQ::class.java)
            transaction.insertOrUpdate(faqs)
        })
        realm.close()
    }

    companion object {

        private var realmDataRepository: RealmDataRepository? = null

        private val repoCache = HashMap<Realm, RealmDataRepository>()

        val defaultInstance: RealmDataRepository
            @JvmStatic
            get() {
                if (realmDataRepository == null)
                    realmDataRepository = RealmDataRepository(Realm.getDefaultInstance())

                return realmDataRepository as RealmDataRepository
            }

        /**
         * For threaded operation, a separate Realm instance is needed, not the default
         * instance, and thus all Realm objects can not pass through threads, extra care
         * must be taken to close the Realm instance after use or else app will crash
         * onDestroy of MainActivity. This is to ensure the database remains compact and
         * application remains free of silent bugs
         * @param realmInstance Separate Realm instance to be used
         * @return Realm Data Repository
         */
        @JvmStatic
        fun getInstance(realmInstance: Realm): RealmDataRepository? {
            if (!repoCache.containsKey(realmInstance)) {
                repoCache[realmInstance] = RealmDataRepository(realmInstance)
            }
            return repoCache[realmInstance]
        }

        @JvmStatic
        fun isNull(track: Track?): Boolean {
            return track == null || TextUtils.isEmpty(track.name) || TextUtils.isEmpty(track.color)
        }

        /**
         * Convert RealmResults to LiveRealmData
         */
        @JvmStatic
        fun <K : RealmObject> asLiveData(data: RealmResults<K>): LiveRealmData<K> {
            return LiveRealmData(data)
        }

        /**
         * Convert RealmObject to LiveRealmDataObject
         */
        @JvmStatic
        fun <K : RealmObject> asLiveDataForObject(data: K): LiveRealmDataObject<K> {
            return LiveRealmDataObject(data)
        }

        /**
         * Convert RealmResults to FilterableRealmLiveData
         */
        @JvmStatic
        fun <K : RealmObject> asFilterableLiveData(data: RealmResults<K>): FilterableRealmLiveData<K> {
            return FilterableRealmLiveData(data)
        }

        /**
         * Compacts the database to save space
         * Should be called when exiting application to ensure
         * all Realm instances are ready to be closed.
         *
         * Closing the repoCache instances is the responsibility
         * of caller
         */
        @JvmStatic
        fun compactDatabase() {
            val realm = realmDataRepository!!.realmInstance

            Timber.d("Vacuuming the database")
            Realm.compactRealm(realm.configuration)
        }
    }

}