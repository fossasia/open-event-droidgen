package org.fossasia.openevent.data.extras;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SessionType extends RealmObject {

    @Expose
    @PrimaryKey
    private int id;

    @Expose
    private String length;

    @Expose
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
