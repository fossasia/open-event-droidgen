package org.fossasia.openevent.common.api.processor;

import org.fossasia.openevent.common.events.DownloadEvent;
import org.fossasia.openevent.common.events.TicketDownloadEvent;
import org.fossasia.openevent.data.Ticket;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import java.util.List;

public class TicketResponseProcessor extends ResponseProcessor<List<Ticket>> {

    @Override
    protected void onSuccess(List<Ticket> ticket) {
        complete(RealmDataRepository.getDefaultInstance()
                .saveTickets(ticket));
    }

    @Override
    protected DownloadEvent getDownloadEvent(boolean success) {
        return new TicketDownloadEvent(success);
    }

    @Override
    protected Object getErrorResponseEvent(int errorCode) {
        return getDownloadEvent(false);
    }
}