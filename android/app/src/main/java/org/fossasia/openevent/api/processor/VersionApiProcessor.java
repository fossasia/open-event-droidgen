package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.VersionResponseList;
import org.fossasia.openevent.data.Version;
import org.fossasia.openevent.dbutils.DataDownloadManager;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.CounterEvent;
import org.fossasia.openevent.events.RetrofitError;
import org.fossasia.openevent.events.RetrofitResponseEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 12-06-2015
 */
public class VersionApiProcessor implements Callback<VersionResponseList> {

    int counterRequests;

    @Override
    public void onResponse(Call<VersionResponseList> call, final Response<VersionResponseList> response) {
        if (response.isSuccessful()) {
            CommonTaskLoop.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> queries = new ArrayList<>();
                    DbSingleton dbSingleton = DbSingleton.getInstance();
                    for (Version version : response.body().versions) {
                        counterRequests = 0;

                        if ((dbSingleton.getVersionIds() == null)) {
                            queries.add(version.generateSql());
                            dbSingleton.insertQueries(queries);
                            DataDownloadManager download = DataDownloadManager.getInstance();
                            download.downloadSpeakers();
                            download.downloadTracks();
                            download.downloadMicrolocations();
                            download.downloadSession();
                            download.downloadSponsors();
                            counterRequests += 5;

                        } else {
                            DataDownloadManager download = DataDownloadManager.getInstance();
                            if (dbSingleton.getVersionIds().getEventVer() != version.getEventVer()) {
                                download.downloadEvents();
                                Timber.d("Downloading Events");
                                counterRequests++;
                            }
                            if (dbSingleton.getVersionIds().getSpeakerVer() != version.getSpeakerVer()) {
                                download.downloadSpeakers();
                                Timber.d("Downloading Speakers");
                                counterRequests++;

                            }
                            if (dbSingleton.getVersionIds().getSponsorVer() != version.getSponsorVer()) {
                                download.downloadSponsors();
                                Timber.d("Downloading Sponsor");
                                counterRequests++;

                            }
                            if (dbSingleton.getVersionIds().getTracksVer() != version.getTracksVer()) {
                                download.downloadTracks();

                                Timber.d("Downloading Tracks");
                                counterRequests++;

                            }
                            if (dbSingleton.getVersionIds().getSessionVer() != version.getSessionVer()) {
                                download.downloadSession();

                                Timber.d("Downloading Sessions");
                                counterRequests++;

                            }
                            if (dbSingleton.getVersionIds().getMicrolocationsVer() != version.getMicrolocationsVer()) {
                                download.downloadMicrolocations();
                                Timber.d("Downloading microlocations");
                                counterRequests++;

                            }
                            if (counterRequests == 0) {
                                Timber.d("Data fresh");
                            }
                        }
                        CounterEvent counterEvent = new CounterEvent(counterRequests);
                        OpenEventApp.postEventOnUIThread(counterEvent);
                    }
                }
            });
        } else {
            OpenEventApp.postEventOnUIThread(new RetrofitResponseEvent(response.code()));
        }
    }

    @Override
    public void onFailure(Call<VersionResponseList> call, Throwable t) {
        OpenEventApp.postEventOnUIThread(new RetrofitError(t));
    }
}