package org.fossasia.openevent.data.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rohanagarwal94 on 6/09/2017.
 */
public class FeedItem {

    private String id;
    private String message;
    @JsonProperty("created_time")
    private String createdTime;
    private Comments comments;
    @JsonProperty("full_picture")
    private String fullPicture;
    private String link;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }

    public String getFullPicture() {
        return fullPicture;
    }

    public void setFullPicture(String fullPicture) {
        this.fullPicture = fullPicture;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}