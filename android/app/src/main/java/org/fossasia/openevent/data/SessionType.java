package org.fossasia.openevent.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SessionType extends RealmObject {

    @PrimaryKey
    private int id;
    private String length;
    private String name;

    public String getLength() {
        return length;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
