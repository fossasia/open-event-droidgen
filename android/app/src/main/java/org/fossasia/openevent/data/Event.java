package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.fossasia.openevent.data.extras.CallForPapers;
import org.fossasia.openevent.data.extras.Copyright;
import org.fossasia.openevent.data.extras.LicenseDetails;
import org.fossasia.openevent.data.extras.SocialLink;
import org.fossasia.openevent.data.extras.Version;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject {

    @PrimaryKey
    private Integer id;
    @JsonProperty("event_url")
    private String eventUrl;
    private String description;
    @JsonProperty("licence_details")
    private LicenseDetails licenseDetails;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    @JsonProperty("ticket_url")
    private String ticketUrl;
    private String topic;
    private String thumbnail;
    private String timezone;
    private String logo;
    @JsonProperty("searchable_location_name")
    private String searchableLocationName;
    private String large;
    @JsonProperty("background_image")
    private String backgroundImage;
    @JsonProperty("call_for_papers")
    private CallForPapers callForPapers;
    @JsonProperty("location_name")
    private String locationName;
    private String name;
    private Copyright copyright;
    private String privacy;
    @JsonProperty("placeholder_url")
    private String placeholderUrl;
    @JsonProperty("social_links")
    private RealmList<SocialLink> socialLinks;
    private Double longitude;
    @JsonProperty("organizer_name")
    private String organizerName;
    @JsonProperty("schedule_published_on")
    private String schedulePublishedOn;
    private String state;
    private Version version;
    @JsonProperty("has_session_speakers")
    private Boolean hasSessionSpeakers;
    @JsonProperty("sub_topic")
    private String subTopic;
    private Double latitude;
    @JsonProperty("organizer_description")
    private String organizerDescription;
    private String identifier;
    private String type;
    private String email;
    @JsonProperty("code_of_conduct")
    private String codeOfConduct;

    public String getEventUrl() {
        return eventUrl;
    }

    public String getDescription() {
        return description;
    }

    public LicenseDetails getLicenseDetails() {
        return licenseDetails;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public String getTopic() {
        return topic;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getLogo() {
        return logo;
    }

    public String getSearchableLocationName() {
        return searchableLocationName;
    }

    public int getId() {
        return id;
    }

    public String getLarge() {
        return large;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public CallForPapers getCallForPapers() {
        return callForPapers;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getName() {
        return name;
    }

    public Copyright getCopyright() {
        return copyright;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getPlaceholderUrl() {
        return placeholderUrl;
    }

    public RealmList<SocialLink> getSocialLinks() {
        return socialLinks;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public String getSchedulePublishedOn() {
        return schedulePublishedOn;
    }

    public String getState() {
        return state;
    }

    public Version getVersion() {
        return version;
    }

    public String getEndTime() {
        return endTime;
    }

    public Boolean getHasSessionSpeakers() {
        return hasSessionSpeakers;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getOrganizerDescription() {
        return organizerDescription;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    public String getCodeOfConduct() {
        return codeOfConduct;
    }
}