package org.fossasia.openevent.data.parsingExtras;

/**
 * Created by Manan Wason on 09/08/16.
 */
public class Track {
    int id;

    String name;

    public Track(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
