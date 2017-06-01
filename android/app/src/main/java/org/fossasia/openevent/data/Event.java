package org.fossasia.openevent.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.data.extras.CallForPapers;
import org.fossasia.openevent.data.extras.Copyright;
import org.fossasia.openevent.data.extras.LicenseDetails;
import org.fossasia.openevent.data.extras.SocialLink;
import org.fossasia.openevent.data.extras.Version;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject {

    @SerializedName("id")
    @Expose
    @PrimaryKey
    private Integer id;

    @SerializedName("event_url")
    @Expose
    private String eventUrl;

    @Expose
    private String description;

    @SerializedName("licence_details")
    @Expose
    private LicenseDetails licenseDetails;

    @SerializedName("start_time")
    @Expose
    private String startTime;

    @SerializedName("end_time")
    @Expose
    private String endTime;

    @SerializedName("ticket_url")
    @Expose
    private String ticketUrl;

    @Expose
    private String topic;

    @Expose
    private String thumbnail;

    @Expose
    private String timezone;

    @Expose
    private String logo;

    @SerializedName("searchable_location_name")
    @Expose
    private String searchableLocationName;

    @Expose
    private String large;

    @SerializedName("background_image")
    @Expose
    private String backgroundImage;

    @SerializedName("call_for_papers")
    @Expose
    private CallForPapers callForPapers;

    @SerializedName("location_name")
    @Expose
    private String locationName;

    @Expose
    private String name;

    @Expose
    private Copyright copyright;

    @Expose
    private String privacy;

    @SerializedName("placeholder_url")
    @Expose
    private String placeholderUrl;

    @SerializedName("social_links")
    @Expose
    private RealmList<SocialLink> socialLinks;

    @Expose
    private Double longitude;

    @SerializedName("organizer_name")
    @Expose
    private String organizerName;

    @SerializedName("schedule_published_on")
    @Expose
    private String schedulePublishedOn;

    @Expose
    private String state;

    @Expose
    private Version version;

    @SerializedName("has_session_speakers")
    @Expose
    private Boolean hasSessionSpeakers;

    @SerializedName("sub_topic")
    @Expose
    private String subTopic;

    @Expose
    private Double latitude;

    @SerializedName("organizer_description")
    @Expose
    private String organizerDescription;

    @Expose
    private String identifier;

    @Expose
    private String type;

    @Expose
    private String email;

    @SerializedName("code_of_conduct")
    @Expose
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