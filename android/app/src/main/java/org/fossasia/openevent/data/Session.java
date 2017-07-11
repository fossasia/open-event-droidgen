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
@Type("session")
@ToString(of = {"id", "title"})
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class Session extends RealmObject {

    /* Sort criteria */

    public static final String TITLE = "title";
    // Track is object in Realm. So sort by track.name
    public static final String TRACK = "track.name";
    public static final String START_TIME = "startsAt";

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    @Index
    private String title;
    private String subtitle;
    private String shortAbstract;
    private String longAbstract;
    private String comments;
    private String startsAt;
    private String endsAt;
    private String language;
    private String slidesUrl;
    private String videoUrl;
    private String audioUrl;
    private String signupUrl;
    private String state;
    private String level;
    @Index
    private String startDate;
    private boolean isBookmarked;
    private String createdAt;
    private String deletedAt;
    private String submittedAt;
    private boolean isMailSent;
    @Relationship("session-type")
    private SessionType sessionType;
    @Relationship("track")
    private Track track;
    @Relationship("microlocation")
    private Microlocation microlocation;
    @Relationship("speakers")
    private RealmList<Speaker> speakers;
}