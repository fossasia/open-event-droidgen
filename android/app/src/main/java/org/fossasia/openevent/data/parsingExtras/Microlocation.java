package org.fossasia.openevent.data.parsingextras;

/**
 * Created by Manan Wason on 27/07/16.
 */
public class Microlocation {
    private int id;

    private String name;

    public Microlocation(int id, String name) {
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
