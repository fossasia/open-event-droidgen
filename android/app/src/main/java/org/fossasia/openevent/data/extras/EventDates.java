package org.fossasia.openevent.data.extras;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Manan Wason on 18/06/16.
 */
public class EventDates extends RealmObject {
    @PrimaryKey
    private String date;

    public EventDates() {}

    public EventDates(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
