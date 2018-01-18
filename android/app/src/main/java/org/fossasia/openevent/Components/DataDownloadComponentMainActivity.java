package org.fossasia.openevent.Components;

import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.utils.DataDownloadListenerMainActivity;

public class DataDownloadComponentMainActivity {

    private DataDownloadListenerMainActivity dataDownloadListenerMainActivity;

    public DataDownloadComponentMainActivity(DataDownloadListenerMainActivity dataDownloadListenerMainActivity) {
        this.dataDownloadListenerMainActivity = dataDownloadListenerMainActivity;
    }

    public void downloadData() {
        switch (Urls.getBaseUrl()) {
            case Urls.INVALID_LINK:
                dataDownloadListenerMainActivity.onDataDownload(1);
                break;
            case Urls.EMPTY_LINK:
                dataDownloadListenerMainActivity.onDataDownload(2);
                break;
            default:
                dataDownloadListenerMainActivity.onDataDownload(3);
                break;
        }
    }

}
