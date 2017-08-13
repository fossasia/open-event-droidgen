package org.fossasia.openevent.data.feed;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;

import lombok.Data;

/**
 * Created by rohanagarwal94 on 6/09/2017.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FeedItem {

    //Facebook
    private String id;
    private String message;
    private String createdTime;
    private Comments comments;
    private String fullPicture;

    //common
    private String link;

    //Loklak
    private ArrayList<String> hashtags;
    private String text;
    private ArrayList<String> links;
    private ArrayList<String> images;

    @JsonGetter("created_time")
    public String getCreatedTime() {
        return createdTime;
    }

    @JsonSetter("created_time")
    public void setcreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    @JsonSetter("created_at")
    public void setCreatedAt(String createdAt) {
        if(this.createdTime == null) {
            String[] splitString = createdAt.split("\\.");
            setcreatedTime(splitString[0] + "+0000");
        }
    }
}