package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.DownloadEvent;
import org.fossasia.openevent.events.SponsorDownloadEvent;

import java.util.List;

public class SponsorListResponseProcessor extends ResponseProcessor<List<Sponsor>> {

    @Override
    protected void onSuccess(List<Sponsor> sponsors) {
        complete(RealmDataRepository.getDefaultInstance()
                .saveSponsors(sponsors));
    }

    @Override
    protected DownloadEvent getDownloadEvent(boolean success) {
        return new SponsorDownloadEvent(success);
    }

    @Override
    protected Object getErrorResponseEvent(int errorCode) {
        return getDownloadEvent(false);
    }
}