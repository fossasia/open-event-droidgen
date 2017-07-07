package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import org.fossasia.openevent.data.extras.CallForPapers;
import org.fossasia.openevent.data.extras.Copyright;
import org.fossasia.openevent.data.extras.SocialLink;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Type("event")
public class Event extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    private String identifier;
    private String name;
    private Double latitude;
    private Double longitude;
    @JsonProperty("location-name")
    private String locationName;
    @JsonProperty("starts-at")
    private String startTime;
    @JsonProperty("ends-at")
    private String endTime;
    private String timezone;
    private String description;
    @JsonProperty("logo-url")
    private String logo;
    @JsonProperty("organizer-name")
    private String organizerName;
    @JsonProperty("organizer-description")
    private String organizerDescription;
    @Relationship("social-links")
    private RealmList<SocialLink> socialLinks;
    @JsonProperty("ticket-url")
    private String ticketUrl;
    private String privacy;
    private String type;
    private String topic;
    @JsonProperty("sub-topic")
    private String subTopic;
    @JsonProperty("code-of-conduct")
    private String codeOfConduct;
    @Relationship("copyright")
    private Copyright copyright;
    private String email;
    @JsonProperty("schedule-published-on")
    private String schedulePublishedOn;
    @JsonProperty("searchable-location-name")
    private String searchableLocationName;
    private String state;
    @JsonProperty("is-sessions-speakers-enabled")
    private Boolean hasSessionSpeakers;
    @JsonProperty("thumbnail-image-url")
    private String thumbnail;
    @JsonProperty("original-image-url")
    private String backgroundImage;
    @JsonProperty("large-image-url")
    private String large;
    @JsonProperty("icon-image-url")
    private String iconImageUrl;
    @JsonProperty("created-at")
    private String createdAt;
    @JsonProperty("deleted-at")
    private String deletedAt;
    @Relationship("speakers-call")
    private CallForPapers callForPapers;

    public int getId() {
        return id;
    }

    public void setCallForPapers(CallForPapers callForPapers) {
        this.callForPapers = callForPapers;
    }

    public CallForPapers getCallForPapers() {
        return callForPapers;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getIconImageUrl() {
        return iconImageUrl;
    }

    public void setIconImageUrl(String iconImageUrl) {
        this.iconImageUrl = iconImageUrl;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getLarge() {
        return large;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public void setOrganizerDescription(String organizerDescription) {
        this.organizerDescription = organizerDescription;
    }

    public String getOrganizerDescription() {
        return organizerDescription;
    }

    public void setSocialLinks(RealmList<SocialLink> socialLinks) {
        this.socialLinks = socialLinks;
    }

    public RealmList<SocialLink> getSocialLinks() {
        return socialLinks;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setCodeOfConduct(String codeOfConduct) {
        this.codeOfConduct = codeOfConduct;
    }

    public String getCodeOfConduct() {
        return codeOfConduct;
    }

    public void setCopyright(Copyright copyright) {
        this.copyright = copyright;
    }

    public Copyright getCopyright() {
        return copyright;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSchedulePublishedOn(String schedulePublishedOn) {
        this.schedulePublishedOn = schedulePublishedOn;
    }

    public String getSchedulePublishedOn() {
        return schedulePublishedOn;
    }

    public String getSearchableLocationName() {
        return searchableLocationName;
    }

    public void setSearchableLocationName(String searchableLocationName) {
        this.searchableLocationName = searchableLocationName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getHasSessionSpeakers() {
        return hasSessionSpeakers;
    }

    public void setHasSessionSpeakers(Boolean hasSessionSpeakers) {
        this.hasSessionSpeakers = hasSessionSpeakers;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}