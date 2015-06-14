package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.api.protocol.VersionResponseList;
import org.fossasia.openevent.data.Version;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbSingleton;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 12-06-2015.
 */
public class VersionApiProcessor implements Callback<VersionResponseList> {
    private static final String TAG = "Version";

    @Override
    public void success(VersionResponseList versionResponseList, Response response) {
        ArrayList<String> queries = new ArrayList<>();
        DbSingleton dbSingleton = DbSingleton.getInstance();
        for (Version version : versionResponseList.versions) {

            if ((dbSingleton.getVersionIds() == null)) {
                queries.add(version.generateSql());
                dbSingleton.insertQueries(queries);
                Log.d(TAG, "null");


                DataDownload download = new DataDownload();
                download.downloadEvents();
                download.downloadSpeakers();
                download.downloadTracks();
                download.downloadMicrolocations();
                download.downloadSession();
                download.downloadSponsors();

            } else if ((dbSingleton.getVersionIds().getId() != version.getId())) {
                DataDownload download = new DataDownload();
                if (dbSingleton.getVersionIds().getEventVer() != version.getEventVer()) {
                    download.downloadEvents();
                    Log.d(TAG, "events");
                }
                if (dbSingleton.getVersionIds().getSpeakerVer() != version.getSpeakerVer()) {
                    download.downloadSpeakers();
                    Log.d(TAG, "speaker");
                }
                if (dbSingleton.getVersionIds().getSponsorVer() != version.getSponsorVer()) {
                    download.downloadSponsors();
                    Log.d(TAG, "sponsor");
                }
                if (dbSingleton.getVersionIds().getTracksVer() != version.getTracksVer()) {
                    download.downloadTracks();
                    Log.d(TAG, "tracks");
                }
                if (dbSingleton.getVersionIds().getSessionVer() != version.getSessionVer()) {
                    download.downloadSession();
                    Log.d(TAG, "session");
                }
                if (dbSingleton.getVersionIds().getMicrolocationsVer() != version.getMicrolocationsVer()) {
                    download.downloadMicrolocations();
                    Log.d(TAG, "micro");
                }
            } else {
                Log.d(TAG, "data fresh");
                return;
            }
        }


    }

    @Override
    public void failure(RetrofitError error) {

    }


}
