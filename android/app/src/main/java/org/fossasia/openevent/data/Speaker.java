package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Type("speaker")
@ToString(of = {"id", "name"})
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Speaker extends RealmObject {

    public static final String SPEAKER = "speaker";
    public static final String NAME = "name";
    public static final String ORGANISATION = "organisation";
    public static final String COUNTRY = "country";

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    @Index
    private String name;
    @Index
    private String country;
    @Index
    private String organisation;
    private String photo;
    private String thumbnail;
    private String small;
    private String icon;
    private String twitter;
    private String linkedin;
    private String facebook;
    private String github;
    private String website;
    private boolean featured;
    private String city;
    private String longBiography;
    private String shortBiography;
    private String speakingExperience;
    private String gender;
    private String position;
    @Relationship("sessions")
    private RealmList<Session> sessions;
}