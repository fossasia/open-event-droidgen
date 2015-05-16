package org.fossasia.openevent.data;

import java.util.ArrayList;

/**
 * Created by championswimmer on 16/5/15.
 */
public class Track {

    int id;
    String name;
    String description;

    public Track(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static ArrayList<Track> getTrackList () {
        ArrayList<Track> tracks = new ArrayList<>();
        //TODO: Get data from database
        return tracks;
    }
}
