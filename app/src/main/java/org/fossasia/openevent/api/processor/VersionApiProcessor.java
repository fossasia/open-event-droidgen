package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.VersionResponseList;
import org.fossasia.openevent.data.Version;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.CounterEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 12-06-2015.
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
                        DataDownload download = new DataDownload();
                        download.downloadEvents();
                        download.downloadSpeakers();
                        download.downloadTracks();
                        download.downloadMicrolocations();
                        download.downloadSession();
                        download.downloadSponsors();
                        counterRequests += 6;

                    } else if ((dbSingleton.getVersionIds().getId() != version.getId())) {
                        DataDownload download = new DataDownload();
                        if (dbSingleton.getVersionIds().getEventVer() != version.getEventVer()) {
                            download.downloadEvents();
                            Log.d(TAG, "events");
                            counterRequests++;
                        }
                        if (dbSingleton.getVersionIds().getSpeakerVer() != version.getSpeakerVer()) {
                            download.downloadSpeakers();
                            Log.d(TAG, "speaker");

                            counterRequests++;

                        }
                        if (dbSingleton.getVersionIds().getSponsorVer() != version.getSponsorVer()) {
                            download.downloadSponsors();
                            Log.d(TAG, "sponsor");

                            counterRequests++;

                        }
                        if (dbSingleton.getVersionIds().getTracksVer() != version.getTracksVer()) {
                            download.downloadTracks();

                            Log.d(TAG, "tracks");

                            counterRequests++;

                        }
                        if (dbSingleton.getVersionIds().getSessionVer() != version.getSessionVer()) {
                            download.downloadSession();

                            Log.d(TAG, "session");

                            counterRequests++;

                        }
                        if (dbSingleton.getVersionIds().getMicrolocationsVer() != version.getMicrolocationsVer()) {
                            download.downloadMicrolocations();
                            Log.d(TAG, "micro");
                            counterRequests++;

                        }
                    } else {
                        Log.d(TAG, "data fresh");
                    }
                    CounterEvent counterEvent = new CounterEvent(counterRequests);
                    OpenEventApp.postEventOnUIThread(counterEvent);
                }


            }
        });

    }

    @Override
    public void failure(RetrofitError error) {
        Log.d("Ret", "RetrofitError");
        OpenEventApp.postEventOnUIThread(error);
        Log.d("ErrorDescription", String.valueOf(error.getCause()));
    }

}
