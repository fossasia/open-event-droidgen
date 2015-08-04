package org.fossasia.openevent.api.processor;

import android.util.Log;

import com.squareup.otto.Bus;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.VersionResponseList;
import org.fossasia.openevent.data.Version;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.DownloadComplete;
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
    int counter = 0;

    @Override
    public void success(final VersionResponseList versionResponseList, Response response) {
        CommonTaskLoop.getInstance().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> queries = new ArrayList<>();
                DbSingleton dbSingleton = DbSingleton.getInstance();
                Log.d("!", "3");
                for (Version version : versionResponseList.versions) {

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
                        counter += 6;
                        Log.d(TAG, "counter full " + counter);


                    } else if ((dbSingleton.getVersionIds().getId() != version.getId())) {
                        DataDownload download = new DataDownload();
                        if (dbSingleton.getVersionIds().getEventVer() != version.getEventVer()) {
                            download.downloadEvents();
                            Log.d(TAG, "events");
                            counter += 1;

                        }
                        if (dbSingleton.getVersionIds().getSpeakerVer() != version.getSpeakerVer()) {
                            download.downloadSpeakers();
                            Log.d(TAG, "speaker");
                            counter += 1;

                        }
                        if (dbSingleton.getVersionIds().getSponsorVer() != version.getSponsorVer()) {
                            download.downloadSponsors();
                            counter += 1;

                            Log.d(TAG, "sponsor");
                        }
                        if (dbSingleton.getVersionIds().getTracksVer() != version.getTracksVer()) {
                            download.downloadTracks();
                            counter += 1;

                            Log.d(TAG, "tracks");
                        }
                        if (dbSingleton.getVersionIds().getSessionVer() != version.getSessionVer()) {
                            download.downloadSession();
                            counter += 1;

                            Log.d(TAG, "session");
                        }
                        if (dbSingleton.getVersionIds().getMicrolocationsVer() != version.getMicrolocationsVer()) {
                            download.downloadMicrolocations();
                            counter += 1;
                            Log.d(TAG, "micro");
                        }
                    } else {
                        Log.d(TAG, "data fresh");
                        return;
                    }
                }


            }
        });

        Bus bus = OpenEventApp.getEventBus();
        bus.post(new DownloadComplete());
        Log.d("DWONLOAD", "POSTED");


    }

    @Override
    public void failure(RetrofitError error) {

    }


}
