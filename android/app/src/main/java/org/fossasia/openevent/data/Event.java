package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import org.fossasia.openevent.data.extras.CallForPapers;
import org.fossasia.openevent.data.extras.Copyright;
import org.fossasia.openevent.data.extras.LicenseDetails;
import org.fossasia.openevent.data.extras.SocialLink;
import org.fossasia.openevent.data.extras.Version;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Type("event")
public class Event extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    private String eventUrl;
    private String description;
    @JsonProperty("licence_details")
    private LicenseDetails licenseDetails;
    @JsonProperty("background_image")
    private String backgroundImage;
    private String startTime;
    private String endTime;
    private String ticketUrl;
    private String topic;
    private String thumbnail;
    private String timezone;
    private String logo;
    private String searchableLocationName;
    private String large;
    private CallForPapers callForPapers;
    private String locationName;
    private String name;
    private Copyright copyright;
    private String privacy;
    private String placeholderUrl;
    @Relationship("social-links")
    private RealmList<SocialLink> socialLinks;
    private Double longitude;
    private String organizerName;
    private String schedulePublishedOn;
    private String state;
    private Version version;
    private Boolean hasSessionSpeakers;
    private String subTopic;
    private Double latitude;
    private String organizerDescription;
    private String identifier;
    private String type;
    private String email;
    private String codeOfConduct;

    @JsonSetter("thumbnail")
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @JsonSetter("thumbnail-image-url")
    public void setThumbnailNewModel(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @JsonSetter("logo")
    public void setLogo(String logo) {
        this.logo = logo;
    }

    @JsonSetter("logo-url")
    public void setLogoForNewModel(String logo) {
        this.logo = logo;
    }

    @JsonSetter("event_url")
    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    @JsonSetter("event-url")
    public void setEventUrlForNewModel(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    @JsonSetter("start_time")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @JsonSetter("starts-at")
    public void setStartTimeForNewModel(String startTime) {
        this.startTime = startTime;
    }

    @JsonSetter("end_time")
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @JsonSetter("ends-at")
    public void setEndTimeForNewModel(String endTime) {
        this.endTime = endTime;
    }

    @JsonSetter("ticket_url")
    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    @JsonSetter("ticket-url")
    public void setTicketUrlForNewModel(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    @JsonSetter("searchable_location_name")
    public void setSearchableLocationName(String searchableLocationName) {
        this.searchableLocationName = searchableLocationName;
    }

    @JsonSetter("searchable-location-name")
    public void setSearchableLocationNameForNewModel(String searchableLocationName) {
        this.searchableLocationName = searchableLocationName;
    }

    @JsonSetter("call_for_papers")
    public void setCallForPapers(CallForPapers callForPapers) {
        this.callForPapers = callForPapers;
    }

    @JsonSetter("call-for-papers")
    public void setCallForPapersForNewModel(CallForPapers callForPapers) {
        this.callForPapers = callForPapers;
    }

    @JsonSetter("copyright")
    public void setCopyright(Copyright copyright) {
        this.copyright = copyright;
    }

    @JsonSetter("event-copyright")
    public void setCopyrightForNewModel(Copyright copyright) {
        this.copyright = copyright;
    }

    @JsonSetter("location_name")
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @JsonSetter("location-name")
    public void setLocationNameForNewModel(String locationName) {
        this.locationName = locationName;
    }

    @JsonSetter("placeholder_url")
    public void setPlaceholderUrl(String placeholderUrl) {
        this.placeholderUrl = placeholderUrl;
    }

    @JsonSetter("placeholder-url")
    public void setPlaceholderUrlForNewModel(String placeholderUrl) {
        this.placeholderUrl = placeholderUrl;
    }

    @JsonSetter("social_links")
    public void setSocialLinks(RealmList<SocialLink> socialLinks) {
        this.socialLinks = socialLinks;
    }

    @JsonSetter("social-links")
    public void setSocialLinksForNewModel(RealmList<SocialLink> socialLinks) {
        this.socialLinks = socialLinks;
    }

    @JsonSetter("organizer_name")
    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    @JsonSetter("organizer-name")
    public void setOrganizerNameForNewModel(String organizerName) {
        this.organizerName = organizerName;
    }

    @JsonSetter("code_of_conduct")
    public void setCodeOfConduct(String codeOfConduct) {
        this.codeOfConduct = codeOfConduct;
    }

    @JsonSetter("code-of-conduct")
    public void setCodeOfConductForNewModel(String codeOfConduct) {
        this.codeOfConduct = codeOfConduct;
    }

    @JsonSetter("organizer_description")
    public void setOrganizerDescription(String organizerDescription) {
        this.organizerDescription = organizerDescription;
    }

    @JsonSetter("organizer-description")
    public void setOrganizerDescriptionForNewModel(String organizerDescription) {
        this.organizerDescription = organizerDescription;
    }

    @JsonSetter("sub_topic")
    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    @JsonSetter("sub-topic")
    public void setSubTopicForNewModel(String subTopic) {
        this.subTopic = subTopic;
    }

    @JsonSetter("has_session_speakers")
    public void setHasSessionSpeakers(Boolean sessionSpeakers) {
        this.hasSessionSpeakers = hasSessionSpeakers;
    }

    @JsonSetter("has-session-speakers")
    public void setHasSessionSpeakersForNewModel(Boolean sessionSpeakers) {
        this.hasSessionSpeakers = hasSessionSpeakers;
    }

    @JsonSetter("schedule_published_on")
    public void setSchedulePublishedOn(String schedulePublishedOn) {
        this.schedulePublishedOn = schedulePublishedOn;
    }

    @JsonSetter("schedule-published-on")
    public void setSchedulePublishedOnForNewModel(String schedulePublishedOn) {
        this.schedulePublishedOn = schedulePublishedOn;
    }

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