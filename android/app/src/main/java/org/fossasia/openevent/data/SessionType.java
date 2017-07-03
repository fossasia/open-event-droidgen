package org.fossasia.openevent.data;

import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Type("session-type")
public class SessionType extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
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
