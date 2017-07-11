package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import org.fossasia.openevent.data.extras.Copyright;
import org.fossasia.openevent.data.extras.SocialLink;
import org.fossasia.openevent.data.extras.SpeakersCall;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Type("event")
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class Event extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    private String identifier;
    private String name;
    private Double latitude;
    private Double longitude;
    private String locationName;
    private String startsAt;
    private String endsAt;
    private String timezone;
    private String description;
    private String logoUrl;
    private String organizerName;
    private String organizerDescription;
    private String ticketUrl;
    private String privacy;
    private String type;
    private String topic;
    private String subTopic;
    private String codeOfConduct;
    private String email;
    private String schedulePublishedOn;
    private String searchableLocationName;
    private String state;
    private boolean isSessionsSpeakersEnabled;
    private String thumbnailImageUrl;
    private String originalImageUrl;
    private String largeImageUrl;
    private String iconImageUrl;
    private String createdAt;
    private String deletedAt;
    @Relationship("event-copyright")
    private Copyright eventCopyright;
    @Relationship("speakers-call")
    private SpeakersCall speakersCall;
    @Relationship("social-links")
    private RealmList<SocialLink> socialLinks;
}