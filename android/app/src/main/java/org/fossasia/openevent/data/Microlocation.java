package org.fossasia.openevent.data;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


/**
 * Created by MananWason on 26-05-2015.
 */
public class Microlocation extends RealmObject {
    @PrimaryKey
    private int id;

    private String name;

    private float latitude;

    private float longitude;

    private int floor;

    public Microlocation() {}

    public Microlocation(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Microlocation(int id, String name, float latitude,
                         float longitude, int floor) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.floor = floor;

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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

}
