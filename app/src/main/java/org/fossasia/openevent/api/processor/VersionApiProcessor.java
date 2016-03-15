package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.VersionResponseList;
import org.fossasia.openevent.data.Version;
import org.fossasia.openevent.dbutils.DataDownloadManager;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.CounterEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 12-06-2015
 */
public class VersionApiProcessor implements Callback<VersionResponseList> {
    private static final String TAG = "Version";

    int counterRequests;

    @Override
    public void success(final VersionResponseList versionResponseList, Response response) {
        CommonTaskLoop.getInstance().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> queries = new ArrayList<>();
                DbSingleton dbSingleton = DbSingleton.getInstance();
                for (Version version : versionResponseList.versions) {
                    counterRequests = 0;

                    if ((dbSingleton.getVersionIds() == null)) {
                        queries.add(version.generateSql());
                        dbSingleton.insertQueries(queries);
                        DataDownloadManager download = DataDownloadManager.getInstance();
                        download.downloadEvents();
                        download.downloadSpeakers();
                        download.downloadTracks();
                        download.downloadMicrolocations();
                        download.downloadSession();
                        download.downloadSponsors();
                        counterRequests += 6;

                    } else if ((dbSingleton.getVersionIds().getId() != version.getId())) {
                        DataDownloadManager download = DataDownloadManager.getInstance();
                        if (dbSingleton.getVersionIds().getEventVer() != version.getEventVer()) {
                            download.downloadEvents();
                            Timber.tag(TAG).d("events");
                            counterRequests++;
                        }
                        if (dbSingleton.getVersionIds().getSpeakerVer() != version.getSpeakerVer()) {
                            download.downloadSpeakers();
                            Timber.tag(TAG).d("speaker");

                            counterRequests++;

                        }
                        if (dbSingleton.getVersionIds().getSponsorVer() != version.getSponsorVer()) {
                            download.downloadSponsors();
                            Timber.tag(TAG).d("sponsor");

                            counterRequests++;

                        }
                        if (dbSingleton.getVersionIds().getTracksVer() != version.getTracksVer()) {
                            download.downloadTracks();

                            Timber.tag(TAG).d("tracks");

                            counterRequests++;

                        }
                        if (dbSingleton.getVersionIds().getSessionVer() != version.getSessionVer()) {
                            download.downloadSession();

                            Timber.tag(TAG).d("session");

                            counterRequests++;

                        }
                        if (dbSingleton.getVersionIds().getMicrolocationsVer() != version.getMicrolocationsVer()) {
                            download.downloadMicrolocations();
                            Timber.tag(TAG).d("micro");
                            counterRequests++;

                        }
                    } else {
                        Timber.tag(TAG).d("data fresh");
                    }
                    CounterEvent counterEvent = new CounterEvent(counterRequests);
                    OpenEventApp.postEventOnUIThread(counterEvent);
                }
            }
        });
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.tag("RetrofitError").d(String.valueOf(error.getCause()));
        OpenEventApp.postEventOnUIThread(error);
    }
}
