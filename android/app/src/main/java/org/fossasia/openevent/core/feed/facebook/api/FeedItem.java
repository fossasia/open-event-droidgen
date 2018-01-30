package org.fossasia.openevent.core.feed.facebook.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FeedItem {

    private String id;
    private String message;
    private String createdTime;
    private Comments comments;
    private String fullPicture;
    private String link;

}